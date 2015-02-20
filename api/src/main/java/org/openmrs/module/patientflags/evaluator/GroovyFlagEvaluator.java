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
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.FlagValidationResult;

/**
 * A FlagEvaluator that takes a Groovy Script as it's criteria
 */
public class GroovyFlagEvaluator implements FlagEvaluator {
	
	/**
	 * @see org.openmrs.module.patientflags.evaluator.FlagEvaluator#eval(Flag, Patient)
	 */
	public Boolean eval(Flag flag, Patient patient) {
		
		if(patient.isVoided())
			throw new APIException("Unable to evaluate Groovy flag " + flag.getName() + " against voided patient");
		
		// create a Cohort that contains just the patient to test, and then evaluate that Cohort
		Cohort cohort = new Cohort();
		cohort.addMember(patient.getId());
		
		Cohort resultCohort = evalCohort(flag, cohort);
		return !resultCohort.isEmpty();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.evaluator.FlagEvaluator#eval(Flag, Cohort)
	 */
	public Cohort evalCohort(Flag flag, Cohort cohort) {
		
		// if the Cohort is null, use a Cohort that contains the entire patient set
		if (cohort == null) {
			cohort = new Cohort(Context.getPatientService().getAllPatients());
		}
		
		try {
			// create a thread to evaluate the groovy script
			// (note that we pass 'null' as the user parameter because we don't want to restrict flag execution based on user)
			GroovyFlagEvaluatorThread evaluatorThread = new GroovyFlagEvaluatorThread(flag, cohort, null);
			
			// HACK: could call run method directly instead of using thread to increase performance
			// but we'd lose the security benefits running in a thread provides
			// if we do this, then we must also comment out the wait() in fetchResultCohort() in the GroovyFlagEvaluator Thread
			//evaluatorThread.run();
			
			// TODO: do I need to maintain a handle to this thread?
			new Thread(evaluatorThread).start();
			
			// fetch the result from the thread
			Cohort resultCohort = evaluatorThread.fetchResultCohort();
			
			// return the intersection of the originalCohort and the result,
			// just in case the Groovy script didn't operate on "testCohort"
			if (resultCohort != null) {
				return Cohort.intersect(cohort, resultCohort);
			} else {
				// return an empty Cohort if the result set is null
				return new Cohort();
			}
		}
		catch (Exception e) {
			throw new APIException("Unable to evaluate Groovy Flag " + flag.getName() + ", " + e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * @see org.openmrs.module.patientflags.evaluator.FlagEvaluator#validate(Flag)
	 */
	public FlagValidationResult validate(Flag flag) {
		try {
			// create a thread to test evaluating the groovy script against an empty cohort
			// (note that in this case we pass the current user to the thread, because we want to restrict flag validation based on the user-
			//  a user can evaluate a flag even if she doesn't have all the required privileges, but shouldn't be able to create/update a flag
			//  the does something she doesn't have privileges to do herself)
			GroovyFlagEvaluatorThread evaluatorThread = new GroovyFlagEvaluatorThread(flag, new Cohort(), Context.getAuthenticatedUser());
			new Thread(evaluatorThread).start();
			
			// attempt to fetch result
			evaluatorThread.fetchResultCohort();
			
			// if the fetch was successful, flag should be successfully validated
			return new FlagValidationResult(true);
		}
		catch (Exception e) {
			// if the exception was caused by a problem in the groovy script, handle 
			// the error by failing the validation and passing back the error message
			if (e instanceof groovy.lang.GroovyRuntimeException) {
				return new FlagValidationResult(false, e.getLocalizedMessage());
			}
			// if it is some other kind of exception, we have larger issues, so throw an API exception
			else {
				throw new APIException("Unable to evaluate Groovy Flag " + flag.getName() + ", " + e.getLocalizedMessage(),
				        e);
			}
		}
	}

	@Override
	public String evalMessage(Flag flag, int patientId) {
		return flag.getMessage();
	}
}
