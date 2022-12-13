package org.openmrs.module.patientflags.web.rest.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.DisplayPoint;
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

@Resource(name = RestConstants.VERSION_1 + PatientFlagsRestController.PATIENT_FLAGS_REST_NAMESPACE + "/displaypoint", supportedClass = DisplayPoint.class, supportedOpenmrsVersions = {
		"1.*", "2.*" })
public class PatientFlagDisplayPointResource extends MetadataDelegatingCrudResource<DisplayPoint> {
	
	public DisplayPoint newDelegate() {
		return new DisplayPoint();
	}
	
	public DisplayPoint save(DisplayPoint displayPoint) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public DisplayPoint getByUniqueId(String displayPointId) {
		DisplayPoint displayPoint = getService().getDisplayPointByUuid(displayPointId);
		if (displayPoint == null)
			displayPoint = getService().getDisplayPoint(displayPointId);
		return displayPoint;
	}
	
	@Override
	public void purge(DisplayPoint displayPoint, RequestContext arg1) throws ResponseException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		
		// NOTE: description for ref representation is provided by MetaDataDelegatingCrudResource
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			
			// metadata
			description.addProperty("uuid");
			description.addProperty("displayPointId");
			description.addProperty("name");
			
			// links
			description.addSelfLink();
			if (rep instanceof DefaultRepresentation) {
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			} else {
				// Relies on a getter method annotated with @PropertyGetter
				description.addProperty("auditInfo");
			}
		}
		
		return description;
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<DisplayPoint>(getService().getAllDisplayPoints(), context);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String q = getStringFilter("q", context);
		
		List<DisplayPoint> displayPoints = new ArrayList<DisplayPoint>();
		
		displayPoints.add(getService().getDisplayPoint(q));
		return new NeedsPaging<DisplayPoint>(displayPoints, context);
	}
	
	private FlagService getService() {
		return Context.getService(FlagService.class);
	}
	
	private static String getStringFilter(String param, RequestContext req) {
		return StringUtils.isEmpty(req.getParameter(param)) ? null : req.getParameter(param);
	}
}
