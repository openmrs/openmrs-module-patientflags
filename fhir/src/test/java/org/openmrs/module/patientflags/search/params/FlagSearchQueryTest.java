/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patientflags.search.params;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.TestFhirSpringConfiguration;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patienttflags.dao.FhirFlagDao;
import org.openmrs.module.patienttflags.translators.PatientFlagTranslator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@ContextConfiguration(classes = TestFhirSpringConfiguration.class, inheritLocations = false)
public class FlagSearchQueryTest extends BaseModuleContextSensitiveTest {
	
	private static final String TEST_DATASET_FILE = "org/openmrs/module/patientflags/testdata/fhirPatientflagtest-dataset.xml";
	
	private static final int START_INDEX = 0;
	
	private static final int END_INDEX = 10;
	
	private static final String PATIENT_UUID = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
	
	private static final String WRONG_PATIENT_UUID = "7d89924e-e8df-6666-a956-95de80529735";
	
	private static final String PATIENT_NAME = "Azalaïs Aygul";
	
	private static final String BAD_PATIENT_NAME = "bad name";
	
	private static final String PATIENT_GIVEN_NAME = "Azalaïs";
	
	private static final String PATIENT_FAMILY_NAME = "Aygul";
	
	private static final String BAD_PATIENT_FAMILY_NAME = "Azalaïs";
	
	private static final String WRONG_GIVEN_NAME = "wrong";
	
	private static final String FLAG_CODE = "Test";
	
	private static final String WRONG_FLAG_CODE = "wrong";
	
	@Autowired
	private FhirFlagDao dao;
	
	@Autowired
	private PatientFlagTranslator translator;
	
	@Autowired
	private SearchQueryInclude<Flag> searchQueryInclude;
	
	@Autowired
	SearchQuery<PatientFlag, Flag, FhirFlagDao, PatientFlagTranslator, SearchQueryInclude<Flag>> searchQuery;
	
	private List<IBaseResource> get(IBundleProvider results) {
		return results.getResources(START_INDEX, END_INDEX);
	}
	
	private IBundleProvider search(SearchParameterMap theParams) {
		return searchQuery.getQueryResults(theParams, dao, translator, searchQueryInclude);
	}
	
	@Before
	public void setup() throws Exception {
		executeDataSet(TEST_DATASET_FILE);
	}
	
