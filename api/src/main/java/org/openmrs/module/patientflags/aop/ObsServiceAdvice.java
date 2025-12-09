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
package org.openmrs.module.patientflags.aop;

import java.lang.reflect.Method;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.patientflags.task.PatientFlagTask;
import org.springframework.aop.AfterReturningAdvice;

public class ObsServiceAdvice implements AfterReturningAdvice {

	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		
		String methodName = method.getName();
		if (methodName.equals("voidObs") || methodName.equals("saveObs") || methodName.equals("unvoidObs") || methodName.equals("purgeObs")) {
			if (args[0] != null) {
				Obs obs = (Obs) args[0];
				Patient patient = getPatientFromObs(obs);
				patient = FlagGenerationTransactionTracker.handlePatient(patient);
				if (patient != null) {
					new PatientFlagTask().generatePatientFlags(patient);
				}
			}
		}
	}
	
	/**
	 * Extracts the Patient from an Obs object
	 * @param obs the observation
	 * @return the Patient, or null if not found
	 */
	private Patient getPatientFromObs(Obs obs) {
		if (obs == null) {
			return null;
		}
		
		// Try to get patient from encounter first
		if (obs.getEncounter() != null && obs.getEncounter().getPatient() != null) {
			return obs.getEncounter().getPatient();
		}
		
		// Fall back to getting patient from person
		if (obs.getPerson() != null && obs.getPerson() instanceof Patient) {
			return (Patient) obs.getPerson();
		}
		
		return null;
	}
}

