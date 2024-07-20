package org.openmrs.module.patientflags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	@Test
	public void savePatientFLags_shouldAcceptListOfPatientFlag() {
		Patient patient = Context.getService(PatientService.class).getPatient(2);
		List<PatientFlag> patientFlags = createPatientFlags(patient.getPatientId());
		flagService.savePatientFlags(patientFlags);
		List<PatientFlag> savedPatientFlags = flagService.getPatientFlags(patient);
		assertFalse(savedPatientFlags.isEmpty());
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

	private List<PatientFlag> createPatientFlags(Integer patientId) {
		List<Flag> flags = flagService.getAllFlags();
		return flags.stream().map(flag -> {
			PatientFlag patientFlag = new PatientFlag();
			patientFlag.setPatient(new Patient(patientId));
			patientFlag.setFlag(flag);
			patientFlag.setMessage(flag.getMessage());
			return patientFlag;
		}).collect(Collectors.toList());
	}
}
