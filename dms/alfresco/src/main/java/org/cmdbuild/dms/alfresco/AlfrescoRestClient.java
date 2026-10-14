package org.cmdbuild.dms.alfresco;

import com.fasterxml.jackson.core.type.TypeReference;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.activation.DataHandler;
import java.io.IOException;
import static java.lang.String.format;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.alfresco.config.AlfrescoDmsConfiguration;
import static org.cmdbuild.dms.alfresco.utils.AlfrescoDmsUtils.decodeDocumentId;
import org.cmdbuild.exception.DmsException;
import static org.cmdbuild.utils.http.HttpClientUtils.checkStatus;
import static org.cmdbuild.utils.http.HttpClientUtils.checkStatusAndReadResponse;
import org.cmdbuild.utils.io.CmHttpRequestException;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlfrescoRestClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AlfrescoDmsConfiguration config;
    private final CloseableHttpClient httpClient;
    private String token;

    public static final String RENDITION_TYPE = "doclib",
            ALFRESCO_PROPERTY_AUTHOR = "cm:author",
            ALFRESCO_PROPERTY_CATEGORY = "cmdbuild:classification",
            ALFRESCO_PROPERTY_DESCRIPTION = "cm:description",
            ALFRESCO_PROPERTY_VERSION = "cm:versionLabel";

    public AlfrescoRestClient(AlfrescoDmsConfiguration config) {
        this.config = config;
        httpClient = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
    }

    public boolean isReady() {
        try {
            doGetResourceContent(buildAlfrescoUrl("probes/-ready-"));
            return true;
        } catch (Exception ex) {
            logger.warn("alfresco is not ready", ex);
            return false;
        }
    }

    public Map<String, Object> getResourceInformation(String resourceId) {
        logger.debug("GET resource information for resource with id =< {} >", decodeDocumentId(resourceId));
        Map<String, Map<String, Object>> resp = doGetResourceInformation(buildAlfrescoUrl("nodes/%s", decodeDocumentId(resourceId)));
        return resp.get("entry");
    }

    public Map<String, Object> getResourceInformationByPath(String resourcePath) {
        logger.debug("GET resource information for resource with path =< {} >", resourcePath);
        Map<String, Map<String, Object>> resp = doGetResourceInformation(buildAlfrescoUrl("nodes/-root-"), map("relativePath", resourcePath));
        return resp.get("entry");
    }

    public String getResourceIdByPath(String resourcePath) {
        return toStringNotBlank(getResourceInformationByPath(resourcePath).get("id"), "could not find resource id for path =< %s >", resourcePath);
    }

    public List<Map<String, Object>> listResourceChildren(String resourcePath) {
        logger.debug("GET children information for resource with path =< {} >", resourcePath);
        try {
            return handleRequestWithPagination(buildAlfrescoUrl("nodes/-root-/children"), map("relativePath", resourcePath));
        } catch (DmsException ex) {
            if (ex.getMessage().matches(".*The entity with relativePath: .* was not found\\.")) {
                logger.debug("items not found for path =< {} >", resourcePath);
                return emptyList();
            } else {
                throw ex;
            }
        }
    }

    public List<Map<String, Object>> listResourceVersions(String resourceId) {
        logger.debug("GET versions information for resource with path =< {} >", decodeDocumentId(resourceId));
        return handleRequestWithPagination(buildAlfrescoUrl("nodes/%s/versions", decodeDocumentId(resourceId)));
    }

    public DataHandler getResourceContent(String resourceId, String version) {
        if (version == null) {
            logger.debug("GET content for resource with id =< {} >", decodeDocumentId(resourceId));
            return doGetResourceContent(buildAlfrescoUrl("nodes/%s/content", decodeDocumentId(resourceId)));
        } else {
            logger.debug("GET content for resource with id =< {} >, version =< {} >", decodeDocumentId(resourceId), version);
            return doGetResourceContent(buildAlfrescoUrl("nodes/%s/versions/%s/content", decodeDocumentId(resourceId), version));
        }
    }

    public DataHandler getResourcePreview(String resourceId) {
        logger.debug("GET preview for resource with id =< {} >", decodeDocumentId(resourceId));
        try {
            return doGetResourceContent(buildAlfrescoUrl("nodes/%s/renditions/%s/content", decodeDocumentId(resourceId), RENDITION_TYPE));
        } catch (DmsException ex) {
            if (ex.getMessage().matches(".*Thumbnail was not found for .*")) {
                logger.debug("preview not found for resource with id =< {} >", decodeDocumentId(resourceId));
                return null;
            } else {
                throw ex;
            }
        }
    }

    public Map<String, Object> createResource(String resourcePath, DocumentData document) {
        logger.debug("POST new document for path =< {} >", resourcePath);
        Map<String, Map<String, Object>> resp = doPostResourceContent(buildAlfrescoUrl("nodes/-root-/children"),
                mapOf(String.class, String.class).with("relativePath", resourcePath).with(buildAlfrescoDocumentProperties(document)),
                document);
        generatePreview(toStringNotBlank(resp.get("entry").get("id")));
        return resp.get("entry");
    }

    public Map<String, Object> updateResourceInformation(String resourceId, DocumentData document) {
        logger.debug("PUT new document information for document with id =< {} >", decodeDocumentId(resourceId));
        Map<String, Map<String, Object>> resp = doPutResourceInformation(buildAlfrescoUrl("nodes/%s", decodeDocumentId(resourceId)), buildAlfrescoDocumentProperties(document));
        return resp.get("entry");
    }

    private Map<String, String> buildAlfrescoDocumentProperties(DocumentData document) {
        return map(ALFRESCO_PROPERTY_AUTHOR, toStringOrNull(document.getAuthor()), ALFRESCO_PROPERTY_CATEGORY, nullToEmpty(document.getCategory()), ALFRESCO_PROPERTY_DESCRIPTION, nullToEmpty(document.getDescription()));
    }

    public Map<String, Object> updateResourceData(String resourceId, DocumentData document) {
        logger.debug("PUT new document content for document with id =< {} >", decodeDocumentId(resourceId));
        Map<String, Map<String, Object>> resp = doPutResourceContent(buildAlfrescoUrl("nodes/%s/content", decodeDocumentId(resourceId)), document);
        return resp.get("entry");
    }

    public void deleteResource(String resourceId) {
        logger.debug("DELETE document with id =< {} >", decodeDocumentId(resourceId));
        doDeleteResource(buildAlfrescoUrl("nodes/%s", decodeDocumentId(resourceId)));
    }

    public List<Map<String, Object>> queryResources(String queryTerm, String parentDocumentId) {
        logger.debug("GET query for resources with term =< {} > in folder with id =< {} >", queryTerm, parentDocumentId);
        return handleRequestWithPagination(buildAlfrescoUrl("queries/nodes"), map("term", "*%s*".formatted(queryTerm), "rootNodeId", parentDocumentId));
    }

    private void generatePreview(String resourceId) {
        try {
            HttpPost request = buildHttpRequestWithEntity(buildAlfrescoUrl("nodes/%s/renditions", resourceId), HttpPost::new, new StringEntity(toJson(map("id", RENDITION_TYPE))));
            executeRequestAndCheckStatus(request);
        } catch (Exception ex) {
            logger.debug("could not generate preview for document with id =< {} >", resourceId);
        }
    }

    private <T extends HttpRequestBase> T buildHttpRequest(String path, Function<URI, T> constructor, Map<String, String> parameters) {
        try {
            URIBuilder uriBuilder = new URIBuilder(path);
            // Add query parameter
            parameters.forEach(uriBuilder::addParameter);

            T request = constructor.apply(uriBuilder.build());
            request.setHeader("Authorization", "Basic %s".formatted(getToken()));
            return request;
        } catch (URISyntaxException e) {
            throw new DmsException(e);
        }
    }

    private <T extends HttpRequestBase> T buildHttpRequest(String path, Function<URI, T> constructor) {
        return buildHttpRequest(path, constructor, map());
    }

    private <T extends HttpEntityEnclosingRequestBase> T buildHttpRequestWithEntity(String path, Function<URI, T> constructor, HttpEntity entity, Boolean isMajor) {
        T request = buildHttpRequest(path, constructor, applyOrDefault(isMajor, major -> map("majorVersion", Boolean.toString(major)), map()));
        request.setEntity(entity);
        return request;
    }

    private <T extends HttpEntityEnclosingRequestBase> T buildHttpRequestWithEntity(String path, Function<URI, T> constructor, HttpEntity entity) {
        return buildHttpRequestWithEntity(path, constructor, entity, null);
    }

    private String getAlfrescoApiBaseUrl() {
        return checkNotBlank(config.getAlfrescoApiBaseUrl(), "missing alfresco api base url");
    }

    private String buildAlfrescoUrl(String format, Object... args) {
        return "%s/%s".formatted(getAlfrescoApiBaseUrl(), format(format, args));
    }

    private <T extends Map<String, ?>> T doGetResourceInformation(String url) {
        return doGetResourceInformation(url, map());
    }

    private <T extends Map<String, ?>> T doGetResourceInformation(String url, Map<String, String> parameters) {
        HttpGet request = buildHttpRequest(url, HttpGet::new, map(parameters).with("include", "properties"));
        return executeRequestAndLogResponse(request);
    }

    private List<Map<String, Object>> handleRequestWithPagination(String url) {
        return handleRequestWithPagination(url, map());
    }

    private List<Map<String, Object>> handleRequestWithPagination(String url, Map<String, String> properties) {
        List<Map<String, Object>> values = list();
        Map<String, Map<String, Object>> resp;
        int iterations = 0;
        do {
            resp = doGetResourceInformation(url, map(properties).with("maxItems", config.getAlfrescoPageSize(), "skipCount", String.valueOf(iterations++ * Integer.parseInt(config.getAlfrescoPageSize()))));
            List<Map<String, Map<String, Object>>> data = (List<Map<String, Map<String, Object>>>) resp.get("list").get("entries");
            values.addAll(data.stream().map(entry -> entry.get("entry")).toList());
        } while (((Boolean) ((Map<String, Object>) resp.get("list").get("pagination")).get("hasMoreItems")));
        return values;
    }

    private DataHandler doGetResourceContent(String url) {
        return safe(() -> {
            HttpGet request = buildHttpRequest(url, HttpGet::new);
            CloseableHttpResponse response = httpClient.execute(request);
            checkStatus(response);
            return prepareEntity(response);
        });
    }

    private <T extends Map<String, ?>> T doPostResourceContent(String url, Map<String, String> bodyParameters, DocumentData document) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.RFC6532);
        bodyParameters.forEach(entityBuilder::addTextBody);
        entityBuilder.addBinaryBody("filedata", document.getData(), ContentType.parse(getContentType(document.getData())), document.getFilename());
        HttpPost request = buildHttpRequestWithEntity(url, HttpPost::new, entityBuilder.build());
        return executeRequestAndLogResponse(request);
    }

    private <T extends Map<String, ?>> T doPutResourceInformation(String url, Map<String, String> bodyParameters) {
        return safe(() -> {
            HttpPut request = buildHttpRequestWithEntity(url, HttpPut::new, new StringEntity(toJson(map("properties", bodyParameters))));
            return executeRequestAndLogResponse(request);
        });
    }

    private <T extends Map<String, ?>> T doPutResourceContent(String url, DocumentData document) {
        return safe(() -> {
            HttpPut request = buildHttpRequestWithEntity(url, HttpPut::new, new ByteArrayEntity(document.getData(), ContentType.parse(getContentType(document.getData()))), document.isMajorVersion());
            return executeRequestAndLogResponse(request);
        });
    }

    private void doDeleteResource(String url) {
        HttpDelete request = buildHttpRequest(url, HttpDelete::new);
        executeRequestAndCheckStatus(request);
    }

    private DataHandler prepareEntity(HttpResponse response) {
        return safe(() -> {
            HttpEntity entity = response.getEntity();
            byte[] data = CmIoUtils.toByteArray(entity.getContent());
            String contentType = Optional.ofNullable(entity.getContentType()).map(Header::getValue).orElseGet(() -> getContentType(data));
            logger.debug("got data = {} bytes {}", data.length, entity.getContentType());
            logger.debug("headers = \n\n{}\n", mapToLoggableStringLazy(map(list(response.getAllHeaders()), Header::getName, Header::getValue)));
            EntityUtils.consumeQuietly(entity);
            return newDataHandler(data, contentType);
        });
    }

    private <T> T safe(Callable<T> runnable) {
        try {
            return runnable.call();
        } catch (CmHttpRequestException ex) {
            // to read the response, increase `org.cmdbuild.utils.io.HttpClientUtils` log to DEBUG
            throw new DmsException("%s %s - %s", Integer.toString(ex.getStatusLine().getStatusCode()), ex.getStatusLine().getProtocolVersion(), firstNotNull(getErrorCodeSafe(ex), "error"));
        } catch (IOException | ClassCastException ex) {
            throw new DmsException(ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T extends Map<String, ?>> T executeRequestAndLogResponse(HttpRequestBase request) {
        return safe(() -> {
            T resp = fromJson(checkStatusAndReadResponse(httpClient.execute(request)), new TypeReference<>() {
            });
            logger.debug("got response = \n\n{}\n", mapToLoggableString(resp));
            return resp;
        });
    }

    private void executeRequestAndCheckStatus(HttpRequestBase request) {
        safe(() -> {
            checkStatus(httpClient.execute(request));
            logger.debug("request executed successfully");
            return null;
        });
    }

    private String getToken() {
        if (token == null) {
            token = Base64.getEncoder().encodeToString("%s:%s".formatted(config.getAlfrescoUser(), config.getAlfrescoPassword()).getBytes());
        }
        return token;
    }

    private String getErrorCodeSafe(CmHttpRequestException ex) {
        try {
            if (!ex.hasJsonContent()) {
                return null;
            } else {
                Map<String, Object> map = ex.getContentAsJsonSafe();
                if (map.containsKey("error") && map.get("error") instanceof Map errorCode) {
                    return toStringOrNull(errorCode.get("errorKey"));
                } else {
                    return null;
                }
            }
        } catch (Exception exx) {
            logger.warn("error reading json response", exx);
            return null;
        }
    }
}
