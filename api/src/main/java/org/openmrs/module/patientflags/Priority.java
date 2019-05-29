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

import org.openmrs.BaseOpenmrsMetadata;

/**
 * Class for Priority type
 */
public class Priority extends BaseOpenmrsMetadata{
	
	private Integer priorityId;
	
	private String name;
	
	private String style;
	
	private Integer rank;
	
	/**
	 * Constructors
	 */
	public Priority() {
	}
	
	public Priority(String name, String style, Integer rank) {
		this.name = name;
		this.style = style;
		this.rank = rank;
	}
	
	/** 
	 * Custom Equals and HashCode methods
	 */
	
	public boolean equals(Object obj) {
		if (obj instanceof Priority) {
			Priority priority = (Priority) obj;
			
			if (getPriorityId() != null && priority.getPriorityId() != null)
				return getPriorityId().equals(priority.getPriorityId());
		}
		
		// if flagId is null for either object, for equality the
		// two objects must be the same
		return this == obj;
	}
	
	public int hashCode() {
		if (getPriorityId() == null)
			return super.hashCode();
		return getPriorityId().hashCode();
	}
	
	/**
	 * Getters and Setters
	 */
	public void setId(Integer priorityId) {
		this.priorityId = priorityId;
	}
	
	public Integer getId() {
		return priorityId;
	}
	
	public void setPriorityId(Integer priorityId) {
		this.priorityId = priorityId;
	}
	
	public Integer getPriorityId() {
		return priorityId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStyle() {
		return style;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}
	
	public Integer getRank() {
		return rank;
	}
	
	public void setRank(Integer rank) {
		this.rank = rank;
	}
}
