/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patienttflags.translators.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.openmrs.module.fhir2.api.util.FhirUtils;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patienttflags.translators.TagTranslator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Slf4j
@Component
@Setter
public class TagTranslatorImpl implements TagTranslator {
	
	/**
	 * Maps an OpenMRS data element to a FHIR resource
	 * 
	 * @param tag the OpenMRS data element to translate
	 * @return the corresponding FHIR resource
	 */
	@Override
	public CodeableConcept toFhirResource(@Nonnull Tag tag) {
		if (tag == null) {
			return null;
		}
		
		CodeableConcept codeableConcept = new CodeableConcept();
		codeableConcept.setText(tag.getName());
		addConceptCoding(codeableConcept.addCoding(), null, tag.getName(), tag);
		return codeableConcept;
	}
	
	private void addConceptCoding(Coding coding, String system, String code, Tag tag) {
		coding.setSystem(system);
		coding.setCode(code);
		if (system == null) {
			coding.setDisplay(FhirUtils.getMetadataTranslation(tag));
		}
	}
}
