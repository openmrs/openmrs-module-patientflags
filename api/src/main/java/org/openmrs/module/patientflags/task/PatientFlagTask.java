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

	
	private void generatePatientFlags(Patient patient, FlagService service) {
		service.deletePatientFlagsForPatient(patient);
		
		List<Flag> flags = service.generateFlagsForPatient(patient);
		for (Flag flag : flags) {
			service.savePatientFlag(new PatientFlag(patient, flag));
		}
	}
	
	private void generatePatientFlags(Flag flag, FlagService service) {
		org.openmrs.Cohort cohort = service.getFlaggedPatients(flag);
		if (cohort == null) {
			return;
		}
		
		java.util.Set<Integer> members =  cohort.getMemberIds();
		for (Integer patientId : members) {
			Patient pt = new Patient(patientId);
			
			service.deletePatientFlagsForPatient(pt);
			
			service.savePatientFlag(new PatientFlag(pt, flag));
		}
	}
}
