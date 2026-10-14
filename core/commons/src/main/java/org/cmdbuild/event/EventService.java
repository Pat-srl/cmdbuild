/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

import com.google.common.eventbus.EventBus;
import java.util.Map;
import static org.cmdbuild.event.RawEvent.ALERT_LEVEL;
import static org.cmdbuild.event.RawEvent.ALERT_LEVEL_SYSTEM;
import static org.cmdbuild.event.RawEvent.ALERT_MESSAGE;
import static org.cmdbuild.event.RawEvent.ALERT_MESSAGE_SHOW_USER;
import static org.cmdbuild.event.RawEvent.EVENT_CODE_ALERT;
import static org.cmdbuild.event.RawEvent.EVENT_SESSION_ID_BROADCAST;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_OUTGOING;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface EventService {

    /**
     * register with this eventbus to receive incoming event objects of type {@link RawEvent} and outgoing event objects of type {@link OutgoingEvent}
     */
    EventBus getEventBus();

    void sendEventMessage(RawEvent event);

    void handleReceivedEventMessage(Event event);

    default void sendEventMessage(String sessionId, String eventCode, Map<String, Object> payload) {
        sendEventMessage(RawEventImpl.builder().withDirection(ED_OUTGOING).withSessionId(sessionId).withEventCode(eventCode).withPayload(payload).build());
    }

    default void sendEventMessage(String sessionId, String clientId, String eventCode, Map<String, Object> payload) {
        sendEventMessage(RawEventImpl.builder().withDirection(ED_OUTGOING).withSessionId(sessionId).withClientId(clientId).withEventCode(eventCode).withPayload(payload).build());
    }

    default void sendBroadcastEventMessage(String eventCode, Map<String, Object> payload) {
        sendEventMessage(EVENT_SESSION_ID_BROADCAST, eventCode, payload);
    }

    default void sendBroadcastAlert(String message) {
        sendEventMessage(EVENT_SESSION_ID_BROADCAST, EVENT_CODE_ALERT, map(ALERT_LEVEL, ALERT_LEVEL_SYSTEM, ALERT_MESSAGE, checkNotBlank(message), ALERT_MESSAGE_SHOW_USER, true));
    }

    interface OutgoingEvent {

        RawEvent getRawEvent();
    }

}
