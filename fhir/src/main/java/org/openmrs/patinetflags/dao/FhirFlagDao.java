package org.openmrs.patinetflags.dao;

import org.openmrs.module.fhir2.api.dao.FhirDao;
import org.openmrs.module.patientflags.PatientFlag;

import javax.annotation.Nonnull;

public interface FhirFlagDao extends FhirDao<PatientFlag> {

    @Override
    PatientFlag get(@Nonnull String uuid);
}
