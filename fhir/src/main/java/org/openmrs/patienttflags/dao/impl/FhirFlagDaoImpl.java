package org.openmrs.patienttflags.dao.impl;

import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import lombok.Setter;
import lombok.AccessLevel;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hl7.fhir.r4.model.Patient;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.dao.impl.BaseFhirDao;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.patientflags.PatientFlag;
import org.openmrs.patienttflags.dao.FhirFlagDao;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Setter(AccessLevel.PACKAGE)
public class FhirFlagDaoImpl extends BaseFhirDao<PatientFlag> implements FhirFlagDao {

    public List getFlagsByPatientId(@Nonnull Patient patient) {
            return getSessionFactory().getCurrentSession().createCriteria(PatientFlag.class)
                    .add(Restrictions.eq("patient", patient)).list();
    }

    /**
     * @param criteria 
     * @param theParams
     */
    @Override
    protected void setupSearchParams(Criteria criteria, SearchParameterMap theParams) {
        theParams.getParameters().forEach(para->{
            System.out.println(para.getKey());
            if (FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER.equals(para.getKey())) {
                para.getValue().forEach(param -> handlePatientReference(criteria, (ReferenceAndListParam) param.getParam(),"patient"));
            }
        });
    }

    protected void handlePatientReference(Criteria criteria, ReferenceAndListParam patientReference, String associationPath) {
        if (patientReference != null) {
            criteria.createAlias(associationPath, "p");
            this.handleAndListParam(patientReference, (patientToken) -> {
                if (patientToken.getChain() != null) {
                    switch (patientToken.getChain()) {
                        case Patient.SP_RES_ID:
                            return Optional.of(Restrictions.eq("p.uuid", patientToken.getValue()));
                        case Patient.SP_IDENTIFIER:
                            if (this.lacksAlias(criteria, "pi")) {
                                criteria.createAlias("p.identifiers", "pi");
                            }

                            return Optional.of(Restrictions.ilike("pi.identifier", patientToken.getValue()));
                        case Patient.SP_GIVEN:
                            if (this.lacksAlias(criteria, "pn")) {
                                criteria.createAlias("p.names", "pn");
                            }

                            return Optional.of(Restrictions.ilike("pn.givenName", patientToken.getValue(), MatchMode.START));
                        case Patient.SP_FAMILY:
                            if (this.lacksAlias(criteria, "pn")) {
                                criteria.createAlias("p.names", "pn");
                            }

                            return Optional.of(Restrictions.ilike("pn.familyName", patientToken.getValue(), MatchMode.START));
                        case Patient.SP_NAME:
                            if (this.lacksAlias(criteria, "pn")) {
                                criteria.createAlias("p.names", "pn");
                            }

                            List<Optional<? extends Criterion>> criterionList = new ArrayList<>();
                            String[] var6 = StringUtils.split(patientToken.getValue(), " \t,");
                            int var7 = var6.length;

                            for (int var8 = 0; var8 < var7; ++var8) {
                                String token = var6[var8];
                                criterionList.add(this.propertyLike("pn.givenName", token));
                                criterionList.add(this.propertyLike("pn.middleName", token));
                                criterionList.add(this.propertyLike("pn.familyName", token));
                            }

                            return Optional.of(Restrictions.or(this.toCriteriaArray(criterionList)));
                        default:
                            return Optional.empty();
                    }
                }
                return Optional.empty();
            }).ifPresent(criteria::add);
        }

    }

}
