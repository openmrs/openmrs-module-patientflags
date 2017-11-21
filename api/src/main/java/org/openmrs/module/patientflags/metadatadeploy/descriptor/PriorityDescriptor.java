package org.openmrs.module.patientflags.metadatadeploy.descriptor;

import org.openmrs.module.metadatadeploy.descriptor.MetadataDescriptor;
import org.openmrs.module.patientflags.Priority;

public abstract class PriorityDescriptor extends MetadataDescriptor<Priority> {

    public Class<Priority> getDescribedType() {
        return Priority.class;
    }

    public abstract String style();

    public abstract Integer rank();
}
