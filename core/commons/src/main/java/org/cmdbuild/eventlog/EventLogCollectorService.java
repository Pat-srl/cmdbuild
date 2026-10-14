/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.eventlog;

import java.util.Map;

public interface EventLogCollectorService {

    void storeEvent(EventLogInfo event);

    default void storeEvent(String code, Map<String, Object> data) {
        storeEvent(EventLogInfoImpl.builder().withCode(code).withData(data).build());
    }

    default void storeEvent(String code) {
        storeEvent(EventLogInfoImpl.builder().withCode(code).build());
    }
}
