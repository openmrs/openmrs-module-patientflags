package org.openmrs.module.patientflags.web.rest.resources;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.web.PatientFlagsRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + PatientFlagsRestController.PATIENT_FLAGS_REST_NAMESPACE + "/patientflag", supportedClass = PatientFlag.class, supportedOpenmrsVersions = {
        "1.*", "2.*" })
public class PatientFlagResource extends DataDelegatingCrudResource<PatientFlag> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("message");
			description.addProperty("patient", Representation.REF);
			description.addProperty("flag", Representation.REF);
			description.addProperty("tags", Representation.REF);
			description.addProperty("voided");
			
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

	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription cp = super.getCreatableProperties();
		cp.addProperty("patient");
		cp.addProperty("flag");
		cp.addProperty("message");
        return cp;
    }

	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		return getCreatableProperties();
	}

	@Override
	public PatientFlag newDelegate() {
		return new PatientFlag();
	}

	@Override
	public PatientFlag getByUniqueId(String uniqueId) {
		return Context.getService(FlagService.class).getPatientFlagByUuid(uniqueId);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getParameter("patient");
		if (StringUtils.isNotBlank(patientUuid)) {
			Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
			if( patient != null) {
				return new NeedsPaging<PatientFlag>(Context.getService(FlagService.class).getPatientFlags(patient), context);
			}
		} 
		
		return new EmptySearchResult();
	}

	@Override
	public PatientFlag save(PatientFlag delegate) {
		Context.getService(FlagService.class).savePatientFlag(delegate);
		return delegate;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(PatientFlag delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.getVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getService(FlagService.class).voidPatientFlag(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#undelete(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected PatientFlag undelete(PatientFlag delegate, RequestContext context) throws ResponseException {
		if (delegate.getVoided()) {
			Context.getService(FlagService.class).unvoidPatientFlag(delegate);
		}
		return delegate;
	}

	@Override
	public void purge(PatientFlag delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("PatientFlag cannot be purged");
	}
	
	@PropertyGetter("display")
	public String getDisplay(PatientFlag instance) {
		return instance.getMessage();
	}
}