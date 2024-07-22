package org.openmrs.module.patientflags;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.task.PatientFlagTask;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.lang.reflect.Method;
import java.util.List;

import static junit.framework.TestCase.assertFalse;

public class PatientFlagTaskTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";

    private static final String TEST_DATASET_FILE = XML_DATASET_PATH + "patientflagtest-dataset.xml";

    PatientFlagTask patientFlagTask;

    @Before
    public void initTestData() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet(TEST_DATASET_FILE);
        patientFlagTask = new PatientFlagTask();
        PatientFlagTask.setDaemonToken(new DaemonToken("daemon token"));

        authenticate();

        Module flagModule = new Module("flag module");
        flagModule.setModuleId("flag module");
        flagModule.setModuleActivator(new PatientFlagsModuleActivator());

        Method passDaemonTokenMethod = ModuleFactory.class.getDeclaredMethod("passDaemonToken", Module.class);
        passDaemonTokenMethod.setAccessible(true);
        passDaemonTokenMethod.invoke(null, flagModule);

        Method getDaemonTokenMethod = ModuleFactory.class.getDeclaredMethod("getDaemonToken", Module.class);
        getDaemonTokenMethod.setAccessible(true);
        DaemonToken token = (DaemonToken)getDaemonTokenMethod.invoke(null, flagModule);

        PatientFlagTask.setDaemonToken(token);
    }
 
    @Test
    public void generatePatientFlags_shouldGeneratePatientFlagsForPatient() {
        Patient patient = Context.getService(PatientService.class).getPatient(1);
        patientFlagTask.generatePatientFlags(patient);
        List<PatientFlag> patientFlags = Context.getService(FlagService.class).getPatientFlags(patient);
        assertFalse(patientFlags.isEmpty());
    }

    @Test
    public void generatePatientFlags_shouldGeneratePatientFlagsForFlag() {
        Patient patient = Context.getService(PatientService.class).getPatient(1);
        Flag flag = Context.getService(FlagService.class).getFlag(1);
        patientFlagTask.generatePatientFlags(flag);
        List<PatientFlag> patientFlags = Context.getService(FlagService.class).getPatientFlags(patient);
        assertFalse(patientFlags.isEmpty());
    }

    @Test
    public void evaluateAllFlags_shouldEvaluateAllFlagsAndAllPatients() {
        Patient patient = Context.getService(PatientService.class).getPatient(1);
        patientFlagTask.run();
        List<PatientFlag> patientFlags = Context.getService(FlagService.class).getPatientFlags(patient);
        assertFalse(patientFlags.isEmpty());
    }
}
