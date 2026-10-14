/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.eventbus;

import com.google.common.eventbus.EventBus;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.function.BiConsumer;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventBusServiceImpl implements EventBusService {

    private final EventBus daoEventBus, grantEventBus, cardEventBus, workflowEventBus, filterEventBus, contextEventBus, requestEventBus, systemEventBus, sysCommandEventBus, clusterMessagesEventBus, jobRunEventBus, configEventBus, waterwayEventBus, emailAccountEventBus, activityLogEventBus;

    private final Map<String, EventBus> all;

    public EventBusServiceImpl() {
        Map<String, EventBus> map = map();
        daoEventBus = buildEventBus("DAO", map::put);
        grantEventBus = buildEventBus("GRANT", map::put);
        cardEventBus = buildEventBus("CARD", map::put);
        workflowEventBus = buildEventBus("WORKFLOW", map::put);
        filterEventBus = buildEventBus("FILTER", map::put);
        contextEventBus = buildEventBus("CONTEXT", map::put);
        requestEventBus = buildEventBus("REQUEST", map::put);
        systemEventBus = buildEventBus("SYSTEM", map::put);
        sysCommandEventBus = buildEventBus("SYSCOMMAND", map::put);
        clusterMessagesEventBus = buildEventBus("CLUSTERMSG", map::put);
        jobRunEventBus = buildEventBus("JOBRUN", map::put);
        configEventBus = buildEventBus("CONFIG", map::put);
        waterwayEventBus = buildEventBus("WATERWAY", map::put);
        emailAccountEventBus = buildEventBus("EMAILACCOUNT", map::put);
        activityLogEventBus = buildEventBus("ACTIVITYLOG", map::put);
        all = Map.copyOf(map);
    }

    private static EventBus buildEventBus(String name, BiConsumer<String, EventBus> consumer) {
        EventBus eventBus = new EventBus(logExceptions(LoggerFactory.getLogger(format("%s.%s", MethodHandles.lookup().lookupClass().getName(), checkNotBlank(name)))));
        consumer.accept(name, eventBus);
        return eventBus;
    }

    @Override
    public Map<String, EventBus> getAllByCode() {
        return all;
    }

    @Override
    public EventBus getWaterwayEventBus() {
        return waterwayEventBus;
    }

    @Override
    public EventBus getDaoEventBus() {
        return daoEventBus;
    }

    @Override
    public EventBus getGrantEventBus() {
        return grantEventBus;
    }

    @Override
    public EventBus getCardEventBus() {
        return cardEventBus;
    }

    @Override
    public EventBus getWorkflowEventBus() {
        return workflowEventBus;
    }

    @Override
    public EventBus getFilterEventBus() {
        return filterEventBus;
    }

    @Override
    public EventBus getContextEventBus() {
        return contextEventBus;
    }

    @Override
    public EventBus getRequestEventBus() {
        return requestEventBus;
    }

    @Override
    public EventBus getSystemEventBus() {
        return systemEventBus;
    }

    @Override
    public EventBus getSysCommandEventBus() {
        return sysCommandEventBus;
    }

    @Override
    public EventBus getClusterMessagesEventBus() {
        return clusterMessagesEventBus;
    }

    @Override
    public EventBus getJobRunEventBus() {
        return jobRunEventBus;
    }

    @Override
    public EventBus getConfigEventBus() {
        return configEventBus;
    }

    @Override
    public EventBus getEmailAccountEventBus() {
        return emailAccountEventBus;
    }

    @Override
    public EventBus getActivityLogEventBus() {
        return activityLogEventBus;
    }

}
