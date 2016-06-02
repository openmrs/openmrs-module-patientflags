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
package org.openmrs.module.patientflag_rest.web.resource;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientflag_rest.util.FlagUtil;
import org.openmrs.module.patientflag_rest.web.wrapper.FlagWrapper;
import org.openmrs.module.patientflags.api.FlagService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingReadableResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * {@link Resource} for Patient Flag
 *
 * @author Stélio Moiane
 *
 */
@Resource(name = RestConstants.VERSION_1
		+ "/flags", order = 2, supportedClass = FlagWrapper.class, supportedOpenmrsVersions = { "1.8.*", "1.9.*",
				"1.10.*", "1.11.*", "1.12.*" })
// order must be greater than that for PatientResource(order=0) RESTWS-273
public class FlagsResource extends BaseDelegatingReadableResource<FlagWrapper> {

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
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
	public FlagWrapper newDelegate() {
		return null;
	}

	@Override
	public FlagWrapper getByUniqueId(final String uniqueId) {

		return this.getFlagWrapper(uniqueId);
	}

	private FlagWrapper getFlagWrapper(final String uniqueId) {
		final Patient patient = Context.getPatientService().getPatientByUuid(uniqueId);
		final FlagService flagService = Context.getService(FlagService.class);

		final List<org.openmrs.module.patientflags.Flag> flagsForPatient = flagService.generateFlagsForPatient(patient);
		final FlagWrapper flagWrapper = new FlagWrapper(FlagUtil.flagsConverter(flagsForPatient));

		flagWrapper.setPatient(FlagUtil.patientConverter(patient));
		return flagWrapper;
	}
}
