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

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.evaluator.FlagEvaluator;

/**
 * This class represents a row in the flag_flag table. It also provides a means to validate a flag,
 * and test a flag against a certain Patient
 */
public class Flag extends BaseOpenmrsMetadata {
	
	private Integer flagId;
	
	private String name;
	
	/* Criteria to determine whether to trigger a flag */
	private String criteria;
	
	/* The evaluator to use when evaluating the flag */
	/* (Name of the class, stored as a string) */
	private String evaluator;
	
	/* Message to display when a the flag is triggered */
	private String message;
	
	/* The flag's priority level */
	private Priority priority;
	
	/* Whether or not the flag is enabled */
	private Boolean enabled;
	
	/* Tags associated with the flag */
	private Set<Tag> tags;
	
	//private FlagPoint referenceLink; // TO DO... some sort of reference link?
	
	/**
	 * Generic constructor
	 */
	public Flag() {
		enabled = true;
	}
	
	public Flag(String name, String criteria, String message) {
		this.name = name;
		this.criteria = criteria;
		this.message = message;
		this.enabled = true;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Flag) {
			Flag flag = (Flag) obj;
			
			if (getFlagId() != null && flag.getFlagId() != null)
				return getFlagId().equals(flag.getFlagId());
		}
		
		// if flagId is null for either object, for equality the
		// two objects must be the same
		return this == obj;
	}
	
	public int hashCode() {
		if (getFlagId() == null)
			return super.hashCode();
		return getFlagId().hashCode();
	}
	
	public void setFlagId(Integer flagId) {
		this.flagId = flagId;
	}
	
	public Integer getFlagId() {
		return flagId;
	}
	
	public void setId(Integer id) {
		setFlagId(id);
	}
	
	public Integer getId() {
		return getFlagId();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	
	public String getCriteria() {
		return criteria;
	}
	
	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}
	
	public String getEvaluator() {
		return evaluator;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	public Priority getPriority() {
		return priority;
	}
	
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	public Set<Tag> getTags() {
		return tags;
	}
	
	public String getLocalizedMessage() {
		return Context.getMessageSourceService().getMessage(message);
	}
	
	/**
	 * Adds a tag to the set associated with the flag
	 * 
	 * @param tag the tag to add
	 */
	public void addTag(Tag tag) {
		tags.add(tag);
	}
	
	/**
	 * Removes a tag from the set associated with the flag
	 * 
	 * @param tag the tag to be removed set
	 */
	public void removeTag(Tag tag) {
		tags.remove(tag);
	}
	
	/**
	 * Instantiates the evaluator
	 * 
	 * @return FlagEvaluator the class used to evaluate the given flag
	 */
	public FlagEvaluator instantiateEvaluator() {
		if (evaluator != null) {
			try {
				return (FlagEvaluator) Context.loadClass(evaluator).newInstance();
			}
			catch (Exception e) {
				throw new APIException("Unable to instantiate FlagEvaluator " + evaluator, e);
			}
		} else {
			throw new APIException("FlagEvaluator is null");
		}
	}
	
	/**
	 * Evaluates a given Patient against the flag's criteria by calling the flag's evaluator
	 * 
	 * @param patient
	 * @return true/false
	 */
	
	public EvaluatedFlag eval(Patient patient) {
		if (evaluator != null && patient != null) {
			return instantiateEvaluator().eval(this, patient);
		} else {
			return null;
		}
	}
	
	public String evalMessage(Integer patientId) {
		if (evaluator != null && patientId != null) {
			return instantiateEvaluator().evalMessage(this, patientId);
		} else {
			return getLocalizedMessage();
		}
	}
	
	public String evalMessage(Patient patient, Object[] objects) {
		String literal = "\\$\\{\\d{1,2}\\}";
		String message = getMessage();
		
		if (!message.matches(".*(" + literal + ")+.*")) {
			return message;
		}
		
		try {
			if (objects != null && objects.length > 0) {// empty resultset means no data returned
			
				if (objects[0] instanceof Object[]) {
					String finalMessage = "";
					for (Object itemObject : objects) {
						Object[] items = (Object[]) itemObject;
						finalMessage += extractMessage(literal, message, items) + " ";
					}
					
					return finalMessage;
				} else {
					return extractMessage(literal, message, objects);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	private String extractMessage(String literal, String messageStr, Object[] objects) {
		Matcher m = Pattern.compile(literal).matcher(messageStr);
		while (!m.hitEnd() && m.find()) {// replace each instance until end
			String replaceString = m.group();
			//get index between the brackets ${indexNumber}
			int index = Integer.parseInt(replaceString.replace("${", "").replace("}", ""));
			if (index < objects.length) {// do nothing if index is invalid
				messageStr = messageStr.replace(replaceString, objects[index].toString());
			}
		}
		return messageStr;
	}
	
	public String evalMessageArray(Patient patient, Object[] objects) {
		String literal = "\\$\\{\\d{1,2}\\}";
		String message = getMessage();
		
		if (!message.matches(".*(" + literal + ")+.*")) {
			return message;
		}
		
		try {
			if (objects != null && objects.length > 0) {// empty resultset means no data returned
				// each item in array must be array of objects
				for (Object object : objects) {
					Object[] items = (Object[]) object;
					Matcher m = Pattern.compile(literal).matcher(message);
					while (!m.hitEnd() && m.find()) {// replace each instance until end
						String replaceString = m.group();
						//get index between the brackets ${indexNumber}
						int index = Integer.parseInt(replaceString.replace("${", "").replace("}", ""));
						if (index < items.length) {// do nothing if index is invalid
							message = message.replace(replaceString, items[index].toString());
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	/**
	 * Evaluates a given Cohort against the flag's criteria by calling the flag's evaluator
	 * 
	 * @param cohort
	 * @return cohort the subset of patients that evaluate true for the given flag
	 */
	public Cohort evalCohort(Cohort cohort) {
		if (evaluator != null) {
			return instantiateEvaluator().evalCohort(this, cohort);
		} else {
			return null;
		}
	}
	
	/**
	 * Determines whether the criteria associated with the flag is well-formed
	 * 
	 * @return true/false
	 */
	public FlagValidationResult validate() {
		if (evaluator != null) {
			return instantiateEvaluator().validate(this);
		} else {
			return null;
		}
	}
}
