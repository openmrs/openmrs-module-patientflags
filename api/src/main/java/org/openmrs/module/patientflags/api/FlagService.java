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
package org.openmrs.module.patientflags.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.PatientFlagsConstants;
import org.openmrs.module.patientflags.PatientFlagsProperties;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.filter.Filter;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides methods to access flag services
 */
@Transactional
public interface FlagService extends OpenmrsService {
	
	/**
	 * Service methods that operate on Flags
	 */
	
	/**
	 * Generates a list of all Flags that are triggers for the specified Patient
	 * 
	 * @param patient the patient to test
	 * @return the list of Flags the Patient triggers
	 */
	@Transactional(readOnly = true)
	public List<Flag> generateFlagsForPatient(Patient patient);
	
	/**
	 * Generates a list of Flags that are triggers for the specified Patient, filtering the Flags by
	 * the specified filter
	 * 
	 * @param patient the patient to test
	 * @param filter the filter to filter the Flags by
	 * @return the list of Flags the Patient triggers
	 */
	@Transactional(readOnly = true)
	public List<Flag> generateFlagsForPatient(Patient patient, Filter filter);
	
	/**
	 * Generates a list of Flags that are triggers for the specified Patient, filtered by Flags 
	 * Tagged for a specified location and role
	 * 
	 * @param patient the patient to test
	 * @param roles to filter tagged flags by
	 * @param displayPoint displayPoint to filter tagged flags by
	 * @return the list of Flags the Patient triggers
	 */
	@Transactional(readOnly = true)
	public List<Flag> generateFlagsForPatient(Patient patient, Set<Role> roles, DisplayPoint displayPoint);
	
	/**
	 * Generates a list of Flags that are triggers for the specified Patient, filtered by Flags 
	 * Tagged for a specified location and role
	 * 
	 * (This method is identical to generateFlagsForPatient(Patient,Set<Role>,DisplayPoint) except that
	 * it takes a display point name as a string, instead of an actual DisplayPoint object)
	 * 
	 * @param patient the patient to test
	 * @param roles to filter tagged flags by
	 * @param displayPointName name of the displayPoint (as a String) to filter tagged flags by
	 * @return the list of Flags the Patient triggers
	 */
	@Transactional(readOnly = true)
	public List<Flag> generateFlagsForPatient(Patient patient, Set<Role> roles, String displayPointName);
	
	
	/**
	 * Generates a list of all Patients that match the criteria of the specified Flag
	 * 
	 * @param flag the flag to test
	 * @return the list of Patients who match the criteria of the Flag
	 */
	@Transactional(readOnly = true)
	@Authorized( { PatientFlagsConstants.PRIV_TEST_FLAGS })
	public Cohort getFlaggedPatients(Flag flag);
	
	/**
	 * Generate a map of all Patients that match the criteria of the specific Flags
	 * 
	 * @param flags
	 * @return a map of Patients and there corresponding flags
	 */
	@Transactional(readOnly = true)
	@Authorized( { PatientFlagsConstants.PRIV_TEST_FLAGS })
	public Cohort getFlaggedPatients(List<Flag> flags);
	
	/**
	 * Generates a list of all Patient Flags in the database
	 * 
	 * @return a list of all the flags in the database
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public List<Flag> getAllFlags();
	
	/**
	 * Generates a list of all Patient Flags that match a certain filter
	 * 
	 * @param filter the filter to filter by
	 * @return a list of all flags that match the filter
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public List<Flag> getFlagsByFilter(Filter filter);
	
	/**
	 * Retrieve a specific Flag based on its flagId
	 * 
	 * @param flagId
	 * @return the flag which matches the specific ID
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public Flag getFlag(Integer flagId);
	
	/**
	 * Adds or updates a specific flag in the database
	 * 
	 * @param flag the flag to add or update
	 */
	@Authorized( { PatientFlagsConstants.PRIV_MANAGE_FLAGS })
	public void saveFlag(Flag flag);
	
	/**
	 * Removes a specific flag from the database
	 * 
	 * @param flagId the flagId of the flag to remove
	 */
	@Authorized( { PatientFlagsConstants.PRIV_MANAGE_FLAGS })
	public void purgeFlag(Integer flagId);
	
