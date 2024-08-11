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
package org.openmrs.module.patientflags.task;

import java.util.HashMap;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patientflags.api.FlagService;

public class PatientFlagTask implements Runnable {

	private static DaemonToken daemonToken;
	
	private Patient patient;
	
	private Flag flag;
	
	@Override
	public void run() {
		FlagService flagService = Context.getService(FlagService.class);
		
		if (patient != null) {
			generatePatientFlags(patient, flagService);
		}
		else if (flag != null) {
			generatePatientFlags(flag, flagService);
		}
		else {
			evaluateAllFlags();
		}
	}

	public void evaluateAllFlags() {
		Daemon.runInDaemonThread(new AllFlagsEvaluator(), daemonToken);
	}

	public static void setDaemonToken(DaemonToken token) {
		daemonToken = token;
	}
	
	public void generatePatientFlags(Patient patient) {
		this.patient = patient;
		
		if (daemonToken != null) {
			Daemon.runInDaemonThread(this, daemonToken);
		}
	}
	
	public void generatePatientFlags(Flag flag) {
		this.flag = flag;
		
		if (daemonToken != null) {
			Daemon.runInDaemonThread(this, daemonToken);
		}
	}

	private static void generatePatientFlags(Flag flag, FlagService service) {
		service.deletePatientFlagsForFlag(flag);

		if (!flag.getEnabled() || flag.isRetired()) {
			return;
		}
		
		HashMap<Object, Object> context = new HashMap<Object, Object>();
		org.openmrs.Cohort cohort = service.getFlaggedPatients(flag, context);
		if (cohort == null) {
			return;
		}
		
		java.util.Set<Integer> members =  cohort.getMemberIds();
		for (Integer patientId : members) {
			List<String> flgs = (List<String>)context.get(patientId);
			if (flgs != null) {
				for (String flg : flgs) {
					service.savePatientFlag(new PatientFlag(new Patient(patientId), flag, flg));
				}
			}
			else {
				service.savePatientFlag(new PatientFlag(new Patient(patientId), flag, flag.evalMessage(patientId)));
			}
		}
	}
	
	private void generatePatientFlags(Patient patient, FlagService service) {
		service.deletePatientFlagsForPatient(patient);
		
		HashMap<Object, Object> context = new HashMap<Object, Object>();
		List<Flag> flags = service.generateFlagsForPatient(patient, context);
		for (Flag flag : flags) {
			List<String> flgs = (List<String>)context.get(patient.getPatientId());
			if (flgs != null) {
				for (String flg : flgs) {
					service.savePatientFlag(new PatientFlag(patient, flag, flg));
				}
			}
			else {
				service.savePatientFlag(new PatientFlag(patient, flag, flag.evalMessage(patient.getPatientId())));
			}
		}
	}
	
	//The only reason why we have this class is to be able to run in
	//a daemon thread in order to get daemon access to the database
	private static class AllFlagsEvaluator implements Runnable {

		@Override
		public void run() {
			FlagService flagService = Context.getService(FlagService.class);
			flagService.getAllFlags().forEach(flag -> Daemon.runInNewDaemonThread(new PatientFlagGenerator(flag)));
		}
	}

	private static class PatientFlagGenerator implements  Runnable {
		private final Flag flag;

		PatientFlagGenerator(Flag flag){
			this.flag = flag;
		}

		@Override
		public void run() {
			FlagService service = Context.getService(FlagService.class);
			generatePatientFlags(flag, service);
		}
	}
}
