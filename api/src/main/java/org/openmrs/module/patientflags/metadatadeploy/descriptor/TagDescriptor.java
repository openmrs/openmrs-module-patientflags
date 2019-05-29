package org.openmrs.module.patientflags.metadatadeploy.descriptor;

import org.openmrs.module.metadatadeploy.descriptor.MetadataDescriptor;
import org.openmrs.module.patientflags.Tag;

import java.util.List;

public abstract class TagDescriptor extends MetadataDescriptor<Tag> {

    public Class<Tag> getDescribedType() {
        return Tag.class;
    }

    public abstract List<String> roles();
}
