/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.boolex.OnMarkerEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.spi.FilterReply;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.fault.FaultEventImpl;
import org.cmdbuild.fault.FaultLevel;
import org.cmdbuild.log.LogbackHelper.LogbackConfigurationReloadedEvent;
import static org.cmdbuild.log.LogbackUtils.getExceptionFromLogbackIThrowableProxy;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * intercept logger messages marker with {@link #marker() } and attach them to
 * {@link FaultEventCollectorService} (to be possibly tracked or returner to the
 * user).
 */
@Component
public class ErrorOrWarningLogbackAppenderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FaultEventCollectorService errorAndWarningCollectorService;
    private final MyEventListener listener = new MyEventListener();

    public ErrorOrWarningLogbackAppenderService(FaultEventCollectorService errorAndWarningCollectorService) {
        this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
    }

    @PostConstruct
    public void init() {
        logger.info("init");
        LogbackHelper.getInstance().getEventBus().register(listener);
        registerAppender();
    }

    @PreDestroy
    public void cleanup() {
        LogbackHelper.getInstance().getEventBus().unregister(listener);
    }

    private void registerAppender() {
        logger.debug("register custom logback appenders for log event collection");
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

            {
                OnMarkerEvaluator evaluator = new OnMarkerEvaluator();
                evaluator.setContext(loggerContext);
                evaluator.addMarker(marker().getName());

                EvaluatorFilter<ILoggingEvent> filter = new EvaluatorFilter<>();
                filter.setContext(loggerContext);
                filter.setEvaluator(evaluator);
                filter.setOnMatch(FilterReply.ACCEPT);
                filter.setOnMismatch(FilterReply.DENY);

                AppenderBase<ILoggingEvent> appender = new AppenderBase<ILoggingEvent>() {
                    @Override
                    protected void append(ILoggingEvent eventObject) {
                        FaultLevel level;
                        if (eventObject.getLevel().isGreaterOrEqual(Level.ERROR)) {
                            level = FaultLevel.FL_ERROR;
                        } else if (eventObject.getLevel().isGreaterOrEqual(Level.WARN)) {
                            level = FaultLevel.FL_WARNING;
                        } else {
                            level = FaultLevel.FL_INFO;
                        }
                        errorAndWarningCollectorService.getCurrentRequestEventCollectorIfExists().ifPresent(c -> c.addEvent(new FaultEventImpl(
                                level,
                                eventObject.getFormattedMessage(),
                                getExceptionFromLogbackIThrowableProxy(eventObject.getThrowableProxy()))));
                    }

                };
                appender.setContext(loggerContext);
                appender.setName("ErrorOrWarningEventCollector");
                appender.addFilter(filter);

                evaluator.start();
                filter.start();
                appender.start();

                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender);
            }

            {
                PatternLayout patternLayout = new PatternLayout();
                patternLayout.setContext(loggerContext);
                patternLayout.setPattern(firstNotBlank(loggerContext.getProperty("CM_LOG_PATTERN"), "FALLBACK_LOG: %d{YYYY-MM-dd} %d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n"));

                AppenderBase<ILoggingEvent> appender = new AppenderBase<ILoggingEvent>() {
                    @Override
                    protected void append(ILoggingEvent eventObject) {
                        errorAndWarningCollectorService.getCurrentRequestEventCollectorIfExists().ifPresent((collector) -> {
                            if (collector.isFullLogCollectionEnabled()) {
                                String message = patternLayout.doLayout(eventObject);
                                collector.addLogs(message);
                            }
                        });
                    }

                };
                appender.setContext(loggerContext);
                appender.setName("ErrorOrWarningFullLogCollector");

                patternLayout.start();
                appender.start();

                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender);
            }
        } catch (Exception ex) {
            logger.error("error registering custom appender", ex);
        }
    }

    private class MyEventListener {

        @Subscribe
        public void handleLogbackConfigurationReloadedEvent(LogbackConfigurationReloadedEvent event) {
            registerAppender();
        }
    }

}
