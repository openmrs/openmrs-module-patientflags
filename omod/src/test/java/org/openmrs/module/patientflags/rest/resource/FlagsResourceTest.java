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
package org.openmrs.module.patientflags.rest.resource;


import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.rest.resource.FlagsResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;



public class FlagsResourceTest extends BaseDelegatingResourceTest<FlagsResource, Flag> {
	
	private FlagService service;
	public static final String TEST_DATASETS_PROPERTIES_FILE = "test-datasets.properties";
	protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";
	protected static final String XML_DATASET = "flagtest-dataset-openmrs-1.10.xml";

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + XML_DATASET);
	}

	@Override
	public Flag newObject() {
		service = Context.getService(FlagService.class);
		List<Flag> flags = service.getAllFlags();
		Flag flag = flags.get(0);

		 return flag;
	}

	@Override
	public void validateRefRepresentation() throws Exception {
		super.validateRefRepresentation();
		//assertPropEquals("voided", null); // voided parameter only included if voided
	}

	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertPropPresent("patient");
		assertPropPresent("uuid");
		assertPropPresent("flags");
		assertPropNotPresent("auditInfo");
	}

	@Override
	public void validateFullRepresentation() throws Exception {
		assertPropPresent("patient");
		assertPropPresent("uuid");
		assertPropPresent("flags");
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Mr. Dumb Test Hornblower Esq., Xanadu: 2005-01-03 00:00:00.0 - 2005-01-03 11:00:00.0";
	}

	@Override
	public String getUuidProperty() {
		return "759799ab-c9a5-435e-b671-77773ada7499";
	}

}
