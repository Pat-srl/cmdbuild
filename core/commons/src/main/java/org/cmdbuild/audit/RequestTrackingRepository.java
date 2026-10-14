/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import com.google.common.collect.Ordering;
import java.time.ZonedDateTime;
import java.util.List;
import static java.util.stream.Collectors.toList;

public interface RequestTrackingRepository {

    List<RequestInfo> getRequests(ZonedDateTime from, ZonedDateTime to);

    List<RequestInfo> getRequestsSince(ZonedDateTime from);

    List<RequestInfo> getLastRequests(long limit);

    RequestData getRequest(String requestIdOrTrackingId);

    List<RequestInfo> getErrorsSince(ZonedDateTime dateTime);

    List<RequestInfo> getLastErrors(long limit);

    default List<RequestInfo> getRequestsSince(ZonedDateTime from, long limit) {
        return getRequestsSince(from).stream().sorted(Ordering.natural().onResultOf(RequestInfo::getTimestamp).reversed()).limit(limit).sorted(Ordering.natural().onResultOf(RequestInfo::getTimestamp)).collect(toList());
    }
}
