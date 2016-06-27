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
package org.openmrs.module.patientflags.rest.util;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.module.patientflags.rest.wrapper.FlagBean;
import org.openmrs.module.patientflags.rest.wrapper.PatientBean;
import org.openmrs.module.patientflags.Flag;

public class FlagUtil {

	public static PatientBean patientConverter(final Patient patient) {

		final PatientBean patientBean = new PatientBean(patient.getUuid(),
				patient.getGivenName() + "  " + patient.getMiddleName() + " " + patient.getFamilyName());

		patientBean.setId(patient.getId());

		return patientBean;
	}

	public static List<FlagBean> flagsConverter(final List<Flag> flags) {

		final List<FlagBean> flagsBean = new ArrayList<>();

		for (final Flag flag : flags) {
			flagsBean.add(new FlagBean(flag.getName(), flag.getMessage()));
		}

		return flagsBean;
	}
}
