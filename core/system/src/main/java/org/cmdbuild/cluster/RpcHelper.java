/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import jakarta.annotation.Nullable;

public interface RpcHelper {

    String invokeRpcMethod(@Nullable String sessionId, String payload);

    default String invokeRpcMethod(String payload) {
        return invokeRpcMethod(null, payload);
    }
}
