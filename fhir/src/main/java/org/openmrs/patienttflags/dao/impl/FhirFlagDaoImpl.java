package org.openmrs.patienttflags.dao.impl;

import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import lombok.Setter;
import lombok.AccessLevel;
import org.hibernate.Criteria;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.patienttflags.dao.FhirFlagDao;
import org.springframework.stereotype.Component;


@Component
@Setter(AccessLevel.PACKAGE)
public class FhirFlagDaoImpl extends BaseFhirDao<PatientFlag> implements FhirFlagDao {

    /**
     * @param criteria 
     * @param theParams
     */
    @Override
    protected void setupSearchParams(Criteria criteria, SearchParameterMap theParams) {
        theParams.getParameters().forEach(entry->{
            switch (entry.getKey()) {
                case FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER:
                    entry.getValue().forEach(param -> handlePatientReference(criteria, (ReferenceAndListParam) param.getParam()));
                    break;
//                case FhirConstants.CATEGORY_SEARCH_HANDLER:
//
//                    break;
//                case FhirConstants.DATE_RANGE_SEARCH_HANDLER:
//                    break;
//                case FhirConstants.CODED_SEARCH_HANDLER:
//                    entry.getValue().forEach(param -> handleCode(criteria, (StringAndListParam) param.getParam()));
//                    break;

            }
        });
    }

//    private void handleCode(Criteria criteria, StringAndListParam code) {
//        if (code != null) {
//
//        }
//    }

}
