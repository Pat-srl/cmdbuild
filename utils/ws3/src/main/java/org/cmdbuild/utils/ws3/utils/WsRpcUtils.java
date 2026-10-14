/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;
import jakarta.annotation.Nullable;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.ws3.utils.WsUtils.*;

import org.cmdbuild.utils.ws3.inner.WsRpcRequest;
import org.cmdbuild.utils.ws3.inner.WsRpcRequestImpl;

public class WsRpcUtils {

    private final static WsRpcRequestData EMPTY_DATA = new WsRpcRequestData(null, null, null, null, null, null, null, null);

    public static WsRpcRequest parseRpcRequestV4(String payload) {
        return buildRpcRequestV4(null, checkNotBlank(payload), emptyMap(), emptyMap(), null);
    }

    public static WsRpcRequest buildRpcRequestV4(@Nullable String resourceUri, @Nullable String payload, Map<String, String> requestParams, Map<String, String> requestHeaders, @Nullable Object request) {
        WsRpcRequestData data = isBlank(payload) ? EMPTY_DATA : readPayloadToData(payload);
        if (isBlank(resourceUri)) {
            resourceUri = data.buildResourceUriV4();
        }
        return new WsRpcRequestImpl(
                data.id,
                resourceUri,
                map(requestParams).with(data.params),
                map(requestHeaders).with(data.headers),
                data.data,
                request,
                data.extract,
                data.batch.stream().map(r -> new WsRpcRequestImpl(r.id, buildWs4RpcResourceUri(r.service, r.method), r.params, requestHeaders, r.data, request, r.extract, emptyList())).collect(toImmutableList()));
    }

    public static WsRpcRequest parseRpcRequestV3(String payload) {
        return buildRpcRequestV3(null, checkNotBlank(payload), emptyMap(), emptyMap(), null);
    }

    public static WsRpcRequest buildRpcRequestV3(@Nullable String resourceUri, @Nullable String payload, Map<String, String> requestParams, Map<String, String> requestHeaders, @Nullable Object request) {
        WsRpcRequestData data = isBlank(payload) ? EMPTY_DATA : readPayloadToData(payload);
        if (isBlank(resourceUri)) {
            resourceUri = data.buildResourceUriV3();
        }
        return new WsRpcRequestImpl(
                data.id,
                resourceUri,
                map(requestParams).with(data.params),
                map(requestHeaders).with(data.headers),
                data.data,
                request,
                data.extract,
                data.batch.stream().map(r -> new WsRpcRequestImpl(r.id, buildWs3RpcResourceUri(r.service, r.method), r.params, requestHeaders, r.data, request, r.extract, emptyList())).collect(toImmutableList()));
    }

    public static WsRpcRequestData readPayloadToData(String payload) {
        checkNotBlank(payload);
        JsonNode json = fromJson(payload, JsonNode.class);
        if (json.isArray()) {
            List<WsRpcRequestData> elements = stream(json.elements()).map(e -> fromJson(e, WsRpcRequestData.class)).collect(toList());
            return new WsRpcRequestData(null, null, null, null, null, null, elements, null);
        } else {
            return fromJson(json, WsRpcRequestData.class);
        }
    }

    public static String serializeWs3RpcRequest(WsRpcRequest request) {
        return toJson(map(
                "service", request.getService(),
                "method", request.getMethod(),
                "id", request.getId(),
                "params", request.getParams(),
                "headers", request.getHeaders(),
                "extract", request.getBindingsToExtractForNextBatchRequests(),
                "data", fromJson(request.getPayload(), JsonNode.class),
                "batch", request.getBatchRequests().stream().map(WsRpcUtils::serializeWs3RpcRequest).collect(toImmutableList())
        ));
    }

    public static class WsRpcRequestData {

        private final String service, method, id;
        private final Map<String, String> params, headers, extract;
        private final List<WsRpcRequestData> batch;
        private final JsonNode data;

        public WsRpcRequestData(
                @JsonProperty("service") String service,
                @JsonProperty("method") String method,
                @JsonProperty("id") String id,
                @JsonProperty("params") Map<String, String> params,
                @JsonProperty("headers") Map<String, String> headers,
                @JsonProperty("extract") Map<String, String> extract,
                @JsonProperty("batch") List<WsRpcRequestData> batch,
                @JsonProperty("data") JsonNode data) {
            this.id = id;
            this.params = firstNotNull(params, emptyMap());
            this.headers = firstNotNull(headers, emptyMap());
            this.extract = firstNotNull(extract, emptyMap());
            this.batch = firstNotNull(batch, emptyList());
            this.data = data;
            this.service = service;
            this.method = method;
        }

        public boolean isBatch() {
            return !batch.isEmpty();
        }

        private String buildResourceUriV4() {
            if (isBatch()) {
                return WS4RPC_BATCH_REQUEST;
            } else {
                return buildWs4RpcResourceUri(service, method);
            }
        }

        private String buildResourceUriV3() {
            if (isBatch()) {
                return WS3RPC_BATCH_REQUEST;
            }
            return buildWs3RpcResourceUri(service, method);
        }

    }
}
