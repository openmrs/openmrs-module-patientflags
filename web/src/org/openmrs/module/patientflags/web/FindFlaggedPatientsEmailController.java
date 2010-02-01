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
package org.openmrs.module.patientflags.web;

import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;


/**
 * Controller that handles retrieving Patients that match a Flag criteria for email output
 */
@Controller
@RequestMapping("/module/patientflags/findFlaggedPatientsEmail.form")
public class FindFlaggedPatientsEmailController {

	/**
	 * Generic constructor
	 */
	public FindFlaggedPatientsEmailController() {
	}
	
	/**
	 *  Handle the request to create an flag email
	 */
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView processRequest(@ModelAttribute("flag") Flag flag, BindingResult result, SessionStatus status) {
		
		if (result.hasErrors()) {
			throw new APIException("Invalid parameter passed to FindFlaggedPatientsEmailController");
		}
		
		// get all Patients that trigger the selected Flag
		FlagService flagService = Context.getService(FlagService.class);
		flag = flagService.getFlag(flag.getFlagId());
		Cohort flaggedPatients = flagService.getFlaggedPatients(flag);
		Cohort allPatients = Context.getPatientSetService().getAllPatients();
		
		// create the model map
		ModelMap model = new ModelMap();
		model.addAttribute("flag", flag);
		model.addAttribute("allPatients", allPatients);
		if(flaggedPatients != null){
			model.addAttribute("flaggedPatients", flaggedPatients.getMemberIds());
		}
		else{
			model.addAttribute("flaggedPatients", null);
		}
		
		// clears the command object from the session
		status.setComplete();
		
		// displays the query results
		return new ModelAndView("/module/patientflags/findFlaggedPatientsEmailResults", model);
	}
}
