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
import java.util.Collection;

import org.openmrs.Condition;
import org.openmrs.Patient;
import org.openmrs.module.patientflags.task.PatientFlagTask;
import org.springframework.aop.AfterReturningAdvice;

/**
 * AOP advice that triggers flag re-evaluation whenever a Condition is saved, voided, or changed.
 * This ensures that patient flags remain up-to-date with the patient's clinical conditions.
 */
public class ConditionServiceAdvice implements AfterReturningAdvice {

    /**
     * @see org.springframework.aop.AfterReturningAdvice#afterReturning(Object, Method, Object[], Object)
     */
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {

        String methodName = method.getName();
        Patient patient = null;

        // We monitor any method related to Conditions to ensure flags are re-evaluated
        if (methodName.contains("Condition") && args != null && args.length > 0 && args[0] != null) {
            Object firstArg = args[0];

            // Handle single Condition object
            if (firstArg instanceof Condition) {
                patient = ((Condition) firstArg).getPatient();
            }
            // Handle Collection of Conditions (e.g., saveConditions method)
            else if (firstArg instanceof Collection) {
                Collection<?> conditions = (Collection<?>) firstArg;
                if (!conditions.isEmpty()) {
                    Object firstItem = conditions.iterator().next();
                    if (firstItem instanceof Condition) {
                        patient = ((Condition) firstItem).getPatient();
                    }
                }
            }
        }

        // If a valid patient is identified, delegate to the transaction tracker and trigger flag generation
        patient = FlagGenerationTransactionTracker.handlePatient(patient);
        if (patient != null) {
            new PatientFlagTask().generatePatientFlags(patient);
        }
    }
}