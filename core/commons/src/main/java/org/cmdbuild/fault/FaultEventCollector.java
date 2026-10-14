/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import com.google.common.base.Joiner;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;

public interface FaultEventCollector {

    void enableFullLogCollection();

    boolean isFullLogCollectionEnabled();

    void addEvent(FaultEvent event);

    void addLogs(String logs);

    List<FaultEvent> getCollectedEvents();

    String getLogs();

    default void addInfo(Throwable exception) {
        addEvent(FaultEventImpl.info(exception));
    }

    default void addWarning(Throwable exception) {
        addEvent(FaultEventImpl.warning(exception));
    }

    default void addError(Throwable exception) {
        addEvent(FaultEventImpl.error(exception));
    }

    default FaultEventCollector withError(Exception exception) {
        addError(exception);
        return this;
    }

    default boolean hasEvents() {
        return !getCollectedEvents().isEmpty();
    }

    default void addEventsFrom(FaultEventCollector inner) {
        inner.getCollectedEvents().forEach(this::addEvent);
    }

    default String getMessageFromEvents() {
        return Joiner.on("; ").join(getCollectedEvents().stream().map((event) -> event.getLevel().name() + ": " + event.getMessage()).collect(toList()));
    }

    static FaultEventCollector dummyErrorOrWarningEventCollector() {
        return new FaultEventCollector() {
            @Override
            public void addEvent(FaultEvent event) {
                //quietly ignore
            }

            @Override
            public List<FaultEvent> getCollectedEvents() {
                return emptyList();
            }

            @Override
            public void enableFullLogCollection() {
                //do nothing
            }

            @Override
            public boolean isFullLogCollectionEnabled() {
                return false;
            }

            @Override
            public void addLogs(String logs) {
                //do nothing
            }

            @Override
            public String getLogs() {
                return "";
            }

        };
    }

    default boolean hasErrors() {
        return hasEvents() && getCollectedEvents().stream().allMatch(FaultEvent::isError);
    }

    default void copyErrorsAndLogsFrom(FaultEventCollector otherCollector) {
        addLogs(otherCollector.getLogs());
        otherCollector.getCollectedEvents().forEach(this::addEvent);
    }

}