	/**
	 * Generates a list of all Patient Flag Tags
	 * 
	 * @return a list of all patient Tag in the database
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public List<Tag> getAllTags();
	
	/**
	 * Retrieve a specific Tag based on its tagId
	 * 
	 * @param tagId
	 * @return the Tag which matches the specific ID
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public Tag getTag(Integer tagId);
	
	/**
	 * Retrieve a specific Tag based on its name string
	 * 
	 * @param name then name of tag (case-insensitive) to retrieve
	 * @return the Tag which matches the specific name
	 */
	// TODO add functionality so as not to allow two flags with the same name
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public Tag getTag(String name);
	
	/**
	 * Adds or updates a specific Tag in the database
	 * 
	 * @param tag the Tag to add or update
	 */
	@Authorized( { PatientFlagsConstants.PRIV_MANAGE_FLAGS })
	public void saveTag(Tag tag);
	
	/**
	 * Removes a specific Tag from the database
	 * 
	 * @param tagId the tagId of the Tag to remove
	 */
	@Authorized( { PatientFlagsConstants.PRIV_MANAGE_FLAGS })
	public void purgeTag(Integer tagId);
	
	/**
	 * Service methods that operate on Priorities
	 */
	
	/**
	 * Generates a list of all Patient Flag Priority
	 * 
	 * @return a list of all priorities in the database
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public List<Priority> getAllPriorities();
	
	/**
	 * Retrieve a specific Priority based on its priorityId
	 * 
	 * @param tagId
	 * @return the Tag which matches the specific ID
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public Priority getPriority(Integer priorityId);
	
	/**
	 * Adds or updates a specific priority in the database
	 * 
	 * @param priority the priority to add or update
	 */
	@Authorized( { PatientFlagsConstants.PRIV_MANAGE_FLAGS })
	public void savePriority(Priority priority);
	
	/**
	 * Removes a specific priority from the database
	 * 
	 * @param priorityId the priorityId of the priority to remove
	 */
	@Authorized( { PatientFlagsConstants.PRIV_MANAGE_FLAGS })
	public void purgePriority(Integer priorityId);
	
	/**
	 * Generates a list of all DisplayPoints
	 * 
	 * @return a list of all DisplayPoints in the database
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public List<DisplayPoint> getAllDisplayPoints();
	
	/**
	 * Retrieve a specific DisplayPoint based on its displayPointId
	 * 
	 * @param displayPointId
	 * @return the DisplayPoint which matches the specific ID
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public DisplayPoint getDisplayPoint(Integer displayPointId);
	
	/**
	 * Retrieve a specific DisplayPoint based on its name string
	 * 
	 * @param name then name of DisplayPoint (case-insensitive) to retrieve
	 * @return the DisplayPoint which matches the specific name
	 */
	@Transactional(readOnly = true)
	@Authorized(value = { "Manage Flags", "Test Flags" }, requireAll = false)
	public DisplayPoint getDisplayPoint(String name);
	
	/**
	 * Adds or updates a specific DisplayPoint in the database
	 * 
	 * @param tag the DisplayPoint to add or update
	 */
	@Authorized( { PatientFlagsConstants.PRIV_MANAGE_FLAGS })
	public void saveDisplayPoint(DisplayPoint displayPoint);
	
	/**
	 * Removes a specific DisplayPoint from the database
	 * 
	 * @param displayPointId the DisplayPointId of the DisplayPoint to remove
	 */
	@Authorized( { PatientFlagsConstants.PRIV_MANAGE_FLAGS })
	public void purgeDisplayPoint(Integer DisplayPointId);
	
	/**
	 * Returns the current Patient Flags properties in a PatientFlagsProperties object
	 * 
	 * @return patientFlagProperties current Patient Flag properties
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	@Authorized( { PatientFlagsConstants.PRIV_MANAGE_FLAGS })
	public PatientFlagsProperties getPatientFlagsProperties();
	
	/**
	 * Saves Patient Flags properties to Global Properties
	 * 
	 * @param properties current Patient Flag properties
	 * @throws APIException
	 */
	@Authorized(value = { "Manage Flags", "Manage Global Properties" }, requireAll = true)
	public void savePatientFlagsProperties(PatientFlagsProperties properties);
	
	/**
	 * Returns the privileges that the Groovy evaluator should have when 
	 * executing a Groovy script
	 * 
	 * @return privileges the privileges allowed
	 */
	public Collection<Privilege> getPrivileges();
}
