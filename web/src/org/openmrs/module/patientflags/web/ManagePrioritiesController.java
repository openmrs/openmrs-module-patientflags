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

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles the management of Priorities via the webapp Called by http request
 * ".../module/patientflags/managePriorities.form"
 */
@Controller
public class ManagePrioritiesController {

	/**
	 * Displays a list of Priorities, and allows for editing of ranks
	 * 
	 * @return new ModelAndView
	 */

	@RequestMapping("/module/patientflags/managePriorities.list")
	public ModelAndView showPriorities() {
		ModelMap model = new ModelMap();
		List<Priority> priorities = Context.getService(FlagService.class).getAllPriorities();
		if (priorities != null)
			model.addAttribute("priorities", priorities);
		
		// add the command object for the filter form
		//model.addAttribute("filter", new Filter());
		
		return new ModelAndView("/module/patientflags/managePriorities", model);
	}
	
}