package org.openmrs.patinetflags.translators.impl;

import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.Nonnull;

import java.util.Collections;


import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.openmrs.module.fhir2.api.translators.PatientReferenceTranslator;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.patinetflags.translators.PatientFlagTranslator;
import org.openmrs.patinetflags.translators.PriorityTranslator;
import org.openmrs.patinetflags.translators.TagTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.nio.cs.ext.COMPOUND_TEXT_Encoder;

@Component
public class PatientFlagTranslatorImpl implements PatientFlagTranslator {

    @Autowired
    private PatientReferenceTranslator patientReferenceTranslator;

    @Autowired
    private TagTranslator tagTranslator;

    @Autowired
    private PriorityTranslator priorityTranslator;

    @Override
    public Flag toFhirResource(@Nonnull PatientFlag patientFlag) {
        notNull(patientFlag, "The Openmrs Patient flag object should not be null");

        Flag flag = new Flag();

        flag.setId(patientFlag.getUuid());

        Narrative narrative = new Narrative();
        narrative.setStatus(Narrative.NarrativeStatus.GENERATED);

        XhtmlNode xhtmlNode = new XhtmlNode();
        xhtmlNode.setName(patientFlag.getMessage());
        narrative.setDiv(xhtmlNode);
        flag.setText(narrative);


        Identifier identifier = new Identifier();
        identifier.setValue(String.valueOf(patientFlag.getPatientFlagId()));
        flag.setIdentifier(Collections.singletonList(identifier));

        if (patientFlag.getVoided()) {
            flag.setStatus(Flag.FlagStatus.INACTIVE);
        } else {
            flag.setStatus(Flag.FlagStatus.ACTIVE);
        }

        patientFlag.getFlag().getTags().forEach(tag -> {
            CodeableConcept codeableConcept = tagTranslator.toFhirResource(tag);
            flag.addCategory(codeableConcept);
        });

        flag.setCode(priorityTranslator.toFhirResource(patientFlag.getFlag().getPriority()));

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