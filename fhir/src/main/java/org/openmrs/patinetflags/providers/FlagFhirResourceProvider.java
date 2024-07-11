package org.openmrs.patinetflags.providers;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.Setter;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Flag;
import org.openmrs.module.fhir2.api.annotations.R4Provider;
import org.openmrs.patinetflags.FhirFlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static lombok.AccessLevel.PACKAGE;

@Component("FlagFhirR4ResourceProvider")
@R4Provider
@Setter(PACKAGE)
public class FlagFhirResourceProvider implements IResourceProvider {

    @Autowired
    private FhirFlagService flagService;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Flag.class;
    }

    @Read
    public Flag getFlagById(@IdParam @Nonnull IdType id) {
        Flag flag = flagService.get(id.getIdPart());
        if (flag == null) {
            throw new ResourceNotFoundException("Could not find Flag with Id " + id.getIdPart());
        }
        return flag;
    }

}
