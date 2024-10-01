package org.openmrs.module.patientflags.web.rest.resources;

import static org.openmrs.module.patientflags.web.rest.util.WebUtils.getBooleanFilter;
import static org.openmrs.module.patientflags.web.rest.util.WebUtils.getStringFilter;

import java.util.Arrays;
import java.util.List;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
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
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + PatientFlagsRestController.PATIENT_FLAGS_REST_NAMESPACE + "/flag", supportedClass = Flag.class, supportedOpenmrsVersions = {
        "1.*", "2.*" })
public class PatientFlagFlagResource extends MetadataDelegatingCrudResource<Flag> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("criteria");
			description.addProperty("evaluator");
			description.addProperty("message");
			description.addProperty("priority");
			description.addProperty("enabled");
			description.addProperty("tags");
			
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
	
	public Flag newDelegate() {
		return new Flag();
	}
	
	public Flag save(Flag flag) {
		Context.getService(FlagService.class).saveFlag(flag);
		return Context.getService(FlagService.class).getFlagByName(flag.getName());
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription cp = super.getCreatableProperties();
		cp.addRequiredProperty("name");
		cp.addRequiredProperty("criteria");
		cp.addRequiredProperty("evaluator");
		cp.addRequiredProperty("message");
		cp.addProperty("priority");
		cp.addProperty("enabled");
		cp.addProperty("tags");
		
		return cp;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription cp = super.getUpdatableProperties();
		cp.addRequiredProperty("name");
		cp.addRequiredProperty("criteria");
		cp.addRequiredProperty("evaluator");
		cp.addRequiredProperty("message");
		cp.addProperty("priority");
		cp.addProperty("enabled");
		cp.addProperty("tags");
		
		return cp;
	}

	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getGETModel(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model.property("uuid", new StringProperty());
			model.property("name", new StringProperty());
			model.property("criteria", new StringProperty());
			model.property("evaluator", new StringProperty());
			model.property("message", new StringProperty());
			model.property("priority", new RefProperty("#/definitions/PatientflagsPriorityGet"));
			model.property("enabled", new BooleanProperty());
			model.property("tags", new RefProperty("#/definitions/PatientflagsTagGet"));
			model.property("auditInfo", new StringProperty());
		}
		return model;
	}

	@Override
	public Model getCREATEModel(Representation rep) {
		ModelImpl model = (ModelImpl) super.getCREATEModel(rep);
		model.property("name", new StringProperty());
		model.property("criteria", new StringProperty());
		model.property("evaluator", new StringProperty());
		model.property("message", new StringProperty());
		model.property("priority", new RefProperty("#/definitions/PatientflagsPriorityCreate"));
		model.property("enabled", new BooleanProperty());
		model.property("tags", new RefProperty("#/definitions/PatientflagsTagCreate"))
				.required("name").required("criteria").required("evaluator").required("message");
        return model;
    }

	@Override
	public Model getUPDATEModel(Representation rep) {
		return getCREATEModel(rep);
	}

	@Override
	public Flag getByUniqueId(String flagId) {
		Flag flag = Context.getService(FlagService.class).getFlagByUuid(flagId);
		if (flag == null) {
			flag = Context.getService(FlagService.class).getFlagByName(flagId);
		}
		return flag;
	}

	@Override
	public void purge(Flag flag, RequestContext request) throws ResponseException {
		//TODO test it
		Context.getService(FlagService.class).purgeFlag(flag.getFlagId());
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getParameter("patient");
		if (StringUtils.isNotBlank(patientUuid)) {
			Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
			if( patient != null) {
				return new NeedsPaging<Flag>(Context.getService(FlagService.class).getFlagsForPatient(patient), context);
			}
			return new EmptySearchResult();
		} else {
			String q = getStringFilter("q", context);
			String evaluator = getStringFilter("evaluator", context);
			Boolean enabled = getBooleanFilter("enabled", context);
			String tagsStr = getStringFilter("tags", context);

			List<String> tags = null;

			if (StringUtils.isNotBlank(tagsStr)) {
				tags = Arrays.asList(tagsStr.split(","));
			}
			return new NeedsPaging<Flag>(Context.getService(FlagService.class).searchFlags(q, evaluator, enabled, tags), context);
		}
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<Flag>(Context.getService(FlagService.class).getAllFlags(), context);
	}
	
}
