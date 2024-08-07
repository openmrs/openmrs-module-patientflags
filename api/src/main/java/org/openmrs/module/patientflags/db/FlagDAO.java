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
package org.openmrs.module.patientflags.db;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.Tag;

/**
 * Database access object for the Flag table (flag_flag) and Tag table (flag_tag)
 */
public interface FlagDAO {
	
	/**
	 * Get all the Patient Flags in the Flag table
	 * 
	 * @return a list of all Patient Flags in the Flag table
	 * @throws DAOException
	 */
	public List<Flag> getAllFlags() throws DAOException;
	
	/**
	 * Gets all Patient Flags that are currently set as "Enabled"
	 * 
	 * @return a list of all enabled Patient Flags in the Flag table
	 * @throws DAOException
	 */
	public List<Flag> getAllEnabledFlags() throws DAOException;
	
	/**
	 * Gets a Flag from the Flag table
	 * 
	 * @param flagId the flagId of the flag to retrieve
	 * @return the specified Flag
	 * @throws DAOException
	 */
	public Flag getFlag(Integer flagId) throws DAOException;

	/**
	 * Gets a Flag from the Flag table by its uuid
	 *
	 * @param uuid the uuid of the flag to retrieve
	 * @return the specified Flag
	 * @throws DAOException
	 */
	public Flag getFlagByUuid(String uuid) throws DAOException;
	
	/**
	 * Gets a PatientFlag from the PatientFlag table by its uuid
	 *
	 * @param uuid the uuid of the patient flag to retrieve
	 * @return the specified PatientFlag
	 * @throws DAOException
	 */
	public PatientFlag getPatientFlagByUuid(String uuid) throws DAOException;

	/**
	 * Gets a Flag from the Flag table by its name
	 *
	 * @param name the name of the flag to retrieve
	 * @return the specified Flag
	 * @throws DAOException
	 */
	public Flag getFlagByName(String name) throws DAOException;
	
	/**
	 * Saves a Flag in the Flag table
	 * 
	 * @param flag the flag to save
	 * @throws DAOException
	 */
	public void saveFlag(Flag flag) throws DAOException;
	
	/**
	 * Saves a Patient Flag in the Patient Flag table
	 * 
	 * @param patientFlag the patient flag to save
	 * @throws DAOException
	 */
	public void savePatientFlag(PatientFlag patientFlag) throws DAOException;

	/**
	 * Searches Flags from the Flag table by matching name, evaluator, enabled state or tags mapped.
	 * Any parameter can be null to skip the filter
	 *
	 * @param name
	 * @param evaluator
	 * @param enabled
	 * @param tags
	 * @return the list of flags matching specified criteria
	 * @throws DAOException DAO Exception thrown
	 */
	List<Flag> searchFlags(String name, String evaluator, Boolean enabled, List<String> tags) throws DAOException;

	/**
	 * Remove a Flag from the Flag table
	 * 
	 * @param flagId the flagId of the Flag to remove
	 * @throws DAOException
	 */
	public void purgeFlag(Integer flagId) throws DAOException;
	
	/**
	 * Get all the Patient Tags in the Tag table
	 * 
	 * @return a list of all Patient tags in the Tag table
	 * @throws DAOException
	 */
	public List<Tag> getAllTags() throws DAOException;
	
	/**
	 * Gets a Tag from the Tag table
	 * 
	 * @param tagId the tagId of the Tag to retrieve
	 * @return the specified Tag
	 * @throws DAOException
	 */
	public Tag getTag(Integer tagId) throws DAOException;
	
	/**
	 * Gets a Tag from the Tag table
	 * 
	 * @param name the name (case-insensitive) of the Tag to retrieve
	 * @return the specified Tag
	 * @throws DAOException
	 */
	public Tag getTag(String name) throws DAOException;

	/**
	 * Gets a Tag from the Tag table by its uuid
	 *
	 * @param uuid
	 * @return the specified Tag
	 * @throws DAOException
	 */
	public Tag getTagByUuid(String uuid) throws DAOException;
	
	/**
	 * Saves a Tag in the Tag table
	 * 
	 * @param tag the Tag to save
	 * @throws DAOException
	 */
	public void saveTag(Tag tag) throws DAOException;
	
	/**
	 * Remove a Tag from the Tag table
	 * 
	 * @param tagId the tagId of the Tag to remove
	 * @throws DAOException
	 */
	public void purgeTag(Integer tagId) throws DAOException;
	
	/**
	 * Get all the Patient Flag Priorities in the Priorities table
	 * 
	 * @return a list of all Patient Flag Priorities in the Priorities table
	 * @throws DAOException
	 */
	public List<Priority> getAllPriorities() throws DAOException;
	
