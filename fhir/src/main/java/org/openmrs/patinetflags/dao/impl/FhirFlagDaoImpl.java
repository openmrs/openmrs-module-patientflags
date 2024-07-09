package org.openmrs.patinetflags.dao.impl;

import lombok.Setter;
import lombok.AccessLevel;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.patinetflags.dao.FhirFlagDao;
import org.springframework.stereotype.Component;

@Component
@Setter(AccessLevel.PACKAGE)
public class FhirFlagDaoImpl extends BaseFhirDao<PatientFlag> implements FhirFlagDao {

}
