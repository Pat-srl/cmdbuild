/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

import static com.google.common.base.Objects.equal;
import java.time.ZonedDateTime;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_INCOMING;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_OUTGOING;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface RawEvent extends Event {

    final String EVENT_SESSION_ID_BROADCAST = "BROADCAST",
            EVENT_CODE_ACTION = "action",
            EVENT_CODE_ALERT = "alert",
            EVENT_CODE_CHAT = "chat",
            ALERT_MESSAGE = "message",
            ALERT_LEVEL = "level",
            ALERT_MESSAGE_SHOW_USER = "show_user",
            ALERT_LEVEL_SYSTEM = "SYSTEM",
            ALERT_LEVEL_WARNING = "WARNING",
            ALERT_LEVEL_ERROR = "ERROR",
            ALERT_LEVEL_INFO = "INFO";

    EventDirection getDirection();

    @Nullable
    String getSessionIdOrNull();

    @Nullable
    String getClientIdOrNull();

    String getMessageId();

    String getEventCode();

    Map<String, Object> getPayload();

    ZonedDateTime getTimestamp();

    default boolean hasSessionId() {
        return isNotBlank(getSessionIdOrNull());
    }

    default boolean hasClientId() {
        return isNotBlank(getClientIdOrNull());
    }

    default String getSessionId() {
        return checkNotBlank(getSessionIdOrNull());
    }

    default String getClientId() {
        return checkNotBlank(getClientIdOrNull());
    }

    @Nullable
    default String getStringValue(String key) {
        return toStringOrNull(getPayload().get(key));
    }

    default String getStringNotBlank(String key) {
        return toStringNotBlank(getPayload().get(key), "missing value for key =< %s >", key);
    }

    default boolean hasAction() {
        return isNotBlank(getStringValue("_action"));
    }

    default String getAction() {
        return checkNotBlank(getStringValue("_action"), "action param not found");
    }

    default boolean isBroadcast() {
        return equal(EVENT_SESSION_ID_BROADCAST, getSessionIdOrNull());
    }

    default boolean isOutgoing() {
        return equal(ED_OUTGOING, getDirection());
    }

    default boolean isIncoming() {
        return equal(ED_INCOMING, getDirection());
    }

    enum EventDirection {
        ED_INCOMING, ED_OUTGOING;
    }

}
