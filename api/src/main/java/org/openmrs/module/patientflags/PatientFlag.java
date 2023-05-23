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

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Patient;

public class PatientFlag extends BaseOpenmrsObject{

	private Integer patientFlagId;
	
	private Patient patient;
	
	private Flag flag;
	
	
	public Integer getPatientFlagId() {
		return patientFlagId;
	}

	public void setPatientFlagId(Integer patientFlagId) {
		this.patientFlagId = patientFlagId;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Flag getFlag() {
		return flag;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	@Override
	public Integer getId() {
		return getPatientFlagId();
	}

	@Override
	public void setId(Integer id) {
		setPatientFlagId(id);
	}
}
