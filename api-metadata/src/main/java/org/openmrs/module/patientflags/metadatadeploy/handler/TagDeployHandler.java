package org.openmrs.module.patientflags.metadatadeploy.handler;

import org.openmrs.annotation.Handler;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Handler(supports = {Tag.class})
public class TagDeployHandler extends AbstractObjectDeployHandler<Tag> {

    @Autowired
    @Qualifier("flagService")
    private FlagService flagService;

    public Tag fetch(String identifier) {
        return this.flagService.getTagByUuid(identifier);
    }

    public Tag save(Tag tag) {
        this.flagService.saveTag(tag);
        return tag;
    }

    public void uninstall(Tag tag, String reason) {
        this.flagService.retireTag(tag, reason);
    }

    public Tag findAlternateMatch(Tag incomingTag) {
        return this.flagService.getTagByName(incomingTag.getName());
    }
}
