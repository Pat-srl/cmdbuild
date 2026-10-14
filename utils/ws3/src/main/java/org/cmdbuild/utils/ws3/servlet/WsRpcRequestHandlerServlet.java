/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.servlet;

import static com.google.common.base.Preconditions.checkArgument;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;

import org.cmdbuild.utils.ws3.inner.*;
import org.cmdbuild.utils.ws3.inner.WsRestRequest;
import org.cmdbuild.utils.ws3.inner.WsRpcRequest;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;

import static org.cmdbuild.utils.ws3.utils.WsRpcUtils.buildRpcRequestV3;
import static org.cmdbuild.utils.ws3.utils.WsRpcUtils.buildRpcRequestV4;
import static org.cmdbuild.utils.ws3.utils.WsUtils.buildWs3RpcResourceUri;
import static org.cmdbuild.utils.ws3.utils.WsUtils.buildWs4RpcResourceUri;

public class WsRpcRequestHandlerServlet extends WsAbstractHandlerServlet {

    @Override
    protected WsResponseHandler handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        WsRestRequest restRequest = buildWsRestRequest(request, "dummy");
        String resourceUri;
        String payload;
        if (isNotBlank(restRequest.getParam("request"))) {
            payload = restRequest.getParam("request");
        } else if (restRequest.hasPart("request")) {
            payload = readToString(restRequest.getPart("request").getDataSource());
        } else if (restRequest.hasPayload()) {
            payload = restRequest.getPayload();
        } else {
            payload = null;
        }

        boolean isV3 = request.getServletPath() != null && request.getServletPath().matches(".*/v3/?$");

        if (isNotBlank(path)) {
            Matcher matcher = Pattern.compile("^/?([^/]+)/([^/]+)/?$").matcher(path);
            checkArgument(matcher.matches(), "invalid rpc request path =< %s >", path);
            resourceUri = isV3
                    ? buildWs3RpcResourceUri(matcher.group(1), matcher.group(2))
                    : buildWs4RpcResourceUri(matcher.group(1), matcher.group(2));
        } else if (isNotBlank(restRequest.getParam("service")) && isNotBlank(restRequest.getParam("method"))) {
            resourceUri = isV3
                    ? buildWs3RpcResourceUri(restRequest.getParam("service"), restRequest.getParam("method"))
                    : buildWs4RpcResourceUri(restRequest.getParam("service"), restRequest.getParam("method"));
        } else {
            resourceUri = null;
        }
        WsRpcRequest rpcRequest = isV3
                ? buildRpcRequestV3(resourceUri, payload, restRequest.getParams(), restRequest.getHeaders(), restRequest.getInner())
                : buildRpcRequestV4(resourceUri, payload, restRequest.getParams(), restRequest.getHeaders(), restRequest.getInner());
        try {
            logger.debug("processing ws rpc request =< {} >", resourceUri);
            if (rpcRequest.isBatch()) {
                return new WsBatchRequestHelper(getHandler()).handleBatchRequest(rpcRequest);
            } else {
                return getHandler().handleRequest(rpcRequest);
            }
        } catch (Exception ex) {
            throw new Ws3Exception(ex, "error processing request =< %s >", resourceUri);
        }
    }
}
