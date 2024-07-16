package org.openmrs.patinetflags.translators.impl;

import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.Nonnull;

import java.util.Collections;


import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.Period;
import org.openmrs.module.fhir2.api.translators.PatientReferenceTranslator;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.patinetflags.translators.FlagTranslator;
import org.openmrs.patinetflags.translators.PatientFlagTranslator;
import org.openmrs.patinetflags.translators.TagTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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

    @Override
    public PatientFlag toOpenmrsType(@Nonnull Flag flag) {
        return null;
    }
}