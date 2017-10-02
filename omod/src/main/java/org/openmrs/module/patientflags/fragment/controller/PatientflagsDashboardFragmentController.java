package org.openmrs.module.patientflags.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

public class PatientflagsDashboardFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient) {
        FlagService flagService = Context.getService(FlagService.class);
        List<Flag> list = flagService.generateFlagsForPatient(patient);
        model.addAttribute("flags", list);
    }

}
