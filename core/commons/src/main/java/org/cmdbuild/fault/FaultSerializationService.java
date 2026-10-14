/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Ordering;
import java.util.List;
import java.util.Map;

public interface FaultSerializationService {

    List<Map<String, Object>> errorToJsonMessages(FaultEvent event);

    default List<Map<String, Object>> buildResponseMessages(FaultEventCollector errors) {
        return buildResponseMessages(errors.getCollectedEvents());
    }

    default List<Map<String, Object>> buildResponseMessages(List<FaultEvent> events) {
        return events.stream().sorted(Ordering.from(FaultLeveOrderErrorsFirst.INSTANCE).onResultOf(FaultEvent::getLevel)).map(this::errorToJsonMessages).flatMap(List::stream).collect(toImmutableList());
    }

}
