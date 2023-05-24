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
package org.openmrs.module.patientflags.extension.html;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.patientflags.PatientFlagsConstants;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/*
 * Class that adds Flag module menu items to the Admin page
 */
public class FlagAdminExt extends AdministrationSectionExt {
	
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getTitle() {
		return "patientflags.admin.title";
	}
	
	public String getRequiredPrivilege() {
		return "Manage Flags,Test Flags";
	}
	
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		User currentUser = Context.getAuthenticatedUser();
		
		if (currentUser.hasPrivilege(PatientFlagsConstants.PRIV_MANAGE_FLAGS)) {
			map.put("module/patientflags/manageFlags.form", "patientflags.manageFlags");
			map.put("module/patientflags/manageTags.list", "patientflags.manageTags");
			map.put("module/patientflags/managePriorities.list", "patientflags.managePriorities");
			map.put("module/patientflags/managePatientFlagsProperties.form", "patientflags.managePatientFlagsProperties");
			map.put("module/patientflags/evaluateAllFlags.htm", "patientflags.evaluateAllFlags");
		}
		if (currentUser.hasPrivilege(PatientFlagsConstants.PRIV_TEST_FLAGS)) {
			map.put("module/patientflags/findFlaggedPatients.form", "patientflags.findFlaggedPatients");
		}
		
		return map;
	}
	
}
