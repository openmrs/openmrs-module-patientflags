/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patienttflags.providers;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.Setter;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.Patient;
import org.openmrs.module.fhir2.api.annotations.R4Provider;
import org.openmrs.module.patienttflags.FhirFlagService;
import org.openmrs.module.patienttflags.search.param.FlagSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static lombok.AccessLevel.PACKAGE;

@Component("FlagFhirR4ResourceProvider")
@R4Provider
@Setter
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

    @Search
    public IBundleProvider searchFlags(@OptionalParam(name = Flag.SP_PATIENT, chainWhitelist = { "", Patient.SP_IDENTIFIER,
                                                Patient.SP_GIVEN, Patient.SP_FAMILY, Patient.SP_NAME }, targetTypes = Patient.class) ReferenceAndListParam patientReference,
                                       @OptionalParam(name = Flag.SP_RES_ID) TokenAndListParam id,
                                       @OptionalParam(name = "code") TokenAndListParam codeParam,
                                       @OptionalParam(name = "category") TokenAndListParam category,
                                       @OptionalParam(name = Flag.SP_DATE) DateRangeParam dateRangeParam) {

        FlagSearchParams searchParams = new FlagSearchParams();
        searchParams.setPatient(patientReference);
        searchParams.setCategory(category);
        searchParams.setCode(codeParam);
        searchParams.setDate(dateRangeParam);
        searchParams.setId(id);
        return flagService.searchFlags(searchParams);
    }

}
