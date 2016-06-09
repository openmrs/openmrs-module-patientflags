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
package org.openmrs.module.webservices.rest.web.wrapper;

import java.io.Serializable;


public class PatientBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String uuid;

	private String name;

	public PatientBean(final String uuid, final String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getName() {
		return this.name;
	}
}
