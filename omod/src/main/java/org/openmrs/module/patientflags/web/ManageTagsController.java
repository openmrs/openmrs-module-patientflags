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
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles the management of Tags via the webapp. Called by http request
 * ".../module/patientflags/manageTags.list"
 */
@Controller
public class ManageTagsController {
	
	/**
	 * Displays a list of all Patient Tags
	 * 
	 * @return new ModelAndView
	 */
	@RequestMapping("/module/patientflags/manageTags.list")
	public ModelAndView showFlags() {
		ModelMap model = new ModelMap();
		List<Tag> tags = Context.getService(FlagService.class).getAllTags();
		if (tags != null)
			model.addAttribute("tags", tags);
		
		return new ModelAndView("/module/patientflags/manageTags", model);
	}
}
