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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;

public class FlagPatientDashboardHeaderExt extends Extension {
	
	@Override
	public MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getOverrideContent(String test) {
		
		FlagService flagService = Context.getService(FlagService.class);
		
		Integer patientID = Integer.valueOf(getParameterMap().get("patientId"));
		
		Patient patient = Context.getPatientService().getPatient(patientID);
		
		List<Flag> results = new ArrayList<Flag>();
		
		results = flagService.generateFlagsForPatient(patient, Context.getAuthenticatedUser().getAllRoles(), flagService.getDisplayPoint("Patient Dashboard Header"));
		
		if (!results.isEmpty()) {
			String content = "<TABLE><TR><TD>";
			
			// comment out the header text for now
			//        + Context.getMessageSourceService().getMessage("patientflags.flagpatientdashboard.header") + " ";
			
			for (Flag flag : results) {
				content = content + "<SPAN " + flag.getPriority().getStyle() + ">" + flag.getLocalizedMessage() + "</SPAN>, ";
			}
			
			// cut off the trailing delimiter
			content = content.substring(0, content.length() - 2);
			content = content + "</TD></TR></TABLE>";
			return content;
		} else {
			return "";
		}
	}
}
