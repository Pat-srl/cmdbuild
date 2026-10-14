/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.ws3.utils.WsUtils.parseWs4RpcResourceUri;

public interface WsRpcRequest extends WsRequest {

    String getRequestUri();

    @Nullable
    String getId();

    Map<String, String> getBindingsToExtractForNextBatchRequests();

    List<WsRpcRequest> getBatchRequests();

    default String getService() {
        return parseWs4RpcResourceUri(getRequestUri()).getService();
    }

    default String getMethod() {
        return parseWs4RpcResourceUri(getRequestUri()).getMethod();
    }

    default boolean isBatch() {
        return !getBatchRequests().isEmpty();
    }

    default boolean hasId() {
        return isNotBlank(getId());
    }

}
