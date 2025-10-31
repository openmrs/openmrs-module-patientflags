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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.api.FlagService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;

@Controller
public class EvaluateAllFlagsController {
	
	private static final Logger log = LoggerFactory.getLogger(EvaluateAllFlagsController.class);
	
	private Future<?> evaluateFlagsAsync = null;
	
	@RequestMapping(method = RequestMethod.GET, value = "module/patientflags/evaluateAllFlags.htm")
	public String showPage() {
		return "/module/patientflags/evaluateAllFlags";
	}
	
	/**
	 * @should return true for success if the evaluation does not fail
	 * @should return false for success if a RuntimeException is thrown
	 * @return a marker indicating success
	 */
	@RequestMapping(method = RequestMethod.POST, value = "module/patientflags/revaluateAllFlags.htm")
	public @ResponseBody
	Map<String, Object> rebuilAllFlags() {
		boolean success = true;
		Map<String, Object> results = new HashMap<String, Object>();
		log.debug("evaluating all patient flags");
		if (!Context.getUserContext().isAuthenticated()) {
			success = false;
		} else {
			try {
				evaluateFlagsAsync = Context.getService(FlagService.class).evaluateAllFlags();
			}
			catch (RuntimeException e) {
				success = false;
			}
		}
		results.put("success", success);
		return results;
	}
	
	/**
	 * @should return return inProgress for status if a evaluateAllFlags is not completed
	 * @should return success for status if a evaluateAllFlags is completed successfully
	 * @should return error for status if a evaluateAllFlags is not completed normally
	 * @return hashMap of String, String holds a key named "status" indicating the status of
	 *         evaluating all patient flags
	 */
	@RequestMapping(method = RequestMethod.GET, value = "module/patientflags/evaluateAllFlagsStatus.htm")
	public @ResponseBody
	Map<String, String> getStatus() {
		if (evaluateFlagsAsync == null) {
			throw new APIException("There was a problem evaluating all patient flags");
		}
		
		Map<String, String> results = new HashMap<String, String>();
		if (evaluateFlagsAsync.isDone()) {
			results.put("status", evaluateFlagsAsync.isCancelled() ? "error" : "success");
			evaluateFlagsAsync = null;
		} else {
			results.put("status", "inProgress");
		}
		return results;
	}
}
