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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Privilege;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;

/**
 * Underlying thread to handle groovy flag evaluations  (for security reasons)
 */
public class GroovyFlagEvaluatorThread implements Runnable{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private Cohort testCohort;
	
	private Cohort resultCohort;
	
	private Flag flag;
	
	private User user;
	
	private Exception exception; 
	
	public GroovyFlagEvaluatorThread(){
	}
	
	public GroovyFlagEvaluatorThread(Flag flag, Cohort cohort, User user){
		this.flag = flag;
		this.testCohort = cohort;
		this.user = user;
	}
	
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
	
	public synchronized Cohort fetchResultCohort() throws Exception{
		// TODO: change this to processResultCohort!!!
		// assign and then notify, and then wait???
		
		try {
	        wait();
        }
        catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        
        // if the evaluator thread created an exception throw it; other return resultCohort
        Exception e = getException();
        if(e != null){
        	throw e;
        }
        else{
        	return getResultCohort();
        }
	}

	public synchronized void run() {
		
		
		// TODO: should I make this into two try/catches that I handle differently so that non "criteria" issues don't end up in the criteria display
		try{
			Context.openSession();
			setPrivileges();
			
			//Context.addProxyPrivilege("View Global Properties");
			//String username = Context.getAdministrationService().getGlobalProperty("patientflags.username");
			//String password = Context.getAdministrationService().getGlobalProperty("patientflags.password");
			//Context.authenticate(username, password);
		
			// get the script to execute
			String criteria = flag.getCriteria();
		
			// get the set of bindings to use
			Binding bindings = getBindings();
		
			// bind the test Cohort
			bindings.setVariable("testCohort", getTestCohort());
		
			// create a Groovy shell & import org.openmrs.*
			GroovyShell shell = new GroovyShell(bindings);
		
			// TODO: move this processing to "processResultCohort"??
		
			// wait here!!!
		
			setResultCohort((Cohort) shell.parse("import org.openmrs.*;" + criteria).run());
			notify();
		}
		catch (Exception e) {
			// save the exception
			setException(e);
			notify();
		}
    }
	
	//TODO: add a better version of this which is driven by a config file.
	private Binding getBindings() {
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
		return binding;
	}
	
	private void setPrivileges(){
		Collection<Privilege> privileges = Context.getService(FlagService.class).getPrivileges();
		
		if(privileges != null){
				if(!user.isSuperUser()){
					privileges.retainAll(user.getPrivileges());
				}
		
				for(Privilege privilege : privileges){
					// TODO: get rid of this logging
					log.error("Adding privilege " + privilege.getPrivilege());
					Context.addProxyPrivilege(privilege.getPrivilege());	
				}
		}
	}
}