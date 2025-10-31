package org.openmrs.module.patientflags;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.task.PatientFlagTask;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
import java.util.List;

import static junit.framework.TestCase.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
public class PatientFlagTaskTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";
	
	protected static final String INITIAL_CORE_DATASET = "org/openmrs/api/db/include/EncounterDAOTest-initialData.xml";
	
	private static final String TEST_DATASET_FILE = XML_DATASET_PATH + "patientflagtest-dataset.xml";
	
	PatientFlagTask patientFlagTask;
	
	@Autowired
	private PatientService patientService;
	
	@Before
	public void initTestData() throws Exception {
		initializeInMemoryDatabase();
		authenticate();
		executeDataSet(INITIAL_CORE_DATASET);
		executeDataSet(TEST_DATASET_FILE);
		patientFlagTask = new PatientFlagTask();
		PatientFlagTask.setDaemonToken(new DaemonToken("daemon token"));
		
		Module flagModule = new Module("flag module");
		flagModule.setModuleId("flag module");
		flagModule.setModuleActivator(new PatientFlagsModuleActivator());
		
		Method passDaemonTokenMethod = ModuleFactory.class.getDeclaredMethod("passDaemonToken", Module.class);
		passDaemonTokenMethod.setAccessible(true);
		passDaemonTokenMethod.invoke(null, flagModule);
		
		Method getDaemonTokenMethod = ModuleFactory.class.getDeclaredMethod("getDaemonToken", Module.class);
		getDaemonTokenMethod.setAccessible(true);
		DaemonToken token = (DaemonToken) getDaemonTokenMethod.invoke(null, flagModule);
		
		PatientFlagTask.setDaemonToken(token);
	}
	
	@Test
	public void generatePatientFlags_shouldGeneratePatientFlagsForPatient() {
		Patient patient = patientService.getPatient(2);
		patientFlagTask.generatePatientFlags(patient);
		List<PatientFlag> patientFlags = Context.getService(FlagService.class).getPatientFlags(patient);
		assertFalse(patientFlags.isEmpty());
	}
	
	@Test
	public void generatePatientFlags_shouldGeneratePatientFlagsForFlag() {
		Patient patient = patientService.getPatient(2);
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		patientFlagTask.generatePatientFlags(flag);
		List<PatientFlag> patientFlags = Context.getService(FlagService.class).getPatientFlags(patient);
		assertFalse(patientFlags.isEmpty());
	}
	
	@Test
	public void evaluateAllFlags_shouldEvaluateAllFlagsAndAllPatients() {
		Patient patient = patientService.getPatient(2);
		patientFlagTask.run();
		List<PatientFlag> patientFlags = Context.getService(FlagService.class).getPatientFlags(patient);
		assertFalse(patientFlags.isEmpty());
	}
}
