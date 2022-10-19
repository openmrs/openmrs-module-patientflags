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

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Flag;
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
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Flag.class);
		
		return (List<Flag>) criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Flag> getAllEnabledFlags() throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Flag.class);
		criteria.add(Restrictions.eq("enabled", true));
		
		return (List<Flag>) criteria.list();
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
		return (Flag)this.sessionFactory.getCurrentSession().createQuery("from Flag f where f.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getFlagByName(String)
	 */
	public Flag getFlagByName(String name) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Flag.class);
		if (Context.getAdministrationService().isDatabaseStringComparisonCaseSensitive()) {
			criteria.add(Restrictions.ilike("name", name));
		} else {
			criteria.add(Restrictions.eq("name", name));
		}

		List<Flag> list = criteria.list();

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
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Tag.class);
		return (List<Tag>) criteria.list();
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
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Tag.class);
		if (Context.getAdministrationService().isDatabaseStringComparisonCaseSensitive()) {
			criteria.add(Restrictions.ilike("name", name));
		} else {
			criteria.add(Restrictions.eq("name", name));
		}

		List<Tag> list = criteria.list();

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
		return (Tag)this.sessionFactory.getCurrentSession().createQuery("from Tag t where t.uuid = :uuid").setString("uuid", uuid).uniqueResult();
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
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Flag.class).createCriteria("tags");
		criteria.add(Restrictions.eq("tagId", tagId));
		List<Flag> flags = criteria.list();
		for (Flag flag : flags) {
			flag.removeTag(tag);
			sessionFactory.getCurrentSession().saveOrUpdate(flag);
		}
		
		// then we can delete the tag itself
		sessionFactory.getCurrentSession().delete(tag);
	}
	
	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getAllPriorities()
	 */
	@SuppressWarnings("unchecked")
	public List<Priority> getAllPriorities() throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Priority.class);
		return (List<Priority>) criteria.list();
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
		return (Priority) this.sessionFactory.getCurrentSession().createQuery("from Priority p where p.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see org.openmrs.module.patientflags.db.FlagDAO#getPriorityByName(String)
	 */
	public Priority getPriorityByName(String name) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Priority.class);
		if (Context.getAdministrationService().isDatabaseStringComparisonCaseSensitive()) {
			criteria.add(Restrictions.ilike("name", name));
		} else {
			criteria.add(Restrictions.eq("name", name));
		}

		List<Priority> list = criteria.list();

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
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DisplayPoint.class);
		return (List<DisplayPoint>) criteria.list();
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
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DisplayPoint.class);
		criteria.add(Restrictions.ilike("name", name, MatchMode.EXACT));
		
		if (criteria.list().size() > 0) {
			// note the assumption here is that two displaypoints with the same case-insensitive tags aren't allowed; if there are two, this just returns the first one it finds
			return (DisplayPoint) criteria.list().get(0);
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
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Priority.class);
		addEqualsRestriction(criteria, "name", priority.getName());
		addNotEqualsRestriction(criteria, "priorityId", priority.getPriorityId());
		addEqualsRestriction(criteria, "retired", false);

		return criteria.uniqueResult() != null;
	}

	public boolean isFlagNameDuplicated(Flag flag) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Flag.class);
		addEqualsRestriction(criteria, "name", flag.getName());
		addNotEqualsRestriction(criteria, "flagId", flag.getFlagId());
		addEqualsRestriction(criteria, "retired", false);

		return criteria.uniqueResult() != null;
	}

	private void addEqualsRestriction(Criteria criteria, String propertyName, Object value) {
		if (value != null) {
			criteria.add(Restrictions.eq(propertyName, value));
		}
	}

	private void addNotEqualsRestriction(Criteria criteria, String propertyName, Object value) {
		if (value != null) {
			criteria.add(Restrictions.not(Restrictions.eq(propertyName, value)));
		}
	}
}
