package org.openmrs.module.patientflags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.filter.Filter;
import org.openmrs.test.BaseModuleContextSensitiveTest;


/**
 * This tests the {@link FlagService}
 */
public class FlagServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";

	private static final String TEST_DATASET_FILE = XML_DATASET_PATH + "patientflagtest-dataset.xml";

	private FlagService flagService;

	/**
	 * Tests of the Flags
	 */

	@Before
	public void initTestData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet(TEST_DATASET_FILE);
		flagService = Context.getService(FlagService.class);
		authenticate();
	}
	
	/**
	 * Tests of the generateFlagsForPatient(Patient patient) method
	 */
	@Test
	public void generateFlagsForPatient_shouldAcceptNullParameter() {
		Patient patient = Context.getService(PatientService.class).getPatient(2);
		List<Flag> flags = flagService.generateFlagsForPatient(patient, null);
		assertFalse(flags.isEmpty());
	}

	@Test
	public void generateFlagsForPatient_shouldReturnListOfFlags() {
		Map<Object, Object> context = new HashMap<>();
		Patient patient = Context.getService(PatientService.class).getPatient(2);
		List<Flag> flags = flagService.generateFlagsForPatient(patient, context);
		assertFalse(flags.isEmpty());
	}
	
	/**
	 * Tests of the getFlaggedPatients(Flag flag) method
	 */
	@Test
	public void getFlaggedPatients_shouldAcceptNullFlagParameter() {
		Flag flag = flagService.getFlag(1);
		Cohort cohort = flagService.getFlaggedPatients(flag, null);
		assertFalse(cohort.isEmpty());
	}

	@Test
	public void getFlaggedPatients_shouldReturnListOfFlags() {
		Map<Object, Object> context = new HashMap<>();
		Flag flag = flagService.getFlag(1);
		Cohort cohort = flagService.getFlaggedPatients(flag, context);
		assertFalse(cohort.isEmpty());
	}
	
	/**
	 * Tests of the getFlaggedPatients(List<Flag> flags) method
	 */
	@Test
	public void getFlaggedPatients_shouldAcceptNullFlagListParameter() {
		List<Flag> flags = flagService.getAllFlags();
		Cohort cohort = flagService.getFlaggedPatients(flags, null);
		assertFalse(cohort.isEmpty());
	}

	@Test
	public void getFlaggedPatients_shouldReturnListOfFlagsListParameter() {
		Map<Object, Object> context = new HashMap<>();
		List<Flag> flags = flagService.getAllFlags();
		Cohort cohort = flagService.getFlaggedPatients(flags, context);
		assertFalse(cohort.isEmpty());
	}
	
	
	/**
	 * Tests of methods the save and retrieve flag prioritiess
	 */
	@Test
	public void savePriority_shouldSaveNewPriority() {
		Priority priority = new Priority("High", "style='background-color:red'", 1);
		flagService.savePriority(priority);
		
		// get newly saved priority by it's name
		Priority savedPriority = flagService.getPriorityByName("High");
		assertNotNull(savedPriority);
		assertEquals(priority.getName(), savedPriority.getName());
	}
	
	/**
	 *  Tests of methods that save and retrieve flags
	 */
	@Test
	public void saveFlag_shouldSaveNewFlag() {
		Flag flag = createTestFlag();
		flagService.saveFlag(flag);

		// get newly saved flag
		Flag savedFlag = flagService.getFlagByName("test");
  		assertEquals(flag.getName(), savedFlag.getName());
	}
	
	@Test
	public void saveFlag_shouldUpdateFlag() {
		Flag flag = flagService.getFlag(1);
		flag.setName("Drug allergy");
		
		// save this updated flag
		flagService.saveFlag(flag);
		
		// get flag again
		Flag updatedFlag = flagService.getFlag(1);
		// confirm that the name has been changed again
		assertEquals("Drug allergy", updatedFlag.getName());
	}
	
	@Test
	public void getAllFlags_shouldGetAllFlags() {
		List<Flag> flags = flagService.getAllFlags();
		assertFalse(flags.isEmpty());
	}

	@Test
	public void removeFlag_shouldRemoveFlag() {
		Flag flag = flagService.getFlag(2);
		flagService.purgeFlag(flag.getFlagId());
		
		// fetch flags again
		Flag removedFlag = flagService.getFlag(2);

		// when fetching a removed flag it should return null object
		assertNull(removedFlag);
	}

	@Test
	public void getFlagsByFilter_shouldGetAllFlagsForNullFilter() {
		List<Flag> flags = flagService.getFlagsByFilter(null);
		List<Flag> allFlags = flagService.getAllFlags();
		assertEquals(flags.size(), allFlags.size());
	}

	@Test
	public void getFlagsByFilter_shouldGetFilteredFlags() {
		Filter filter = new Filter();
		List<Flag> flags = flagService.getFlagsByFilter(filter);
		assertFalse(flags.isEmpty());
	}

	@Test
	public void getFlag_shouldReturnFlagById() {
		Integer flagId = 1;
		Flag flag = flagService.getFlag(flagId);
		assertNotNull(flag);
		assertEquals(flagId, flag.getFlagId());
	}

	@Test
	public void getFlagByUuid_shouldReturnFlagByUuid() {
		String uuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		Flag flag = flagService.getFlagByUuid(uuid);
		assertNotNull(flag);
		assertEquals(uuid, flag.getUuid());
	}

	/**
	 *  Tests of methods that save and retrieve tags
	 */
	@Test
	public void addOrUpdateTag_shouldSaveNewTag() {
		// insert a "test" tag
		Context.getService(FlagService.class).saveTag(new Tag("test"));

		// get all tags
		List<Tag> tags = Context.getService(FlagService.class).getAllTags();
		// since there is no test data loaded, the first (and only) tag should be the "test" one we just added
		assertEquals("test",tags.get(0).getName());
	}
	
	@Test
	public void addOrUpdateTag_shouldUpdateTag() {
		// insert a "test" tag
		Context.getService(FlagService.class).saveTag(new Tag("test"));

		// get all tags
		List<Tag> tags = Context.getService(FlagService.class).getAllTags();
		
		// since there is no test data loaded, the first (and only) tag should be the "test" one we just added
		// lets try changing the name of this flag
		tags.get(0).setName("testagain");
		
		// save this updated tag
		Context.getService(FlagService.class).saveTag(tags.get(0));
		
		// get all tags
		tags = Context.getService(FlagService.class).getAllTags();
		// confirm that the name has been changed again
		assertEquals("testagain", tags.get(0).getName());
	}
	
	@Test
	public void getTag_shouldGetTag() {
		// insert a "test" tag
		Context.getService(FlagService.class).saveTag(new Tag("test"));

		// get all tags
		List<Tag> tags = Context.getService(FlagService.class).getAllTags();
		
		// now, see if we can fetch the same tag by it's id
		Tag tag = Context.getService(FlagService.class).getTag(tags.get(0).getTagId());
		assertEquals("test", tag.getName());
	}

	@Test
	public void removeTag_shouldRemoveTag() {
		// insert a "test" tag
		Context.getService(FlagService.class).saveTag(new Tag("test"));

		// get all tags
		List<Tag> tags = Context.getService(FlagService.class).getAllTags();
		
		// now, lets try to remove the tag we just created
		Context.getService(FlagService.class).purgeTag(tags.get(0).getTagId());
		
		// get all tags again
		tags = Context.getService(FlagService.class).getAllTags();
		
		// the list should be empty, as we have deleted the only flag we created
		assertEquals(0, tags.size());
	}
	
	/*
	 * Tests of flag filtering
	 */
	
	// TODO: add tests of actual filters here
	
	@Test
	public void getFlagsByFilter_shouldAcceptNullParameter() {
		Context.getService(FlagService.class).getFlagsByFilter(null);
	}


	/**
	 * Test methods of patient flags
	 */

	@Test
	public void getFlagsForPatient_shouldReturnFlagList() {
		Patient patient = Context.getService(PatientService.class).getPatient(1);

		List<Flag> patientFlags = flagService.getFlagsForPatient(patient);
		assertFalse(patientFlags.isEmpty());
	}

	@Test
	public void savePatientFlag_shouldSaveSinglePatientFlag() {
		Patient patient = Context.getService(PatientService.class).getPatient(1);
		Flag flag = flagService.getFlag(2);

		PatientFlag patientFlag = createPatientFlag(patient,flag);
		flagService.savePatientFlag(patientFlag);
		PatientFlag savedPatientFlag = flagService.getPatientFlagByUuid("7d89924e-e8df-4551-a956-95de80529735");
		assertNotNull(savedPatientFlag);
	}

	@Test
	public void  deletePatientFlagsForPatient_shouldDeletePatientFlagsForPatient() {
		Patient patient = Context.getService(PatientService.class).getPatient(1);
		flagService.deletePatientFlagsForPatient(patient);
		List<PatientFlag> patientFlags = flagService.getPatientFlags(patient);
		assertTrue(patientFlags.isEmpty());
	}

	@Test
	public void  deletePatientFlagForPatient_shouldDeletePatientFlagForPatient() {
		Patient patient = Context.getService(PatientService.class).getPatient(1);
		PatientFlag patientFlag = flagService.getPatientFlagByUuid("7d89924e-e8df-4553-a956-95de80529735");
		assertNotNull(patientFlag);

		Flag flag = flagService.getFlag(1);
		flagService.deletePatientFlagForPatient(patient,flag);
		List<PatientFlag> patientFlags = flagService.getPatientFlags(patient);
		assertFalse(patientFlags.contains(patientFlag));
	}

	@Test
	public void  deletePatientFlagsForFlag_shouldDeletePatientFlagForFlag() {
		PatientFlag patientFlag = flagService.getPatientFlagByUuid("7d89924e-e8df-4553-a956-95de80529735");
		assertNotNull(patientFlag);

		Flag flag = flagService.getFlag(1);
		flagService.deletePatientFlagsForFlag(flag);
		PatientFlag deletedPatientFlag = flagService.getPatientFlagByUuid("7d89924e-e8df-4553-a956-95de80529735");
		assertNull(deletedPatientFlag);
	}

	@Test
	public void voidPatientFlag_shouldVoidPatientFlag() {
		PatientFlag patientFlag = flagService.getPatientFlagByUuid("7d89924e-e8df-4553-a956-95de80529735");
		assertNotNull(patientFlag);

		String voidReason = "remove form system";
		flagService.voidPatientFlag(patientFlag,voidReason);
		PatientFlag voidedPatientFlag = flagService.getPatientFlagByUuid("7d89924e-e8df-4553-a956-95de80529735");
		assertTrue(voidedPatientFlag.getVoided());
		assertEquals(voidReason,voidedPatientFlag.getVoidReason());
	}

	@Test
	public void unvoidPatientFlag_shouldUnvoidPatientFlag() {
		PatientFlag patientFlag = flagService.getPatientFlagByUuid("7d89924e-e8df-4543-a956-95de80529735");
		assertNotNull(patientFlag);

		flagService.unvoidPatientFlag(patientFlag);
		PatientFlag voidedPatientFlag = flagService.getPatientFlagByUuid("7d89924e-e8df-4543-a956-95de80529735");
		assertFalse(voidedPatientFlag.getVoided());
	}

	@Test
	public void getPatientFlags_shouldReturnListOfPatientFLags() {
		Patient patient = Context.getService(PatientService.class).getPatient(1);
		List<PatientFlag> patientFlags = flagService.getPatientFlags(patient);
		assertFalse(patientFlags.isEmpty());
	}

	@Test
	public void getPatientFlags_shouldAcceptNullParameters() {
		Patient patient = Context.getService(PatientService.class).getPatient(1);
		List<PatientFlag> patientFlags = flagService.getPatientFlags(patient,null,"");
		assertFalse(patientFlags.isEmpty());
	}

	/**
	 * Utility methods
	 */
	private Flag createTestFlag() {
		// create & save priority to use
		Priority priority = new Priority("test", "test", 1);
		flagService.savePriority(priority);
		
		// now create the flag
		Flag flag = new Flag("test", "test", "test");
		flag.setPriority(priority);
		flag.setEvaluator("org.openmrs.module.patientflags.evaluator.SQLFlagEvaluator");
		return flag;
	}

	private PatientFlag createPatientFlag(Patient patient, Flag flag) {
			PatientFlag patientFlag = new PatientFlag();
			patientFlag.setPatient(patient);
			patientFlag.setFlag(flag);
			patientFlag.setUuid("7d89924e-e8df-4551-a956-95de80529735");
			patientFlag.setMessage(flag.getMessage());
			return patientFlag;
	}
}
