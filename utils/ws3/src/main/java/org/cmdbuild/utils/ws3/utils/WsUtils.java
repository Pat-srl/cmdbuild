/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.Ordering;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsUtils {

    public static final String WS3RPC_BATCH_REQUEST = "ws3rpc:system.batch";
    public static final String WS4RPC_BATCH_REQUEST = "ws4rpc:system.batch";

    private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String buildWs3RestResourceUri(String httpMethod, String path) {
        return format("ws3rest:%s:%s", checkNotBlank(httpMethod).toLowerCase(), nullToEmpty(path).replaceAll("^/+|/+$", "").replaceAll("/+", "/"));
    }

    public static String buildWs3RpcResourceUri(String service, String method) {
        return format("ws3rpc:%s.%s", checkNotBlank(service, "rpc service cannot be null").replaceFirst("(?i)(Ws|Service)$", ""), checkNotBlank(method, "rpc method cannot be null")).toLowerCase();
    }

    public static String buildWs4RestResourceUri(String httpMethod, String path) {
        return format("ws4rest:%s:%s", checkNotBlank(httpMethod).toLowerCase(), nullToEmpty(path).replaceAll("^/+|/+$", "").replaceAll("/+", "/"));
    }

    public static String buildWs4RpcResourceUri(String service, String method) {
        return format("ws4rpc:%s.%s", checkNotBlank(service, "rpc service cannot be null").replaceFirst("(?i)(Ws|Service)$", ""), checkNotBlank(method, "rpc method cannot be null")).toLowerCase();
    }

    public static WsRpcResourceUri parseWs3RpcResourceUri(String uri) {
        return new Ws3RpcResourceUri(uri);
    }

    public static WsRpcResourceUri parseWs4RpcResourceUri(String uri) {
        return new Ws4RpcResourceUri(uri);
    }

    public abstract static class WsRpcResourceUri {

        private final String service, method;

        public WsRpcResourceUri(String uri, String regexp) {
            Matcher matcher = Pattern.compile(regexp).matcher(uri);
            checkArgument(matcher.matches());
            service = checkNotBlank(matcher.group(1));
            method = checkNotBlank(matcher.group(2));
        }

        public String getService() {
            return service;
        }

        public String getMethod() {
            return method;
        }
    }

    public static class Ws3RpcResourceUri extends WsRpcResourceUri {
        public Ws3RpcResourceUri(String uri) {
            super(uri, "ws3rpc:([^.]+)[.]([^.]+)");
        }
    }

    public static class Ws4RpcResourceUri extends WsRpcResourceUri {
        public Ws4RpcResourceUri(String uri) {
            super(uri, "ws4rpc:([^.]+)[.]([^.]+)");
        }
    }

    public interface ResourceMatch<T> {

        T getResource();

        Map<String, String> getParams();
    }

    @Nullable
    public static <T> ResourceMatch<T> getBestRestResourceMatch(String requestUri, Map<String, T> resources) {
        LOGGER.trace("get resource match for uri =< {} >", requestUri);
        if (resources.containsKey(requestUri)) {
            T res = resources.get(requestUri);
            LOGGER.trace("found exact match for resource = {}", res);
            return new ResourceMatchImpl(res, emptyMap());
        } else {
            List<String> parts = getResourceUriPathParts(requestUri);
            int count = parts.size();
            List<CandidateResource> candidates = resources
                    .entrySet()
                    .stream()
                    .filter(e -> getResourceUriPathParts(e.getKey()).size() == count)
                    .map(e -> new CandidateResource(e.getKey(), getResourceUriPath(e.getKey()), e.getValue()))
                    .sorted(Ordering.natural().onResultOf(CandidateResource::getResourceUri))
                    .collect(toList());
            while (true) {
                if (candidates.isEmpty()) {
                    LOGGER.trace("no resource found for uri =< {} >", requestUri);
                    return null;
                } else if (candidates.size() == 1) {
                    CandidateResource element = getOnlyElement(candidates);
                    LOGGER.trace("found single match resource = {}", element.getResource());
                    return new ResourceMatchImpl(element.getResource(), getParamsFromUrl(element, requestUri));
                } else if (parts.isEmpty()) {
                    CandidateResource element = candidates.get(0);
                    LOGGER.trace("found many candidates, returning first one = {}", element.getResource());
                    return new ResourceMatchImpl(element.getResource(), getParamsFromUrl(element, requestUri));
                } else {
                    String part = parts.remove(0);
                    LOGGER.trace("filter candidates using part =< {} >", part);
                    List<CandidateResource> selected = list();
                    candidates.stream().filter(c -> equal(c.getTestPath(), part) || c.getTestPath().startsWith(part + "/")).forEach(selected::add);
                    candidates.stream().filter(c -> matchFirstPart(c.getTestPath(), part)).forEach(selected::add);
                    LOGGER.trace("selected candidates = \n\n{}\n", lazyString(() -> selected.stream().map(c -> format("        %-60s = %s", c.getTestPath(), c.getResource())).collect(joining("\n"))));
                    candidates = selected.stream().map(c -> c.withTestUri(c.getTestPath().replaceFirst("[^/]+/?", ""))).collect(toList());
                }
            }
        }
    }

    private static String getResourceUriPath(String uri) {
        return uri.replaceFirst("ws[34]rest:([^:]+):", "$1/");
    }

    public static List<String> getResourceUriPathParts(String uri) {
        return list(getResourceUriPath(uri).split("/"));
    }

    public static boolean matchFirstPart(String restResourcePattern, String part) {
        String firstPart = getResourceUriPathParts(restResourcePattern).get(0);
        Matcher matcher = Pattern.compile("^[{]([^: ]+)( *: *(.*))?[}]$").matcher(firstPart);
        if (matcher.find()) {
            String pattern = matcher.group(3);
            if (isBlank(pattern)) {
                return true;
            } else {
                return part.matches(pattern);
            }
        } else {
            return false;
        }
    }

    private static Map<String, String> getParamsFromUrl(CandidateResource resource, String requestUri) {
        String resourceUri = resource.getResourceUri();
        List<String> requestParts = list(requestUri.replaceFirst("ws[34]rest:[^:]+:", "").split("/")),
                resourceParts = list(resourceUri.replaceFirst("ws[34]rest:[^:]+:", "").split("/"));
        checkArgument(requestParts.size() == resourceParts.size());
        Map<String, String> parts = map();
        for (int i = 0; i < requestParts.size(); i++) {
            String requestPart = requestParts.get(i),
                    resourcePart = resourceParts.get(i);
            Matcher matcher = Pattern.compile("^[{]([^: ]+)( *:.*)?[}]$").matcher(resourcePart);
            if (matcher.find()) {
                String key = trimAndCheckNotBlank(matcher.group(1));
                LOGGER.trace("load path param {} =< {} >", key, requestPart);
                parts.put(key, requestPart);
            }
        }
        return parts;
    }

    private static class CandidateResource<T> {

        private final String resourceUri, testPath;
        private final T resource;

        public CandidateResource(String resourceUri, String testPath, T resource) {
            this.resourceUri = checkNotBlank(resourceUri);
            this.testPath = checkNotNull(testPath);
            this.resource = checkNotNull(resource);
        }

        public String getResourceUri() {
            return resourceUri;
        }

        public String getTestPath() {
            return testPath;
        }

        public T getResource() {
            return resource;
        }

        public CandidateResource<T> withTestUri(String testUri) {
            return new CandidateResource<>(resourceUri, testUri, resource);
        }

    }

    private static class ResourceMatchImpl<T> implements ResourceMatch<T> {

        private final T resource;
        private final Map<String, String> params;

        public ResourceMatchImpl(T resource, Map<String, String> params) {
            this.resource = checkNotNull(resource);
            this.params = ImmutableMap.copyOf(params);
        }

        @Override
        public T getResource() {
            return resource;
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }

    }
}
