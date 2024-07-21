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
}
