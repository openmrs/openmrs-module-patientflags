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
package org.openmrs.patientflags.rest.wrapper;

import java.io.Serializable;


public class FlagBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String message;

	public FlagBean(final String name, final String message) {
		this.name = name;
		this.message = message;
	}

	public String getName() {
		return this.name;
	}

	public String getMessage() {
		return this.message;
	}
}
