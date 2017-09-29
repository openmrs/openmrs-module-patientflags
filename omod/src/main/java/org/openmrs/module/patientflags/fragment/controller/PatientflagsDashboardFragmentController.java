package org.openmrs.module.patientflags.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

public class PatientflagsDashboardFragmentController {

    protected final Log log = LogFactory.getLog(getClass());

    public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient) {
        List<String> list = new ArrayList<String>();
        list.add("IMC Anormal");
        list.add("IMC Anormal");
        model.addAttribute("flags", list);
    }

}
