/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.OutputStreamAppender;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import jakarta.annotation.PreDestroy;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.cmdbuild.log.LogService;
import org.cmdbuild.log.LogbackHelper;
import org.cmdbuild.log.LogbackHelper.LogbackConfigurationReloadedEvent;
import static org.cmdbuild.log.LogbackUtils.getExceptionFromLogbackIThrowableProxy;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogServiceImpl implements LogService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus = new EventBus(logExceptions(logger));
    private final ExecutorService executor = Executors.newSingleThreadExecutor(namedThreadFactory(getClass()));
    private final MyEventListener listener = new MyEventListener();

    public LogServiceImpl() throws ClassNotFoundException {
        logger.info("init");
        LogbackHelper.getInstance().getEventBus().register(listener);
        registerAppender();

        Class.forName("ch.qos.logback.core.status.WarnStatus");//preload logback classes; TODO improve this
    }

    @PreDestroy
    public void cleanup() {
        LogbackHelper.getInstance().getEventBus().unregister(listener);
        shutdownQuietly(executor);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    private void registerAppender() {
        logger.debug("register custom logback appender for log event collecting");
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg");
            try {
                OutputStreamAppender appender = (OutputStreamAppender) loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("CMDBUILD");
                if (appender != null && appender.getEncoder() != null && appender.getEncoder() instanceof PatternLayoutEncoder) {
                    String pattern = ((PatternLayoutEncoder) appender.getEncoder()).getPattern();
                    pattern = pattern.replaceFirst("%n$", "");
                    encoder.setPattern(pattern);
                } else {
                    logger.warn("unable to get encoder from cmdbuild logger, using fallback encoder");
                }
            } catch (Exception ex) {
                logger.warn("unable to get encoder from cmdbuild logger, using fallback encoder", ex);
            }
            encoder.setContext(loggerContext);
            encoder.start();

            AppenderBase<ILoggingEvent> customAppender = new AppenderBase<ILoggingEvent>() {
                @Override
                protected void append(ILoggingEvent eventObject) {
                    try {
                        LogEvent event = new LogEventImpl(eventObject, new String(encoder.encode(eventObject)));
                        if (!executor.isShutdown()) {
                            executor.submit(() -> {
                                eventBus.post(event);
                            });
                        }
                    } catch (Exception ex) {
                        logger.error("error processing logback event = {}", eventObject, ex);
                    }
                }

            };

            customAppender.setContext(loggerContext);
            customAppender.setName("LogServiceAppender");
            customAppender.start();

            loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(customAppender);
        } catch (Exception ex) {
            logger.error("error registering custom appender", ex);
        }
    }

    private class LogEventImpl implements LogEvent {

        private final LogLevel level;
        private final String message, line;
        private final ZonedDateTime timestamp;
        private final Throwable exception;

        private LogEventImpl(ILoggingEvent eventObject, String line) {
            level = parseEnum(eventObject.getLevel().toString(), LogLevel.class);
            message = eventObject.getFormattedMessage();
            timestamp = toDateTime(eventObject.getTimeStamp());
            exception = getExceptionFromLogbackIThrowableProxy(eventObject.getThrowableProxy());
            this.line = checkNotBlank(line);
        }

        @Override
        public LogLevel getLevel() {
            return level;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public @Nullable
        Throwable getException() {
            return exception;
        }

        @Override
        public String getLine() {
            return line;
        }

    }

    private class MyEventListener {

        @Subscribe
        public void handleLogbackConfigurationReloadedEvent(LogbackConfigurationReloadedEvent event) {
            registerAppender();
        }
    }

}
