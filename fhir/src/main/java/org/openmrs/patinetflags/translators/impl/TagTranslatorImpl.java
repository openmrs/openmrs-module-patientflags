package org.openmrs.patinetflags.translators.impl;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.patinetflags.translators.TagTranslator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
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
        addConceptCoding(codeableConcept.addCoding(),null,tag.getUuid(),tag);
        return codeableConcept;
    }

    private void addConceptCoding(Coding coding, String system, String code, Tag tag) {
        coding.setSystem(system);
        coding.setCode(code);
        if (system == null) {
            coding.setDisplay(tag.getName());
        }
    }
}
