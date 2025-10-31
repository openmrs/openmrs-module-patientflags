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

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.web.validators.PriorityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles adding and editing Priority via the webapp. Called by http request
 * ".../module/patientflags/editPriority.form"
 */
@Controller
@RequestMapping("/module/patientflags/editPriority.form")
@SessionAttributes("priority")
public class EditPriorityController {
	
	/** Validator for this controller */
	private PriorityValidator validator;
	
	/**
	 * Generic Constructor
	 */
	public EditPriorityController() {
	}
	
	/**
	 * Constructor that registers validator
	 * 
	 * @param validator
	 */
	@Autowired
	public EditPriorityController(PriorityValidator validator) {
		this.validator = validator;
	}
	
	/**
	 * Displays the form to edit (or add) a Priority
	 * 
	 * @param request
	 * @param model
	 * @return new ModelAndView
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(HttpServletRequest request, ModelMap model) throws ServletRequestBindingException {
		Priority priority;
		Integer priorityId = ServletRequestUtils.getIntParameter(request, "priorityId");
		
		if (priorityId != null) {
			priority = Context.getService(FlagService.class).getPriority(priorityId);
		} else {
			priority = new Priority();
		}
		
		model.addAttribute("priority", priority);
		return new ModelAndView("/module/patientflags/editPriority", model);
	}
	
	/**
	 * Processes the form to edit (or add) a Priority
	 * 
	 * @param priority the priority to add/update
	 * @param result
	 * @param status
	 * @return new ModelAndView
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("priority") Priority priority, BindingResult result,
	        SessionStatus status) {
		
		// validate form entries
		validator.validate(priority, result);
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/patientflags/editPriority");
		}
		
		// add the new tag
		Context.getService(FlagService.class).savePriority(priority);
		
		// clears the command object from the session
		status.setComplete();
		
		// just display the edit page again
		return new ModelAndView("redirect:/module/patientflags/managePriorities.list");
	}
	
}
