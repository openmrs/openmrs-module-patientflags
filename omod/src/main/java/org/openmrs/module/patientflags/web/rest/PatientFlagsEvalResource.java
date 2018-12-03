package org.openmrs.module.patientflags.web.rest;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.EvaluatedFlag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.util.PatientFlags;
import org.openmrs.module.patientflags.web.PatientFlagsRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + PatientFlagsRestController.PATIENT_FLAGS_REST_NAMESPACE + "/eval", supportedClass = PatientFlags.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.2.*" })
public class PatientFlagsEvalResource extends DataDelegatingCrudResource<PatientFlags> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("flags");
			description.addProperty("identifier");
			
			if (rep instanceof DefaultRepresentation) {
				description.addSelfLink();
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			} else if (rep instanceof FullRepresentation) {
				description.addProperty("auditInfo");
				description.addSelfLink();
				return description;
			}
		}
		
		return description;
	}
	
	public PatientFlags save(PatientFlags priority) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public PatientFlags getByUniqueId(String uniqueId) {
		Patient patient = Context.getPatientService().getPatientByUuid(uniqueId);
		List<EvaluatedFlag> flags = Context.getService(FlagService.class).generateFlagsForPatient(patient);
		return new PatientFlags(patient, flags);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		/*flag = ;
		patient = ;
		tags = ;
		enabled = ;*/
		
		// TODO Auto-generated method stub
		return super.doSearch(context);
	}
	
	public PatientFlags newDelegate() {
		return null;
	}
	
	@Override
	protected void delete(PatientFlags delegate, String reason, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void purge(PatientFlags delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException();
	}
	
	@PropertyGetter("display")
	public String getDisplay(PatientFlags data) {
		return data.getPatientIdentifier().getIdentifier() + ":" + data.getPersonName().getFullName();
	}
	
	@PropertyGetter("identifier")
	public PatientIdentifier getIdentifier(PatientFlags data) {
		return data.getPatientIdentifier();
	}
	
	@PropertyGetter("personName")
	public PersonName getPersonName(PatientFlags data) {
		return data.getPersonName();
	}
	
}
