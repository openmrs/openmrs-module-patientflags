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
package org.openmrs.module.patientflags;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class FlagTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";

	private static final String TEST_DATASET_FILE = XML_DATASET_PATH + "flagtest-dataset.xml";

	/**
	 * Tests of the Flag class
	 */
	
	/**
	 * setEvaluator method tests
	 */
	@BeforeEach
	public void initTestData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet(TEST_DATASET_FILE);
		authenticate();
	}
	
	@Test
	public void setEvaluator_shouldSetSQLFlagEvaluator() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assertions.assertEquals(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"), flag.getEvaluator());
	}
	
	@Test
	public void setEvaluator_shouldSetGroovyFlagEvaluator() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assertions.assertEquals(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"), flag.getEvaluator());
	}
	
	/**
	 * validate() method tests
	 */
	
	@Test
	public void validate_shouldAcceptValidSQLCriteria() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assertions.assertTrue(flag.validate().getResult());
	}
	
	@Test
	public void validate_shouldRejectInvalidSQLCriteria() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(2);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assertions.assertFalse(flag.validate().getResult());
	}
	
	@Test
	public void validate_shouldAcceptValidGroovyCriteria() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(3);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assertions.assertTrue(flag.validate().getResult());
	}
	
	@Test
	public void validate_shouldRejectInvalidGroovyCriteria() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(4);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assertions.assertFalse(flag.validate().getResult());
	}
	
	/**
	 * eval(Patient) method tests
	 */
	
	@Test
	public void eval_sqlShouldReturnNullIfNoPatient() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assertions.assertNull(flag.eval(null, null));
	}
	
	@Test
	public void eval_groovyShouldReturnNullIfNoPatient() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(3);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assertions.assertNull(flag.eval(null, null));
	}
	
	@Test
	public void eval_sqlShouldReturnFalseForTestPatient() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assertions.assertFalse(flag.eval(Context.getPatientService().getPatient(2), null));
	}
	
	@Test
	public void eval_groovyShouldReturnFalseForTestPatient() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(3);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assertions.assertFalse(flag.eval(Context.getPatientService().getPatient(2), null));
	}
	
	/**
	 * evalCohort(Cohort) method tests
	 */
	
	// TODO add tests that actually create non-empty result sets
	// TODO add tests to verify what evalCohort does if invalid evaluator
	
	@Test
	public void evalCohort_sqlShouldReturnEmptyCohort() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Cohort cohort = flag.evalCohort(new Cohort(), null);
		Assertions.assertTrue(cohort.isEmpty());
	}
	
	@Test
	public void evalCohort_sqlNullShouldReturnCohort() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Cohort cohort = flag.evalCohort(null, null);
		Assertions.assertTrue(cohort.isEmpty());
	}
}
