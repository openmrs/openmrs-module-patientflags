/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.patientflags.providers;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.fhir2.providers.BaseFhirProvenanceResourceTest;
import org.openmrs.module.fhir2.providers.r4.MockIBundleProvider;
import org.openmrs.module.patienttflags.FhirFlagService;
import org.openmrs.module.patienttflags.providers.FlagFhirResourceProvider;
import org.openmrs.module.patienttflags.search.param.FlagSearchParams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FlagFhirResourceProviderTest extends BaseFhirProvenanceResourceTest<Flag>  {

    String FLAG_UUID = "123xx34-623hh34-22hj89-23hjy5";

    String PATIENT_UUID = "123xx34-623hh34-22hj89-23hjy5";

    String FLAG_CODE = "FLAG MESSAGE";

    String FLAG_CATEGORY = "FLAG CATEGORY";

    private static final String LAST_UPDATED_DATE = "2020-09-03";

    private static final String CREATED_DATE = "2020-09-03";

    String SUBJECT_DISPLAY = "Patient/ 123xx34-623hh34-22hj89-23hjy5 Patient Name 10000X";

    private static final int START_INDEX = 0;

    private static final int END_INDEX = 10;

    private static final int PREFERRED_PAGE_SIZE = 10;

    private static final int COUNT = 1;

    FlagFhirResourceProvider flagFhirResourceProvider;

    @Mock
    private FhirFlagService fhirFlagService;

    Flag flag;

    @Before
    public void setup() {
        flagFhirResourceProvider = new FlagFhirResourceProvider();
        flagFhirResourceProvider.setFlagService(fhirFlagService);

        flag = new Flag();
        flag.setId(FLAG_UUID);

        Reference reference = new Reference();
        reference.setDisplay(SUBJECT_DISPLAY);
        flag.setSubject(reference);

        CodeableConcept code = new CodeableConcept();
        code.addCoding().setCode(FLAG_CODE);
        flag.setCode(code);

        CodeableConcept category = new CodeableConcept();
        category.addCoding().setCode(FLAG_CATEGORY);
        flag.setCategory(Collections.singletonList(category));

    }

    @Test
    public void getResourceType_shouldReturnResourceType() {
        assertThat(flagFhirResourceProvider.getResourceType(), equalTo(Flag.class));
        assertThat(flagFhirResourceProvider.getResourceType().getName(), equalTo(Flag.class.getName()));
    }

    @Test
    public void getFlagByUuid_shouldReturnMatchingFlag() {
        IdType idType = new IdType();
        idType.setValue(FLAG_UUID);

        when(fhirFlagService.get(idType.getIdPart())).thenReturn(flag);

        Flag flag1 = flagFhirResourceProvider.getFlagById(idType);
        MatcherAssert.assertThat(flag1, notNullValue());
        MatcherAssert.assertThat(flag1.getId(), notNullValue());
        MatcherAssert.assertThat(flag1.getId(), Matchers.equalTo(FLAG_UUID));
    }

    @Test( expected = ResourceNotFoundException.class )
    public void getFlagByUuid_shouldThrowResourceNotFoundException() {
        IdType idType = new IdType();
        idType.setValue(FLAG_UUID);

        when(fhirFlagService.get(idType.getIdPart())).thenReturn(null);

        Flag flag1 = flagFhirResourceProvider.getFlagById(idType);
        assertThat(flag1, nullValue());
    }

    @Test
     public void searchFlags_shouldReturnMatchingFlagsForPatientUuid() {
         ReferenceAndListParam patientUuid = new ReferenceAndListParam();
         patientUuid.addValue(new ReferenceOrListParam()
                 .add(new ReferenceParam().setValue(PATIENT_UUID).setChain(Patient.SP_RES_ID)));

         when(fhirFlagService.searchFlags(new FlagSearchParams(patientUuid, null, null, null, null, null, null, null, null)))
                 .thenReturn(new MockIBundleProvider<>(Collections.singletonList(flag), PREFERRED_PAGE_SIZE, COUNT));

         IBundleProvider results = flagFhirResourceProvider.searchFlags(patientUuid, null,  null,  null,  null);

         MatcherAssert.assertThat(results, CoreMatchers.notNullValue());

         List<Flag> resultList = get(results);

         MatcherAssert.assertThat(results, CoreMatchers.notNullValue());
         MatcherAssert.assertThat(resultList.get(0).fhirType(), is("Flag"));
         MatcherAssert.assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
         MatcherAssert.assertThat(resultList.get(0).getSubject().getDisplay(), equalTo(SUBJECT_DISPLAY));
     }

    @Test
    public void searchFlags_shouldReturnMatchingFlagsForPatientName() {
        ReferenceAndListParam patientUuid = new ReferenceAndListParam();
        patientUuid.addValue(new ReferenceOrListParam()
                .add(new ReferenceParam().setValue("patient name").setChain(Patient.SP_NAME)));

        when(fhirFlagService.searchFlags(new FlagSearchParams(patientUuid, null, null, null, null, null, null, null, null)))
                .thenReturn(new MockIBundleProvider<>(Collections.singletonList(flag), PREFERRED_PAGE_SIZE, COUNT));

        IBundleProvider results = flagFhirResourceProvider.searchFlags(patientUuid, null, null, null, null);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());

        List<Flag> resultList = get(results);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(resultList.get(0).fhirType(), is("Flag"));
        MatcherAssert.assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
        MatcherAssert.assertThat(resultList.get(0).getSubject().getDisplay(), equalTo(SUBJECT_DISPLAY));
    }

    @Test
    public void searchFlags_shouldReturnMatchingFlagsForPatientNaneGiven() {
        ReferenceAndListParam patientUuid = new ReferenceAndListParam();
        patientUuid.addValue(new ReferenceOrListParam()
                .add(new ReferenceParam().setValue("patient").setChain(Patient.SP_GIVEN)));

        when(fhirFlagService.searchFlags(new FlagSearchParams(patientUuid, null, null, null, null, null, null, null, null)))
                .thenReturn(new MockIBundleProvider<>(Collections.singletonList(flag), PREFERRED_PAGE_SIZE, COUNT));

        IBundleProvider results = flagFhirResourceProvider.searchFlags(patientUuid, null,  null,  null,  null);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());

        List<Flag> resultList = get(results);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(resultList.get(0).fhirType(), is("Flag"));
        MatcherAssert.assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
        MatcherAssert.assertThat(resultList.get(0).getSubject().getDisplay(), equalTo(SUBJECT_DISPLAY));
    }

    @Test
    public void searchFlags_shouldReturnMatchingFlagsForPatientIdentifier() {
        ReferenceAndListParam patientUuid = new ReferenceAndListParam();
        patientUuid.addValue(new ReferenceOrListParam()
                .add(new ReferenceParam().setValue("1000X").setChain(Patient.SP_IDENTIFIER)));

        when(fhirFlagService.searchFlags(new FlagSearchParams(patientUuid, null, null, null, null, null, null, null, null)))
                .thenReturn(new MockIBundleProvider<>(Collections.singletonList(flag), PREFERRED_PAGE_SIZE, COUNT));

        IBundleProvider results = flagFhirResourceProvider.searchFlags(patientUuid, null,  null,  null,  null);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());

        List<Flag> resultList = get(results);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(resultList.get(0).fhirType(), is("Flag"));
        MatcherAssert.assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
        MatcherAssert.assertThat(resultList.get(0).getSubject().getDisplay(), equalTo(SUBJECT_DISPLAY));
    }

    @Test
    public void searchFlags_shouldReturnMatchingFlagsForFlagCode() {
        TokenAndListParam codeParam = new TokenAndListParam()
                .addAnd(new TokenOrListParam().add(new TokenParam(FLAG_CODE)));
        when(fhirFlagService.searchFlags(new FlagSearchParams(null, codeParam, null, null, null, null, null, null, null)))
                .thenReturn(new MockIBundleProvider<>(Collections.singletonList(flag), PREFERRED_PAGE_SIZE, COUNT));

        IBundleProvider results = flagFhirResourceProvider.searchFlags(null, null, codeParam, null, null);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());

        List<Flag> resultList = get(results);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(resultList.get(0).fhirType(), is("Flag"));
        MatcherAssert.assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
        MatcherAssert.assertThat(resultList.get(0).getCode().getCoding().get(0).getCode(), equalTo(FLAG_CODE));
    }

    @Test
    public void searchFlags_shouldReturnMatchingFlagsForFlagCategory() {
        TokenAndListParam category = new TokenAndListParam()
                .addAnd(new TokenOrListParam().add(new TokenParam(FLAG_CATEGORY)));
        when(fhirFlagService.searchFlags(new FlagSearchParams(null, null,category, null, null, null, null, null, null)))
                .thenReturn(new MockIBundleProvider<>(Collections.singletonList(flag), PREFERRED_PAGE_SIZE, COUNT));

        IBundleProvider results = flagFhirResourceProvider.searchFlags(null, null,  null, category, null);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());

        List<Flag> resultList = get(results);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(resultList.get(0).fhirType(), is("Flag"));
        MatcherAssert.assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
        MatcherAssert.assertThat(resultList.get(0).getCategory().get(0).getCoding().get(0).getCode(), CoreMatchers.notNullValue());
    }

    @Test
    public void searchFlags_shouldReturnMatchingFlagsForDateRange() {
        DateRangeParam date = new DateRangeParam().setUpperBound(CREATED_DATE).setLowerBound(LAST_UPDATED_DATE);
        when(fhirFlagService.searchFlags(new FlagSearchParams( null, null, null,date, null, null, null, null, null)))
                .thenReturn(new MockIBundleProvider<>(Collections.singletonList(flag), PREFERRED_PAGE_SIZE, COUNT));

        IBundleProvider results = flagFhirResourceProvider.searchFlags( null, null, null, null, date);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());

        List<Flag> resultList = get(results);

        MatcherAssert.assertThat(results, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(resultList.get(0).fhirType(), is("Flag"));
        MatcherAssert.assertThat(resultList, hasSize(greaterThanOrEqualTo(1)));
        MatcherAssert.assertThat(resultList.get(0).getId(), equalTo(FLAG_UUID));
    }


    private List<Flag> get(IBundleProvider results) {
        return results.getResources(START_INDEX, END_INDEX).stream().filter(it -> it instanceof Flag)
                .map(it -> (Flag) it).collect(Collectors.toList());
    }

}