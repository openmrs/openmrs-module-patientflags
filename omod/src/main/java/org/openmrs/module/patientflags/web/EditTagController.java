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

import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.web.validators.TagValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles adding and editing Tags via the webapp. Called by http request
 * ".../module/patientflags/editTag.form"
 */
@Controller
@RequestMapping("/module/patientflags/editTag.form")
@SessionAttributes("tag")
public class EditTagController {
	
	/** Validator for this controller */
	private TagValidator validator;
	
	/**
	 * Generic Constructor
	 */
	public EditTagController() {
	}
	
	/**
	 * Constructor that registers validator
	 * 
	 * @param validator
	 */
	@Autowired
	public EditTagController(TagValidator validator) {
		this.validator = validator;
	}
	
	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		// map the role name to the actual role
		binder.registerCustomEditor(Set.class, "roles", new CustomCollectionEditor(
		                                                                           Set.class) {
			
			@Override
			public Object convertElement(Object element) {
				return Context.getUserService().getRole((String) element);
			}
		});
		
		// map the  id to the actual tag
		binder.registerCustomEditor(Set.class, "displayPoints", new CustomCollectionEditor(
		                                                                                   Set.class) {
			
			@Override
			public Object convertElement(Object element) {
				return Context.getService(FlagService.class).getDisplayPoint(Integer.valueOf((String) element));
			}
		});
	}
	
	@ModelAttribute("roles")
	public List<Role> populateRoles() {
		return Context.getUserService().getAllRoles();
	}
	
	@ModelAttribute("displayPoints")
	public List<DisplayPoint> populateDisplayPoints() {
		return Context.getService(FlagService.class).getAllDisplayPoints();
	}
	
	/**
	 * Displays the form to edit (or add) a Tag
	 * 
	 * @param request
	 * @param model
	 * @return new ModelAndView
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(HttpServletRequest request, ModelMap model) throws ServletRequestBindingException {
		Tag tag;
		Integer tagId = ServletRequestUtils.getIntParameter(request, "tagId");
		
		if (tagId != null) {
			tag = Context.getService(FlagService.class).getTag(tagId);
		} else {
			tag = new Tag();
		}
		
		model.addAttribute("tag", tag);
		return new ModelAndView("/module/patientflags/editTag", model);
	}
	
	/**
	 * Processes the form to edit (or add) a Flag
	 * 
	 * @param flag the flag to add/update
	 * @param result
	 * @param status
	 * @return new ModelAndView
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(@ModelAttribute("tag") Tag tag, BindingResult result, SessionStatus status) {
		
		// validate form entries
		validator.validate(tag, result);
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/patientflags/editTag");
		}
		
		// add the new tag
		Context.getService(FlagService.class).saveTag(tag);
		
		// clears the command object from the session
		status.setComplete();
		
		// just display the edit page again
		return new ModelAndView("redirect:/module/patientflags/manageTags.list");
	}
	
}
