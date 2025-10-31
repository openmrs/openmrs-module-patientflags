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
package org.openmrs.module.patientflags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.Extension;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.patientflags.task.PatientFlagTask;

import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class PatientFlagsModuleActivator extends BaseModuleActivator implements DaemonTokenAware {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public void started() {
		
		// create necessary global properties if they have not been created
		if ((Context.getAdministrationService().getGlobalProperty("patientflags.patientHeaderDisplay")) == null) {
			Context.getAdministrationService()
			        .saveGlobalProperty(
			            new GlobalProperty(
			                    "patientflags.patientHeaderDisplay",
			                    "true",
			                    "DO NOT MODIFY HERE: use \"manage flag global properties\" to modify; true/false whether or not to display flags in the Patient Dashboard overview"));
		}
		
		if ((Context.getAdministrationService().getGlobalProperty("patientflags.patientOverviewDisplay")) == null) {
			Context.getAdministrationService()
			        .saveGlobalProperty(
			            new GlobalProperty(
			                    "patientflags.patientOverviewDisplay",
			                    "true",
			                    "DO NOT MODIFY HERE: use \"manage flag global properties\" to modify; true/false whether or not to display flags in the Patient Dashboard header"));
		}
		
		// if no username has been defined, as a default use the username used by the scheduler
		if ((Context.getAdministrationService().getGlobalProperty("patientflags.username")) == null) {
			Context.getAdministrationService()
			        .saveGlobalProperty(
			            new GlobalProperty(
			                    "patientflags.username",
			                    Context.getAdministrationService().getGlobalProperty("scheduler.username"),
			                    "DO NOT MODIFY HERE: user \"manage flag global properties\" to modify; Username for the OpenMRS user that will evaluate Groovy flags"));
		}
		
		// configure extension points based on global properties
		Module thisModule = ModuleFactory.getModuleByPackage("org.openmrs.module.patientflags");
		
		IdentityHashMap<String, String> extensionPoints = new IdentityHashMap<String, String>();
		
		extensionPoints.put("org.openmrs.admin.list", "org.openmrs.module.patientflags.extension.html.FlagAdminExt");
		
		if ((Context.getAdministrationService().getGlobalProperty("patientflags.patientHeaderDisplay")).equals("true")) {
			extensionPoints.put("org.openmrs.patientDashboard.afterLastEncounter",
			    "org.openmrs.module.patientflags.extension.html.FlagPatientDashboardHeaderExt");
		}
		
		if ((Context.getAdministrationService().getGlobalProperty("patientflags.patientOverviewDisplay")).equals("true")) {
			extensionPoints.put("org.openmrs.patientDashboard.overviewBox",
			    "org.openmrs.module.patientflags.extension.html.FlagPatientOverviewExt");
		}
		
		// set the new names
		try {
			thisModule.setExtensionNames(extensionPoints);
		}
		catch (NoSuchMethodError ex) {
			try {
				Map<String, String> extensionPointsMap = extensionPoints;
				Method method = thisModule.getClass().getMethod("setExtensionNames", Map.class);
				method.invoke(thisModule, extensionPointsMap);
			}
			catch (Exception e) {
				log.error("Failed to setExtensionNames", e);
			}
		}
		
		// this code is copied from  ModuleFactory.startModule();
		// unfortunately it needs to be executed twice since extensions are added to module before activator is executed 
		// (and therefore before the extensions dynamically defined here have been set)
		for (Extension ext : thisModule.getExtensions()) {
			
			String extId = ext.getExtensionId();
			List<Extension> tmpExtensions = ModuleFactory.getExtensions(extId);
			if (tmpExtensions == null)
				tmpExtensions = new Vector<Extension>();
			
			log.debug("Adding to mapping ext: " + ext.getExtensionId() + " ext.class: " + ext.getClass());
			
			tmpExtensions.add(ext);
			ModuleFactory.getExtensionMap().put(extId, tmpExtensions);
		}
		
		log.info("Starting Patient Flags Module");
	}
	
	public void shutdown() {
		
		// need to delete the configured extensions here to assure that they aren't double-loaded on next startup
		Module thisModule = ModuleFactory.getModuleByPackage("org.openmrs.module.patientflags");
		thisModule.setExtensionNames(new IdentityHashMap<String, String>());
		thisModule.setExtensions(new Vector<Extension>());
		
		log.info("Shutting down Patient Flags Module");
	}
	
	@Override
	public void setDaemonToken(DaemonToken token) {
		PatientFlagTask.setDaemonToken(token);
	}
}
