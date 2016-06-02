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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;



public class FlagTest extends BaseModuleWebContextSensitiveTest {

	public static final String TEST_DATASETS_PROPERTIES_FILE = "test-datasets.properties";

	protected static final String XML_DATASET_PATH = "org/openmrs/module/patientflags/include/";

	protected static final String XML_DATASET = "flagTestDataSet";

	/**
	 * Tests of the Flag class
	 */

	/**
	 * setEvaluator method tests
	 */
	@Before
	public void initTestData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet(XML_DATASET_PATH + getTestDatasetFilename(XML_DATASET));
		authenticate();
	}

	@Test
	public void setEvaluator_shouldSetLogicFlagEvaluator() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("logic"));
		Assert.assertEquals(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("logic"), flag.getEvaluator());
	}

	@Test
	public void setEvaluator_shouldSetSQLFlagEvaluator() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assert.assertEquals(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"), flag.getEvaluator());
	}

	@Test
	public void setEvaluator_shouldSetGroovyFlagEvaluator() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(1);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assert.assertEquals(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"), flag.getEvaluator());
	}

	/**
	 * validate() method tests
	 */

	@Test
	public void validate_shouldAcceptValidLogicCriteria() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(2);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("logic"));
		Assert.assertTrue(flag.validate().getResult());
	}

	/* This won't be working until we get a better validator in place for logic evaluator */
	/**
	 * @Test public void validate_shouldRejectInvalidLogicCriteria() throws Exception{ Flag flag =
	 *       Context.getService(FlagService.class).getFlag(3);
	 *       flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("logic"));
	 *       Assert.assertFalse(flag.validate().getResult()); }
	 **/

	@Test
	public void validate_shouldAcceptValidSQLCriteria() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(4);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assert.assertTrue(flag.validate().getResult());
	}

	@Test
	public void validate_shouldRejectInvalidSQLCriteria() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(5);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assert.assertFalse(flag.validate().getResult());
	}

	@Test
	public void validate_shouldAcceptValidGroovyCriteria() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(6);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assert.assertTrue(flag.validate().getResult());
	}

	@Test
	public void validate_shouldRejectInvalidGroovyCriteria() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(7);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assert.assertFalse(flag.validate().getResult());
	}

	/**
	 * eval(Patient) method tests
	 */

	@Test
	public void eval_logicShouldReturnNullIfNoPatient() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(2);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("logic"));
		Assert.assertNull(flag.eval(null));
	}

	@Test
	public void eval_sqlShouldReturnNullIfNoPatient() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(4);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assert.assertNull(flag.eval(null));
	}

	@Test
	public void eval_groovyShouldReturnNullIfNoPatient() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(6);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assert.assertNull(flag.eval(null));
	}


	// TODO: get tests that require logic module to be running to work
	// @Test
	// public void eval_logicShouldReturnFalseForTestPatient() throws Exception {
		//Flag flag = Context.getService(FlagService.class).getFlag(2);
		//flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("logic"));
		//Assert.assertFalse(flag.eval(Context.getPatientService().getPatient(2)));
	//}

	@Test
	public void eval_sqlShouldReturnFalseForTestPatient() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(4);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Assert.assertFalse(flag.eval(Context.getPatientService().getPatient(2)));
	}

	@Test
	public void eval_groovyShouldReturnFalseForTestPatient() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(6);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("groovy"));
		Assert.assertFalse(flag.eval(Context.getPatientService().getPatient(2)));
	}

	/**
	 * evalCohort(Cohort) method tests
	 */

	// TODO add tests that actually create non-empty result sets
	// TODO add tests to verify what evalCohort does if invalid evaluator

	@Test
	public void evalCohort_logicShouldReturnEmptyCohort() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(2);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("logic"));
		Cohort cohort = flag.evalCohort(new Cohort());
		Assert.assertTrue(cohort.isEmpty());
	}

	@Test
	public void evalCohort_sqlShouldReturnEmptyCohort() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(4);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Cohort cohort = flag.evalCohort(new Cohort());
		Assert.assertTrue(cohort.isEmpty());
	}

	// TODO: get tests that require logic module to be running to work
	//@Test
	//public void evalCohort_logicNullShouldReturnEmptyCohort() throws Exception {
		//Flag flag = Context.getService(FlagService.class).getFlag(2);
		//flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("logic"));
		//Cohort cohort = flag.evalCohort(null);
		//Assert.assertTrue(cohort.isEmpty());
	//}

	@Test
	public void evalCohort_sqlNullShouldReturnCohort() throws Exception {
		Flag flag = Context.getService(FlagService.class).getFlag(4);
		flag.setEvaluator(PatientFlagsConstants.FLAG_EVALUATOR_MAP.get("sql"));
		Cohort cohort = flag.evalCohort(null);
		Assert.assertTrue(cohort.isEmpty());
	}



	/**
	 * Mimics org.openmrs.web.Listener.getRuntimeProperties()
	 *
	 * @param webappName name to use when looking up the runtime properties env var or filename
	 * @return Properties runtime
	 * @throws Exception
	 */
    @SuppressWarnings("deprecation")
    public String getTestDatasetFilename(String testDatasetName) throws Exception {

		InputStream propertiesFileStream = null;

		// try to load the file if its a straight up path to the file or
		// if its a classpath path to the file
		if (new File(TEST_DATASETS_PROPERTIES_FILE).exists()) {
			propertiesFileStream = new FileInputStream(TEST_DATASETS_PROPERTIES_FILE);
		} else {
			propertiesFileStream = getClass().getClassLoader().getResourceAsStream(TEST_DATASETS_PROPERTIES_FILE);
			if (propertiesFileStream == null)
				throw new FileNotFoundException("Unable to find '" + TEST_DATASETS_PROPERTIES_FILE + "' in the classpath");
		}

		Properties props = new Properties();

		OpenmrsUtil.loadProperties(props, propertiesFileStream);

		if (props.getProperty(testDatasetName) == null) {
			throw new Exception ("Test dataset named " + testDatasetName + " not found in properties file");
		}

		return props.getProperty(testDatasetName);
	}
}
