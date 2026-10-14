/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.listener;

import com.google.common.eventbus.EventBus;

public interface PostgresNotificationEventService {

    final String PG_NOTIFICATION_EVENTS_CHANNEL = "cmevents",
            PG_NOTIFICATION_INFO_CHANNEL="cminfo";

    /**
     * events: {@link PostgresNotificationEvent}
     */
    EventBus getEventBus();

}
