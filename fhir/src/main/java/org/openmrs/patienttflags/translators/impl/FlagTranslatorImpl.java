package org.openmrs.patienttflags.translators.impl;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.patienttflags.translators.FlagTranslator;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
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
        addConceptCoding(codeableConcept.addCoding(),null, flag.getMessage(), flag);
        return codeableConcept;
    }

    private void addConceptCoding(Coding coding, String system, String code, Flag flag) {
        coding.setSystem(system);
        coding.setCode(code);

        if (system == null) {
            coding.setDisplay(flag.getName());
        }
    }
}