	@Test
	public void searchForFlag_shouldSearchForFlagsByPatientGivenName() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_GIVEN_NAME);
		patient.setChain(Patient.SP_GIVEN);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, not(empty()));
		assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
	}
	
	@Test
	public void searchForFlag_shouldSearchForFlagsByMultiplePatientGivenNameOr() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_GIVEN_NAME);
		patient.setChain(Patient.SP_GIVEN);
		
		ReferenceParam badPatient = new ReferenceParam();
		
		badPatient.setValue(WRONG_GIVEN_NAME);
		badPatient.setChain(Patient.SP_GIVEN);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient).add(badPatient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, not(empty()));
		assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
	}
	
	@Test
	public void searchForFlag_shouldReturnEmptyListForFlagsByMultiplePatientGivenNameAnd() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_GIVEN_NAME);
		patient.setChain(Patient.SP_GIVEN);
		
		ReferenceParam badPatient = new ReferenceParam();
		
		badPatient.setValue(WRONG_GIVEN_NAME);
		badPatient.setChain(Patient.SP_GIVEN);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient))
		        .addAnd(new ReferenceOrListParam().add(badPatient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, empty());
	}
	
	@Test
	public void searchForFlag_shouldSearchForFlagsByPatientFamilyName() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_FAMILY_NAME);
		patient.setChain(Patient.SP_FAMILY);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, not(empty()));
		assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
	}
	
	@Test
	public void searchForFlag_shouldSearchForFlagsByMultiplePatientFamilyNameOr() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_FAMILY_NAME);
		patient.setChain(Patient.SP_FAMILY);
		
		ReferenceParam badPatient = new ReferenceParam();
		
		badPatient.setValue(BAD_PATIENT_FAMILY_NAME);
		badPatient.setChain(Patient.SP_FAMILY);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient).add(badPatient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, not(empty()));
		assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
	}
	
	@Test
	public void searchForFlag_shouldReturnEmptyListForFlagsByMultiplePatientFamilyNameAnd() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(BAD_PATIENT_NAME);
		patient.setChain(Patient.SP_FAMILY);
		
		ReferenceParam badPatient = new ReferenceParam();
		
		badPatient.setValue(BAD_PATIENT_FAMILY_NAME);
		badPatient.setChain(Patient.SP_FAMILY);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient).add(patient)).addAnd(
		    new ReferenceOrListParam().add(badPatient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, empty());
	}
	
	@Test
	public void searchForFlag_shouldSearchForFlagsByPatientName() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_NAME);
		patient.setChain(Patient.SP_NAME);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, not(empty()));
		assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
	}
	
	@Test
	public void searchForFlag_shouldSearchForFlagsByMultiplePatientNameOr() {
		ReferenceAndListParam referenceParam = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_GIVEN_NAME);
		patient.setChain(Patient.SP_GIVEN);
		
		ReferenceParam badPatient = new ReferenceParam();
		
		badPatient.setValue(WRONG_GIVEN_NAME);
		badPatient.setChain(Patient.SP_GIVEN);
		
		referenceParam.addValue(new ReferenceOrListParam().add(patient).add(badPatient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    referenceParam);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, not(empty()));
		assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
	}
	
	@Test
	public void searchForFlag_shouldReturnEmptyListForFlagsByMultiplePatientNameAnd() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_NAME);
		patient.setChain(Practitioner.SP_NAME);
		
		ReferenceParam badPatient = new ReferenceParam();
		
		badPatient.setValue(BAD_PATIENT_NAME);
		badPatient.setChain(Practitioner.SP_NAME);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient))
		        .addAnd(new ReferenceOrListParam().add(badPatient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, empty());
	}
	
	@Test
	public void searchForFlags_shouldSearchForFlagsByPatientUuid() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_UUID);
		patient.setChain(Patient.SP_RES_ID);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, not(empty()));
		assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
	}
	
	@Test
	public void searchForFlags_shouldReturnEmptyListForFlagsByMultiplePatientUuidAnd() {
		ReferenceAndListParam patientReference = new ReferenceAndListParam();
		ReferenceParam patient = new ReferenceParam();
		
		patient.setValue(PATIENT_UUID);
		
		ReferenceParam badPatient = new ReferenceParam();
		
		patient.setValue(WRONG_PATIENT_UUID);
		
		patientReference.addValue(new ReferenceOrListParam().add(patient))
		        .addAnd(new ReferenceOrListParam().add(badPatient));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER,
		    patientReference);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, empty());
	}
	
	@Test
	public void searchForFlags_shouldSearchForFlagsByFlagCode() {
		TokenAndListParam flagCodes = new TokenAndListParam().addAnd(new TokenOrListParam().add(new TokenParam(FLAG_CODE)));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter("code", flagCodes);
		IBundleProvider results = search(theParams);
		
		List<IBaseResource> resultList = get(results);
		
		assertThat(results, notNullValue());
		assertThat(resultList, not(empty()));
		assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
	}
	
	@Test
	public void searchForFlags_shouldReturnEmptyCollectionWhenCalledWithUnknownByFlagCode() {
		TokenAndListParam wrongFlagCodes = new TokenAndListParam().addAnd(new TokenOrListParam().add(new TokenParam(
		        WRONG_FLAG_CODE)));
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.CODED_SEARCH_HANDLER,
		    wrongFlagCodes);
		IBundleProvider results = search(theParams);
		
		assertThat(results, notNullValue());
		assertThat(results.size(), equalTo(0));
	}
	
}
