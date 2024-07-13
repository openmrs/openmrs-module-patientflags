package org.openmrs.patinetflags.dao.impl;

import lombok.Setter;
import lombok.AccessLevel;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.patinetflags.dao.FhirFlagDao;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

@Component
@Setter(AccessLevel.PACKAGE)
public class FhirFlagDaoImpl extends BaseFhirDao<PatientFlag> implements FhirFlagDao {

    public List getFlagsByPatientId(@Nonnull Patient patient) {
            return getSessionFactory().getCurrentSession().createCriteria(PatientFlag.class)
                    .add(Restrictions.eq("patient", patient)).list();
    }
}
