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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.FlagValidationResult;

public class CQLFlagEvaluator implements FlagEvaluator {

	@Override
	public Boolean eval(Flag flag, Patient patient) {
		if(patient.isVoided())
			throw new APIException("Unable to evaluate CQL flag " + flag.getName() + " against voided patient");
		
		// create a Cohort that contains just the patient, and then evaluate that Cohort
		Cohort cohort = new Cohort();
		cohort.addMember(patient.getId());
		
		Cohort resultCohort = evalCohort(flag, cohort);
		return !resultCohort.isEmpty();
	}

	@Override
	public Cohort evalCohort(Flag flag, Cohort cohort) {
		return cohort;
	}

	@Override
	public FlagValidationResult validate(Flag flag) {
		if (StringUtils.isBlank(flag.getCriteria())) {
			return new FlagValidationResult(false, "Criteria should be the ID of an existing plan definition. e.g ANCDT17");
		}
		
		//return new FlagValidationResult(false, flag.getCriteria() + " should be the ID of an existing plan definition. e.g ANCDT17");
		return new FlagValidationResult(true);
	}

	@Override
	public String evalMessage(Flag flag, int patientId) {
		return flag.getMessage();
	}

}
