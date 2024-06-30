package org.openmrs.module.patientflags.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatientflagsDashboardFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient) {

        model.addAttribute("patientId", patient.getPatientId());
    }

    public SimpleObject processPatientFlags(@FragmentParam("patientId") Patient patient) {
        FlagService flagService = Context.getService(FlagService.class);
        User user = Context.getAuthenticatedUser();
        Set<Role> userRoles = user.getRoles();
        StringBuilder flagsString = new StringBuilder();
        
        List<PatientFlag> patientFlags = flagService.getPatientFlags(patient);
        
        for (PatientFlag patientFlag : patientFlags) {
        	Flag flag = patientFlag.getFlag();
            for (Tag tag : flag.getTags()) {
                Set<Role> intersection = new HashSet<Role>(tag.getRoles());
                intersection.retainAll(userRoles);
                if (intersection.size() > 0) {
                	flag.setMessage(patientFlag.getMessage());
                    flagsString.append("<li><span ").append(flag.getPriority().getStyle()).append(">").append(flag.evalMessage(patient.getPatientId())).append("</span></li>");
                    continue;
                }
            }
        }

        SimpleObject simpleObject = new SimpleObject();

        simpleObject.put("patientflags", flagsString);

        return simpleObject;
    }
}
