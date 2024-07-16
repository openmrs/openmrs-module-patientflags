package org.openmrs.patinetflags.impl;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Flag;

import org.openmrs.module.fhir2.api.impl.BaseFhirService;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.patinetflags.FhirFlagService;
import org.openmrs.patinetflags.dao.FhirFlagDao;
import org.openmrs.patinetflags.translators.PatientFlagTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PROTECTED)
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
    public IBundleProvider searchFlags(ReferenceAndListParam patient) {
        SearchParameterMap theParams = new SearchParameterMap();
        if(patient != null ){
            theParams.addParameter("aa",patient);
        }
        return searchQuery.getQueryResults(theParams, dao, translator, searchQueryInclude);
    }
}