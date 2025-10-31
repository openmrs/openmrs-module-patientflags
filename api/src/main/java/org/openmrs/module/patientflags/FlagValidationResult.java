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
package org.openmrs.module.patientflags;

import org.openmrs.api.context.Context;

/**
 * This class represents the result of a flag validation
 */
public class FlagValidationResult {
	
	// whether or not the validation test passed
	private Boolean result;
	
	// error message associated with the test
	private String message;
	
	/**
	 * Constructors
	 */
	
	public FlagValidationResult() {
	}
	
	public FlagValidationResult(Boolean result) {
		this.result = result;
		this.message = null;
	}
	
	public FlagValidationResult(Boolean result, String message) {
		this.result = result;
		this.message = message;
	}
	
	/**
	 * Getters and Setters
	 */
	public void setResult(Boolean result) {
		this.result = result;
	}
	
	public Boolean getResult() {
		return result;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	/**
	 * Return the localized message by utilizing the MessageSource Service
	 */
	public String getLocalizedMessage() {
		return Context.getMessageSourceService().getMessage(message);
	}
}
