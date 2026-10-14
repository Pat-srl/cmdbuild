/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.eventlog;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class EventLogCollectorServiceImpl implements EventLogCollectorService {

    private final EventBus eventBus;

    public EventLogCollectorServiceImpl(EventLogEventBusService eventbusService) {
        this.eventBus = checkNotNull(eventbusService.getEventBus());
    }

    @Override
    public void storeEvent(EventLogInfo event) {
        eventBus.post(checkNotNull(event));
    }

}
