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

public class DisplayPoint extends BaseOpenmrsMetadata {
	
	private Integer displayPointId;
	
	/**
	 * Constructors
	 */
	
	public DisplayPoint() {
	}
	
	public DisplayPoint(String name) {
		setName(name);
	}
	
	/**
	 * Custom Equals and HashCode methods
	 */
	
	public boolean equals(Object obj) {
		if (obj instanceof DisplayPoint) {
			DisplayPoint point = (DisplayPoint) obj;
			
			if (getDisplayPointId() != null && point.getDisplayPointId() != null)
				return getDisplayPointId().equals(point.getDisplayPointId());
		}
		
		// if id is null for either object, for equality the
		// two objects must be the same
		return this == obj;
	}
	
	public int hashCode() {
		if (getDisplayPointId() == null)
			return super.hashCode();
		return getDisplayPointId().hashCode();
	}
	
	public void setDisplayPointId(Integer displayPointId) {
		this.displayPointId = displayPointId;
	}
	
	public Integer getDisplayPointId() {
		return displayPointId;
	}
	
	public void setId(Integer id) {
		setDisplayPointId(id);
	}
	
	public Integer getId() {
		return getDisplayPointId();
	}
}
