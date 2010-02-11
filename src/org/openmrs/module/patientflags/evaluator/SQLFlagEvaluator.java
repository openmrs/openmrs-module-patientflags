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
package org.openmrs.module.patientflags.evaluator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.FlagValidationResult;

/**
 * A FlagEvaluator that takes SQL statement as it's criteria. The SQL statement must contain the
 * string "*.patient_id" so that the evaluator knows how to modify the SQL statement to operate on a
 * single patient when required.
 */
public class SQLFlagEvaluator implements FlagEvaluator {
	
	/**
	 * @see org.openmrs.module.patientflags.evaluator.FlagEvaluator#eval(Flag, Patient)
	 */
	public Boolean eval(Flag flag, Patient patient) {
		
		String criteria = flag.getCriteria();
		
		// pull out the "*.patient_id" clause
		// is this robust enough?
		Matcher matcher = Pattern.compile("(\\w+\\.patient_id)").matcher(criteria);
		matcher.find(); // just check for the first occurrence of the pattern... is this enough?
		String patientIdColumn = matcher.group();
			
		// since we are going to append a where/and to the end of this sql statement, we need to trim off trailing ";" and any trailing whitespace
		matcher = Pattern.compile(";?\\s*$").matcher(criteria);
		criteria = matcher.replaceFirst(""); // replace first, because there should only be one occurrence
			
		// create the criteria for a single patient by appending a "where" or "and" clause
		String toEval = criteria + (criteria.matches(".*(?i)where.*") ? " and " : " where ") + patientIdColumn + " = "
			+ patient.getPatientId();
		
		try {
			Context.addProxyPrivilege("SQL Level Access");
			List<List<Object>> resultSet = Context.getAdministrationService().executeSQL(toEval, true);
			// if the list is empty, return false, otherwise, return true
			return !resultSet.isEmpty();
		}
		catch (Exception e) {
			throw new APIException("Unable to evaluate SQL Flag " + flag.getName() + ", " + e.getLocalizedMessage(), e);
		}
		finally{
			Context.removeProxyPrivilege("SQL Level Access");
		}
	}
	
	/**
	 * @see org.openmrs.module.patientflags.evaluator.FlagEvaluator#eval(Flag, Cohort)
	 */
	public Cohort evalCohort(Flag flag, Cohort cohort) {
		
		List<List<Object>> resultSet;
		
		try {
			Context.addProxyPrivilege("SQL Level Access");
			resultSet = Context.getAdministrationService().executeSQL(flag.getCriteria(), true);
		}
		catch (Exception e) {
			throw new APIException("Unable to evaluate SQL Flag " + flag.getName() + ", " + e.getLocalizedMessage(), e);
		}
		finally{
			Context.removeProxyPrivilege("SQL Level Access");
		}
		
		Cohort resultCohort = new Cohort();
		
		for (List<Object> row : resultSet) {
			resultCohort.addMember((Integer) row.get(0));
		}
		
		if (cohort != null) {
			resultCohort = Cohort.intersect(resultCohort, cohort);
		}
		
		return resultCohort;
	}
	
	/**
	 * @see org.openmrs.module.patientflags.evaluator.FlagEvaluator#validate(Flag)
	 */
	public FlagValidationResult validate(Flag flag) {
		
		String criteria = flag.getCriteria();
		
		// if the *.patient_id pattern cannot be found, reject the criteria
		Matcher matcher = Pattern.compile("(\\w+\\.patient_id)").matcher(criteria);
		if (!matcher.find())
			return new FlagValidationResult(false, "patientflags.errors.noPatientIdCriteria");
		
		// try to execute the query, if it throws an exception, fail
		try {
			// note that unlike the two eval methods, we don't proxy SQL Level Access privilege here
			// because we don't want users without SQL Level Access to be able to create SQL Flags
			Context.getAdministrationService().executeSQL(criteria, true);
		}
		catch (Exception e) {
			return new FlagValidationResult(false, e.getLocalizedMessage());
		}
		
		// if we've gotten this far, mark the criteria as valid
		return new FlagValidationResult(true);
	}
}
