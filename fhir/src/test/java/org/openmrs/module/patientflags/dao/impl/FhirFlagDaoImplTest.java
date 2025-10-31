/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patientflags.dao.impl;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import org.openmrs.module.fhir2.TestFhirSpringConfiguration;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patienttflags.dao.impl.FhirFlagDaoImpl;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@ContextConfiguration(classes = TestFhirSpringConfiguration.class, inheritLocations = false)
public class FhirFlagDaoImplTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATASET_FILE = "org/openmrs/module/patientflags/testdata/fhirPatientflagtest-dataset.xml";
	
	String FLAG_UUID = "7d89924e-e8df-4553-a956-95de80529735";
	
	String WRONG_FLAG_UUID = "7d89924e-e8df-6666-a956-95de80529735";
	
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;
	
	private FhirFlagDaoImpl fhirFlagDao;
	
	@Before
	public void setup() throws Exception {
		fhirFlagDao = new FhirFlagDaoImpl();
		fhirFlagDao.setSessionFactory(sessionFactory);
		executeDataSet(TEST_DATASET_FILE);
	}
	
	@Test
	public void getByUuid_shouldReturnMatchingPatientFlag() {
		PatientFlag patientFlag = fhirFlagDao.get(FLAG_UUID);
		assertThat(patientFlag, notNullValue());
		assertThat(patientFlag.getUuid(), equalTo(FLAG_UUID));
	}
	
	@Test
	public void getByWithWrongUuid_shouldReturnNullPatientFlag() {
		PatientFlag patientFlag = fhirFlagDao.get(WRONG_FLAG_UUID);
		assertThat(patientFlag, nullValue());
	}
	
}
