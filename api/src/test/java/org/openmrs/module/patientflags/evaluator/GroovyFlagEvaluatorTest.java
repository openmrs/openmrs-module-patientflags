package org.openmrs.module.patientflags.evaluator;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GroovyFlagEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";

    private static final String TEST_DATASET_FILE = XML_DATASET_PATH + "flagtest-dataset.xml";

    GroovyFlagEvaluator groovyFlagEvaluator;

    @Before
    public void setup() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet(TEST_DATASET_FILE);
        authenticate();

        groovyFlagEvaluator = new GroovyFlagEvaluator();
    }

    @Test
    public void eval_shouldEvaluateFLag() {
        Flag flag = Context.getService(FlagService.class).getFlag(8);
        Patient patient = Context.getService(PatientService.class).getPatient(2);
        Map<Object, Object> context = new HashMap<>();

        Boolean result = groovyFlagEvaluator.eval(flag, patient, context);
        assertTrue(result);
    }

    @Test(expected = APIException.class)
    public void eval_shouldThrowApiExceptionWhenPatientIsVoided() {
        Flag flag = Context.getService(FlagService.class).getFlag(8);
        Patient patient = Context.getService(PatientService.class).getPatient(1);
        Map<Object, Object> context = new HashMap<>();
        groovyFlagEvaluator.eval(flag, patient, context);
    }

    @Test
    public void evalCohort_shouldReturnCohortWithNullCohortParameter() {
        Flag flag = Context.getService(FlagService.class).getFlag(8);
        Map<Object, Object> context = new HashMap<>();

        Cohort resultCohort = groovyFlagEvaluator.evalCohort(flag, null, context);

        assertNotNull(resultCohort);
        assertFalse(resultCohort.isEmpty());
    }

    @Test
    public void evalCohort_shouldReturnCohortWithCohortParameter() {
        Flag flag = Context.getService(FlagService.class).getFlag(8);
        Map<Object, Object> context = new HashMap<>();

        Cohort cohort = new Cohort();
        cohort.addMember(1);

        Cohort  resultCohort = groovyFlagEvaluator.evalCohort(flag, cohort, context);

        assertNotNull(resultCohort);
        assertFalse(resultCohort.isEmpty());
    }

    @Test(expected = APIException.class)
    public void evalCohort_shouldThrowsException() {
        Flag flag = Context.getService(FlagService.class).getFlag(4);
        Map<Object, Object> context = new HashMap<>();

        groovyFlagEvaluator.evalCohort(flag, null, context);
    }

    @Test
    public void validate_shouldReturnFlagValidationResult() {
        Flag flag = Context.getService(FlagService.class).getFlag(8);

        FlagValidationResult result = groovyFlagEvaluator.validate(flag);
        assertTrue(result.getResult());
    }

    @Test
    public void validate_shouldReturnFalseResultWithLocalizedMessage() {
        Flag flag = Context.getService(FlagService.class).getFlag(4);

        FlagValidationResult result = groovyFlagEvaluator.validate(flag);
        assertFalse(result.getResult());
    }

    @Test
    public void evalMessage_ShouldReturnMessage() {
        Flag flag = Context.getService(FlagService.class).getFlag(8);
        Patient patient = Context.getService(PatientService.class).getPatient(2);

        String message = groovyFlagEvaluator.evalMessage(flag, patient.getPatientId());

        assertNotNull(message);
        assertEquals(flag.getMessage(), message);
    }

}
