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

import java.util.Map;
import java.util.Set;

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
	
	public Flag(Integer flagId) {
		this.flagId = flagId;
	}
	
	/**
	 * Constructor with name criteria, and message
	 * 
	 * @param name
	 * @param criteria
	 * @param message
	 */
	
	public Flag(String name, String criteria, String message) {
		this.name = name;
		this.criteria = criteria;
		this.message = message;
		this.enabled = true;
	}
	
	/**
	 * Custom Equals and HashCode methods
	 */
	
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
	
	/**
	 * Getters and Setters
	 */
	
	/**
	 * @param flagId
	 */
	public void setFlagId(Integer flagId) {
		this.flagId = flagId;
	}
	
	/**
	 * @return flagId
	 */
	public Integer getFlagId() {
		return flagId;
	}
	
	public void setId(Integer id) {
		setFlagId(id);
	}
	
	public Integer getId() {
		return getFlagId();
	}
	
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param criteria
	 */
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	
	/**
	 * @return criteria
	 */
	public String getCriteria() {
		return criteria;
	}
	
	/**
	 * @param evaluator
	 * @throws APIException
	 */
	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}
	
	/**
	 * @return evaluatorType
	 */
	public String getEvaluator() {
		return evaluator;
	}
	
	/**
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @param priority
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	/**
	 * @return priority
	 */
	public Priority getPriority() {
		return priority;
	}
	
	/**
	 * @param enabled
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * @return enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}
	
	/**
	 * @param tags
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	/**
	 * @return tags
	 */
	public Set<Tag> getTags() {
		return tags;
	}
	
	/**
	 * Public Methods
	 */
	
	/**
	 * Return the localized message by utilizing the MessageSource Service
	 */
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
	 */
	public FlagEvaluator instantiateEvaluator() {
		if (evaluator != null) {
			try {
				return (FlagEvaluator) Context.loadClass(evaluator).newInstance();
			}
			catch (Exception e) {
				throw new APIException("Unable to instantiate FlagEvaluator " + evaluator, e);
			}
		}
		else {
			throw new APIException("FlagEvaluator is null");
		}
	}
	
	/**
	 * Evaluates a given Patient against the flag's criteria by calling the flag's evaluator
	 * 
	 * @param patient
	 * @param context
	 * @return true/false
	 */
	
	public Boolean eval(Patient patient, Map<Object, Object> context) {
		if (evaluator != null && patient != null) {
			return instantiateEvaluator().eval(this, patient, context);
		} else {
			return null;
		}
	}
	
	public String evalMessage(Integer patientId){
		if (evaluator != null && patientId != null) {
			return instantiateEvaluator().evalMessage(this, patientId);
		} else {
			return getLocalizedMessage();
		}
	}
	
	/**
	 * Evaluates a given Cohort against the flag's criteria by calling the flag's evaluator
	 * 
	 * @param cohort
	 * @param context
	 * @return cohort the subset of patients that evaluate true for the given flag
	 */
	public Cohort evalCohort(Cohort cohort, Map<Object, Object> context) {
		if (evaluator != null) {
			return instantiateEvaluator().evalCohort(this, cohort, context);
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
