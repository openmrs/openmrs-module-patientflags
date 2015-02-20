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

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.FlagValidationResult;

/**
 * This interface represents a Flag Evaluator. Each Flag must have a FlagEvaluator that defines how
 * to test the Flag's criteria against a Patient or Cohort
 */
public interface FlagEvaluator {
	
	/**
	 * Evaluates the given patient against the given flag
	 * 
	 * @param flag the flag to evaluate
	 * @param patient the patient to evaluate
	 * @return true/false
	 */
	public Boolean eval(Flag flag, Patient patient);
	
	/**
	 * Evaluates the given cohort against the given flag
	 * 
	 * @param flag the flag to evaluate
	 * @param cohort the cohort to evaluate; evaluators should be implemented so that passing a null
	 *            results in testing against all patients in the database
	 * @return the subset of patients who evaluate true for the given flag
	 */
	public Cohort evalCohort(Flag flag, Cohort cohort);
	
	/**
	 * Validates that the Flag's criteria is valid for this FlagEvaluator
	 * 
	 * @param flag the flag to test
	 * @return FlagValidationResult
	 */
	public FlagValidationResult validate(Flag flag);
	
	public String evalMessage(Flag flag, int patientId);
}
