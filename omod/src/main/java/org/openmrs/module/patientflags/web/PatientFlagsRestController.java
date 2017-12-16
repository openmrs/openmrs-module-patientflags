package org.openmrs.module.patientflags.web;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + PatientFlagsRestController.PATIENT_FLAGS_REST_NAMESPACE)
public class PatientFlagsRestController extends MainResourceController {
	
	public static final String PATIENT_FLAGS_REST_NAMESPACE = "/patientflags";
	
	@Override
	public String getNamespace() {
		return RestConstants.VERSION_1 + PATIENT_FLAGS_REST_NAMESPACE;
	}
	
}
