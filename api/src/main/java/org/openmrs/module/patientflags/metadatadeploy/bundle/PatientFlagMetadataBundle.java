package org.openmrs.module.patientflags.metadatadeploy.bundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.openmrs.OpenmrsObject;
import org.openmrs.Role;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.metadatadeploy.descriptor.FlagDescriptor;
import org.openmrs.module.patientflags.metadatadeploy.descriptor.PriorityDescriptor;
import org.openmrs.module.patientflags.metadatadeploy.descriptor.TagDescriptor;

import java.util.HashSet;
import java.util.Set;

public abstract class PatientFlagMetadataBundle extends AbstractMetadataBundle {

    protected void install(TagDescriptor tagDescriptor) {
        Tag tag = new Tag(tagDescriptor.name());
        if (StringUtils.isNotBlank(tagDescriptor.uuid())) {
            tag.setUuid(tagDescriptor.uuid());
        }

        if (CollectionUtils.isNotEmpty(tagDescriptor.roles())) {
            tag.setRoles((Set)CollectionUtils.collect(tagDescriptor.roles(), new Transformer() {
                public Object transform(Object o) {
                    return MetadataUtils.existing(Role.class, (String)o);
                }
            }, new HashSet()));
        }

        this.install((OpenmrsObject) tag);
    }

    protected void install(PriorityDescriptor priorityDescriptor) {
        Priority priority = new Priority(priorityDescriptor.name(), priorityDescriptor.style(), priorityDescriptor.rank());
        if (StringUtils.isNotBlank(priorityDescriptor.description())) {
            priority.setDescription(priorityDescriptor.description());
        }
        if (StringUtils.isNotBlank(priorityDescriptor.uuid())) {
            priority.setUuid(priorityDescriptor.uuid());
        }

        this.install((OpenmrsObject) priority);
    }

    protected void install(FlagDescriptor flagDescriptor) {
        Flag flag = new Flag(flagDescriptor.name(), flagDescriptor.criteria(), flagDescriptor.message());
        if (StringUtils.isNotBlank(flagDescriptor.priority())) {
            flag.setPriority(MetadataUtils.existing(Priority.class, (String) flagDescriptor.priority()));
        }

        flag.setEvaluator(flagDescriptor.evaluator());

        if (StringUtils.isNotBlank(flagDescriptor.uuid())) {
            flag.setUuid(flagDescriptor.uuid());
        }

        if (CollectionUtils.isNotEmpty(flagDescriptor.tags())) {
            flag.setTags((Set)CollectionUtils.collect(flagDescriptor.tags(), new Transformer() {
                public Object transform(Object o) {
                    return MetadataUtils.existing(Tag.class, (String)o);
                }
            }, new HashSet()));
        }

        this.install((OpenmrsObject) flag);
    }
}
