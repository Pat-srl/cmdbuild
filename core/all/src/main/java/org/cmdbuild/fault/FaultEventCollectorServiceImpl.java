/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.requestcontext.RequestContextHolder;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FaultEventCollectorServiceImpl implements FaultEventCollectorService {

    private final static int MAX_COLLECTED_FAULT_EVENTS = 1000, MAX_COLLECTED_LOG_CHARS = 10000000;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RequestContextHolder<FaultEventCollector> holder;
    private final CoreConfiguration config;
    private final FaultSerializationService helper;

    private int maxCollectedFaultEvents = MAX_COLLECTED_FAULT_EVENTS;

    public FaultEventCollectorServiceImpl(FaultSerializationService helper, RequestContextService requestContextService, CoreConfiguration config) {
        holder = requestContextService.createRequestContextHolder(this::newEventCollector);
        this.config = checkNotNull(config);
        this.helper = checkNotNull(helper);
    }

    @Override
    public FaultEventCollector getCurrentRequestEventCollector() {
        return holder.get();
    }

    @Override
    public Optional<FaultEventCollector> getCurrentRequestEventCollectorIfExists() {
        if (holder.hasContent()) {
            return Optional.of(getCurrentRequestEventCollector());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public FaultEventCollector newEventCollector() {
        return new EventCollectorImpl();
    }

    @Override
    public List buildMessagesForJsonResponse(Throwable... additionalExceptions) {
        FaultEventCollector collector = getCurrentRequestEventCollector();
        list(additionalExceptions).forEach(collector::addError);
        return helper.buildResponseMessages(collector.getCollectedEvents().stream().filter(e -> e.hasLevel(config.getNotificationMessagesLevelThreshold())).collect(toImmutableList()));
    }

    @Override
    public String getUserMessages(Throwable... additionalExceptions) {
        FaultEventCollector collector = getCurrentRequestEventCollector();
        list(additionalExceptions).forEach(collector::addError);
        return list(helper.buildResponseMessages(list(collector.getCollectedEvents()).filter(e -> e.hasLevel(config.getNotificationMessagesLevelThreshold())))).filter(m -> toBoolean(m.get("show_user"))).map(m -> toStringOrEmpty(m.get("message"))).collect(joining(", "));
    }

    @Override
    public void overrideMaxCollectedFaultEvents(int max) {
        this.maxCollectedFaultEvents = max;
    }

    private class EventCollectorImpl implements FaultEventCollector {

        private final StringBuilder logs = new StringBuilder();
        private final List<FaultEvent> collectedEvents = newArrayList();
        private boolean enableFullLogCollection = false;

        @Override
        public synchronized void addEvent(FaultEvent event) {
            logger.trace("collect error event = {}", event);
            if (this.collectedEvents.size() >= maxCollectedFaultEvents - 1) {
                logger.warn("skip fault event collection (too many fault events, max collector capacity exceeded)");
                if (this.collectedEvents.size() == maxCollectedFaultEvents - 1) {
                    this.collectedEvents.add(FaultEventImpl.error("fault event collection truncated here (too many fault events, max collector capacity exceeded)"));
                }
            } else {
                this.collectedEvents.add(event);
            }
        }

        @Override
        public List<FaultEvent> getCollectedEvents() {
            return collectedEvents;
        }

        @Override
        public void enableFullLogCollection() {
            enableFullLogCollection = true;
        }

        @Override
        public boolean isFullLogCollectionEnabled() {
            return enableFullLogCollection;
        }

        @Override
        public void addLogs(String logs) {
            if (this.logs.length() >= MAX_COLLECTED_LOG_CHARS) {
                //skip log collecting
            } else if (this.logs.length() < MAX_COLLECTED_LOG_CHARS && logs.length() + this.logs.length() >= MAX_COLLECTED_LOG_CHARS) {
                this.logs.append(abbreviate(logs, MAX_COLLECTED_LOG_CHARS - this.logs.length()))
                        .append("\n\nLOG MESSAGES TRUNCATED HERE (TOO MANY LOG MESSAGES, MAX COLLECTOR CAPACITY EXCEEDED)\n");
            } else {
                this.logs.append(logs);
            }
        }

        @Override
        public String getLogs() {
            return logs.toString();
        }


    }

}
