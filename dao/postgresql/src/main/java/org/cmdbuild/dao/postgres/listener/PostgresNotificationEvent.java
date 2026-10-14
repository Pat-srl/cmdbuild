/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.listener;

import static com.google.common.base.Objects.equal;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface PostgresNotificationEvent {

    final String PG_NOTIFICATION_TYPE = "type",
            PG_NOTIFICATION_TYPE_COMMAND = "command",
            PG_NOTIFICATION_TYPE_EVENT = "event",
            PG_NOTIFICATION_TYPE_RESPONSE = "response",
            PG_NOTIFICATION_ACTION = "action",
            PG_NOTIFICATION_SOURCE = "source",
            PG_NOTIFICATION_EVENT = "event",
            PG_NOTIFICATION_OUTPUT = "output";

    int getServerPid();

    String getChannel();

    String getPayload();

    Map<String, Object> getData();

    default String getType() {
        return checkNotBlank(toStringOrNull(getData().get(PG_NOTIFICATION_TYPE)));
    }

    default boolean isCommand() {
        return equal(PG_NOTIFICATION_TYPE_COMMAND, getType());
    }

    default boolean isEvent() {
        return equal(PG_NOTIFICATION_TYPE_EVENT, getType());
    }

    default String getAction() {
        return checkNotBlank(toStringOrNull(getData().get(PG_NOTIFICATION_ACTION)));
    }

    default String getEvent() {
        return checkNotBlank(toStringOrNull(getData().get(PG_NOTIFICATION_EVENT)));
    }

    default boolean isEvent(String event) {
        return isEvent() && equal(getEvent(), event);
    }

}
