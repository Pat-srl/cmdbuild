/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Streams.stream;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import static java.lang.String.format;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.ws3.utils.WsUtils.*;

import org.cmdbuild.utils.ws3.api.WsMethod;
import org.cmdbuild.utils.ws3.api.WsResourceRepository;
import org.cmdbuild.utils.ws3.api.WsService;
import org.cmdbuild.utils.ws3.api.WsWarningSource;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;
import org.cmdbuild.utils.ws3.utils.WsUtils.ResourceMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

public class WsRequestHandlerImpl implements WsRequestHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WsWarningSource warningSource;

    private final Map<String, WsRequestMethodHandler> rpcHandlers = map(),
            restHandlers = map();

    public WsRequestHandlerImpl(WsResourceRepository resourceRepository, WsWarningSource warningSource) {
        this.warningSource = checkNotNull(warningSource);
        stream(resourceRepository.getResources()).forEach(this::addHandlersFromServiceBean);
    }

    public WsRequestHandlerImpl(WsResourceRepository resourceRepository) {
        this(resourceRepository, WsDummyWarningSource.INSTANCE);
    }

    @Override
    public WsWarningSource getWarningSource() {
        return warningSource;
    }

    @Override
    public WsResponseHandler handleRequest(WsRpcRequest request) throws Exception {
        WsRequestMethodHandler handler = checkNotNull(rpcHandlers.get(request.getRequestUri()), "ws rpc handler not found for uri =< %s >", request.getRequestUri());
        return handler.handleRequest(request);
    }

    @Override
    public WsResponseHandler handleRequest(WsRestRequest request) throws Exception {
        ResourceMatch<WsRequestMethodHandler> match = checkNotNull(getBestRestResourceMatch(request.getResourceUri(), restHandlers), "ws rest handler not found for uri =< %s >", request.getResourceUri());
        request = request.withParams(match.getParams());
        WsRequestMethodHandler handler = match.getResource();
        try {
            return handler.handleRequest(request);
        } catch (Exception ex) {
            throw new Ws3Exception(ex, "error processing request with handler = %s", handler);
        }
    }

    private void addHandlersFromServiceBean(WsResourceBeanWithInterface service) {
        try {
            logger.debug("add ws handlers from service = {}", service);
            list(service.getIface().getMethods()).stream()
                    .filter(m -> AnnotationUtils.findAnnotation(m, WsMethod.class) != null || AnnotationUtils.findAnnotation(m, Path.class) != null)
                    .forEach(m -> addHandlersFromMethod(service.getIface(), service.getBean(), m));
        } catch (Exception ex) {
            throw new Ws3Exception(ex, "error while loading handlers for service = %s", service);
        }
    }

    private void addHandlersFromMethod(Class classe, Object service, Method method) {
        try {
            logger.debug("add method = {}", method);
            WsMethod wsMethodAnnotation = AnnotationUtils.findAnnotation(method, WsMethod.class);
            Path servicePathAnnotation = AnnotationUtils.findAnnotation(classe, Path.class),
                    wsPathAnnotation = AnnotationUtils.findAnnotation(method, Path.class);
            String serviceName = getServicename(classe, service), methodName;
            if (wsMethodAnnotation != null && isNotBlank(wsMethodAnnotation.value())) {
                methodName = wsMethodAnnotation.value();
            } else {
                methodName = method.getName();
            }
            WsRequestMethodHandler methodHanlder = new WsRequestMethodHandler(service, method, warningSource);
            String rpcResourceUri = method.toString().matches(".*/v3/?$")
                                ? buildWs3RpcResourceUri(serviceName, methodName)
                                : buildWs4RpcResourceUri(serviceName, methodName);
            logger.debug("load ws rpc  resource = {}", rpcResourceUri);
            checkArgument(rpcHandlers.put(rpcResourceUri, methodHanlder) == null, "duplicate rpc handler found for uri =< %s > handler = %s", rpcResourceUri, methodHanlder);
            if (wsPathAnnotation != null) {
                getPathExprs(servicePathAnnotation == null ? null : servicePathAnnotation.value(), wsPathAnnotation.value()).forEach(pathExpr -> {
                    list(GET.class, POST.class, PUT.class, DELETE.class, OPTIONS.class, HEAD.class).stream().filter(a -> AnnotationUtils.findAnnotation(method, a) != null).forEach(a -> {
                        String restMethodName = a.getSimpleName(),
                                restResourceUri = method.toString().matches(".*/v3/?$")
                                        ? buildWs3RestResourceUri(restMethodName, pathExpr)
                                        : buildWs4RestResourceUri(restMethodName, pathExpr);
                        logger.debug("load ws rest resource = {}", restResourceUri);
                        checkArgument(restHandlers.put(restResourceUri, methodHanlder) == null, "duplicate rest handler found for uri =< %s > handler = %s", restResourceUri, methodHanlder);
                    });
                });
            }
        } catch (Exception ex) {
            throw new Ws3Exception(ex, "error while loading handlers for method = %s", method);
        }
    }

    public static Collection<String> getPathExprs(@Nullable String servicePathExpr, @Nullable String wsPathExpr) {
        Set<String> parts = set();
        splitPathExprs(servicePathExpr).forEach(expr1 -> {
            splitPathExprs(wsPathExpr).forEach(expr2 -> {
                parts.add(format("%s/%s", expr1, expr2));
            });
        });
        return parts;
    }

    public static Collection<String> splitPathExprs(@Nullable String expr) {
        Set<String> parts = set();
        Matcher matcher = Pattern.compile("([^{|]*([{][^}]+[}][^{|]*)*)[|]?").matcher(nullToEmpty(expr));
        while (matcher.find()) {
            parts.add(nullToEmpty(matcher.group(1)));
        }
        parts.removeIf(StringUtils::isBlank);
        if (parts.isEmpty()) {
            parts.add("");
        }
        return parts;
    }

    private static String getServicename(Class classe, Object service) {
        WsService wsService = AnnotationUtils.findAnnotation(classe, WsService.class);
        Component component = AnnotationUtils.findAnnotation(classe, Component.class);
        if (wsService != null && isNotBlank(wsService.value())) {
            return wsService.value();
        } else if (component != null && isNotBlank(component.value())) {
            return component.value();
        } else {
            return classe.getSimpleName();
        }
    }

}
