/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patientflags.impl;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Patient;
import org.openmrs.module.fhir2.api.FhirGlobalPropertyService;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryBundleProvider;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patienttflags.dao.FhirFlagDao;
import org.openmrs.module.patienttflags.impl.FhirFlagServiceImpl;
import org.openmrs.module.patienttflags.search.param.FlagSearchParams;
import org.openmrs.module.patienttflags.translators.PatientFlagTranslator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * The type Fhir flag service impl test.
 */
@RunWith(MockitoJUnitRunner.class)
public class FhirFlagServiceImplTest {
	
	private static final int START_INDEX = 0;
	
	private static final int END_INDEX = 10;
	
	String FLAG_UUID = "123xx34-623hh34-22hj89-23hjy5";
	
	String FLAG_CODE = "FLAG MESSAGE";
	
	String FLAG_CATEGORY = "FLAG CATEGORY";
	
	String SUBJECT_DISPLAY = "Patient/ 123xx34-623hh34-22hj89-23hjy5 Patient Name 10000X";
	
	Flag fhirFlag;
	
	PatientFlag patientFlag;
	
	@Mock
	private PatientFlagTranslator patientFlagTranslator;
	
	@Mock
	private FhirGlobalPropertyService globalPropertyService;
	
	@Mock
	private FhirFlagDao fhirFlagDao;
	
	@Mock
	private SearchQueryInclude<Flag> searchQueryInclude;
	
	@Mock
	private SearchQuery<PatientFlag, Flag, FhirFlagDao, PatientFlagTranslator, SearchQueryInclude<Flag>> searchQuery;
	
	private FhirFlagServiceImpl fhirFlagService;
	
	/**
	 * Sets .
	 */
	@Before
	public void setup() {
		fhirFlagService = new FhirFlagServiceImpl();
		
		fhirFlagService.setDao(fhirFlagDao);
		fhirFlagService.setTranslator(patientFlagTranslator);
		fhirFlagService.setSearchQuery(searchQuery);
		fhirFlagService.setSearchQueryInclude(searchQueryInclude);
		
		fhirFlag = new Flag();
		fhirFlag.setId(FLAG_UUID);
		
		Reference reference = new Reference();
		reference.setDisplay(SUBJECT_DISPLAY);
		fhirFlag.setSubject(reference);
		
		CodeableConcept code = new CodeableConcept();
		code.setText(FLAG_CODE);
		fhirFlag.setCode(code);
		
		CodeableConcept category = new CodeableConcept();
		category.addCoding().setCode(FLAG_CATEGORY);
		fhirFlag.setCategory(Collections.singletonList(category));
		
		patientFlag = new PatientFlag();
		
		Tag tag = new Tag(FLAG_CATEGORY, null, null);
		Priority priority = new Priority("priority", "red", 1);
		org.openmrs.module.patientflags.Flag openmrsFlag = new org.openmrs.module.patientflags.Flag("1", "test", FLAG_CODE);
		openmrsFlag.setTags(Collections.singleton(tag));
		openmrsFlag.setPriority(priority);
		
		patientFlag.setFlag(openmrsFlag);
		patientFlag.setUuid(FLAG_UUID);
		patientFlag.setPatient(new Patient());
		patientFlag.setMessage(openmrsFlag.getMessage());
	}
	
	@Test
	public void getFlagByUuid_shouldReturnFhrFlagForUuid() {
		when(fhirFlagDao.get(FLAG_UUID)).thenReturn(patientFlag);
		when(patientFlagTranslator.toFhirResource(patientFlag)).thenReturn(fhirFlag);
		
		Flag result = fhirFlagService.get(FLAG_UUID);
		
		assertThat(result, notNullValue());
		assertThat(result.getId(), equalTo(FLAG_UUID));
	}
	
	@Test
    public void searchFlags_shouldReturnLocationsByParameters() {
        List<PatientFlag> patientFlags = new ArrayList<>();
        patientFlags.add(patientFlag);

        SearchParameterMap theParams = new SearchParameterMap();

        when(searchQuery.getQueryResults(any(), any(), any() , any())).thenReturn(new SearchQueryBundleProvider<>(
                theParams, fhirFlagDao, patientFlagTranslator, globalPropertyService, searchQueryInclude));

        when(searchQueryInclude.getIncludedResources(any(), any())).thenReturn(Collections.emptySet());
        when(patientFlagTranslator.toFhirResource(patientFlag)).thenReturn(fhirFlag);
        when(fhirFlagDao.getSearchResults(any())).thenReturn(patientFlags);

        IBundleProvider results = fhirFlagService.searchFlags(
                new FlagSearchParams( null, null, null, null, null, null, null, null, null));

        assertThat(results, notNullValue());

        List<Flag> resultList = get(results);
        assertThat(resultList, not(empty()));
        assertThat(resultList, hasItem(hasProperty("id", equalTo(FLAG_UUID))));
    }
	
	private List<Flag> get(IBundleProvider results) {
        return results.getResources(START_INDEX, END_INDEX).stream().filter(it -> it instanceof Flag)
                .map(it -> (Flag) it).collect(Collectors.toList());
    }
}
