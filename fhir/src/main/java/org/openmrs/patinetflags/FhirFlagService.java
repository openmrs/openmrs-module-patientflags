package org.openmrs.patinetflags;

import org.hl7.fhir.r4.model.Flag;
import org.openmrs.module.fhir2.api.FhirService;

import javax.annotation.Nonnull;

public interface FhirFlagService extends FhirService<Flag> {

    @Override
    Flag get(@Nonnull String uuid);
}
