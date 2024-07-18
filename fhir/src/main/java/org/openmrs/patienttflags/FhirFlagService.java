package org.openmrs.patienttflags;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import org.hl7.fhir.r4.model.Flag;
import org.openmrs.module.fhir2.api.FhirService;
import org.openmrs.patienttflags.search.param.FlagSearchParams;

import javax.annotation.Nonnull;

public interface FhirFlagService extends FhirService<Flag> {

    @Override
    Flag get(@Nonnull String uuid);

    IBundleProvider searchFlags(FlagSearchParams patient);
}
