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

import org.openmrs.Condition;
import org.openmrs.Patient;
import org.openmrs.module.patientflags.task.PatientFlagTask;
import org.springframework.aop.AfterReturningAdvice;

public class ConditionServiceAdvice implements AfterReturningAdvice {

	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		
		String methodName = method.getName();
		Patient patient = null;
		
		if ((methodName.equals("saveCondition") || methodName.equals("voidCondition")
				|| methodName.equals("unvoidCondition") || methodName.equals("purgeCondition"))) {
			if (args[0] != null) {
				Condition condition = (Condition) args[0];
				patient = condition.getPatient();
			}
		}
		patient = FlagGenerationTransactionTracker.handlePatient(patient);
		if (patient != null) {
			new PatientFlagTask().generatePatientFlags(patient);
		}
	}
}

