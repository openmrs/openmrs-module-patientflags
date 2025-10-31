/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patientflags.translators.impl;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Patient;
import org.openmrs.module.fhir2.api.translators.PatientReferenceTranslator;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patienttflags.translators.FlagTranslator;
import org.openmrs.module.patienttflags.translators.TagTranslator;
import org.openmrs.module.patienttflags.translators.impl.PatientFlagTranslatorImpl;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PatientFlagTranslatorImplTest {
	
	String FLAG_UUID = "123xx34-623hh34-22hj89-23hjy5";
	
	@Mock
	private PatientReferenceTranslator patientReferenceTranslator;
	
	@Mock
	private TagTranslator tagTranslator;
	
	@Mock
	private FlagTranslator flagTranslator;
	
	PatientFlagTranslatorImpl patientFlagTranslator;
	
	PatientFlag patientFlag;
	
	org.openmrs.module.patientflags.Flag openmrsFlag;
	
	Tag tag;
	
	Patient patient;
	
	@Before
	public void setup() {
		patientFlagTranslator = new PatientFlagTranslatorImpl();
		patientFlagTranslator.setFlagTranslator(flagTranslator);
		patientFlagTranslator.setPatientReferenceTranslator(patientReferenceTranslator);
		patientFlagTranslator.setTagTranslator(tagTranslator);
		
		patient = new Patient();
		tag = new Tag();
		openmrsFlag = new org.openmrs.module.patientflags.Flag();
		openmrsFlag.setTags(Collections.singleton(tag));
		patientFlag = new PatientFlag();
		patientFlag.setUuid(FLAG_UUID);
		patientFlag.setFlag(openmrsFlag);
	}
	
	@Test
	public void shouldTranslatePatientFlagToFhirFlag() {
		when(tagTranslator.toFhirResource(tag)).thenReturn(new CodeableConcept());
		when(flagTranslator.toFhirResource(openmrsFlag)).thenReturn(new CodeableConcept());
		when(patientReferenceTranslator.toFhirResource(patient)).thenReturn(new Reference());
		
		Flag flag = patientFlagTranslator.toFhirResource(patientFlag);
		assertThat(flag, notNullValue());
		assertThat(flag.getId(), equalTo(FLAG_UUID));
	}
}
