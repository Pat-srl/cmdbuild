/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;


import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestInfo;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.utils.lang.CmMapUtils;

import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.encode.CmPackUtils.packOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class RequestSerializer {

    public static CmMapUtils.FluentMap<String, Object> serializeRequestInfo(RequestInfo record) {
        return map(
                "user", record.getUser(),
                "nodeId", record.getNodeId(),
                "sessionId", record.getSessionId(),
                "requestId", record.getRequestId(),
                "actionId", record.getActionId(),
                "path", record.getPath(),
                "method", record.getMethod(),
                "isSoap", record.isSoap(),
                "elapsed", record.getElapsedTimeMillis(),
                "soapActionOrMethod", record.isSoap() ? record.getSoapActionOrMethod() : null,
                "query", record.getQuery(),
                "status", record.getStatusCode(),
                "timestamp", toIsoDateTime(toDateTime(record.getTimestamp())));
    }

    public static CmMapUtils.FluentMap<String, Object> serializeDetailedRequestData(RequestData requestData) {
        return serializeRequestInfo(requestData).with("client", requestData.getClient(),
                "payloadText", requestData.getPayloadText(),
                "payloadBytes", packOrNull(requestData.getPayloadBytes()),
                "payloadContentType", requestData.getPayloadContentType(),
                "payloadSize", requestData.getPayloadSize(),
                "responseText", requestData.getResponseText(),
                "responseBytes", packOrNull(requestData.getResponseBytes()),
                "responseContentType", requestData.getResponseContentType(),
                "responseSize", requestData.getResponseSize(),
                "completed", requestData.isCompleted(),
                "userAgent", requestData.getUserAgent(),
                "errors", serializeErrors(requestData.getFaultEvents()),
                "logs", nullToEmpty(requestData.getLogs()),
                "tcpDump", packOrNull(requestData.getTcpDumpBytes()),
                "requestHeaders", requestData.getRequestHeaders(),
                "responseHeaders", requestData.getResponseHeaders());
    }

    public static Object serializeErrors(List<FaultEvent> data) {
        return data.stream().map((e) -> map("level", serializeEnum(e.getLevel()).toUpperCase(), "message", e.getMessage(), "exception", e.getStacktrace())).collect(toList());
    }
}
