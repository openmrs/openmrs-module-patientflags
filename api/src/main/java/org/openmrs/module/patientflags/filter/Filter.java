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

package org.openmrs.module.patientflags.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.Tag;

/**
 * Provides the means to filter a list of Flags by a set of Tags
 */
public class Filter {
	
	/* The type of Filter */
	private FilterType type; // current supported types: ANYTAG,ALLTAGS,ANYTAG_OR_NOTAG
	
	/* Set of Tags to filter by */
	private Set<Tag> tags;
	
	/**
	 * Constructors
	 */
	public Filter() {
		type = FilterType.ANYTAG; // default the Filter	to match all Flags that contain one or more of the tags in the filter
	}
	
	public Filter(Set<Tag> tags){
		type = FilterType.ANYTAG; // default the Filter	to match all Flags that contain one or more of the tags in the filter
		this.tags = tags;
	}
	
	/**
	 * Getters and Setters
	 */
	
	public void setType(FilterType type) {
		this.type = type;
	}
	
	public FilterType getType() {
		return type;
	}
	
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	public Set<Tag> getTags() {
		return tags;
	}
	
	/**
	 * Filter a List of Flags
	 * 
	 * @param flags the list of flags to filter
	 * @result the flags that pass through the filter
	 */
	public List<Flag> filter(List<Flag> flags) {
		// if the filter is empty, just return everything
		if (this.tags == null || tags.size() == 0) {
			return flags;
		}
		
		//otherwise, do the appropriate filter
		List<Flag> results = new ArrayList<Flag>();
		
		if (this.type == FilterType.ANYTAG || this.type == FilterType.ANYTAG_OR_NOTAG) {
			for (Flag flag : flags) {
				Set<Tag> flagTags = flag.getTags();
				if(flagTags != null && !flagTags.isEmpty()){
					for (Tag tag : this.tags) {
						if (flagTags.contains(tag)) {
							results.add(flag);
							break;
						}
					}
				}
				else{
					// if the flag has no tags associated it it and this is an ANYTAG_OR_NOTAG filter, add the flag to the result list
					if(this.type == FilterType.ANYTAG_OR_NOTAG){
						results.add(flag);
					}
				}
			}
		}

		else if (this.type == FilterType.ALLTAGS) {
			for (Flag flag : flags) {
				if (flag.getTags() != null && flag.getTags().containsAll(this.tags))
					results.add(flag);
			}
		} else {
			// add an exception here?
		}
		
		return results;
	}
	
	//TODO need to refactor this method such that it is reused by the one before.
	public boolean filter(Flag flag) {
		// if the filter is empty, just return true
		if (this.tags == null || tags.size() == 0) {
			return true;
		}
		
		if (this.type == FilterType.ANYTAG || this.type == FilterType.ANYTAG_OR_NOTAG) {
			Set<Tag> flagTags = flag.getTags();
			if(flagTags != null && !flagTags.isEmpty()){
				for (Tag tag : this.tags) {
					if (flagTags.contains(tag)) {
						return true;
					}
				}
			}
			else{
				// if the flag has no tags associated it it and this is an ANYTAG_OR_NOTAG filter, add the flag to the result list
				if (this.type == FilterType.ANYTAG_OR_NOTAG){
					return true;
				}
			}
		}
		else if (this.type == FilterType.ALLTAGS) {
			if (flag.getTags() != null && flag.getTags().containsAll(this.tags)) {
				return true;
			}
		} else {
			// add an exception here?
		}
		
		return false;
	}
}
