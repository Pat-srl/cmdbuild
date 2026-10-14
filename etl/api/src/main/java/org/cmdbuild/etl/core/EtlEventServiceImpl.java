/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.core;

import com.google.common.eventbus.EventBus;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.cmdbuild.etl.EtlEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EtlEventServiceImpl implements EtlEventService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus = new EventBus(logExceptions(logger));

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

}
