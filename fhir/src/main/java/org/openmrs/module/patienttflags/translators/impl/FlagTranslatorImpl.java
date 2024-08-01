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
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patienttflags.translators.FlagTranslator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Slf4j
@Component
@Setter
public class FlagTranslatorImpl implements FlagTranslator {
    /**
     * @param flag
     * @return
     */
    @Override
    public CodeableConcept toFhirResource(@Nonnull Flag flag) {
        if(flag == null){
            return null;
        }

        CodeableConcept codeableConcept = new CodeableConcept();
        addConceptCoding(codeableConcept.addCoding(), null, flag);
        codeableConcept.setText(flag.getMessage());
        return codeableConcept;
    }

    private void addConceptCoding(Coding coding, String system, Flag flag) {
        coding.setSystem(system);
        if (system == null) {
            coding.setDisplay(flag.getName());
        }
    }
}
