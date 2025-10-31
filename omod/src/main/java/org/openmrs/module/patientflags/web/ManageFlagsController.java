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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.filter.Filter;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles the management of Patient Flags via the webapp Called by http request
 * ".../module/patientflags/manageFlags.form"
 */
@Controller
@RequestMapping("/module/patientflags/manageFlags.form")
public class ManageFlagsController {
	
	//private Log log = LogFactory.getLog(this.getClass());
	
	@ModelAttribute("tags")
	public List<Tag> populateTags() {
		return Context.getService(FlagService.class).getAllTags();
	}
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Set.class, "tags", new CustomCollectionEditor(
		                                                                          Set.class) {
			
			@Override
			public Object convertElement(Object element) {
				return Context.getService(FlagService.class).getTag(Integer.valueOf((String) element));
			}
		});
	}
	
	/**
	 * Displays a list of all Patient Flags
	 * 
	 * @return new ModelAndView
	 */
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showFlags() {
		ModelMap model = new ModelMap();
		List<Flag> flags = Context.getService(FlagService.class).getAllFlags();
		if (flags != null)
			model.addAttribute("flags", flags);
		
		// add the command object for the filter form
		model.addAttribute("filter", new Filter());
		
		return new ModelAndView("/module/patientflags/manageFlags", model);
	}
	
	/**
	 * Handles a request to filter the results
	 * 
	 * @param filterTags
	 * @param result
	 * @param status
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processFilter(@ModelAttribute("filter") Filter filter, BindingResult result, SessionStatus status) {
		ModelMap model = new ModelMap();
		List<Flag> flags = Context.getService(FlagService.class).getFlagsByFilter(filter);
		if (flags != null)
			model.addAttribute("flags", flags);
		
		// add the command object for the filter form
		model.addAttribute("filter", new Filter());
		
		return new ModelAndView("/module/patientflags/manageFlags", model);
		
	}
}
