/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.proxy;

import com.google.common.collect.ImmutableMap;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import static java.lang.String.format;
import java.util.Map;
import static org.cmdbuild.utils.io.CmNetUtils.checkPortIsAvailable;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.ee10.proxy.ProxyServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ConnectHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmProxyUtils {

    public static CmProxyBuilder newHttpProxy(int sourcePort, int targetPort) {
        return new CmProxyBuilderImpl().withSourcePort(sourcePort).withDestinationPort(targetPort);
    }

    public static String proxyUrl(String url, int port) {
        return url.replaceFirst(":[0-9]+", ":" + port);
    }

    public interface CmProxyBuilder {

        CmProxyHelper start();

        CmProxyBuilder withCustomHeader(String key, String value);
    }

    private static class CmProxyBuilderImpl implements CmProxyBuilder {

        private Integer sourcePort, destinationPort;
        private final Map<String, String> customHeaders = map();

        @Override
        public CmProxyHelper start() {
            return new CmProxyHelperImpl(this);
        }

        public CmProxyBuilderImpl withSourcePort(int port) {
            this.sourcePort = port;
            return this;
        }

        public CmProxyBuilderImpl withDestinationPort(int port) {
            this.destinationPort = port;
            return this;
        }

        @Override
        public CmProxyBuilderImpl withCustomHeader(String key, String value) {
            customHeaders.put(key, value);
            return this;
        }
    }

    private final static String CUSTOM_HEADERS_INIT_PARAM = "CM_CUSTOM_HEADERS";

    private static class CmProxyHelperImpl implements CmProxyHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final int port;
        private final Server server;

        private CmProxyHelperImpl(CmProxyBuilderImpl builder) {
            try {
                port = checkNotNullAndGtZero(builder.sourcePort);
                int destinationPort = checkNotNullAndGtZero(builder.destinationPort);
                checkPortIsAvailable(port);
                String destinationUrl = format("http://localhost:%s/", destinationPort);
                logger.info("start reverse proxy from source url = http://localhost:%s to destination url = %s\n", port, destinationUrl);
                server = new Server();
                ServerConnector connector = new ServerConnector(server);
                connector.setPort(port);
                server.setConnectors(new Connector[]{connector});
                ConnectHandler proxy = new ConnectHandler();
                ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
                context.setContextPath("/");
                proxy.setHandler(context);
                ServletHolder proxyServlet = new ServletHolder(MyProxyServlet.class);
                proxyServlet.setInitParameter("proxyTo", destinationUrl);
                proxyServlet.setInitParameter(CUSTOM_HEADERS_INIT_PARAM, toJson(ImmutableMap.copyOf(builder.customHeaders)));
                context.addServlet(proxyServlet, "/*");
                server.setHandler(proxy);
                server.start();
                logger.info("proxy server is running");
            } catch (Exception ex) {
                throw runtime(ex);
            }
        }

        @Override
        public void stop() {
            try {
                server.stop();
                logger.info("proxy server is stopped");
            } catch (Exception ex) {
                throw runtime(ex);
            }
        }

        @Override
        public int getPort() {
            return port;
        }

    }

    public static class MyProxyServlet extends ProxyServlet.Transparent {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Map<String, String> customHeaders = map();

        @Override
        public void init(ServletConfig config) throws ServletException {
            super.init(config);
            customHeaders.putAll(fromJson(config.getInitParameter(CUSTOM_HEADERS_INIT_PARAM), MAP_OF_STRINGS));
        }

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            logger.info("handle request =< {} {} >", request.getMethod(), request.getRequestURL().toString());
            super.service(request, response);
        }

        @Override
        protected void addProxyHeaders(HttpServletRequest clientRequest, Request proxyRequest) {
            super.addProxyHeaders(clientRequest, proxyRequest);
            customHeaders.forEach((k, v) -> proxyRequest.headers(h -> h.add(k, v)));
        }
    }
}
