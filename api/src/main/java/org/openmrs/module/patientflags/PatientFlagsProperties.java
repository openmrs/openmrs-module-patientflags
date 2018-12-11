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

/**
 * This class represents the configurable properties for the Patient Flags Module
 */
public class PatientFlagsProperties {
	
	/* true/false whether or not to display flags in the Patient Dashboard overview */
	private Boolean patientHeaderDisplay;
	
	/* true/false whether or not to display flags in the Patient Dashboard header */
	private Boolean patientOverviewDisplay;
	
	/* Username for the OpenMRS user that will evaluate Groovy flags */
	private String username;
	
	public PatientFlagsProperties() {
	}
	
	public Boolean getPatientHeaderDisplay() {
		return patientHeaderDisplay;
	}
	
	public void setPatientHeaderDisplay(Boolean patientHeaderDisplay) {
		this.patientHeaderDisplay = patientHeaderDisplay;
	}
	
	public Boolean getPatientOverviewDisplay() {
		return patientOverviewDisplay;
	}
	
	public void setPatientOverviewDisplay(Boolean patientOverviewDisplay) {
		this.patientOverviewDisplay = patientOverviewDisplay;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
}