	/**
	 * Gets a Priority from the Priority table
	 * 
	 * @param priorityId the priorityId of the Tag to retrieve
	 * @return the specified Priority
	 * @throws DAOException
	 */
	public Priority getPriority(Integer priorityId) throws DAOException;

	/**
	 * Gets a Priority from the Priority table by its uuid
	 *
	 * @param uuid the uuid of the Priority to retrieve
	 * @return the specified Priority
	 * @throws DAOException
	 */
	public Priority getPriorityByUuid(String uuid) throws DAOException;

	/**
	 * Gets a Priority from the Priority table by its uuid
	 *
	 * @param name the name of the Priority to retrieve
	 * @return the specified Priority
	 * @throws DAOException
	 */
	public Priority getPriorityByName(String name) throws DAOException;
	
	/**
	 * Saves a Priority in the Priority table
	 * 
	 * @param priority the Priority to save
	 * @throws DAOException
	 */
	public void savePriority(Priority priority) throws DAOException;
	
	/**
	 * Remove a Priority from the Priority table
	 * 
	 * @param priorityId the priorityId of the Priority to remove
	 * @throws DAOException
	 */
	public void purgePriority(Integer priorityId) throws DAOException;
	
	/**
	 * Get all the DisplayPoints in the DisplayPoint table
	 * 
	 * @return a list of all DisplayPoints in the DisplayPoint table
	 * @throws DAOException
	 */
	public List<DisplayPoint> getAllDisplayPoints() throws DAOException;
	
	/**
	 * Gets a DisplayPoint from the DisplayPoint table
	 * 
	 * @param displayPointId the displayPointId of the DisplayPoint to retrieve
	 * @return the specified DisplayPoint
	 * @throws DAOException
	 */
	public DisplayPoint getDisplayPoint(Integer displayPointId) throws DAOException;
	
	/**
	 * Gets a DisplayPoint from the DisplayPoint table
	 * 
	 * @param name the name (case-insensitive) of the DisplayPoint to retrieve
	 * @return the specified DisplayPoint
	 * @throws DAOException
	 */
	public DisplayPoint getDisplayPoint(String name) throws DAOException;
	
	/**
	 * Saves a DisplayPoint in the DisplayPoint table
	 * 
	 * @param displayPoint the DisplayPoint to save
	 * @throws DAOException
	 */
	public void saveDisplayPoint(DisplayPoint displayPoint) throws DAOException;
	
	/**
	 * Remove a DisplayPoint from the DisplayPoint table
	 * 
	 * @param displayPointId the displayPointId of the DisplayPoint to remove
	 * @throws DAOException
	 */
	public void purgeDisplayPoint(Integer displayPointId) throws DAOException;

	/**
	 * Checks if given priority's name is duplicated
	 *
	 * @param priority object to verify uniqueness of priority name
	 * @return true or false depending on priority's name is duplicated or not
	 */
	boolean isPriorityNameDuplicated(Priority priority);

	/**
	 * Checks if given flag's name is duplicated
	 *
	 * @param flag object to verify uniqueness of flag name
	 * @return true or false depending on flag's name is duplicated or not
	 */
	boolean isFlagNameDuplicated(Flag flag);
	
	/**
	 * Gets all Flags for a patient
	 * 
	 * @return a list of all Patient Flags
	 * @throws DAOException
	 */
	List<Flag> getFlagsForPatient(Patient patient) throws DAOException;
	
	/**
	 * Gets all PatientFlags for a patient
	 * 
	 * @return a list of all Patient Flags
	 * @throws DAOException
	 */
	List<PatientFlag> getPatientFlags(Patient patient) throws DAOException;
	
	/**
	 * Deletes all non voided flags for a given patient
	 * 
	 * @param patient the patient whose non voided flags to delete
	 * @throws DAOException
	 */
	void deletePatientFlagsForPatient(Patient patient) throws DAOException;
	
	/**
	 * Deletes a non voided flag for a given patient
	 * 
	 * @param patient the patient whose flag to delete
	 * @param flag the flag to delete for the given patient
	 * @throws DAOException
	 */
	void deletePatientFlagForPatient(Patient patient, Flag flag) throws DAOException;
	
	/**
	 * Deletes all non voided patient flags for a given flag
	 * 
	 * @param flag the flag
	 * @throws DAOException
	 */
	void deletePatientFlagsForFlag(Flag flag) throws DAOException;

	/**
	 * Delete all patient flags.
	 *
	 * @throws DAOException the dao exception
	 */
	void deleteAllPatientFlags() throws DAOException;
}
