/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;

public class WsBatchRequestHelper {

    private final WsRequestHandler requestHandler;

    private final Map<String, String> bindings = map();
    private final List<JsonNode> responses = list();

    public WsBatchRequestHelper(WsRequestHandler requestHandler) {
        this.requestHandler = checkNotNull(requestHandler);
    }

    public WsResponseHandler handleBatchRequest(WsRpcRequest rpcRequest) throws Exception {
        checkArgument(rpcRequest.isBatch());

        rpcRequest.getBatchRequests().forEach(rethrowConsumer(this::processRequest));

        Object response = map("success", true, "batch", responses);
        return new WsResponseHandlerImpl(rpcRequest, response, APPLICATION_JSON, emptyList());//TODO improve warning messageprocessing in batch request
    }

    private void processRequest(WsRpcRequest rpcRequest) throws Exception {
        checkArgument(!rpcRequest.isBatch(), "invalid nested rpc batch request");

        if (!bindings.isEmpty()) {
            Map<String, String> params = map(rpcRequest.getParams()).mapValues((k, v) -> bindings.getOrDefault(v, v));
            String payload = rpcRequest.hasPayload() ? rpcRequest.getPayload() : null;
            if (payload != null) {
                for (Map.Entry<String, String> entry : bindings.entrySet()) {
                    payload = payload.replace(entry.getKey(), entry.getValue());
                }
            }
            rpcRequest = new WsRpcRequestImpl(rpcRequest.getId(), rpcRequest.getRequestUri(), params, rpcRequest.getHeaders(), payload == null ? null : fromJson(payload, JsonNode.class), rpcRequest.getInner(), rpcRequest.getBindingsToExtractForNextBatchRequests(), rpcRequest.getBatchRequests());
        }

        String responseStr = requestHandler.handleRequest(rpcRequest).prepareResponse().getResponseAsString();
        JsonNode node = fromJson(responseStr, JsonNode.class);

        rpcRequest.getBindingsToExtractForNextBatchRequests().forEach((target, source) -> {
            String value = extractJsonBindingFromNode(node.get("data"), source);
            bindings.put(target, value);
        });

        JsonNode res = node;
        if (rpcRequest.hasId()) {
            ObjectNode objectNode = node.deepCopy();
            objectNode.put("id", rpcRequest.getId());
            res = objectNode;
        }

        responses.add(res);
    }

    @Nullable
    private String extractJsonBindingFromNode(@Nullable JsonNode node, String binding) {
        if (node == null || !node.isObject()) {
            return null;
        } else {
            JsonNode value = node.get(binding); //TODO improve this, use multi leven binding or jsonpath
            return value.isValueNode() ? value.asText() : null;
        }
    }

}
