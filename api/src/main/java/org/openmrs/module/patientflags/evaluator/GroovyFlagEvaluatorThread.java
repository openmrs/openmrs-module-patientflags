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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Collection;

import org.openmrs.Cohort;
import org.openmrs.Privilege;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;

/**
 * Underlying thread to handle groovy flag evaluations  (for security reasons)
 */
public class GroovyFlagEvaluatorThread implements Runnable{
	
	/* the cohort to test */
	private Cohort testCohort;
	
	/* the result cohort */
	private Cohort resultCohort;
	
	/* the flag to test */
	private Flag flag;
	
	/* only allow groovy script privileges associated with this user */
	private User user;
	
	/* stores any exception throw during execution */
	private Exception exception; 
	
	/**
	 * Constructors
	 */
	
	public GroovyFlagEvaluatorThread(){
	}
	
	public GroovyFlagEvaluatorThread(Flag flag, Cohort cohort, User user){
		this.flag = flag;
		this.testCohort = cohort;
		this.user = user;
	}
	
	/**
	 * Getters and Setters
	 */
	
	public void setTestCohort(Cohort testCohort) {
	    this.testCohort = testCohort;
    }

	public Cohort getTestCohort() {
	    return testCohort;
    }

	public void setResultCohort(Cohort resultCohort) {
	    this.resultCohort = resultCohort;
    }

	public Cohort getResultCohort() {
	    return resultCohort;
    }

	public void setFlag(Flag flag) {
	    this.flag = flag;
    }

	public Flag getFlag() {
	    return flag;
    }
	
	public void setUser(User user) {
	    this.user = user;
    }

	public User getUser() {
	    return user;
    }

	public void setException(Exception exception) {
	    this.exception = exception;
    }

	public Exception getException() {
	    return exception;
    }
	
	/**
	 * Public Methods
	 */
	
	public synchronized Cohort fetchResultCohort() throws Exception{
		// wait for the evaluator thread to alert notify that it's done evaluating
	    wait();
        
        // if the evaluator thread created an exception, throw it; otherwise, return resultCohort
        Exception e = getException();
        if(e != null){
        	throw e;
        }
        else{
        	return getResultCohort();
        }
	}

	public synchronized void run() {
		try{
			// open a new session and set the privileges that should be allowed
			Context.openSession();
			setPrivileges();
		
			// get the script to execute
			String criteria = flag.getCriteria();
		
			// get the set of bindings to use
			Binding bindings = getBindings();
		
			// bind the test Cohort
			bindings.setVariable("testCohort", getTestCohort());
		
			// create a Groovy shell and execute the criteria, storing the result in the resultCohort
			GroovyShell shell = new GroovyShell(bindings);
			setResultCohort((Cohort) shell.parse("import org.openmrs.*;" + criteria).run());
	
		}
		catch (Exception e) {
			// save the exception and set the result to null
			setException(e);
			setResultCohort(null);
		}
		finally {
			// notify the main thread that execution is complete
			notify();
		}
    }
	
	//TODO: add a better version of this which is driven by a config file.
    private static Binding getBindings() {
		final Binding binding = new Binding();
		binding.setVariable("admin", Context.getAdministrationService());
		binding.setVariable("cohort", Context.getCohortService());
		binding.setVariable("concept", Context.getConceptService());
		binding.setVariable("encounter", Context.getEncounterService());
		binding.setVariable("form", Context.getFormService());
		binding.setVariable("locale", Context.getLocale());
		binding.setVariable("logic", Context.getLogicService());
		binding.setVariable("obs", Context.getObsService());
		binding.setVariable("order", Context.getOrderService());
		binding.setVariable("patient", Context.getPatientService());
		binding.setVariable("patientSet", Context.getPatientSetService());
		binding.setVariable("person", Context.getPersonService());
		binding.setVariable("program", Context.getProgramWorkflowService());
		binding.setVariable("user", Context.getUserService());
		
		// TODO: add bindings for more dynamic services besides MDR-TB
		if(ModuleFactory.getStartedModulesMap().containsKey("mdrtb")) {
			try {
	            Class<?> mdrtbServiceClass = Context.loadClass("org.openmrs.module.mdrtb.service.MdrtbService");
	            binding.setVariable("mdrtb", Context.getService(mdrtbServiceClass));
            }
            catch (ClassNotFoundException e) {
	            // do nothing if the class isn't found
            }
		}
		
		return binding;
	}
	
	private void setPrivileges(){
		// fetch the privileges allowed from the privilege cache
		Collection<Privilege> privileges = Context.getService(FlagService.class).getPrivileges();
		
		if(privileges != null){
			// if a user is specified, further restrict the privileges allowed to the intersection of the cache privileges
			// and the user privileges
			if(user != null && !user.isSuperUser()){
				privileges.retainAll(user.getPrivileges());
			}
	
			// add the privileges by proxy
			for(Privilege privilege : privileges){
				Context.addProxyPrivilege(privilege.getPrivilege());	
			}
		}
	}
}