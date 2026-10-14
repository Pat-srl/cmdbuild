/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.listener;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;
import org.cmdbuild.cluster.NodeIdProvider;
import org.cmdbuild.dao.ConfigurableDataSource;
import static org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent.PG_NOTIFICATION_SOURCE;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PostgresNotificationServiceImpl implements PostgresNotificationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<String> channels = singletonList(PG_NOTIFICATION_EVENTS_CHANNEL);

    private final NodeIdProvider nodeIdProvider;
    private final ConfigurableDataSource dataSource;
    private final EventBus eventBus;
    private final ScheduledExecutorService poller = Executors.newSingleThreadScheduledExecutor(namedThreadFactory(getClass()));

    private Connection connection;

    public PostgresNotificationServiceImpl(ConfigurableDataSource dataSource, PostgresNotificationEventService eventService, NodeIdProvider nodeIdProvider, RequestContextService contextService) {
        this.dataSource = checkNotNull(dataSource);
        this.eventBus = eventService.getEventBus();
        this.nodeIdProvider = checkNotNull(nodeIdProvider);
        logger.debug("ready");
        poller.scheduleAtFixedRate(this::pollDatasourceForNotifications, 1, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        logger.debug("shutdown");
        shutdownQuietly(poller);
        closeConnectionSafe();
    }

    @Override
    public void sendMessage(Map<String, Object> data) {
        sendNotification(PG_NOTIFICATION_EVENTS_CHANNEL, toJson(map(checkNotNull(data)).with(
                PG_NOTIFICATION_SOURCE, getSourceForThisNode()
        )));
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public void sendNotification(String channel, String payload) {
        checkNotBlank(channel);
        checkNotBlank(payload);
        logger.debug("send pg notification to channel =< {} > with payload =< {} >", channel, payload);
        try (Connection thisConnection = dataSource.getConnection(); PreparedStatement statement = thisConnection.prepareStatement("SELECT pg_notify(?,?)")) {
            statement.setString(1, channel);
            statement.setString(2, payload);
            statement.execute();
        } catch (SQLException ex) {
            throw runtime(ex);
        }
    }

    private String getSourceForThisNode() {
        return nodeIdProvider.getNodeInfo();
    }

    private void pollDatasourceForNotifications() {
        logger.trace("pollDatasourceForNotifications");
        try {
            if (!dataSource.isReady()) {
                connection = null;
            } else {
                if (connection == null) {
                    openConnection();
                }
                pollNotificationsFromConnection();
            }
        } catch (Throwable ex) {
            if (Thread.currentThread().isInterrupted()) {
                logger.info("interrupted notification poller job");
                //no exception log
            } else {
                logger.error("error polling connection for notifications from postgres", ex);
            }
            closeConnectionSafe();
        }
    }

    private void openConnection() throws SQLException {
        logger.debug("open sql connection for pg notification listening");
        connection = dataSource.getInner().getConnection();
        for (String channel : channels) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(format("LISTEN %s", channel));
            }
        }
    }

    private void closeConnectionSafe() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception exx) {
            }
            connection = null;
        }
    }

    private void pollNotificationsFromConnection() throws SQLException {
        logger.trace("pollNotificationsFromConnection");

        logger.trace("run dummy query");
        try (Statement statement = connection.createStatement()) {
            statement.execute("");
        }

        logger.trace("get notifications");
        PGNotification notifications[] = connection.unwrap(PGConnection.class).getNotifications();
        if (notifications != null) {
            logger.trace("received {} notifications", notifications.length);
            for (PGNotification notification : notifications) {
                logger.trace("received pg notification = {}", notification);
                try {
                    PostgresNotificationEvent event = new PostgresNotificationEventImpl(notification);
                    logger.trace("received pg notification event = {}", event);
                    if (equal(toStringOrNull(event.getData().get(PG_NOTIFICATION_SOURCE)), getSourceForThisNode())) {
                        logger.trace("skip processing, event sent from this node");
                    } else {
                        logger.debug("processing pg notification event = {}", event);
                        eventBus.post(event);
//                        processor.submit(safe(() -> {
//                            logger.trace("post event = {}", event);
//                            eventBus.post(event);
//                            logger.trace("complete post event = {}", event);
//                        }));
                    }
                } catch (Exception ex) {
                    logger.error("error processing pg notification = {}", notification, ex);
                }
            }
        } else {
            logger.trace("received 0 notifications");
        }
    }

    private class PostgresNotificationEventImpl implements PostgresNotificationEvent {

        private final int pid;
        private final String channel, payload;
        private final Map<String, Object> data;

        public PostgresNotificationEventImpl(PGNotification notification) {
            this.pid = notification.getPID();
            this.channel = checkNotBlank(notification.getName());
            this.payload = checkNotBlank(notification.getParameter());
            data = map(fromJson(payload, MAP_OF_OBJECTS)).immutable();
        }

        @Override
        public int getServerPid() {
            return pid;
        }

        @Override
        public String getChannel() {
            return channel;
        }

        @Override
        public String getPayload() {
            return payload;
        }

        @Override
        public Map<String, Object> getData() {
            return data;
        }

        @Override
        public String toString() {
            return "PostgresNotificationEventImpl{" + "pid=" + pid + ", channel=" + channel + ", payload=" + payload + '}';
        }

    }

}
