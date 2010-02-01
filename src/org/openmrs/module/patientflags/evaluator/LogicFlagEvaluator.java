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

import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.FlagValidationResult;

/**
 * A FlagEvaluator that takes a OpenMRS logic string as it's criteria
 */
public class LogicFlagEvaluator implements FlagEvaluator {
	
	/**
	 * @see org.openmrs.module.patientflags.evaluator.FlagEvaluator#eval(Flag, Patient)
	 */
	public Boolean eval(Flag flag, Patient patient) {
		LogicService logicService = Context.getLogicService();
		boolean result;
		
		// TODO redo this once Logic gets consolidated into one method
		try {
			// try the first logic parse method
			LogicCriteria logicCriteria = logicService.parseString(flag.getCriteria());
			result = logicService.eval(patient, logicCriteria).toBoolean();
		}
		catch (Exception e1) {
			try {
				// try the second logic parse method
				LogicCriteria logicCriteria = LogicCriteria.parse(flag.getCriteria());
				result = logicService.eval(patient, logicCriteria).toBoolean();
			}
			catch (Exception e2) {
				throw new APIException("Unable to evaluate Logic Flag " + flag.getName(), e2);
			}
		}
		return result;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.evaluator.FlagEvaluator#eval(Flag, Cohort)
	 */
	public Cohort evalCohort(Flag flag, Cohort cohort) {
		LogicService logicService = Context.getLogicService();
		
		Map<Integer, Result> resultMap;
		
		// if the Cohort is null, use a Cohort that contains the entire patient set
		if (cohort == null) {
			cohort = new Cohort(Context.getPatientService().getAllPatients());
		}
		
		// TODO redo this once Logic gets consolidated into one method
		try {
			LogicCriteria logicCriteria = logicService.parseString(flag.getCriteria());
			resultMap = logicService.eval(cohort, logicCriteria);
		}
		catch (Exception e1) {
			try {
				LogicCriteria logicCriteria = LogicCriteria.parse(flag.getCriteria());
				resultMap = logicService.eval(cohort, logicCriteria);
			}
			catch (Exception e2) {
				throw new APIException("Unable to evaluate Logic Flag " + flag.getName() + ", " + e2.getLocalizedMessage(),
				        e2);
			}
		}
		
		// turn the result map into a Cohort
		Cohort resultCohort = new Cohort();
		if (resultMap != null) {
			for (Integer patientId : resultMap.keySet()) {
				if (resultMap.get(patientId).toBoolean()) {
					resultCohort.addMember(patientId);
				}
			}
		}
		
		return resultCohort;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.evaluator.FlagEvaluator#validate(Flag)
	 */
	public FlagValidationResult validate(Flag flag) {
		// tests to see if it's a valid query
		// TODO find a better, more robust way of validating,
		// not sure if this will actually catch much
		
		LogicService logicService = Context.getLogicService();
		
		try {
			logicService.parseString(flag.getCriteria());
			return new FlagValidationResult(true);
		}
		catch (Exception e1) {
			try {
				LogicCriteria.parse(flag.getCriteria());
				return new FlagValidationResult(true);
			}
			catch (Exception e2) {
				return new FlagValidationResult(false, e2.getLocalizedMessage());
			}
		}
		
		// this throws an exception in some cases, and is an ugly hack anyway....
		/* // if we test patientIds 1 thru 100 without getting a match, don't bother to validate, return "true"
		return new FlagValidationResult(true);
		
		// test to see if the criteria is valid by testing on an arbitrary patient
		// try getting patients with patientIDs from 1 to 100 until we get a actual patient
		Patient patient;
		for (int i = 1; i < 100; i++) {
			patient = Context.getPatientService().getPatient(i);
			if (patient != null) {
				// TODO redo this once Logic gets consolidated into one method
				try {
					LogicCriteria logicCriteria = logicService.parseString(flag.getCriteria());
					logicService.eval(patient, logicCriteria).toBoolean();
					return new FlagValidationResult(true);
				}
				catch (Exception e1){
						try{
							LogicCriteria logicCriteria = LogicCriteria.parse(flag.getCriteria());
							logicService.eval(patient, logicCriteria).toBoolean();
							return new FlagValidationResult(true);
						}
						catch (Exception e2) {
							return new FlagValidationResult(false,e2.getLocalizedMessage());
						}
				}
			}
		}
		// if we test patientIds 1 thru 100 without getting a match, don't bother to validate, return "true"
		return new FlagValidationResult(true); */
	}
	
}
