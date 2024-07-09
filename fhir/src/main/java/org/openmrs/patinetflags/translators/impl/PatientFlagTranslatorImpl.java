package org.openmrs.patinetflags.translators.impl;

import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.Nonnull;

import java.util.Collections;


import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.openmrs.module.fhir2.api.translators.PatientReferenceTranslator;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.patinetflags.translators.PatientFlagTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientFlagTranslatorImpl implements PatientFlagTranslator {

    @Autowired
    private PatientReferenceTranslator patientReferenceTranslator;

    @Override
    public Flag toFhirResource(@Nonnull PatientFlag patientFlag) {
        notNull(patientFlag, "The Openmrs Patient flag object should not be null");

        Flag flag = new Flag();

        flag.setId(patientFlag.getUuid());

        //        flag.setText();

        Identifier identifier = new Identifier();
        identifier.setValue(String.valueOf(patientFlag.getPatientFlagId()));
        flag.setIdentifier(Collections.singletonList(identifier));

        if (patientFlag.getVoided()) {
            flag.setStatus(Flag.FlagStatus.INACTIVE);
        } else {
            flag.setStatus(Flag.FlagStatus.ACTIVE);
        }

        //        flag.setCategory();

        //        flag.setCode();

        flag.setSubject(patientReferenceTranslator.toFhirResource(patientFlag.getPatient()));

        Period period = new Period();
        period.setStart(patientFlag.getDateCreated());
        flag.setPeriod(period);

        //        Reference reference = new Reference();
        //        reference.set
        //        flag.setAuthor(reference);

        return flag;
    }

    @Override
    public PatientFlag toOpenmrsType(@Nonnull Flag flag) {
        return null;
    }
}
