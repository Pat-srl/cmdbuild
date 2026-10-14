/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import com.google.common.eventbus.EventBus;

public interface EtlEventService {

    EventBus getEventBus();

    default void post(EtlEvent event) {
        getEventBus().post(event);
    }
}
