package org.openmrs.module.patientflags.fragment.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.EvaluatedFlag;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class PatientflagsDashboardFragmentController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient) {
		FlagService flagService = Context.getService(FlagService.class);
		User user = Context.getAuthenticatedUser();
		Set<Role> userRoles = user.getRoles();
		
		List<EvaluatedFlag> listAllFlags = flagService.generateFlagsForPatient(patient);
		List<Flag> resultList = new ArrayList<Flag>();
		
		for (EvaluatedFlag flag : listAllFlags) {
			for (Tag tag : flag.getFlag().getTags()) {
				Set<Role> intersection = new HashSet<Role>(tag.getRoles());
				intersection.retainAll(userRoles);
				if (intersection.size() > 0) {
					resultList.add(flag.getFlag());
					continue;
				}
			}
		}
		
		model.addAttribute("flags", resultList);
	}
	
}
