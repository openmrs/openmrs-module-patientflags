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
package org.openmrs.module.patientflags.web.validators;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator for the Tag class
 */
public class TagValidator {
	
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return Tag.class.isAssignableFrom(clazz);
	}
	
	public void validate(Object target, Errors errors) {
		Tag targetTag = (Tag) target;
		FlagService flagService = Context.getService(FlagService.class);
		
		// name and criteria cannot be empty
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "patientflags.errors.noTag");
		
		// make sure that the tag field isn't too large
		if(targetTag.getName().length() > 255)
			errors.rejectValue("name", "patientflags.errors.tagTooLong");
		
		// if this is a new tag, make sure that a tag with the same name doesn't already exist
		// TODO this doesn't stop you from changing the name of tag to the identical name of another tag
		if(targetTag.getId() == null){
			List<Tag> tags = flagService.getAllTags();
			if (tags != null){
				for(Tag tag:tags){
					if(tag.getName().equalsIgnoreCase(targetTag.getName())){
						errors.rejectValue("tag", "patientflags.errors.tagNameExists");
						break;
					}
				}
			}
		}
	}
}
