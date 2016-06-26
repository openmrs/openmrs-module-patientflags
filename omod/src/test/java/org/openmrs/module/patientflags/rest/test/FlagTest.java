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
package org.openmrs.patientflags.rest.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;


import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.commons.beanutils.PropertyUtils;


public class FlagTest extends MainResourceControllerTest {
	
	private FlagService service;
	protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";
	protected static final String XML_DATASET = "flagtest-dataset-openmrs-1.10";	
	
	@Before
	public void before() throws Exception {
	
		service = Context.getService(FlagService.class);
		executeDataSet(XML_DATASET_PATH + XML_DATASET);
	}
	
	

	@Test
	public void shouldCreateNewPatientFlag() throws Exception {
		
		
		String json = "{ \"flag_id\":3, \"name\":\"Invalid Logic Sample\", "
				+ "\"evaluator\":\"org.openmrs.module.patientflags.evaluator.LogicFlagEvaluator\", \"criteria\":\"BLAH\", "
				+ "\"message\":\"Test\", \"enabled\":\"1\", \"creator\":\"1\", \"date_created\":\"2016-06-26 00:00:00.0\", "
				+ "\"retired\": \"false\", \"uuid\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\" }";
				
				
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		Object flag = deserialize(handle(req));
		Assert.assertNotNull(PropertyUtils.getProperty(flag, "uuid"));
		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(flag, "flag_id"), "uuid"));
		Assert.assertEquals(3,
		    PropertyUtils.getProperty(PropertyUtils.getProperty(flag, "name"), "uuid"));
		Assert.assertEquals("BLAH", PropertyUtils.getProperty(flag, "criteria"));
		Assert.assertEquals("org.openmrs.module.patientflags.evaluator.LogicFlagEvaluator",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(flag, "evaluator"), "uuid"));
		//Assert.assertEquals(getAllCount() + 1, FlagService.getAllFlags().size());
		
	}
	
	@Test
	public void savePriority_shouldSaveNewPriority() throws Exception {
		Priority priority = new Priority("test", "test", 1);
		service.savePriority(priority);
		
		// get all the priorities
		List<Priority> priorities = service.getAllPriorities();
		// since there is no test data loaded, the first (and only) priorites should be the "test" one we just added
		Assert.assertEquals("test", priorities.get(0).getName());
	}
	
	/**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "patientflagsrest";
    }
    
    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
    }
    
	/**
     * @see MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return 1;
    }

}
