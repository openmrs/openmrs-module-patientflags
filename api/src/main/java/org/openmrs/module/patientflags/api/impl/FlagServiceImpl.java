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
package org.openmrs.module.patientflags.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.PatientFlagsProperties;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.comparator.FlagAlphaComparator;
import org.openmrs.module.patientflags.comparator.FlagPriorityComparator;
import org.openmrs.module.patientflags.comparator.PriorityComparator;
import org.openmrs.module.patientflags.comparator.TagAlphaComparator;
import org.openmrs.module.patientflags.db.FlagDAO;
import org.openmrs.module.patientflags.filter.Filter;
import org.openmrs.module.patientflags.filter.FilterType;

/**
 * Implementation of the {@link FlagService}
 */
public class FlagServiceImpl extends BaseOpenmrsService implements FlagService {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/* Local Cache to store all enabled Flags and all Tags to improve real-time searching */
	private List<Flag> flagCache;
	
	private List<Tag> tagCache;
	
	/* Local Cache to store the privileges that should be given to a Groovy Evaluator */
	private Collection<Privilege> privilegeCache;
	
	/* Data access object for Flags */
	private FlagDAO dao;
	
	private boolean isInitialized = false; // a hack to overcome the fact that calling "onStartup" has yet to implemented in OpenMRS core
	
