/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.listener;

import static java.lang.String.format;
import java.util.Map;
import static org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent.PG_NOTIFICATION_EVENT;
import static org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent.PG_NOTIFICATION_TYPE;
import static org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent.PG_NOTIFICATION_TYPE_EVENT;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface PostgresNotificationService extends PostgresNotificationEventService {

    void sendMessage(Map<String, Object> data);

    void sendNotification(String channel, String payload);

    default void sendMessage(String type, Object... data) {
        sendMessage(type, map(data));
    }

    default void sendMessage(String type, Map<String, Object> data) {
        sendMessage((Map) map(PG_NOTIFICATION_TYPE, checkNotBlank(type)).with(data));
    }

    default void sendEvent(String event, Object... data) {
        sendMessage(PG_NOTIFICATION_TYPE_EVENT, (Map) map(PG_NOTIFICATION_EVENT, checkNotBlank(event)).with(data));
    }

    default void sendInfo(String message, Object... args) {
        sendInfo(format(message, args));
    }

    default void sendInfo(Object payload) {
        String payloadStr;
        if (!(payload instanceof String)) {
            payloadStr = toJson(payload);
        } else {
            payloadStr = (String) payload;
        }
        sendNotification(PG_NOTIFICATION_INFO_CHANNEL, payloadStr);
    }
}
