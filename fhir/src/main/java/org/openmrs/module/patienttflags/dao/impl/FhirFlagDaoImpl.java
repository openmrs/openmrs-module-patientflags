/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patienttflags.dao.impl;

import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import lombok.Setter;
import org.hibernate.Criteria;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patienttflags.dao.FhirFlagDao;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.hibernate.criterion.Restrictions.eq;


@Component
@Setter
public class FhirFlagDaoImpl extends BaseFhirDao<PatientFlag> implements FhirFlagDao {

    /**
     * @param criteria
     * @param theParams
     */
    @Override
    protected void setupSearchParams(Criteria criteria, SearchParameterMap theParams) {
        theParams.getParameters().forEach(entry -> {
            switch (entry.getKey()) {
                case FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> handlePatientReference(criteria, (ReferenceAndListParam) param.getParam()));
                    break;
                case FhirConstants.CATEGORY_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> handleCategory(criteria, (TokenAndListParam) param.getParam()));
                    break;
                case FhirConstants.DATE_RANGE_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> handleDateRange("dateCreated", (DateRangeParam) param.getParam()).ifPresent(criteria::add));
                    break;
                case FhirConstants.CODED_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> handleCode(criteria, (TokenAndListParam) param.getParam()));
                    break;
            }
        });
    }

    private void handleCode(Criteria criteria, TokenAndListParam code) {
        if (code != null)
            handleAndListParam(code, (message) -> Optional.of(eq("message", message.getValue()))).ifPresent(criteria::add);
    }

    private void handleCategory(Criteria criteria, TokenAndListParam category) {
        if (category != null) {
            criteria.createAlias("flag", "f");
            criteria.createAlias("f.tags", "ft");
            handleAndListParam(category, (tag) -> Optional.of(eq("ft.name", tag.getValue()))).ifPresent(criteria::add);
        }
    }
}
