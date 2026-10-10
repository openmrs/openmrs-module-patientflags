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
package org.openmrs.module.patientflags.db.hibernate;

import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.db.FlagDAO;

import java.util.List;

/**
 * Implementation of the {@link FlagDAO}
 */
public class HibernateFlagDAO implements FlagDAO {
	
	/**
	 * Hibernate session factory
	 */
	private DbSessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getAllFlags()
	 */
	@SuppressWarnings("unchecked")
	public List<Flag> getAllFlags() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from Flag").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Flag> getAllEnabledFlags() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from Flag f where f.enabled = true").getResultList();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getFlag(Integer)
	 */
	public Flag getFlag(Integer flagId) {
		return (Flag) sessionFactory.getCurrentSession().get(Flag.class, flagId);
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getFlagByUuid(String)
	 */
	public Flag getFlagByUuid(String uuid) throws DAOException {
		return (Flag)this.sessionFactory.getCurrentSession().createQuery("from Flag f where f.uuid = :uuid").setParameter("uuid", uuid).getSingleResultOrNull();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getPatientFlagByUuid(String)
	 */
	public PatientFlag getPatientFlagByUuid(String uuid) throws DAOException {
		return (PatientFlag)this.sessionFactory.getCurrentSession().createQuery("from PatientFlag f where f.uuid = :uuid").setParameter("uuid", uuid).getSingleResultOrNull();
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getFlagByName(String)
	 */
	public Flag getFlagByName(String name) throws DAOException {
		String hql;
		if (Context.getAdministrationService().isDatabaseStringComparisonCaseSensitive()) {
			hql = "from Flag x where lower(x.name) = lower(:name)";
		} else {
			hql = "from Flag x where x.name = :name";
		}

		@SuppressWarnings("unchecked")
		List<Flag> list = sessionFactory.getCurrentSession().createQuery(hql).setParameter("name", name).getResultList();

		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() == 0) {
			return null;
		} else {
			throw new APIException("Multiple flags found with the name '" + name + "'");
		}
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#saveFlag(Flag)
	 */
	public void saveFlag(Flag flag) throws DAOException {
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(flag);
		}
		catch (Throwable t) {
			throw new DAOException(t);
		}
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#searchFlags(String, String, Boolean, List)
	 */
	@SuppressWarnings("unchecked")
	public List<Flag> searchFlags(String name, String evaluator, Boolean enabled, List<String> tags) throws DAOException {
		boolean hasTags = tags != null && tags.size() > 0;
		StringBuilder hql = new StringBuilder("select distinct f from Flag f");
		if (hasTags) {
			hql.append(" join f.tags t");
		}
		hql.append(" where 1 = 1");

		if (StringUtils.isNotBlank(name)) {
			hql.append(" and lower(f.name) like :name");
		}

		if (StringUtils.isNotBlank(evaluator)) {
			hql.append(" and f.evaluator = :evaluator");
		}

		if (enabled != null) {
			hql.append(" and f.enabled = :enabled");
		}

		if (hasTags) {
			hql.append(" and t.name in (:tags)");
		}

		Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
		if (StringUtils.isNotBlank(name)) {
			query.setParameter("name", name.toLowerCase() + "%");
		}
		if (StringUtils.isNotBlank(evaluator)) {
			query.setParameter("evaluator", evaluator);
		}
		if (enabled != null) {
			query.setParameter("enabled", enabled);
		}
		if (hasTags) {
			query.setParameter("tags", tags);
		}

		return query.getResultList();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#purgeFlag(Integer)
	 */
	public void purgeFlag(Integer flagId) throws DAOException {
		Flag flag = getFlag(flagId);
		sessionFactory.getCurrentSession().delete(flag);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getAllTags()
	 */
	@SuppressWarnings("unchecked")
	public List<Tag> getAllTags() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from Tag").getResultList();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getTag(Integer)
	 */
	public Tag getTag(Integer tagId) {
		return (Tag) sessionFactory.getCurrentSession().get(Tag.class, tagId);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getTag(String)
	 */
	public Tag getTag(String name) {
		String hql;
		if (Context.getAdministrationService().isDatabaseStringComparisonCaseSensitive()) {
			hql = "from Tag x where lower(x.name) = lower(:name)";
		} else {
			hql = "from Tag x where x.name = :name";
		}

		@SuppressWarnings("unchecked")
		List<Tag> list = sessionFactory.getCurrentSession().createQuery(hql).setParameter("name", name).getResultList();

		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() == 0) {
			return null;
		} else {
			throw new APIException("Multiple tags found with the name '" + name + "'");
		}
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getTagByUuid(String)
	 */
	public Tag getTagByUuid(String uuid) throws DAOException {
		return (Tag)this.sessionFactory.getCurrentSession().createQuery("from Tag t where t.uuid = :uuid").setParameter("uuid", uuid).getSingleResultOrNull();
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#saveTag(Tag)
	 */
	public void saveTag(Tag tag) throws DAOException {
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(tag);
		}
		catch (Throwable t) {
			throw new DAOException(t);
		}
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#purgeTag(Integer)
	 */
	@SuppressWarnings("unchecked")
	public void purgeTag(Integer tagId) throws DAOException {
		Tag tag = getTag(tagId);
		
		// first, we need to delete all references to the tag within Flags
		List<Flag> flags = sessionFactory.getCurrentSession()
				.createQuery("select distinct f from Flag f join f.tags t where t.tagId = :tagId")
				.setParameter("tagId", tagId).getResultList();
		flags.forEach(flag -> {
			flag.removeTag(tag);
			sessionFactory.getCurrentSession().saveOrUpdate(flag);
		});
		
		// then we can delete the tag itself
		sessionFactory.getCurrentSession().delete(tag);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getAllPriorities()
	 */
	@SuppressWarnings("unchecked")
	public List<Priority> getAllPriorities() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from Priority").getResultList();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getPriority(Integer)
	 */
	public Priority getPriority(Integer priorityId) {
		return (Priority) sessionFactory.getCurrentSession().get(Priority.class, priorityId);
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getPriorityByUuid(String)
	 */
	public Priority getPriorityByUuid(String uuid) throws DAOException {
		return (Priority) this.sessionFactory.getCurrentSession().createQuery("from Priority p where p.uuid = :uuid").setParameter("uuid", uuid).getSingleResultOrNull();
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getPriorityByName(String)
	 */
	public Priority getPriorityByName(String name) throws DAOException {
		String hql;
		if (Context.getAdministrationService().isDatabaseStringComparisonCaseSensitive()) {
			hql = "from Priority x where lower(x.name) = lower(:name)";
		} else {
			hql = "from Priority x where x.name = :name";
		}

		@SuppressWarnings("unchecked")
		List<Priority> list = sessionFactory.getCurrentSession().createQuery(hql).setParameter("name", name).getResultList();

		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() == 0) {
			return null;
		} else {
			throw new APIException("Multiple priorities found with the name '" + name + "'");
		}

	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#savePriority(Priority)
	 */
	public void savePriority(Priority priority) throws DAOException {
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(priority);
		}
		catch (Throwable t) {
			throw new DAOException(t);
		}
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#purgePriority(Integer)
	 */
	public void purgePriority(Integer priorityId) throws DAOException {
		Priority priority = getPriority(priorityId);
		sessionFactory.getCurrentSession().delete(priority);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getAllDisplayPoints()
	 */
	@SuppressWarnings("unchecked")
	public List<DisplayPoint> getAllDisplayPoints() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from DisplayPoint").getResultList();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getDisplayPoint(Integer)
	 */
	public DisplayPoint getDisplayPoint(Integer displayPointId) {
		return (DisplayPoint) sessionFactory.getCurrentSession().get(DisplayPoint.class, displayPointId);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getDisplayPoint(String)
	 */
	public DisplayPoint getDisplayPoint(String name) {
		List<?> list = sessionFactory.getCurrentSession()
				.createQuery("from DisplayPoint d where lower(d.name) = lower(:name)").setParameter("name", name).getResultList();
		
		if (list.size() > 0) {
			// note the assumption here is that two displaypoints with the same case-insensitive tags aren't allowed; if there are two, this just returns the first one it finds
			return (DisplayPoint) list.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#saveDisplayPoint(DisplayPoint)
	 */
	public void saveDisplayPoint(DisplayPoint displayPoint) throws DAOException {
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(displayPoint);
		}
		catch (Throwable t) {
			throw new DAOException(t);
		}
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#purgeDisplayPoint(Integer)
	 */
	public void purgeDisplayPoint(Integer displayPointId) throws DAOException {
		DisplayPoint displayPoint = getDisplayPoint(displayPointId);
		sessionFactory.getCurrentSession().delete(displayPoint);
	}

	public boolean isPriorityNameDuplicated(Priority priority) {
		return isNameDuplicated("Priority", "priorityId", priority.getName(), priority.getPriorityId());
	}

	public boolean isFlagNameDuplicated(Flag flag) {
		return isNameDuplicated("Flag", "flagId", flag.getName(), flag.getFlagId());
	}

	private boolean isNameDuplicated(String entityName, String idProperty, String name, Integer id) {
		StringBuilder hql = new StringBuilder("from ").append(entityName).append(" x where x.retired = false");
		if (name != null) {
			hql.append(" and x.name = :name");
		}
		if (id != null) {
			hql.append(" and x.").append(idProperty).append(" <> :id");
		}

		Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
		if (name != null) {
			query.setParameter("name", name);
		}
		if (id != null) {
			query.setParameter("id", id);
		}

		return !query.getResultList().isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Flag> getFlagsForPatient(Patient patient) throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("select pf.flag from PatientFlag pf where pf.patient = :patient")
				.setParameter("patient", patient).getResultList();
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#savePatientFlag(PatientFlag)
	 */
	public void savePatientFlag(PatientFlag patientFlag) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(patientFlag);
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#deletePatientFlagsForPatient(Patient)
	 */
	@Override
	public void deletePatientFlagsForPatient(Patient patient) throws DAOException {
		@SuppressWarnings("unchecked")
		List<PatientFlag> flags = sessionFactory.getCurrentSession()
				.createQuery("from PatientFlag pf where pf.patient = :patient and pf.voided = false")
				.setParameter("patient", patient).getResultList();
		flags.forEach(patientFlag -> {
			sessionFactory.getCurrentSession().delete(patientFlag);
		});
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#deletePatientFlagForPatient(Patient, Flag)
	 */
	@Override
	public void deletePatientFlagForPatient(Patient patient, Flag flag) throws DAOException {
		@SuppressWarnings("unchecked")
		List<PatientFlag> flags = sessionFactory.getCurrentSession()
				.createQuery("from PatientFlag pf where pf.patient = :patient and pf.flag = :flag and pf.voided = false")
				.setParameter("patient", patient).setParameter("flag", flag).getResultList(); //Should return a maximum of one flag
		flags.forEach(patientFlag -> {
			sessionFactory.getCurrentSession().delete(patientFlag);
		});
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#deletePatientFlagsForFlag(Flag)
	 */
	@Override
	public void deletePatientFlagsForFlag(Flag flag) throws DAOException {
		@SuppressWarnings("unchecked")
		List<PatientFlag> flags = sessionFactory.getCurrentSession()
				.createQuery("from PatientFlag pf where pf.flag = :flag and pf.voided = false")
				.setParameter("flag", flag).getResultList();
		flags.forEach(patientFlag -> {
			sessionFactory.getCurrentSession().delete(patientFlag);
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PatientFlag> getPatientFlags(Patient patient) throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from PatientFlag pf where pf.patient = :patient")
				.setParameter("patient", patient).getResultList();
	}


	/**
	 * Delete all patient flags.
	 *
	 * @throws DAOException the dao exception
	 */
	@Override
	public void deleteAllPatientFlags() throws DAOException {
		List<PatientFlag> flags = sessionFactory.getCurrentSession().createQuery("from PatientFlag").getResultList();

		flags.forEach(patientFlag -> {
			sessionFactory.getCurrentSession().delete(patientFlag);
		});
	}

}
