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

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Role;

/**
 * This class represents a row in the flag_tag table. Tags are strings that can be associated with
 * Flags for categorization purposes.
 */
public class Tag extends BaseOpenmrsMetadata {
	
	private Integer tagId;
	
	private String name;
	
	/** Roles for which flags associated with this tag are visible */
	private Set<Role> roles;
	
	/** Display points where flags associated with this tag are displayed */
	private Set<DisplayPoint> displayPoints;
	
	/**
	 * Constructors
	 */
	public Tag() {
	}
	
	public Tag(String name) {
		this.name = name;
	}
	
	public Tag(String name, Set<Role> roles, Set<DisplayPoint> displayPoints) {
		this.name = name;
		this.roles = roles;
		this.displayPoints = displayPoints;
	}
	
	/**
	 * Custom Equals and HashCode methods
	 */
	
	public boolean equals(Object obj) {
		if (obj instanceof Tag) {
			Tag tag = (Tag) obj;
			
			if (getTagId() != null && tag.getTagId() != null)
				return getTagId().equals(tag.getTagId());
		}
		
		// if tagId is null for either object, for equality the
		// two objects must be the same
		return this == obj;
	}
	
	public int hashCode() {
		if (getTagId() == null)
			return super.hashCode();
		return getTagId().hashCode();
	}
	
	/*
	 * Getters and Setters
	 */
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
	
	public Integer getTagId() {
		return tagId;
	}
	
	public void setId(Integer id) {
		setTagId(id);
	}
	
	public Integer getId() {
		return getTagId();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}
	
	public void setDisplayPoints(Set<DisplayPoint> displayPoints) {
		this.displayPoints = displayPoints;
	}
	
	public Set<DisplayPoint> getDisplayPoints() {
		return displayPoints;
	}
}
