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

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.BoxExt;

/*
 * Class that inserts Patient Flags into the Overview section of the
 * Patient Dashboard
 */
public class FlagPatientOverviewExt extends BoxExt {
	
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getRequiredPrivilege() {
		return "View Patients";
	}
	
	@Override
	public String getContent() {
		return "<br>";
	}
	
	/**
	 * References the FlagPartientOverviewPortletController as well as flagPatientOverview.jsp
	 */
	@Override
	public String getPortletUrl() {
		return "flagPatientOverview";
	}
	
	@Override
	public String getTitle() {
		return "patientflags.patientOverview.title";
	}
	
}
