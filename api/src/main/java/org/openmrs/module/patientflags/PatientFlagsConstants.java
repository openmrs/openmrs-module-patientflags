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
package org.openmrs.module.patientflags;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants used by the Flag Module
 */
public class PatientFlagsConstants {
	
	/*  Flag Module Privileges */
	public static final String PRIV_MANAGE_FLAGS = "Manage Flags";
	
	public static final String PRIV_TEST_FLAGS = "Test Flags";
	
	/* FlagEvaluator mapping */
	public static final Map<String, String> FLAG_EVALUATOR_MAP = createFlagEvaluatorMap();
	
	private static Map<String, String> createFlagEvaluatorMap() {
		Map<String, String> newMap = new HashMap<String, String>();
		newMap.put("sql", "org.openmrs.module.patientflags.evaluator.SQLFlagEvaluator");
		newMap.put("groovy", "org.openmrs.module.patientflags.evaluator.GroovyFlagEvaluator");
		return Collections.unmodifiableMap(newMap);
	}
	
	/* Other Constants */
	public static final String DEFAULT_PATIENT_LINK = "/patientDashboard.form?";
}
