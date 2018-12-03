package org.openmrs.module.patientflags.metadatadeploy.descriptor;

import java.util.List;

import org.openmrs.module.metadatadeploy.descriptor.MetadataDescriptor;
import org.openmrs.module.patientflags.Flag;

public abstract class FlagDescriptor extends MetadataDescriptor<Flag> {
	
	public Class<Flag> getDescribedType() {
		return Flag.class;
	}
	
	public abstract String criteria();
	
	public String evaluator() {
		return "org.openmrs.module.patientflags.evaluator.SQLFlagEvaluator";
	}
	
	public abstract String message();
	
	public abstract String priority();
	
	public abstract List<String> tags();
	
	public Boolean enabled() {
		return Boolean.TRUE;
	}
}
