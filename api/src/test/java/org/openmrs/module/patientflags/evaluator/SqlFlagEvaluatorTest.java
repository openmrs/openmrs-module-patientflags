/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.patientflags.evaluator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.FlagValidationResult;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.HashMap;
import java.util.Map;


public class SqlFlagEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";

    private static final String TEST_DATASET_FILE = XML_DATASET_PATH + "flagtest-dataset.xml";

    SQLFlagEvaluator sqlFlagEvaluator;
    @Before
    public void setup() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet(TEST_DATASET_FILE);
        authenticate();

        sqlFlagEvaluator = new SQLFlagEvaluator();
    }

    @Test
    public void eval_shouldEvaluateFLag() {
        Flag flag = Context.getService(FlagService.class).getFlag(5);
        Patient patient = Context.getService(PatientService.class).getPatient(2);
        Map<Object, Object> context = new HashMap<>();

        Boolean result = sqlFlagEvaluator.eval(flag, patient, context);
        assertTrue(result);
    }

    @Test(expected = APIException.class)
    public void eval_shouldThrowApiExceptionWhenPatientIsVoided() {
        Flag flag = Context.getService(FlagService.class).getFlag(5);
        Patient patient = Context.getService(PatientService.class).getPatient(1);
        Map<Object, Object> context = new HashMap<>();

        sqlFlagEvaluator.eval(flag, patient, context);
    }

    @Test
    public void evalCohort_shouldReturnCohortWithNullCohortParameter() {
        Flag flag = Context.getService(FlagService.class).getFlag(5);
        Map<Object, Object> context = new HashMap<>();

        Cohort  resultCohort = sqlFlagEvaluator.evalCohort(flag, null, context);

        assertNotNull(resultCohort);
        assertFalse(resultCohort.isEmpty());
    }

    @Test
    public void evalCohort_shouldReturnCohortWithCohortParameter() {
        Flag flag = Context.getService(FlagService.class).getFlag(5);
        Map<Object, Object> context = new HashMap<>();

        Cohort cohort = new Cohort();
        cohort.addMember(1);

        Cohort  resultCohort = sqlFlagEvaluator.evalCohort(flag, null, context);

        assertNotNull(resultCohort);
        assertFalse(resultCohort.isEmpty());
    }

    @Test(expected = APIException.class)
    public void evalCohort_shouldThrowsException() {
        Flag flag = Context.getService(FlagService.class).getFlag(6);
        Map<Object, Object> context = new HashMap<>();

        sqlFlagEvaluator.evalCohort(flag, null, context);
    }

    @Test
    public void validate_shouldReturnFlagValidationResult() {
        Flag flag = Context.getService(FlagService.class).getFlag(5);

        FlagValidationResult result = sqlFlagEvaluator.validate(flag);
        assertTrue(result.getResult());
    }

    @Test
    public void validate_shouldReturnFalseResultWithMessage() {
        Flag flag = Context.getService(FlagService.class).getFlag(2);

        FlagValidationResult result = sqlFlagEvaluator.validate(flag);
        assertFalse(result.getResult());
        assertEquals("patientflags.errors.noPatientIdCriteria", result.getMessage());
    }

    @Test
    public void validate_shouldReturnFalseResultWithLocalizedMessage() {
        Flag flag = Context.getService(FlagService.class).getFlag(7);

        FlagValidationResult result = sqlFlagEvaluator.validate(flag);
        assertFalse(result.getResult());
    }

    @Test
    public void evalMessage_ShouldReturnMessage() {
        Flag flag = Context.getService(FlagService.class).getFlag(5);
        Patient patient = Context.getService(PatientService.class).getPatient(2);

        String message = sqlFlagEvaluator.evalMessage(flag, patient.getPatientId());

        assertNotNull(message);
        assertEquals(flag.getMessage(), message);
    }

    @Test(expected = APIException.class)
    public void evalMessage_ShouldThrowsAPIExceptionForVoidedPatient() {
        Flag flag = Context.getService(FlagService.class).getFlag(1);
        Patient patient = Context.getService(PatientService.class).getPatient(1);

        sqlFlagEvaluator.evalMessage(flag, patient.getPatientId());
    }
}
