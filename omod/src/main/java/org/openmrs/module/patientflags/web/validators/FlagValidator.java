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
package org.openmrs.module.patientflags.web.validators;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.FlagValidationResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for the Flag class
 */
public class FlagValidator implements Validator {
	
	@SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
		return Flag.class.isAssignableFrom(clazz);
	}
	
	public void validate(Object target, Errors errors) {
		
		Flag flagToValidate = (Flag) target;
		
		// name cannot be empty
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "patientflags.errors.noName");
		
		// criteria is no longer required, because of custom cases
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "criteria", "patientflags.errors.noCriteria");
		
		// make sure that the string fields aren't too large
		if (flagToValidate.getName().length() > 255)
			errors.rejectValue("name", "patientflags.errors.nameTooLong");
		
		if (flagToValidate.getCriteria().length() > 5000)
			errors.rejectValue("criteria", "patientflags.errors.criteriaTooLong");
		
		if (flagToValidate.getMessage().length() > 255)
			errors.rejectValue("message", "patientflags.errors.messageTooLong");
		
		if (flagToValidate.getEvaluator() == null) {
			errors.rejectValue("evaluator", "patientflags.errors.noEvaluator");
		} else {
			// run the target Flag's validate method to see if the criteria is well-formed
			FlagValidationResult result = flagToValidate.validate();
			
			if (!result.getResult()) {
				String message = result.getLocalizedMessage();
				errors.reject(null, Context.getMessageSourceService().getMessage("patientflags.errors.invalidCriteria")
				        + (message != null ? ": " + message : ""));
			}
		}
	}
}
