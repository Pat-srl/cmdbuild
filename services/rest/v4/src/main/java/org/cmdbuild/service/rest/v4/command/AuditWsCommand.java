/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestInfo;
import org.cmdbuild.audit.RequestTrackingRepository;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;

/**
 * @author ldare
 */
@Component
public class AuditWsCommand {

    private final RequestTrackingRepository requestTrackingRepository;

    public AuditWsCommand(RequestTrackingRepository requestTrackingRepository) {
        this.requestTrackingRepository = requestTrackingRepository;
    }

    public String doMark() {
        return String.valueOf(now().toInstant().toEpochMilli());
    }

    public List<RequestInfo> doGetRequests(String mark, Long limit) {
        List<RequestInfo> requests;
        if (isNotBlank(mark)) {
            ZonedDateTime dateTime = toDateTime(Long.valueOf(mark));
            requests = requestTrackingRepository.getRequestsSince(dateTime);
        } else {
            requests = requestTrackingRepository.getLastRequests(firstNonNull(limit, 10L));
        }
        return requests;
    }

    public List<RequestInfo> doGetErrors(String mark, Long limit) {
        List<RequestInfo> requests;
        if (isNotBlank(mark)) {
            ZonedDateTime dateTime = toDateTime(Long.valueOf(mark));
            requests = requestTrackingRepository.getErrorsSince(dateTime);
        } else {
            requests = requestTrackingRepository.getLastErrors(firstNonNull(limit, 10L));
        }
        return requests;
    }

    public RequestData doGetRequest(String id) {
        return requestTrackingRepository.getRequest(id);
    }
}
