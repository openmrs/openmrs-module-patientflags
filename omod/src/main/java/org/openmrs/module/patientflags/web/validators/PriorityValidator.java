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
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator for the Priority class
 */
public class PriorityValidator {

	@SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
		return Priority.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Priority priorityToValidate = (Priority) target;

		// name, style and rank cannot be empty
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "patientflags.errors.noPriorityName");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "style", "patientflags.errors.noStyleName");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rank", "patientflags.errors.noRank");

		// make sure that the name field isn't too large
		if (priorityToValidate.getName().length() > 255) {
			errors.rejectValue("name", "patientflags.errors.priorityNameTooLong");
		}

		// make sure that the style field isn't too large
		if (priorityToValidate.getStyle().length() > 255) {
			errors.rejectValue("style", "patientflags.errors.styleTooLong");
		}

		//make sure that the name is unique
		if (isPriorityNameDuplicated(priorityToValidate)) {
			errors.rejectValue("name", "patientflags.errors.noUniqueName");
		}
	}

	private boolean isPriorityNameDuplicated(Priority priorityToValidate) {
		return Context.getService(FlagService.class).isPriorityNameDuplicated(priorityToValidate);
	}
}
