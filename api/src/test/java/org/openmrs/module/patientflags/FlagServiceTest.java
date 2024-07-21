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
import org.openmrs.api.APIException;
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

	@Test
	public void getAllPriorities_shouldGetAllPriorities() {
		List<Priority> priorities = flagService.getAllPriorities();
		assertFalse(priorities.isEmpty());
	}

	@Test
	public void getPriority_shouldGetPriorityById() {
		Integer priorityId = 1;
		Priority priority = flagService.getPriority(priorityId);
		assertNotNull(priority);
		assertEquals(priorityId, priority.getPriorityId());
	}

	@Test
	public void getPriorityByUuid_shouldGetPriorityByUuid() {
		String priorityUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		Priority priority = flagService.getPriorityByUuid(priorityUuid);
		assertNotNull(priority);
		assertEquals(priorityUuid, priority.getUuid());
	}

	@Test
	public void getPriorityByName_shouldGetPriorityByName() {
		String priorityName = "pr 2";
		Priority priority = flagService.getPriorityByName(priorityName);
		assertNotNull(priority);
		assertEquals(priorityName, priority.getName());
	}

	@Test
	public void purgePriority_shouldPurgePriority() {
		Integer priorityId = 2;
		flagService.purgePriority(priorityId);
		Priority priority = flagService.getPriority(priorityId);
		assertNull(priority);
	}

	@Test(expected = APIException.class)
	public void purgePriority_shouldTrowExceptionWhenPriorityAssociatedWithAFlag() {
		Integer priorityId = 1;

		flagService.purgePriority(priorityId);
		Priority purgedPriority = flagService.getPriority(priorityId);
		assertNotNull(purgedPriority);
	}

	@Test
	public void retirePriority_shouldRetirePriorityWithReason() {
		String reason = "reason";
		Priority priority = flagService.getPriority(2);
		flagService.retirePriority(priority, reason);
		Priority retiredpriority = flagService.getPriority(2);
		assertTrue(retiredpriority.getRetired());
		assertEquals(reason, retiredpriority.getRetireReason());
	}

	@Test(expected = APIException.class)
	public void retirePriority_shouldNotRetirePriorityWithoutReason() {
		String reason = "";
		Priority priority = flagService.getPriority(2);
		flagService.retirePriority(priority, reason);
		Priority retiredpriority = flagService.getPriority(2);
		assertFalse(retiredpriority.getRetired());
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
	public void getAllTags_shouldReturnAllTags() {
		List<Tag> tags = flagService.getAllTags();
		assertFalse(tags.isEmpty());
	}

	@Test
	public void getTag_shouldGetTagById() {
		Integer id = 1;
		Tag tag = flagService.getTag(id);
		assertNotNull(tag);
		assertEquals(id, tag.getTagId());
	}

	@Test
	public void getTagByUuid_shouldGetTagByUuid() {
		String uuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		Tag tag = flagService.getTagByUuid(uuid);
		assertNotNull(tag);
		assertEquals(uuid, tag.getUuid());
	}

	@Test
	public void getTag_shouldGetTagByName() {
		String tagName = "high";
		Tag tag = flagService.getTag(tagName);
		assertNotNull(tag);
		assertEquals(tagName, tag.getName());
	}

	@Test
	public void addOrUpdateTag_shouldSaveNewTag() {
		Tag tag = createTag();
		flagService.saveTag(tag);

		Tag savedTag = flagService.getTag(tag.getName());
		assertNotNull(savedTag);
	}
	
	@Test
	public void addOrUpdateTag_shouldUpdateTag() {
		Tag tag = flagService.getTag(1);
		tag.setName("high tag");
		flagService.saveTag(tag);

		Tag savedTag = flagService.getTag(tag.getName());
		assertNotNull(savedTag);
	}

	@Test
	public void removeTag_shouldRemoveTag() {
		flagService.purgeTag(1);

		Tag tag = flagService.getTag(1);
		assertNull(tag);
	}

	@Test
	public void retireTag_shouldRetireTag() {
		Tag tag = flagService.getTag(1);
		flagService.retireTag(tag,"retire reason");

		Tag retiredTag = flagService.getTag(1);
		assertTrue(retiredTag.getRetired());
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

	private Tag createTag() {
		Tag tag = new Tag();
		tag.setName("allergy");
		return tag;
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
