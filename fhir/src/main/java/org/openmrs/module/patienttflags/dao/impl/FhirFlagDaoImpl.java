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
import jakarta.persistence.criteria.Join;
import lombok.Setter;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.api.dao.internals.OpenmrsFhirCriteriaContext;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patienttflags.dao.FhirFlagDao;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Optional;


@Component
@Setter
public class FhirFlagDaoImpl extends BaseFhirDao<PatientFlag> implements FhirFlagDao {

    /**
     * @param criteria
     * @param theParams
     */
    @Override
    protected <U> void setupSearchParams(@Nonnull OpenmrsFhirCriteriaContext<PatientFlag, U> criteriaContext,
                                         @Nonnull SearchParameterMap theParams) {
        theParams.getParameters().forEach(entry -> {
            switch (entry.getKey()) {
                case FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> getSearchQueryHelper().handlePatientReference(criteriaContext, (ReferenceAndListParam) param.getParam()));
                    break;
                case FhirConstants.CATEGORY_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> handleCategory(criteriaContext, (TokenAndListParam) param.getParam()));
                    break;
                case FhirConstants.DATE_RANGE_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> getSearchQueryHelper().handleDateRange(criteriaContext, "dateCreated", (DateRangeParam) param.getParam()).ifPresent(criteriaContext::addPredicate));
                    break;
                case FhirConstants.CODED_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> handleCode(criteriaContext, (TokenAndListParam) param.getParam()));
                    break;
            }
        });
    }

    private <U> void handleCode(OpenmrsFhirCriteriaContext<PatientFlag, U> criteriaContext, TokenAndListParam code) {
        if (code != null)
            handleAndListParam(criteriaContext.getCriteriaBuilder(), code, (message) -> Optional.of(criteriaContext.getCriteriaBuilder()
                    .equal(criteriaContext.getRoot().get("message"), message.getValue()))).ifPresent(criteriaContext::addPredicate);
    }

    private <U> void handleCategory(OpenmrsFhirCriteriaContext<PatientFlag, U> criteriaContext, TokenAndListParam category) {
        if (category != null) {
            Join<?, ?> flagJoin = criteriaContext.addJoin("flag", "f");
            Join<?, ?> tagsJoin = criteriaContext.addJoin(flagJoin, "tags", "ft");
            handleAndListParam(criteriaContext.getCriteriaBuilder(), category, (tag) -> Optional.of(criteriaContext.getCriteriaBuilder()
                    .equal(tagsJoin.get("name"), tag.getValue()))).ifPresent(criteriaContext::addPredicate);
        }
    }
}
