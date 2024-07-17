package org.openmrs.patienttflags.translators;

import org.hl7.fhir.r4.model.Flag;
import org.openmrs.module.fhir2.api.translators.OpenmrsFhirTranslator;
import org.openmrs.module.patientflags.PatientFlag;

import javax.annotation.Nonnull;

public interface PatientFlagTranslator extends OpenmrsFhirTranslator<PatientFlag, Flag> {
    /**
     * @param patientFlag 
     * @return
     */
    @Override
    Flag toFhirResource(@Nonnull PatientFlag patientFlag);

    /**
     * @param flag 
     * @return
     */
    @Override
    PatientFlag toOpenmrsType(@Nonnull Flag flag);
}
