package org.openmrs.module.patientflags.web.rest.resources;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.DisplayPoint;
import org.openmrs.module.patientflags.Tag;
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

@Resource(name = RestConstants.VERSION_1 + PatientFlagsRestController.PATIENT_FLAGS_REST_NAMESPACE + "/tag", supportedClass = Tag.class, supportedOpenmrsVersions = {
		"1.*", "2.*" })
public class PatientFlagTagResource extends MetadataDelegatingCrudResource<Tag> {
	
	public Tag newDelegate() {
		return new Tag();
	}
	
	public Tag save(Tag tag) {
		getService().saveTag(tag);
		return getService().getTagByName(tag.getName());
	}
	
	@Override
	public Tag getByUniqueId(String tagId) {
		Tag tag = getService().getTagByUuid(tagId);
		if (tag == null)
			tag = getService().getTagByName(tagId);
		return tag;
	}
	
	@Override
	public void purge(Tag tag, RequestContext arg1) throws ResponseException {
		getService().purgeTag(tag.getTagId());
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription cp = super.getUpdatableProperties();
		cp.addRequiredProperty("name");
		cp.addRequiredProperty("roles");
		cp.addRequiredProperty("displayPoints");
		
		return cp;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription cp = super.getCreatableProperties();
		cp.addRequiredProperty("name");
		cp.addRequiredProperty("roles");
		cp.addRequiredProperty("displayPoints");
		
		return cp;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		return new ObjectSchema()
				.addProperty("name", new StringSchema())
				.addProperty("roles", new ArraySchema().items(new Schema<Role>().$ref("#/components/schemas/RoleCreate")))
				.addProperty("displayPoints", new ArraySchema().items(new Schema<DisplayPoint>().$ref("#/components/schemas/PatientflagsDisplaypointCreate")));
	}

	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return getCREATESchema(rep);
	}

	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> model = super.getGETSchema(rep);
		return model
				.addProperty("tagId", new IntegerSchema())
				.addProperty("name", new StringSchema())
				.addProperty("roles", new ArraySchema().items(new Schema<Role>().$ref("#/components/schemas/RoleGet")))
				.addProperty("displayPoints", new ArraySchema().items(new Schema<DisplayPoint>().$ref("#/components/schemas/PatientflagsDisplaypointGet")))
				.addProperty("auditInfo", new StringSchema());
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		
		// NOTE: description for ref representation is provided by MetaDataDelegatingCrudResource
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			
			// metadata
			description.addProperty("tagId");
			description.addProperty("name");
			description.addProperty("roles");
			description.addProperty("displayPoints");
			
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
		return new NeedsPaging<Tag>(getService().getAllTags(), context);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String q = getStringFilter("q", context);
		
		List<Tag> tags = new ArrayList<Tag>();
		
		tags.add(getService().getTag(q));
		return new NeedsPaging<Tag>(tags, context);
	}
	
	private FlagService getService() {
		return Context.getService(FlagService.class);
	}
	
	private static String getStringFilter(String param, RequestContext req) {
		return StringUtils.isEmpty(req.getParameter(param)) ? null : req.getParameter(param);
	}
	
}
