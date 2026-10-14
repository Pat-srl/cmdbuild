package org.cmdbuild.etl.jobs;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PreDestroy;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import org.cmdbuild.config.WaterwayConfig;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.dao.utils.CmFilterUtils.noopFilter;
import org.cmdbuild.dao.utils.FulltextFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.data.filter.FilterType.ATTRIBUTE;
import static org.cmdbuild.data.filter.FilterType.FULLTEXT;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_TRIGGER;
import org.cmdbuild.etl.waterway.WaterwayService;
import static org.cmdbuild.etl.waterway.WaterwayService.WATERWAY_SERVICE_MINION;
import org.cmdbuild.etl.waterway.event.EtlAfterProcessingEvent;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.event.CardEvent;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.jobs.HardJobTimeoutEvent;
import org.cmdbuild.jobs.JobTimeoutEvent;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerExt;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class WaterwayEventTriggerService implements MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService configService;
    private final WaterwayService service;

    private final MinionHandlerExt minionHandler;

    private final ExecutorService executor;
    private List<EventHandler> handlers = emptyList();

    public WaterwayEventTriggerService(RequestContextService contextService, WaterwayDescriptorService configService, WaterwayService service, EventBusService eventBusService) {
        this.service = checkNotNull(service);
        this.configService = checkNotNull(configService);

        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Waterway_ Events")
                .withDescription("Waterway Event Trigger")
                .withEnabledChecker(service::isEnabled)
                .withRequires(WATERWAY_SERVICE_MINION)
                .reloadOnConfigs(WaterwayConfig.class)
                .build();

        executor = executorService(getClass().getName(), () -> {
            MDC.put("cm_type", "sys");
            MDC.put("cm_id", format("sys:wyevent:%s", randomId(6)));
            contextService.initCurrentRequestContext("event processing job");
        });

        eventBusService.getCardEventBus().register(new Object() {
            @Subscribe
            public void handleCardEvent(CardEvent event) {
                if (service.isReady() && !handlers.isEmpty() && event.getCurrentCard().getType().hasServiceWritePermission()) {
                    logger.trace("processing card event = {}", event);
                    handlers.forEach(h -> executor.submit(() -> h.handleCardEvent(event)));//TODO improve this (?)
                }
            }

        });
        eventBusService.getWaterwayEventBus().register(new Object() {
            @Subscribe
            public void handleWaterwayEventBus(EtlAfterProcessingEvent event) {
                if (service.isReady() && !handlers.isEmpty()) {
                    logger.trace("processing after etl processing event = {}", event);
                    handlers.forEach(h -> executor.submit(() -> h.handleWaterwayEventBus(event)));//TODO improve this (?)
                }
            }
        });
        eventBusService.getJobRunEventBus().register(new Object() {
            @Subscribe
            public void handleJobTimeoutEvent(JobTimeoutEvent event) {
                if (service.isReady() && !handlers.isEmpty()) {
                    logger.trace("processing job timeout event = {}", event); //TODO improve this (?)
                    handlers.forEach(h -> executor.submit(() -> h.handleJobTimeoutEvent(event)));//TODO improve this (?)
                }
            }

        });
        eventBusService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                loadHandlers();
            }

        });
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void start() {
        loadHandlers();
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executor);
    }

    public void loadHandlers() {
        try {
            logger.debug("load wy event handlers");
            handlers = configService.getAllItemsEnabled(WYCIT_TRIGGER).stream().filter(i -> i.hasConfigNotBlank("event")).map(EventHandler::new).collect(toImmutableList());
            logger.info("{} wy event handlers ready", handlers.size());
            minionHandler.setStatus(MRS_READY);
        } catch (Exception ex) {
            logger.error(marker(), "error loading card event triggers", ex);
            handlers = emptyList();
            minionHandler.setStatus(MRS_ERROR);
        }
    }

    private class EventHandler {

        private final WaterwayItem item;
        private final String target;
        private final CmdbFilter filter;
        private final Set<String> events;

        public EventHandler(WaterwayItem item) {
            this.item = checkNotNull(item);
            try {
                target = checkNotBlank(item.getConfig("target"));
                filter = item.hasConfigNotBlank("filter") ? CmFilterUtils.parseFilter(item.getConfig("filter")) : noopFilter();
                events = checkNotEmpty(list(toListOfStrings(item.getConfig("event"))).collect(toImmutableSet()));
                filter.checkHasOnlySupportedFilterTypes(ATTRIBUTE, FULLTEXT);
            } catch (Exception ex) {
                throw new EtlException(ex, "error loading event handler from trigger item = %s", item);
            }
        }

        public void handleCardEvent(CardEvent event) {
            try {
                if (events.contains(serializeEnum(event.getType())) && matchesFilter(event.getCurrentCard())) {
                    logger.debug("trigger handler = {} for card event = {}", item, event);
                    service.submitRequest(target, map("cardClassName", toStringNotBlank(event.getCurrentCard().getClassName()), "cardId", toStringNotBlank(event.getCurrentCard().getId())));//TODO add card attachment, serialize for script/record (?)
                }
            } catch (Exception ex) {
                logger.error(marker(), "error processing event = {} with handler = {}", event, item, ex);
            }
        }

        public void handleJobTimeoutEvent(JobTimeoutEvent event) {
            try {
                String eventCode = event instanceof HardJobTimeoutEvent ? "job_timeout_hard" : "job_timeout_soft"; //TODO improve this
                if (events.contains(eventCode)) {
                    logger.debug("trigger handler = {} for job timeout event = {}", item, event);
                    service.submitRequest(target, map(
                            "job", event.getJob().getCode(),
                            "run", event.getRun().getRunId(),
                            "run_timestamp", toIsoDateTimeUtc(event.getRun().getTimestamp()),
                            "run_node", event.getRun().getNodeId()));
                }
            } catch (Exception ex) {
                logger.error(marker(), "error processing event = {} with handler = {}", event, item, ex);
            }
        }

        private void handleWaterwayEventBus(EtlAfterProcessingEvent event) {
            try {
                if (events.contains("wy_after_processing") && matchesFilter(event.getMessage()) && !equal(event.getMessage().getQueueCode(), target)) {
                    logger.debug("trigger handler = {} for etl event = {}", item, event);
                    service.submitRequest(target, serializeMessage(event.getMessage()));
                }
            } catch (Exception ex) {
                logger.error(marker(), "error processing event = {} with handler = {}", event, item, ex);
            }
        }

        private Map<String, String> serializeMessage(WaterwayMessage message) {
            return map(message.getMeta()).with("messageId", message.getMessageId(), "queue", message.getQueueCode(), "status", serializeEnum(message.getStatus()));
        }

        private boolean matchesFilter(WaterwayMessage message) {
            if (filter.isNoop()) {
                return true;
            } else if (filter.hasAttributeFilter() && !AttributeFilterProcessor.<Map<String, String>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter.getAttributeFilter()).build().match(serializeMessage(message))) {
                return false;
            } else if (filter.hasFulltextFilter() && !FulltextFilterProcessor.<Map<String, String>>build(filter.getFulltextFilter()).withKeyFunction(c -> c.keySet()).withKeyToValueFunction((k, c) -> c.get(k)).match(serializeMessage(message))) {
                return false;
            } else {
                return true;
            }
        }

        private boolean matchesFilter(Card card) {
            if (filter.isNoop()) {
                return true;
            } else if (filter.hasAttributeFilter() && !AttributeFilterProcessor.<Card>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter.getAttributeFilter()).build().match(card)) {
                return false;
            } else if (filter.hasFulltextFilter() && !FulltextFilterProcessor.<Card>build(filter.getFulltextFilter()).withKeyFunction(c -> c.getAllValuesAsMap().keySet()).withKeyToValueFunction((k, c) -> c.get(k)).match(card)) {
                return false;
            } else {
                return true;
            }
        }

    }
}
