/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Optional;

public interface FaultEventCollectorService {

    FaultEventCollector getCurrentRequestEventCollector();

    Optional<FaultEventCollector> getCurrentRequestEventCollectorIfExists();

    FaultEventCollector newEventCollector();

    List buildMessagesForJsonResponse(Throwable... additionalExceptions);

    String getUserMessages(Throwable... additionalExceptions);

    @VisibleForTesting
    void overrideMaxCollectedFaultEvents(int max);
}
