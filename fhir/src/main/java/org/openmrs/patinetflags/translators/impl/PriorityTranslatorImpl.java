package org.openmrs.patinetflags.translators.impl;

import lombok.AccessLevel;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.patinetflags.translators.PriorityTranslator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class PriorityTranslatorImpl implements PriorityTranslator {
    /**
     * Maps an OpenMRS data element to a FHIR resource
     *
     * @param priority the OpenMRS data element to translate
     * @return the corresponding FHIR resource
     */
    @Override
    public CodeableConcept toFhirResource(@Nonnull Priority priority) {

        if(priority == null) {
            return null;
        }

        CodeableConcept codeableConcept = new CodeableConcept();
        addConceptCoding(codeableConcept.addCoding(),null, priority.getUuid(), priority);
        return codeableConcept;
    }

    private void addConceptCoding(Coding coding, String system, String code, Priority priority) {
        coding.setSystem(system);
        coding.setCode(code);
        if (system == null) {
            coding.setDisplay(priority.getName());
        }
    }
}
