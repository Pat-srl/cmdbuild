/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cmdbuild.utils.ws3.inner.WsResponseHandler;
import org.cmdbuild.utils.ws3.inner.WsRestRequest;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;
import static org.cmdbuild.utils.ws3.utils.WsUtils.buildWs3RestResourceUri;
import static org.cmdbuild.utils.ws3.utils.WsUtils.buildWs4RestResourceUri;

public final class WsRestRequestHandlerServlet extends WsAbstractHandlerServlet {

    @Override
    protected WsResponseHandler handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String method = request.getMethod(), path = request.getPathInfo();

        String servletPath = request.getServletPath();
        String resourceUri = (servletPath!= null && servletPath.matches(".*/v3/?$"))
            ? buildWs3RestResourceUri(method, path)
            : buildWs4RestResourceUri(method, path);

        try {
            WsRestRequest wsRequest = buildWsRestRequest(request, resourceUri);
            logger.debug("processing ws rest request =< {} >", resourceUri);
            return getHandler(request).handleRequest(wsRequest);
        } catch (Exception ex) {
            throw new Ws3Exception(ex, "error processing request =< %s >", resourceUri);
        }
    }
}
