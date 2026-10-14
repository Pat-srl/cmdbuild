/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;

@Deprecated//TODO: patch db, remove this
public class FaultEventsData {

    private final static FaultEventsData EMPTY = new FaultEventsData(emptyList());

    private final List<FaultEvent> data;

    @JsonCreator
    public FaultEventsData(@JsonProperty("data") List<FaultEventImpl> data) {
        this.data = ImmutableList.copyOf(data);
    } 

    @JsonProperty("data")
    public List<FaultEvent> getData() {
        return data;
    }

    @Deprecated
    public static FaultEventsData fromErrorsAndWarningEvents(List<FaultEvent> events) {
        return new FaultEventsData(events.stream().map((e) -> new FaultEventImpl(e.getLevel(), e.getMessage(), e.getStacktrace())).collect(toList()));
    }

    public static FaultEventsData emptyErrorMessagesData() {
        return EMPTY;
    }

}
