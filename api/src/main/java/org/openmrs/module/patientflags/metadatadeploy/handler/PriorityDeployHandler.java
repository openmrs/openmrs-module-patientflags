package org.openmrs.module.patientflags.metadatadeploy.handler;

import org.openmrs.annotation.Handler;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.openmrs.module.patientflags.Priority;
import org.openmrs.module.patientflags.api.FlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Handler(supports = {Priority.class})
public class PriorityDeployHandler extends AbstractObjectDeployHandler<Priority> {

    @Autowired
    @Qualifier("flagService")
    private FlagService flagService;

    public Priority fetch(String identifier) {
        return this.flagService.getPriorityByUuid(identifier);
    }

    public Priority save(Priority priority) {
        this.flagService.savePriority(priority);
        return priority;
    }

    public void uninstall(Priority priority, String reason) {
        this.flagService.retirePriority(priority, reason);
    }

    public Priority findAlternateMatch(Priority incomingPriority) {
        return this.flagService.getPriorityByName(incomingPriority.getName());
    }
}
