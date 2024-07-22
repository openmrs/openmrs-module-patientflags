/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patienttflags.impl;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Flag;

import org.openmrs.module.fhir2.api.impl.BaseFhirService;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patienttflags.dao.FhirFlagDao;
import org.openmrs.module.patienttflags.search.param.FlagSearchParams;
import org.openmrs.module.patienttflags.FhirFlagService;
import org.openmrs.module.patienttflags.translators.PatientFlagTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Setter
@Getter
public class FhirFlagServiceImpl extends BaseFhirService<Flag, PatientFlag> implements FhirFlagService {

    @Autowired
    private FhirFlagDao dao;

    @Autowired
    private PatientFlagTranslator translator;

    @Autowired
    private SearchQueryInclude<Flag> searchQueryInclude;

    @Autowired
    private SearchQuery<PatientFlag, Flag, FhirFlagDao, PatientFlagTranslator, SearchQueryInclude<Flag>> searchQuery;

    @Override
    public IBundleProvider searchFlags(FlagSearchParams patient) {
        return searchQuery.getQueryResults(patient.toSearchParameterMap(), dao, translator, searchQueryInclude);
    }
}