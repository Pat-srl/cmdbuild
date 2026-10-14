package org.cmdbuild.service.rest.v4.providers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;

import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.utils.ws3.api.WsWarningSource;
import org.springframework.stereotype.Component;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultSerializationService;

@Component
public class WarningHandlerService implements WsWarningSource {

    private final FaultSerializationService helper;
    private final CoreConfiguration config;
    private final FaultEventCollectorService errorAndWarningCollectorService;

    public WarningHandlerService(FaultSerializationService helper, CoreConfiguration config, FaultEventCollectorService errorAndWarningCollectorService) {
        this.helper = checkNotNull(helper);
        this.config = checkNotNull(config);
        this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
    }

    @Override
    public List<Object> getWarningJsonMessages() {
        List<FaultEvent> eventsToReport = errorAndWarningCollectorService.getCurrentRequestEventCollector().getCollectedEvents().stream().filter(e -> e.hasLevel(config.getNotificationMessagesLevelThreshold())).collect(toImmutableList());
        return (List) helper.buildResponseMessages(eventsToReport);
    }
}
