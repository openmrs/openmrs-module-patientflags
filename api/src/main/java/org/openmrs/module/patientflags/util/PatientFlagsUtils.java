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
package org.openmrs.module.patientflags.util;

import java.util.HashSet;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;

public class PatientFlagsUtils {
	
	/**
	 * Utility function that, given an list of tag names, creates a set of the tags references by
	 * those names. The tag names are case insensitive.
	 * 
	 * @param names tag names to associate with set members
	 * @return tag set
	 */
	public static Set<Tag> createTagSet(String[] names) {
		
		FlagService flagService = Context.getService(FlagService.class);
		Set<Tag> results = new HashSet<Tag>();
		
		for (String name : names) {
			Tag tag = flagService.getTag(name);
			if (tag != null) {
				results.add(tag);
			}
		}
		
		return results;
	}
}
