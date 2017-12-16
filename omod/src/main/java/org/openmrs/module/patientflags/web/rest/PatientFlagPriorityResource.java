package org.openmrs.module.patientflags.web.rest;

import static org.openmrs.module.patientflags.util.WebUtils.getStringFilter;

import java.util.Arrays;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.patientflags.web.PatientFlagsRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + PatientFlagsRestController.PATIENT_FLAGS_REST_NAMESPACE + "/priority", supportedClass = Priority.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*" })
public class PatientFlagPriorityResource extends MetadataDelegatingCrudResource<Priority>{
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("name");
			description.addProperty("style");
			description.addProperty("rank");
			
			if (rep instanceof DefaultRepresentation) {
				description.addSelfLink();
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			} 
			else if (rep instanceof FullRepresentation) {
				description.addProperty("auditInfo");
				description.addSelfLink();
				return description;
			}
		}
		
		return description;
	}

	public Priority newDelegate() {
		return new Priority();
	}

	public Priority save(Priority priority) {
		Context.getService(FlagService.class).savePriority(priority);
		return Context.getService(FlagService.class).getPriorityByName(priority.getName());
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription cp = super.getCreatableProperties();
		cp.addRequiredProperty("name");
		cp.addProperty("style");
		cp.addRequiredProperty("rank");
		
		return cp;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription cp = super.getUpdatableProperties();
		cp.addRequiredProperty("name");
		cp.addProperty("style");
		cp.addRequiredProperty("rank");
		
		return cp;
	}

	@Override
	public Priority getByUniqueId(String priorityId) {
		Priority priority = Context.getService(FlagService.class).getPriorityByUuid(priorityId);
		if(priority == null) {
			priority = Context.getService(FlagService.class).getPriorityByName(priorityId);
		}
		return priority;
	}
	
	@Override
	public void purge(Priority priority, RequestContext request) throws ResponseException {
		//TODO test it
		Context.getService(FlagService.class).purgePriority(priority.getPriorityId());
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String q = getStringFilter("q", context);
		return new NeedsPaging<Priority>(Arrays.asList(Context.getService(FlagService.class).getPriorityByName(q)), context);
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<Priority>(Context.getService(FlagService.class).getAllPriorities(), context);
	}

}
