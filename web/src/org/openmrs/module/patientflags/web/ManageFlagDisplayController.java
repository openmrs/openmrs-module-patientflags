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

import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.PatientFlagsProperties;
import org.openmrs.module.patientflags.api.FlagService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/module/patientflags/manageFlagDisplay.form")
public class ManageFlagDisplayController {
	
	/**
	 * Generic Constructor
	 */
	public ManageFlagDisplayController() {
	}
	
	/**
	 * Displays the form to manage the display of patient flags
	 * 
	 * @param model
	 * @return new ModelAndView
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(ModelMap model) {
		// initialize the command object
		PatientFlagsProperties properties = Context.getService(FlagService.class).getPatientFlagsProperties();
		model.addAttribute("properties", properties);
		
		return new ModelAndView("/module/patientflags/manageFlagDisplay");
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("properties") PatientFlagsProperties properties, BindingResult result,
	                                  SessionStatus status) {
		
		// save updated properties
		Context.getService(FlagService.class).savePatientFlagsProperties(properties);
		
		// clears the command object from the session
		status.setComplete();
		return new ModelAndView("/module/patientflags/manageFlagDisplaySuccess");
	}
}

