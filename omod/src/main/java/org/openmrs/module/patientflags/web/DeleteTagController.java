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
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles deleting Patient Flag via the webapp. Called by http request
 * ".../module/patientflags/deleteTag.form?tagId=2"
 */
@Controller
public class DeleteTagController {
	
	@RequestMapping("/module/patientflags/deleteTag.form")
	ModelAndView processDelete(HttpServletRequest request) throws ServletRequestBindingException {
		
		Integer tagId = ServletRequestUtils.getIntParameter(request, "tagId");
		Context.getService(FlagService.class).purgeTag(tagId);
		
		return new ModelAndView("redirect:/module/patientflags/manageTags.list");
	}
}
