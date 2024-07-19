package org.openmrs.patienttflags.search.param;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.search.param.BaseResourceSearchParams;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;

import java.util.HashSet;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FlagSearchParams extends BaseResourceSearchParams {

    private ReferenceAndListParam patient;

    private StringAndListParam code;

    private StringAndListParam category;

    private DateRangeParam date;


    @Builder
    public FlagSearchParams(ReferenceAndListParam patient, StringAndListParam code, StringAndListParam category,
                            DateRangeParam date, TokenAndListParam id, DateRangeParam lastUpdated,
                            SortSpec sort, HashSet<Include> includes, HashSet<Include> revIncludes) {
        super(id, lastUpdated, sort, includes, revIncludes);
        this.patient = patient;
        this.code = code;
        this.category = category;
        this.date = date;
    }

    @Override
    public SearchParameterMap toSearchParameterMap() {
        return baseSearchParameterMap()
                .addParameter(FhirConstants.PATIENT_REFERENCE_SEARCH_HANDLER, getPatient())
                .addParameter(FhirConstants.CATEGORY_SEARCH_HANDLER, getCategory())
                .addParameter(FhirConstants.DATE_RANGE_SEARCH_HANDLER, getDate())
                .addParameter(FhirConstants.CODED_SEARCH_HANDLER, getCode());
    }
}
