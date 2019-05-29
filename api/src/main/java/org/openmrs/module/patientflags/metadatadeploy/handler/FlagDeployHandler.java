package org.openmrs.module.patientflags.metadatadeploy.handler;

import org.openmrs.annotation.Handler;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.patientflags.Flag;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Handler(supports = {Flag.class})
public class FlagDeployHandler extends AbstractObjectDeployHandler<Flag> {

    @Autowired
    @Qualifier("flagService")
    private FlagService flagService;

    public Flag fetch(String identifier) {
        return this.flagService.getFlagByUuid(identifier);
    }

    public Flag save(Flag flag) {
        this.flagService.saveFlag(flag);
        return flag;
    }

    public void uninstall(Flag flag, String reason) {
        this.flagService.retireFlag(flag, reason);
    }

    public Flag findAlternateMatch(Flag incomingPriority) {
        return this.flagService.getFlagByName(incomingPriority.getName());
    }
}
