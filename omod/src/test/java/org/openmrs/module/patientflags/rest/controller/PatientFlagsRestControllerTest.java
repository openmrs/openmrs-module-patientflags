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
package org.openmrs.module.patientflags.rest.controller;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;



public class PatientFlagsRestControllerTest extends MainResourceControllerTest {

	private FlagService service;
	public static final String TEST_DATASETS_PROPERTIES_FILE = "test-datasets.properties";
	protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";
	protected static final String XML_DATASET = "flagtest-dataset-openmrs-1.10.xml";

	@Before
	public void before() throws Exception {

		service = Context.getService(FlagService.class);
	}


	@Test
	public void shouldSetupContext() {
		Assert.assertNotNull(service);
	}

	/**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "flags";
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
