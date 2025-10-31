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
import org.openmrs.module.patientflags.PatientFlagsProperties;
import org.springframework.validation.Errors;

/**
 * Validator for the PatientFlagsProperties class
 */
public class PatientFlagsPropertiesValidator {
	
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return PatientFlagsProperties.class.isAssignableFrom(clazz);
	}
	
	public void validate(Object target, Errors errors) {
		PatientFlagsProperties propertiesToValidate = (PatientFlagsProperties) target;
		
		// reject username if no user exists for that name
		if (Context.getUserService().getUserByUsername(propertiesToValidate.getUsername()) == null)
			errors.rejectValue("username", "patientflags.errors.invalidUsername");
	}
}
