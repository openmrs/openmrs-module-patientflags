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
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.patientflags.task.PatientFlagTask;

public class EncounterServiceAdvice implements MethodInterceptor {
    
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] args = invocation.getArguments();
		
		if (method.getName().equals("saveEncounter")) {
			FlagGenerationTransactionTracker.startTransaction();
			
			try {
				Object result = invocation.proceed();
				
				Set<Patient> patients = FlagGenerationTransactionTracker.endTransaction();
				PatientFlagTask flagTask = new PatientFlagTask();
				for (Patient patient : patients) {
					flagTask.generatePatientFlags(patient);
				}
				
				if (args.length > 0 && args[0] instanceof Encounter) {
					Encounter encounter = (Encounter) args[0];
					Patient encounterPatient = encounter.getPatient();
					if (encounterPatient != null && !patients.contains(encounterPatient)) {
						flagTask.generatePatientFlags(encounterPatient);
					}
				}
				
				return result;
			} finally {
				FlagGenerationTransactionTracker.clear();
			}
		} else {
			return invocation.proceed();
		}
	}
}
