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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patienttflags.translators.impl.FlagTranslatorImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(MockitoJUnitRunner.class)
public class FlagTranslatorImplTest {
	
	private static final String FLAG_NAME = "flagName";
	
	private static final String FLAG_MESSAGE = "flag message";
	
	FlagTranslatorImpl flagTranslator;
	
	CodeableConcept codeableConcept;
	
	Flag flag;
	
	@Before
	public void setup() {
		flagTranslator = new FlagTranslatorImpl();
	}
	
	@Test
	public void shouldTranslateOpenmrsFlagToFhirCodeableConcept() {
		flag = new Flag();
		flag.setName(FLAG_NAME);
		flag.setMessage(FLAG_MESSAGE);
		
		codeableConcept = flagTranslator.toFhirResource(flag);
		
		assertThat(codeableConcept, notNullValue());
		assertThat(codeableConcept.getText(), equalTo(FLAG_MESSAGE));
	}
	
}
