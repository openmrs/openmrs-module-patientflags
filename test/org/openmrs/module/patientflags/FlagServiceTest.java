package org.openmrs.module.patientflags;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.test.BaseModuleContextSensitiveTest;


/**
 * This tests the {@link FlagService}
 */
public class FlagServiceTest extends BaseModuleContextSensitiveTest {

	// TODO: create some actual methods that test the flagging of patients?
	
	/**
	 * Tests of the generateFlagsForPatient(Patient patient) method
	 */
	@Test
	public void generateFlagsForPatient_shouldAcceptNullParameter() throws Exception {
		((FlagService)Context.getService(FlagService.class)).generateFlagsForPatient(new Patient());
	}
	
	/**
	 * Tests of the getFlaggedPatients(Flag flag) method
	 */
	@Test
	public void getFlaggedPatients_shouldAcceptNullFlagParameter() throws Exception{
		((FlagService)Context.getService(FlagService.class)).getFlaggedPatients(new Flag());
	}
	
	/**
	 * Tests of the getFlaggedPatients(List<Flag> flags) method
	 */
	@Test
	public void getFlaggedPatients_shouldAcceptNullFlagListParameter() throws Exception{
		((FlagService)Context.getService(FlagService.class)).getFlaggedPatients(new ArrayList<Flag>());
	}
	
	/**
	 *  Tests of methods that save and retrieve flags
	 */
	@Test
	public void addOrUpdateFlag_shouldSaveNewFlag() throws Exception{
		// insert a "test" flag
		((FlagService)Context.getService(FlagService.class)).saveFlag(new Flag("test","test","test"));

		// get all flags
		List<Flag> flags = ((FlagService)Context.getService(FlagService.class)).getAllFlags();
		// since there is no test data loaded, the first (and only) flag should be the "test" one we just added
		Assert.assertEquals("test", flags.get(0).getName());
	}
	
	@Test
	public void addOrUpdateFlag_shouldUpdateFlag() throws Exception{
		// insert a "test" flag
		((FlagService)Context.getService(FlagService.class)).saveFlag(new Flag("test","test","test"));
		
		// get all flags
		List<Flag> flags = ((FlagService)Context.getService(FlagService.class)).getAllFlags();
		
		// since there is no test data loaded, the first (and only) flag should be the "test" one we just added
		// lets try changing the name of this flag
		flags.get(0).setName("testagain");
		
		// save this updated flag
		((FlagService)Context.getService(FlagService.class)).saveFlag(flags.get(0));
		
		// get all flags again
		flags = ((FlagService)Context.getService(FlagService.class)).getAllFlags();
		// confirm that the name has been changed again
		Assert.assertEquals("testagain", flags.get(0).getName());
	}
	
	@Test
	public void getFlag_shouldGetFlag() throws Exception{
		// create our test flag again
		((FlagService)Context.getService(FlagService.class)).saveFlag(new Flag("test","test","test"));

		// fetch it again
		List<Flag> flags = ((FlagService)Context.getService(FlagService.class)).getAllFlags();
		
		// now, see if we change fetch the same flag by it's id
		Flag flag = ((FlagService)Context.getService(FlagService.class)).getFlag(flags.get(0).getFlagId());
		Assert.assertEquals("test", flag.getName());
	}

	@Test
	public void removeFlag_shouldRemoveFlag() throws Exception{
		// create our test flag again
		((FlagService)Context.getService(FlagService.class)).saveFlag(new Flag("test","test","test"));

		// fetch it
		List<Flag> flags = ((FlagService)Context.getService(FlagService.class)).getAllFlags();
		
		// now, lets try to remove the flag we just created
		((FlagService)Context.getService(FlagService.class)).purgeFlag(flags.get(0).getFlagId());
		
		// fetch flags again
		flags = ((FlagService)Context.getService(FlagService.class)).getAllFlags();
		
		// the list should be empty, as we have deleted the only flag we created
		Assert.assertEquals(0, flags.size());
	}
	
	/**
	 *  Tests of methods that save and retrieve tags
	 */
	@Test
	public void addOrUpdateTag_shouldSaveNewTag() throws Exception{
		// insert a "test" tag
		((FlagService)Context.getService(FlagService.class)).saveTag(new Tag("test"));

		// get all tags
		List<Tag> tags = ((FlagService)Context.getService(FlagService.class)).getAllTags();
		// since there is no test data loaded, the first (and only) tag should be the "test" one we just added
		Assert.assertEquals("test",tags.get(0).getName());
	}
	
	@Test
	public void addOrUpdateTag_shouldUpdateTag() throws Exception{
		// insert a "test" tag
		((FlagService)Context.getService(FlagService.class)).saveTag(new Tag("test"));

		// get all tags
		List<Tag> tags = ((FlagService)Context.getService(FlagService.class)).getAllTags();
		
		// since there is no test data loaded, the first (and only) tag should be the "test" one we just added
		// lets try changing the name of this flag
		tags.get(0).setName("testagain");
		
		// save this updated tag
		((FlagService)Context.getService(FlagService.class)).saveTag(tags.get(0));
		
		// get all tags
		tags = ((FlagService)Context.getService(FlagService.class)).getAllTags();
		// confirm that the name has been changed again
		Assert.assertEquals("testagain", tags.get(0).getName());
	}
	
	@Test
	public void getTag_shouldGetTag() throws Exception{
		// insert a "test" tag
		((FlagService)Context.getService(FlagService.class)).saveTag(new Tag("test"));

		// get all tags
		List<Tag> tags = ((FlagService)Context.getService(FlagService.class)).getAllTags();
		
		// now, see if we can fetch the same tag by it's id
		Tag tag = ((FlagService)Context.getService(FlagService.class)).getTag(tags.get(0).getTagId());
		Assert.assertEquals("test", tag.getName());
	}

	@Test
	public void removeTag_shouldRemoveTag() throws Exception{
		// insert a "test" tag
		((FlagService)Context.getService(FlagService.class)).saveTag(new Tag("test"));

		// get all tags
		List<Tag> tags = ((FlagService)Context.getService(FlagService.class)).getAllTags();
		
		// now, lets try to remove the tag we just created
		((FlagService)Context.getService(FlagService.class)).purgeTag(tags.get(0).getTagId());
		
		// get all tags again
		tags = ((FlagService)Context.getService(FlagService.class)).getAllTags();
		
		// the list should be empty, as we have deleted the only flag we created
		Assert.assertEquals(0, tags.size());
	}
	
	/*
	 * Tests of flag filtering
	 */
	
	// TODO: add tests of actual filters here
	
	@Test
	public void getFlagsByFilter_shouldAcceptNullParameter() throws Exception{
		((FlagService)Context.getService(FlagService.class)).getFlagsByFilter(null);
	}
}