	/**
	 * Method for spring to set the database access object for this service
	 * 
	 * @param dao
	 */
	public void setFlagDAO(FlagDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#generateFlagsForPatient(Patient)
	 */
	public List<Flag> generateFlagsForPatient(Patient patient) {
		return generateFlagsForPatient(patient, new Filter());
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#generateFlagsForPatient(Patient, Filter)
	 */
	public List<Flag> generateFlagsForPatient(Patient patient, Filter filter) {
		List<Flag> results = new ArrayList<Flag>();
		
		// we can get rid of this once onStartup is implemented
		if (!isInitialized)
			refreshCache();
		
		// test each Flag in the cache against the specific Patient
		for (Flag flag : filter.filter(flagCache)) {
			// trap bad flags so that they don't hang the system
			try {
				if (flag.eval(patient))
					results.add(flag);
			}
			catch (Exception e) {
				log.error("Unable to test flag " + flag.getName() + " on patient #" + patient.getId(), e);
			}
		}
		return results;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#generateFlagsForPatient(Patient,
	 *      Set<Role>, DisplayPoint)
	 */
	public List<Flag> generateFlagsForPatient(Patient patient, Set<Role> roles, DisplayPoint displayPoint) {
		// we can get rid of this once onStartup is implemented
		if (!isInitialized)
			refreshCache();
		
		// generate the set of tags to filter by
		Set<Tag> tags = new HashSet<Tag>();
		for (Tag tag : tagCache) {
			if (displayPoint == null || tag.getDisplayPoints().contains(displayPoint)) {
				if (roles == null) {
					tags.add(tag);
				} else {
					for (Role role : roles) {
						if (tag.getRoles().contains(role)) {
							tags.add(tag);
							break;
						}
					}
				}
			}
		}
		
		// create the filter
		Filter filter = new Filter(tags);
		// set the filter to pass-through any untagged flags
		filter.setType(FilterType.ANYTAG_OR_NOTAG);
		
		return generateFlagsForPatient(patient, filter);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#generateFlagsForPatient(Patient,
	 *      Set<Role>, String)
	 */
	public List<Flag> generateFlagsForPatient(Patient patient, Set<Role> roles, String displayPointName) {
		return generateFlagsForPatient(patient, roles, getDisplayPoint(displayPointName));
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlaggedPatients(Flag)
	 */
	public Cohort getFlaggedPatients(Flag flag) {
		if (flag != null) {
			return flag.evalCohort(null);
		} else {
			return new Cohort();
		}
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlaggedPatients(List<Flag>)
	 */
	public Cohort getFlaggedPatients(List<Flag> flags) {
		Cohort resultCohort = new Cohort();
		
		// test each Flag
		for (Flag flag : flags) {
			resultCohort = Cohort.union(resultCohort, flag.evalCohort(null));
		}
		return resultCohort;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getAllFlags()
	 */
	public List<Flag> getAllFlags() {
		List<Flag> flags = dao.getAllFlags();
		
		// alphabetize the results
		Collections.sort(flags, new FlagAlphaComparator());
		
		return flags;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlagsByFilter()
	 */
	public List<Flag> getFlagsByFilter(Filter filter) {
		// should we change this so the filtering happens in the DAO?
		// (i.e., so we aren't hitting the DB for ALL the patient flags
		// each time we want to filter?)
		if (filter != null) {
			return filter.filter(getAllFlags());
		} else {
			// if filter is Null, just return entire flag set
			return getAllFlags();
		}
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlag(Flag)
	 */
	public Flag getFlag(Integer flagId) {
		return dao.getFlag(flagId);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#saveFlag(Flag)
	 */
	public void saveFlag(Flag flag) {
		// add the flag to the DB table
		dao.saveFlag(flag);
		// then refresh the cache
		refreshCache();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#purgeFlag(Integer)
	 */
	public void purgeFlag(Integer flagId) {
		// remove the flag from the DB table
		dao.purgeFlag(flagId);
		// then refresh the cache
		refreshCache();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getAllTags()
	 */
	public List<Tag> getAllTags() {
		List<Tag> tags = dao.getAllTags();
		
		// alphabetize the results
		Collections.sort(tags, new TagAlphaComparator());
		
		return tags;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getTag(Integer)
	 */
	public Tag getTag(Integer tagId) {
		return dao.getTag(tagId);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getTag(String)
	 */
	public Tag getTag(String name) {
		return dao.getTag(name);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#saveTag(Tag tag)
	 */
	public void saveTag(Tag tag) {
		dao.saveTag(tag);
		// then refresh the cache
		refreshCache();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#purgeTag(Integer)
	 */
	public void purgeTag(Integer tagId) {
		dao.purgeTag(tagId);
		// then refresh the cache
		refreshCache();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getAllPriorities()
	 */
	public List<Priority> getAllPriorities() {
		List<Priority> priorities = dao.getAllPriorities();
		
		// sort by rank
		Collections.sort(priorities, new PriorityComparator());
		
		return priorities;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getPriority(Integer)
	 */
	public Priority getPriority(Integer priorityId) {
		return dao.getPriority(priorityId);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#savePriority(Priority)
	 */
	public void savePriority(Priority priority) {
		// add the priority to the DB table
		dao.savePriority(priority);
		// then refresh the cache -- so that priority name/style changes are picked up (?)
		refreshCache();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#purgePriority(Integer)
	 */
	public void purgePriority(Integer priorityId) {
		// first we need to make sure that this priority is not currently referenced by a flag
		Priority priority = getPriority(priorityId);
		List<Flag> flags = getAllFlags();
		for (Flag flag : flags) {
			if (flag.getPriority().equals(priority)) {
				throw (new APIException("Cannot delete priority, because it's referenced by an existing flag."));
			}
		}
		
		// remove the flag from the DB table
		dao.purgePriority(priorityId);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getAllDisplayPoints()
	 */
	public List<DisplayPoint> getAllDisplayPoints() {
		return dao.getAllDisplayPoints();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getDisplayPoint(Integer)
	 */
	public DisplayPoint getDisplayPoint(Integer displayPointId) {
		return dao.getDisplayPoint(displayPointId);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getDisplayPoint(String)
	 */
	public DisplayPoint getDisplayPoint(String name) {
		return dao.getDisplayPoint(name);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#saveDisplayPoint(DisplayPoint)
	 */
	public void saveDisplayPoint(DisplayPoint displayPoint) {
		dao.saveDisplayPoint(displayPoint);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#purgeDisplayPoint(Integer)
	 */
	public void purgeDisplayPoint(Integer displayPointId) {
		dao.purgeDisplayPoint(displayPointId);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getPatientFlagsProperties()
	 */
	public PatientFlagsProperties getPatientFlagsProperties() {
		PatientFlagsProperties properties = new PatientFlagsProperties();
		
		// initialize Patient Flags Properties based on values in global_properties
		String patientHeaderDisplay = Context.getAdministrationService().getGlobalProperty(
		    "patientflags.patientHeaderDisplay");
		if (patientHeaderDisplay == null) {
			properties.setPatientHeaderDisplay(null);
			log
			        .error("Unable to initialize patientHeaderDisplay parameter, invalid or missing value in global_properties table.");
		} else if (patientHeaderDisplay.equals("true")) {
			properties.setPatientHeaderDisplay(true);
		} else if (patientHeaderDisplay.equals("false")) {
			properties.setPatientHeaderDisplay(false);
		} else {
			properties.setPatientHeaderDisplay(null);
			log
			        .error("Unable to initialize patientHeaderDisplay parameter, invalid or missing value in global_properties table.");
		}
		
		String patientOverviewDisplay = Context.getAdministrationService().getGlobalProperty(
		    "patientflags.patientOverviewDisplay");
		if (patientOverviewDisplay == null) {
			properties.setPatientOverviewDisplay(null);
			log
			        .error("Unable to initialize patientOverviewDisplay parameter, invalid or missing value in global_properties table.");
		} else if (patientOverviewDisplay.equals("true")) {
			properties.setPatientOverviewDisplay(true);
		} else if (patientOverviewDisplay.equals("false")) {
			properties.setPatientOverviewDisplay(false);
		} else {
			properties.setPatientOverviewDisplay(null);
			log
			        .error("Unable to initialize patientOverviewDisplay parameter, invalid or missing value in global_properties table.");
		}
		
		String username = Context.getAdministrationService().getGlobalProperty("patientflags.username");
		if (username == null) {
			properties.setUsername(null);
			log.error("Unable to initialize username parameter, invalid or missing value in global_properties table.");
		} else {
			properties.setUsername(username);
		}
		
		return properties;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#savePatientFlagsProperties(PatientFlagsProperties)
	 */
	public void savePatientFlagsProperties(PatientFlagsProperties properties) {
		// save updated property values back to global_properties
		if (properties != null) {
			try {
				GlobalProperty patientHeaderDisplay = Context.getAdministrationService().getGlobalPropertyObject(
				    "patientflags.patientHeaderDisplay");
				patientHeaderDisplay.setPropertyValue(properties.getPatientHeaderDisplay().toString());
				Context.getAdministrationService().saveGlobalProperty(patientHeaderDisplay);
				
				GlobalProperty patientOverviewDisplay = Context.getAdministrationService().getGlobalPropertyObject(
				    "patientflags.patientOverviewDisplay");
				patientOverviewDisplay.setPropertyValue(properties.getPatientOverviewDisplay().toString());
				Context.getAdministrationService().saveGlobalProperty(patientOverviewDisplay);
				
				GlobalProperty username = Context.getAdministrationService().getGlobalPropertyObject("patientflags.username");
				username.setPropertyValue(properties.getUsername());
				Context.getAdministrationService().saveGlobalProperty(username);
			
				// refresh the cache so the privileges are updated if username changed
				refreshCache();
			}
			catch (Throwable t) {
				throw new APIException(
				        "Unable to update Patient Flags global properties. Try restarting Patient Flags module.", t);
			}
		} else {
			log.error("Cannot save Patient Flags properties - invalid PatientFlagsProperties object");
		}
		
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getPrivileges()
	 */
	public Collection<Privilege> getPrivileges() {
		// we can get rid of this once onStartup is implemented
		if (!isInitialized)
			refreshCache();
		
		return privilegeCache;
	}
	
	public void onStartup() {
		//implement this once OpenMRS "onStartup" functionality has been implemented
		// then can rid of the "isInitialized" hack
		//refreshCache();
	}
	
	/**
	 * Private Utility Methods
	 */
	
	/**
	 * Refresh the local cache of Flags by loading all enabled Flags from the database 
	 * Also refresh the privilege cache
	 * Note that flags and tags must be eagerly loaded for this to work
	 */
	private void refreshCache() {
		// get the enabled flags and all tags for the flag and tag caches
		flagCache = dao.getAllEnabledFlags();
		tagCache = getAllTags();
		
		// alphabetize the flags, and sort by by priority ranking so that result sets are returned that way
		Collections.sort(flagCache, new FlagAlphaComparator());
		Collections.sort(flagCache, new FlagPriorityComparator());
		
		// get the privileges associate with the patient flags default user for the privileges cache
		try{
			Context.addProxyPrivilege("Get Users");
			Context.addProxyPrivilege("View Users");
			String username = Context.getAdministrationService().getGlobalProperty("patientflags.username");
			User user = Context.getUserService().getUserByUsername(username);
		
			if (user != null) {
				if (user.isSuperUser()) { // need to explicitly get all privileges if user is a super user
					privilegeCache = Context.getUserService().getAllPrivileges();
				}
				else {
					privilegeCache = user.getPrivileges();
				}
			}
			else {
				privilegeCache = null;
			}
		}
		
		finally{
			Context.removeProxyPrivilege("Get Users");
			Context.removeProxyPrivilege("View Users");
		}
			
		// set the initialized flag to true
		isInitialized = true;
	}
}
