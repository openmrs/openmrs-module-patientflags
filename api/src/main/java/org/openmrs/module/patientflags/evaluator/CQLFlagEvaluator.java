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

public class CQLFlagEvaluator implements FlagEvaluator {

	@Override
	public Boolean eval(Flag flag, Patient patient) {
		return false;
	}

	@Override
	public Cohort evalCohort(Flag flag, Cohort cohort) {
		return cohort;
	}

	@Override
	public FlagValidationResult validate(Flag flag) {
		return new FlagValidationResult(true);
	}

	@Override
	public String evalMessage(Flag flag, int patientId) {
		return flag.getMessage();
	}

}
