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

import org.apache.commons.lang.StringUtils;
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
import org.openmrs.api.db.DAOException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.PatientFlag;
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
import org.openmrs.module.patientflags.task.PatientFlagTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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
	
	private ExecutorService executor;
	
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
	 * @see org.openmrs.module.patientflags.api.FlagService#generateFlagsForPatient(Patient, Map<Object, Object>)
	 */
	public List<Flag> generateFlagsForPatient(Patient patient, Map<Object, Object> context) {
		return generateFlagsForPatient(patient, new Filter(), context);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#generateFlagsForPatient(Patient, Filter, Map<Object, Object>)
	 */
	public List<Flag> generateFlagsForPatient(Patient patient, Filter filter, Map<Object, Object> context) {
		List<Flag> results = new ArrayList<Flag>();
		
		// we can get rid of this once onStartup is implemented
		if (!isInitialized)
			refreshCache();
		
		// test each Flag in the cache against the specific Patient
		for (Flag flag : filter.filter(flagCache)) {
			// trap bad flags so that they don't hang the system
			try {
				if (flag.eval(patient, context))
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
	public List<Flag> generateFlagsForPatient(Patient patient, Set<Role> roles, DisplayPoint displayPoint, Map<Object, Object> context) {
		return generateFlagsForPatient(patient, getFilter(roles, displayPoint), context);
	}
	
	private Filter getFilter(Set<Role> roles, DisplayPoint displayPoint) {
		// we can get rid of this once onStartup is implemented
		if (!isInitialized)
			refreshCache();
		
		// generate the set of tags to filter by
		Set<Tag> tags = tagCache.stream()
				.filter(tag -> displayPoint == null || tag.getDisplayPoints().contains(displayPoint))
				.filter(tag -> roles == null || roles.stream().anyMatch(role -> tag.getRoles().contains(role)))
				.collect(Collectors.toSet());
		
		// create the filter
		Filter filter = new Filter(tags);
		// set the filter to pass-through any untagged flags
		filter.setType(FilterType.ANYTAG_OR_NOTAG);
		
		return filter;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#generateFlagsForPatient(Patient,
	 *      Set<Role>, String, , Map<Object, Object>)
	 */
	public List<Flag> generateFlagsForPatient(Patient patient, Set<Role> roles, String displayPointName, Map<Object, Object> context) {
		return generateFlagsForPatient(patient, roles, getDisplayPoint(displayPointName), context);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlaggedPatients(Flag, Map<Object, Object>)
	 */
	public Cohort getFlaggedPatients(Flag flag, Map<Object, Object> context) {
		if (flag != null) {
			return flag.evalCohort(null, context);
		} else {
			return new Cohort();
		}
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlaggedPatients(List<Flag>, Map<Object, Object>)
	 */
	public Cohort getFlaggedPatients(List<Flag> flags, Map<Object, Object> context) {
		return flags.stream()
				.map(flag -> flag.evalCohort(null, context))
				.reduce(new Cohort(), Cohort::union);
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
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlagsByFilter(Filter)
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
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlag(Integer)
	 */
	public Flag getFlag(Integer flagId) {
		return dao.getFlag(flagId);
	}

	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlagByUuid(String)
	 */
	public Flag getFlagByUuid(String uuid) {
		return dao.getFlagByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getPatientFlagByUuid(String)
	 */
	public PatientFlag getPatientFlagByUuid(String uuid) {
		return dao.getPatientFlagByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getFlagByName(String)
	 */
	public Flag getFlagByName(String name) {
		return dao.getFlagByName(name);
	}

	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#saveFlag(Flag)
	 */
	public void saveFlag(Flag flag) {
		// add the flag to the DB table
		dao.saveFlag(flag);
		// then refresh the cache
		refreshCache();
		
		new PatientFlagTask().generatePatientFlags(flag);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#purgeFlag(Integer)
	 */
	public void purgeFlag(Integer flagId) {
		// remove the flag from the DB table
		dao.purgeFlag(flagId);
		// then refresh the cache
		refreshCache();
		
		dao.deletePatientFlagsForFlag(new Flag(flagId));
	}

	public void retireFlag(Flag flag, String retiredReason) {
		if (StringUtils.isNotBlank(retiredReason)) {
			flag.setRetired(true);
			flag.setRetireReason(retiredReason);
			Context.getService(FlagService.class).saveFlag(flag);
			
			dao.deletePatientFlagsForFlag(flag);
		} else {
			throw new APIException("A reason is required when retiring a patient flag");
		}
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
	 * @see org.openmrs.module.patientflags.api.FlagService#getTagByUuid(String)
	 */
	public Tag getTagByUuid(String uuid) {
		return dao.getTagByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getTagByName(String)
	 */
	public Tag getTagByName(String name) {
		return dao.getTag(name);
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
	 * @see FlagService#searchFlags(String, String, Boolean, List)
	 */
	public List<Flag> searchFlags(String name, String evaluator, Boolean enabled, List<String> tags) {
		return dao.searchFlags(name, evaluator, enabled, tags);
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
	 * @see org.openmrs.module.patientflags.api.FlagService#retireFlag(Flag, String)
	 */
	public void retireTag(Tag tag, String retiredReason) {
		if (StringUtils.isNotBlank(retiredReason)) {
			tag.setRetired(true);
			tag.setRetireReason(retiredReason);
			Context.getService(FlagService.class).saveTag(tag);
		} else {
			throw new APIException("A reason is required when retiring a tag for patient flags");
		}
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
	 * @see org.openmrs.module.patientflags.api.FlagService#getPriorityByUuid(String)
	 */
	public Priority getPriorityByUuid(String uuid) {
		return dao.getPriorityByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#getPriorityByName(String)
	 */
	public Priority getPriorityByName(String name) {
		return dao.getPriorityByName(name);
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

		getAllFlags().forEach(flag-> {
			Priority flagPriority = flag.getPriority();
			if (flagPriority != null && flagPriority.equals(priority)) {
				throw (new APIException("Cannot delete priority, because it's referenced by an existing flag."));
			}
		});
		
		// remove the flag from the DB table
		dao.purgePriority(priorityId);
	}

	/**
	 *
	 * @see org.openmrs.module.patientflags.api.FlagService#retirePriority(Priority, String)
	 */
	public void retirePriority(Priority priority, String retiredReason) {
		if (StringUtils.isNotBlank(retiredReason)) {
			priority.setRetired(true);
			priority.setRetireReason(retiredReason);
			Context.getService(FlagService.class).savePriority(priority);
		} else {
			throw new APIException("A reason is required when retiring a priority for patient flags");
		}
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
	 * @see org.openmrs.module.patientflags.api.FlagService#getDisplayPointByUuid(String)
	 */
	public DisplayPoint getDisplayPointByUuid(String uuid) {
		return null;
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

	public boolean isFlagNameDuplicated(Flag flag) {
		return dao.isFlagNameDuplicated(flag);
	}

	public void onStartup() {
		//implement this once OpenMRS "onStartup" functionality has been implemented
		// then can rid of the "isInitialized" hack
		//refreshCache();
	}

	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#isPriorityNameDuplicated(Priority) 
	 */
	public boolean isPriorityNameDuplicated(Priority priority) {
		return dao.isPriorityNameDuplicated(priority);
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

	@Override
	public List<Flag> getFlagsForPatient(Patient patient) {
		return dao.getFlagsForPatient(patient);
	}

	@Override
	public void savePatientFlag(PatientFlag patientFlag) throws DAOException {
		dao.savePatientFlag(patientFlag);
	}

	@Override
	public void deletePatientFlagsForPatient(Patient patient) throws DAOException {
		dao.deletePatientFlagsForPatient(patient);
	}
	
	@Override
	public void deletePatientFlagForPatient(Patient patient, Flag flag) throws DAOException {
		dao.deletePatientFlagForPatient(patient, flag);
	}
	
	@Override
	public void deletePatientFlagsForFlag(Flag flag) throws DAOException {
		dao.deletePatientFlagsForFlag(flag);
	}

	@Override
	public List<Flag> getFlagsForPatient(Patient patient, Filter filter) {
		return 	filter.filter(getFlagsForPatient(patient));
	}

	@Override
	public List<Flag> getFlagsForPatient(Patient patient, Set<Role> roles, DisplayPoint displayPoint) {
		return getFlagsForPatient(patient, getFilter(roles, displayPoint));
	}

	@Override
	public List<Flag> getFlagsForPatient(Patient patient, Set<Role> roles, String displayPointName) {
		return getFlagsForPatient(patient, getFilter(roles, getDisplayPoint(displayPointName)));
	}
	
	public List<PatientFlag> getPatientFlags(Patient patient, Filter filter) {
		return getPatientFlags(patient).stream()
				.filter(patientFlag -> filter.filter(patientFlag.getFlag()))
				.collect(Collectors.toList());
	}
	
	private List<Flag> getFlags(List<PatientFlag> patientFlags) {
		return patientFlags.stream()
				.map(PatientFlag::getFlag)
				.collect(Collectors.toList());
	}

	@Override
	public Future<?> evaluateAllFlags() {
		if (executor == null) {
			executor = Executors.newSingleThreadExecutor();
		}
		return executor.submit(new PatientFlagTask());
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#voidPatientFlag(PatientFlag, java.lang.String)
	 * @param patientFlag the PatientFlag to void
	 * @param reason the void reason
	 * @throws APIException
	 */
	@Override
	public void voidPatientFlag(PatientFlag patientFlag, String reason) throws APIException {
		dao.savePatientFlag(patientFlag);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.api.FlagService#unvoidPatientFlag(PatientFlag)
	 * @param patientFlag the PatientFlag to unvoid
	 * @return the unvoided PatientFlag
	 * @throws APIException
	 */
	@Override
	public void unvoidPatientFlag(PatientFlag patientFlag) throws APIException {
		Context.getService(FlagService.class).savePatientFlag(patientFlag);
	}
	
	@Override
	public List<PatientFlag> getPatientFlags(Patient patient) {
		return dao.getPatientFlags(patient);
	}

	@Override
	public List<PatientFlag> getPatientFlags(Patient patient, Set<Role> roles, String displayPointName) {
		return getPatientFlags(patient, getFilter(roles, getDisplayPoint(displayPointName)));
	}
}
