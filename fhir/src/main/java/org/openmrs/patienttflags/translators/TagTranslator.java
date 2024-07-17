package org.openmrs.patienttflags.translators;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.openmrs.module.fhir2.api.translators.ToFhirTranslator;
import org.openmrs.module.patientflags.Tag;

import javax.annotation.Nonnull;

public interface TagTranslator extends ToFhirTranslator<Tag, CodeableConcept> {

    /**
     * Maps an OpenMRS data element to a FHIR resource
     *
     * @param tag the OpenMRS data element to translate
     * @return the corresponding FHIR resource
     */
    @Override
    CodeableConcept toFhirResource(@Nonnull Tag tag);

}
