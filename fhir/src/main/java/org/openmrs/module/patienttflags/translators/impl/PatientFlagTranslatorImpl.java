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

import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.Nonnull;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.Period;
import org.openmrs.module.fhir2.api.translators.PatientReferenceTranslator;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patienttflags.translators.FlagTranslator;
import org.openmrs.module.patienttflags.translators.PatientFlagTranslator;
import org.openmrs.module.patienttflags.translators.TagTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Setter
public class PatientFlagTranslatorImpl implements PatientFlagTranslator {

    @Autowired
    private PatientReferenceTranslator patientReferenceTranslator;

    @Autowired
    private TagTranslator tagTranslator;

    @Autowired
    private FlagTranslator flagTranslator;

    @Override
    public Flag toFhirResource(@Nonnull PatientFlag patientFlag) {
        notNull(patientFlag, "The Openmrs Patient flag object should not be null");

        Flag flag = new Flag();

        flag.setId(patientFlag.getUuid());

        if (patientFlag.getVoided()) {
            flag.setStatus(Flag.FlagStatus.INACTIVE);
        } else {
            flag.setStatus(Flag.FlagStatus.ACTIVE);
        }

        patientFlag.getFlag().getTags().forEach(tag -> {
            CodeableConcept codeableConcept = tagTranslator.toFhirResource(tag);
            flag.addCategory(codeableConcept);
        });

        flag.setCode(flagTranslator.toFhirResource(patientFlag.getFlag()));
        flag.setSubject(patientReferenceTranslator.toFhirResource(patientFlag.getPatient()));

        Period period = new Period();
        period.setStart(patientFlag.getDateCreated());
        flag.setPeriod(period);

        return flag;
    }

    /**
     * @param flag 
     * @return
     */
    @Override
    public PatientFlag toOpenmrsType(@Nonnull Flag flag) {
        return null;
    }
}