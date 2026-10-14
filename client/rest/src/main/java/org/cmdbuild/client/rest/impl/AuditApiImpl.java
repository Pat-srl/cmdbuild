/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import com.google.common.collect.Streams;
import static com.google.common.collect.Streams.stream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.fault.FaultEventImpl;
import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestInfo;
import org.cmdbuild.audit.RequestDataImpl;
import org.cmdbuild.audit.RequestInfoImpl;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.client.rest.api.AuditApi;
import org.cmdbuild.fault.FaultLevel;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytesOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

public class AuditApiImpl extends AbstractServiceClientImpl implements AuditApi {

    public AuditApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public String mark() {
        logger.debug("mark");
        JsonElement response = get("system/audit/mark").asJson();
        return checkNotBlank(toString(response.getAsJsonObject().getAsJsonObject("data").get("mark")));
    }

    @Override
    public RequestData getRequestData(String requestId) {
        JsonElement element = get("system/audit/requests/" + requestId).asJson();
        JsonObject data = element.getAsJsonObject().getAsJsonObject("data");
        return RequestDataImpl.builder()
                .withUser(toString(data.get("user")))
                .withNodeId(toString(data.get("nodeId")))
                .withActionId(toString(data.get("actionId")))
                .withPath(toString(data.get("path")))
                .withRequestId(toString(data.get("requestId")))
                .withSessionId(toString(data.get("sessionId")))
                .withMethod(toString(data.get("method")))
                .withQuery(toString(data.get("query")))
                .withUserAgent(toString(data.get("userAgent")))
                .withClient(toString(data.get("client")))
                .withPayloadText(toString(data.get("payloadText")))
                .withPayloadBytes(unpackBytesOrNull(toString(data.get("payloadBytes"))))
                .withResponseText(toString(data.get("responseText")))
                .withResponseBytes(unpackBytesOrNull(toString(data.get("responseBytes"))))
                .withTcpDumpBytes(unpackBytesOrNull(toString(data.get("tcpDump"))))
                .withPayloadContentType(toString(data.get("payloadContentType")))
                .withPayloadSize(toLong(data.get("payloadSize")))
                .withResponseSize(toLong(data.get("responseSize")))
                .withResponseContentType(toString(data.get("responseContentType")))
                .withTimestamp(toDateTime(data.get("timestamp")))
                .withElapsedTimeMillis(toInteger(data.get("elapsed")))
                .withStatusCode(toInteger(data.get("status")))
                .withSoap(toBoolean(data.get("isSoap")), toString(data.get("soapActionOrMethod")))
                .withFaultEvents(stream(data.get("errors").getAsJsonArray()).map(JsonElement::getAsJsonObject)
                        .map((e) -> new FaultEventImpl(parseEnum(toString(e.get("level")), FaultLevel.class), toString(e.get("message")), toString(e.get("exception")))).collect(toList()))
                .withLogs(toString(data.get("logs")))
                .build();
    }

    @Override
    public List<RequestInfo> getRequestsSince(String mark) {
        return parseResponse(get("system/audit/requests?since=" + mark).asJson());
    }

    @Override
    public List<RequestInfo> getLastRequests(int limit) {
        return parseResponse(get("system/audit/requests?limit=" + limit).asJson());
    }

    @Override
    public List<RequestInfo> getLastErrors(int limit) {
        return parseResponse(get("system/audit/errors?limit=" + limit).asJson());
    }

    private List<RequestInfo> parseResponse(JsonElement reqResponse) {
        JsonArray requests = reqResponse.getAsJsonObject().getAsJsonArray("data");
        return Streams.stream(requests).map((JsonElement::getAsJsonObject)).map((data) -> {
            return RequestInfoImpl.builder()
                    .withUser(toString(data.get("user")))
                    .withActionId(toString(data.get("actionId")))
                    .withPath(toString(data.get("path")))
                    .withRequestId(toString(data.get("requestId")))
                    .withSessionId(toString(data.get("sessionId")))
                    .withMethod(toString(data.get("method")))
                    .withQuery(toString(data.get("query")))
                    .withStatusCode(toInteger(data.get("status")))
                    .withElapsedTimeMillis(toInteger(data.get("elapsed")))
                    .withSoap(toBoolean(data.get("isSoap")), toString(data.get("soapActionOrMethod")))
                    .withTimestamp(toDateTime(data.get("timestamp")))
                    .build();

        }).collect(toList());
    }

}
