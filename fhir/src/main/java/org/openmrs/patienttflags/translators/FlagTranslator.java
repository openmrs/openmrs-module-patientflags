package org.openmrs.patienttflags.translators;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.openmrs.module.fhir2.api.translators.ToFhirTranslator;
import org.openmrs.module.patientflags.Flag;

import javax.annotation.Nonnull;

public interface FlagTranslator extends ToFhirTranslator<Flag, CodeableConcept> {

    /**
     * @param flag
     * @return
     */
    @Override
    CodeableConcept toFhirResource(@Nonnull Flag flag);
}
