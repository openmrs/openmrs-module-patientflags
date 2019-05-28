package org.openmrs.module.patientflags.metadatadeploy.descriptor;

import java.util.List;

import org.openmrs.module.metadatadeploy.descriptor.MetadataDescriptor;
import org.openmrs.module.patientflags.Tag;

public abstract class TagDescriptor extends MetadataDescriptor<Tag> {
	
	public Class<Tag> getDescribedType() {
		return Tag.class;
	}
	
	public abstract List<String> roles();
}
