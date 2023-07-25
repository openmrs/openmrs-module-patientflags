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
package org.openmrs.module.patientflags.web.portlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.web.controller.PortletController;

/**
 * Controller that injects a Patient Flags into a box in the Overview section of the Patient
 * Dashboard page
 */
//(unable to map this controller via annotation since we need to override Openmrs core portlet mapping
//@Controller
//@RequestMapping("**/flagPatientOverview.portlet")
public class FlagPatientOverviewPortletController extends PortletController {
	
	/**
	 * Populates the Model with the Flags triggered by the specified Patient
	 */
	//(unable to map this controller via annotation since we need to override Openmrs core portlet mapping
	//@RequestMapping(method = RequestMethod.GET)
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		Integer patientID = (Integer) model.get("patientId");
		Patient patient = Context.getPatientService().getPatient(patientID);
		
		List<PatientFlag> results = new ArrayList<PatientFlag>();
		FlagService flagService = Context.getService(FlagService.class);
		
		results = flagService.getPatientFlags(patient, Context.getAuthenticatedUser().getAllRoles(), "Patient Dashboard Overview");
		
		List<Map<String, Object>> fgl = new ArrayList<Map<String, Object>>();
		for (PatientFlag patientFlag : results) {
			Flag flag = patientFlag.getFlag();
			flag.setMessage(patientFlag.getMessage());
			
			Map<String, Object> mapFp = new HashMap<String, Object>();

			mapFp.put("flag", flag);
			mapFp.put("flagMessage", flag.evalMessage(patientID));
			
			fgl.add(mapFp);
		}
		
		model.put("flaglist", fgl);
	}
}
