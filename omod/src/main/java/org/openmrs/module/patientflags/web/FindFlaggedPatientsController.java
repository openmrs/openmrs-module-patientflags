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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.PatientFlagsConstants;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.filter.Filter;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles retrieving Patients that match a Flag criteria
 */
@Controller
@RequestMapping("/module/patientflags/findFlaggedPatients.form")
public class FindFlaggedPatientsController {
	
	/**
	 * Generic constructor
	 */
	public FindFlaggedPatientsController() {
	}
	
	@ModelAttribute("flags")
	public List<Flag> populateFlags() {
		return Context.getService(FlagService.class).getAllFlags();
	}
	
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
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(HttpServletRequest request, ModelMap model) throws ServletRequestBindingException {
		
		// add the command object for the flag form
		model.addAttribute("flag", new Flag());
		
		// add the command object for the flag tag form
		model.addAttribute("filter", new Filter());
		
		return new ModelAndView("/module/patientflags/findFlaggedPatients", model);
	}
	
	@RequestMapping(method = RequestMethod.GET, params = "flagId")
	public ModelAndView processSubmit(@ModelAttribute("flag") Flag flag, BindingResult result, SessionStatus status) {
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/patientflags/findFlaggedPatients");
		}
		
		HashMap<Object, Object> context = new HashMap<Object, Object>();
		
		// get all Patients that trigger the selected Flag
		FlagService flagService = Context.getService(FlagService.class);
		flag = flagService.getFlag(flag.getFlagId());
		Cohort flaggedPatients = flagService.getFlaggedPatients(flag, context);
		Cohort allPatients = new Cohort();
		List<Patient> patients = Context.getPatientService().getAllPatients();
		for (Patient i : patients) {
			allPatients.addMember(i.getPatientId());
		}
		
		// create the model map
		ModelMap model = new ModelMap();
		model.addAttribute("flag", flag);
		model.addAttribute("allPatients", allPatients);
		List<Map<String, Object>> fpl = new ArrayList<Map<String, Object>>();
		
		if(flaggedPatients != null){
			Set<Integer> idsFp = flaggedPatients.getMemberIds();
			for (Integer patientId : idsFp) {
				Map<String, Object> mapFp = new HashMap<String, Object>();

				mapFp.put("patientId", patientId);
				mapFp.put("flagMessage", flag.evalMessage(patientId));
				
				fpl.add(mapFp);
			}
		}

		model.addAttribute("flaggedPatients", fpl);

		model.addAttribute("patientLink", Context.getAdministrationService().getGlobalProperty("patientflags.defaultPatientLink", PatientFlagsConstants.DEFAULT_PATIENT_LINK));
		
		// clears the command object from the session
		status.setComplete();
		
		// displays the query results
		return new ModelAndView("/module/patientflags/findFlaggedPatientsResults", model);
	}
	
	@RequestMapping(method = RequestMethod.GET, params = "tags")
	public ModelAndView processSubmit(@ModelAttribute("filter") Filter filter, BindingResult result, SessionStatus status) {
		
		if (result.hasErrors()) {
			return new ModelAndView("/module/patientflags/findFlaggedPatients");
		}
		
		FlagService flagService = Context.getService(FlagService.class);
		
		// get the flags to test on
		List<Flag> flags = flagService.getFlagsByFilter(filter);
		
		// returns a map of flagged Patients and the respective flags
		Cohort flaggedPatients = flagService.getFlaggedPatients(flags, null);
		Cohort allPatients = new Cohort();
		List<Patient> patients = Context.getPatientService().getAllPatients();
		for (Patient i : patients) {
			allPatients.addMember(i.getPatientId());
		}
		
		// create the model map
		ModelMap model = new ModelMap();
		model.addAttribute("allPatients", allPatients);
		List<Map<String, Object>> fpl = new ArrayList<>();

		Set<Integer> idsFp = flaggedPatients.getMemberIds();
		for (Integer patientId : idsFp) {
			Map<String, Object> mapFp = new HashMap<>();

			mapFp.put("patientId", patientId);

			fpl.add(mapFp);
		}

		model.addAttribute("flaggedPatients", fpl);
		model.addAttribute("patientLink", Context.getAdministrationService().getGlobalProperty("patientflags.defaultPatientLink", PatientFlagsConstants.DEFAULT_PATIENT_LINK));

		// clears the command object from the session
		status.setComplete();
		
		// displays the query results
		return new ModelAndView("/module/patientflags/findFlaggedPatientsResults", model);
	}
}
