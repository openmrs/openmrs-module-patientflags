/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.patientflags.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";

    private static final String TEST_DATASET_FILE = XML_DATASET_PATH + "patientflagtest-dataset.xml";

    @Before
    public void initTestData() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet(TEST_DATASET_FILE);
    }

    @Test
    public void filter_shouldFilterListOfFlags() {
        List<Flag> flags = Context.getService(FlagService.class).getAllFlags();
        Tag tag = Context.getService(FlagService.class).getTag(1);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        Filter filter = new Filter(tags);

        List<Flag> filterFlags = filter.filter(flags);
        assertTrue(flags.size() != filterFlags.size());
    }

    @Test
    public void filter_shouldReturnSameFlagListAsParameter() {
        List<Flag> flags = Context.getService(FlagService.class).getAllFlags();
        Filter filter = new Filter();

        List<Flag> filterFlags = filter.filter(flags);
        assertTrue(flags.size() == filterFlags.size());
    }

    @Test
    public void filter_shouldReturnTrueWhenNoTags() {
        Flag flag = Context.getService(FlagService.class).getFlag(1);
        Filter filter = new Filter();
        Boolean result = filter.filter(flag);
        assertTrue(result);
    }

    @Test
    public void filter_shouldReturnTrueWhenFlagHasTag() {
        Flag flag = Context.getService(FlagService.class).getFlag(1);
        Tag tag = Context.getService(FlagService.class).getTag(1);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        Filter filter = new Filter(tags);

        Boolean result = filter.filter(flag);
        assertTrue(result);
    }

    @Test
    public void filter_shouldReturnTrueWhenFlagHasNoTagAndFilterTagTypeANYTAG_OR_NOTAG() {
        Flag flag = Context.getService(FlagService.class).getFlag(2);
        Tag tag = Context.getService(FlagService.class).getTag(1);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        Filter filter = new Filter(tags);
        filter.setType(FilterType.ANYTAG_OR_NOTAG);

        Boolean result = filter.filter(flag);
        assertTrue(result);
    }

    @Test
    public void filter_shouldReturnTrueWhenFlagHasTagAndFilterTagTypeALLTAGS() {
        Flag flag = Context.getService(FlagService.class).getFlag(1);
        Tag tag = Context.getService(FlagService.class).getTag(1);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        Filter filter = new Filter(tags);
        filter.setType(FilterType.ALLTAGS);

        Boolean result = filter.filter(flag);
        assertTrue(result);
    }

    @Test
    public void filter_shouldReturnFalseWhenFlagHasTagAndFilterTagTypeALLTAGS() {
        Flag flag = Context.getService(FlagService.class).getFlag(2);
        Tag tag = Context.getService(FlagService.class).getTag(1);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        Filter filter = new Filter(tags);
        filter.setType(FilterType.ALLTAGS);

        Boolean result = filter.filter(flag);
        assertFalse(result);
    }
}
