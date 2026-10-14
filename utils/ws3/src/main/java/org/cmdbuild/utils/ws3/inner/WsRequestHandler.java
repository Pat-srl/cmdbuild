/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import org.cmdbuild.utils.ws3.api.WsWarningSource;

public interface WsRequestHandler {

    WsResponseHandler handleRequest(WsRpcRequest request) throws Exception;

    WsResponseHandler handleRequest(WsRestRequest request) throws Exception;

    WsWarningSource getWarningSource();

    default String handleRequestToString(WsRpcRequest request) throws Exception {
        return handleRequest(request).prepareResponse().getResponseAsString();
    }

    default String handleRequestToString(WsRestRequest request) throws Exception {
        return handleRequest(request).prepareResponse().getResponseAsString();
    }
}
