/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.patientflags.rest.resource;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingReadableResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;



@Resource(name = RestConstants.VERSION_1 + "/flags", order = 2, supportedClass = Flag.class, supportedOpenmrsVersions = { "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
// order must be greater than that for PatientResource(order=0) RESTWS-273
public class FlagsResource extends BaseDelegatingReadableResource<Flag> {

	/**
	 * @see org.openmrs.module.patientflags.rest.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.patientflags.rest.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("patient");
			description.addProperty("flags");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("patient");
			description.addProperty("flags");
			description.addSelfLink();
			return description;
		}

		return null;
	}

	@Override
	public Flag newDelegate() {
		return new Flag();
	}

	public List<Flag> getByUniqueId(final String uniqueId) {
		final Patient patient = Context.getPatientService().getPatientByUuid(uniqueId);
		return Context.getService(FlagService.class).generateFlagsForPatient(patient);
	}

	private List<Flag> getFlag(final String uniqueId) {
		final Patient patient = Context.getPatientService().getPatientByUuid(uniqueId);
		final FlagService flagService = Context.getService(FlagService.class);

		return flagService.generateFlagsForPatient(patient);
	}
}
