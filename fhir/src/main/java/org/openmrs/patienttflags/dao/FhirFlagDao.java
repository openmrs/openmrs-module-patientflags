package org.openmrs.patienttflags.dao;

import org.openmrs.module.fhir2.api.dao.FhirDao;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.patientflags.PatientFlag;

import javax.annotation.Nonnull;
import java.util.List;

public interface FhirFlagDao extends FhirDao<PatientFlag> {

    @Override
    PatientFlag get(@Nonnull String uuid);

    @Override
    List<PatientFlag> getSearchResults(@Nonnull SearchParameterMap theParams);
}
