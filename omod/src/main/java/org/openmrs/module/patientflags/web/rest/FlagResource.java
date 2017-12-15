package org.openmrs.module.patientflags.web.rest;

import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.web.PatientFlagsRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + PatientFlagsRestController.PATIENT_FLAGS_REST_NAMESPACE + "/flag", supportedClass = Flag.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*" })
public class FlagResource extends MetadataDelegatingCrudResource<Flag>{

	public Flag newDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Flag save(Flag arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flag getByUniqueId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void purge(Flag arg0, RequestContext arg1) throws ResponseException {
		// TODO Auto-generated method stub
		
	}

}
