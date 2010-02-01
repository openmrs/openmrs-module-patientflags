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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

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
		
		// get the script to execute
		String criteria = flag.getCriteria();
		
		// get the set of bindings to use
		Binding bindings = getBindings();
		
		// bind the test Cohort
		bindings.setVariable("testCohort", cohort);
		
		// create a Groovy shell & import org.openmrs.*
		GroovyShell shell = new GroovyShell(bindings);
		
		try {
			Cohort resultCohort = (Cohort) shell.parse("import org.openmrs.*;" + criteria).run();
			
			// return the intersection of the originalCohort and the result,
			// just in case the Groovy script didn't operate on "testCohort"
			if(resultCohort != null){
				return Cohort.intersect(cohort, resultCohort);
			}
			else{
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
		// get the script to execute
		String criteria = flag.getCriteria();
		
		// get the set of bindings to use
		Binding bindings = getBindings();
		
		// bind to an empty Cohort for testing
		bindings.setVariable("testCohort", new Cohort());
		
		// create a Groovy shell with org.openmrs.* imported
		GroovyShell shell = new GroovyShell(bindings);
		
		try {
			@SuppressWarnings("unused")
			Cohort resultCohort = (Cohort) shell.parse("import org.openmrs.*;" + criteria).run();
			return new FlagValidationResult(true);
		}
		catch (Exception e) {
			return new FlagValidationResult(false, e.getLocalizedMessage());
		}
	}
	
	//TODO: add a better version of this which is driven by a config file.
	private static Binding getBindings() {
		final Binding binding = new Binding();
		binding.setVariable("admin", Context.getAdministrationService());
		binding.setVariable("cohort", Context.getCohortService());
		binding.setVariable("concept", Context.getConceptService());
		binding.setVariable("encounter", Context.getEncounterService());
		binding.setVariable("form", Context.getFormService());
		binding.setVariable("locale", Context.getLocale());
		binding.setVariable("logic", Context.getLogicService());
		binding.setVariable("obs", Context.getObsService());
		binding.setVariable("order", Context.getOrderService());
		binding.setVariable("patient", Context.getPatientService());
		binding.setVariable("patientSet", Context.getPatientSetService());
		binding.setVariable("person", Context.getPersonService());
		binding.setVariable("program", Context.getProgramWorkflowService());
		binding.setVariable("user", Context.getUserService());
		return binding;
	}
	
}
