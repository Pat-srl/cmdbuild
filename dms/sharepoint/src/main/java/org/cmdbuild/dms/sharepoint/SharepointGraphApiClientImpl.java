/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.sharepoint;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.cmdbuild.dms.DocumentData;
import static org.cmdbuild.dms.sharepoint.SharepointDmsAuthProtocol.MSAZUREOAUTH2_APPLICATION;
import static org.cmdbuild.dms.sharepoint.SharepointDmsAuthProtocol.MSAZUREOAUTH2_DELEGATED;
import static org.cmdbuild.dms.sharepoint.SharepointDmsAuthProtocol.MSAZUREOAUTH2_PASSWORD;
import static org.cmdbuild.dms.sharepoint.SharepointDmsUtils.escapeUrlPart;
import org.cmdbuild.dms.sharepoint.config.SharepointConfiguration;
import org.cmdbuild.exception.DmsException;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.http.HttpClientUtils.checkStatus;
import static org.cmdbuild.utils.http.HttpClientUtils.checkStatusAndClose;
import static org.cmdbuild.utils.http.HttpClientUtils.checkStatusAndReadResponse;
import org.cmdbuild.utils.io.CmHttpRequestException;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmInlineUtils;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMapsKeepOriginal;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharepointGraphApiClientImpl implements SharepointGraphApiClient {

    private static final int UPLOAD_PART_SIZE = 327680 * 4; // the size of each byte range MUST be a multiple of 320 KiB (327,680 bytes)
    private static final int UPLOAD_SPLIT_THRESHOLD = UPLOAD_PART_SIZE * 2; //must be less than 4MB

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SharepointConfiguration config;
    private final BiConsumer<Map<String, Object>, DocumentData> customPropertySetter;
    private String accessToken;
    private String refreshToken;
    private String tokenRegion;
    private ZonedDateTime tokenRelease;
    private ZonedDateTime tokenExpiration;
    private final Supplier<String> driveId = Suppliers.memoize(this::acquireDriveId);
    private final Supplier<String> listId = Suppliers.memoize(this::acquireListId);
    private final Supplier<String> siteId = Suppliers.memoize(this::acquireSiteId);
    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    private boolean isOk = true;

    protected SharepointGraphApiClientImpl(SharepointConfiguration config) {
        this(config, (m, d) -> {
        });
    }

    protected SharepointGraphApiClientImpl(SharepointConfiguration config, BiConsumer<Map<String, Object>, DocumentData> customPropertySetter) {
        this.config = checkNotNull(config);
        this.customPropertySetter = checkNotNull(customPropertySetter);
    }

    @Override
    public void close() throws IOException {
        connectionManager.close();
    }

    @Override
    public synchronized boolean isOk() {
        if (!isOk) {
            return false;
        } else {
            try {
                checkOk();
                return true;
            } catch (Exception ex) {
                logger.warn("sharepoint helper is not ok = {}", ex.toString());
                return isOk = false;
            }
        }
    }

    @Override
    public synchronized void checkOk() {
        switch (config.getSharepointAuthProtocol()) {
            case MSAZUREOAUTH2_DELEGATED, MSAZUREOAUTH2_PASSWORD ->
                getResource("me");
        }
        getResource(format("drives/%s/root/children", escapeUrlPart(getDriveId())));
        logger.debug("sharepoint graph api client is OK");
    }

    @Override
    public synchronized void checkRefreshToken() {
        switch (config.getSharepointAuthProtocol()) {
            case MSAZUREOAUTH2_DELEGATED, MSAZUREOAUTH2_PASSWORD -> {
                if (tokenRelease != null && tokenExpiration != null && isNotBlank(refreshToken) && isNotBlank(accessToken)) {
                    long initialSeconds = Duration.between(tokenRelease, tokenExpiration).getSeconds();
                    long remainingSeconds = Duration.between(now(), tokenExpiration).getSeconds();
                    if (remainingSeconds < initialSeconds / 2 || remainingSeconds < 600) {
                        logger.debug("{} seconds remaining before token expiration, execute token refresh", remainingSeconds);
                        refreshMsAccessToken();
                    } else {
                        logger.debug("{} seconds remaining before token expiration, token is ok", remainingSeconds);
                    }
                }
            }
            case MSAZUREOAUTH2_APPLICATION -> {
                if (tokenRelease != null && tokenExpiration != null && isNotBlank(accessToken)) {
                    long initialSeconds = Duration.between(tokenRelease, tokenExpiration).getSeconds();
                    long remainingSeconds = Duration.between(now(), tokenExpiration).getSeconds();
                    if (remainingSeconds < initialSeconds / 2 || remainingSeconds < 600) {
                        logger.debug("{} seconds remaining before token expiration, execute token refresh", remainingSeconds);
                        acquireMsApplicationAccessToken();
                    } else {
                        logger.debug("{} seconds remaining before token expiration, token is ok", remainingSeconds);
                    }
                }
            }
            default ->
                throw unsupported("unsupported sharepoint auth protocol =< %s >", config.getSharepointAuthProtocol());
        }
    }

    @Override
    public List<Map<String, Object>> listFolderContent(List<String> path) {
        return listFolderContent(path, false);
    }

    @Override
    public List<Map<String, Object>> listFolderContentCreateIfMissing(List<String> path) {
        return listFolderContent(path, true);
    }

    @Override
    public Map<String, Object> getItemByPath(List<String> path) {
        return checkNotNull(doGetItemByPath(path, false));
    }

    @Nullable
    @Override
    public Map<String, Object> getItemByPathOrNull(List<String> path) {
        return doGetItemByPath(path, true);
    }

    @Override
    public Map<String, Object> getItemById(String itemId, boolean includeVersions) {
        String resource = "drives/%s/items/%s?$expand=listItem($expand=fields)";
        if (includeVersions) {
            resource += ",versions";
        }
        Map<String, Object> item = flattenMapsKeepOriginal(getResource(format(resource, escapeUrlPart(getDriveId()), escapeUrlPart(checkNotBlank(itemId)))));
        if (includeVersions) {
            Map<String, Map<String, Object>> driveItemVersions = map((List<Map<String, Object>>) item.get("versions"), m -> toStringNotBlank(m.get("id")), Function.identity());
            Map<String, Map<String, Object>> listItemVersions = map((List<Map<String, Object>>) getResource(format("sites/%s/lists/%s/items/%s?$expand=versions($expand=fields)", escapeUrlPart(getSiteId()), escapeUrlPart(getListId()), escapeUrlPart(toStringNotBlank(item.get("listItem___id"))))).get("versions"), m -> toStringNotBlank(m.get("id")), Function.identity());
            item = map(item).with("versions", set(driveItemVersions.keySet()).with(listItemVersions.keySet()).stream().map(v -> flattenMapsKeepOriginal(map("listItemVersion", listItemVersions.getOrDefault(v, emptyMap()), "driveItemVersion", driveItemVersions.getOrDefault(v, emptyMap())))).collect(ImmutableList.toImmutableList()));
        }
        return item;
    }

    @Override
    public DataHandler getItemContent(String itemId) {
        return getContent(format(getGraphApiBaseUrl() + "drives/%s/items/%s/content", escapeUrlPart(getDriveId()), escapeUrlPart(checkNotBlank(itemId))));
    }

    @Override
    public DataHandler getItemContentByPath(List<String> path) {
        return getContent(format(getGraphApiBaseUrl() + "drives/%s/root:/%s:/content", escapeUrlPart(getDriveId()), path.stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))));
    }

    @Override
    public DataHandler getItemPreview(String itemId) {
        return getContent(format(getGraphApiBaseUrl() + "drives/%s/items/%s/thumbnails/0/medium/content", escapeUrlPart(getDriveId()), escapeUrlPart(checkNotBlank(itemId))));
    }

    @Override
    public DataHandler getVersionContent(String itemId, String version) {
        return getContent(format(getGraphApiBaseUrl() + "drives/%s/items/%s/versions/%s/content", escapeUrlPart(getDriveId()), escapeUrlPart(checkNotBlank(itemId)), escapeUrlPart(checkNotBlank(version))));
    }

    @Override
    public Map<String, Object> setItem(List<String> folder, DocumentData document) {
        listFolderContentCreateIfMissing(folder);
        return doSetItem(format("drives/%s/root:/%s:", escapeUrlPart(getDriveId()), list(folder).with(document.getFilename()).map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))), document);
    }

    @Override
    public Map<String, Object> setItem(String documentId, DocumentData document) {
        return doSetItem(format("drives/%s/items/%s", escapeUrlPart(getDriveId()), escapeUrlPart(checkNotBlank(documentId))), document);
    }

    @Override
    public void deleteItemIfExists(List<String> path) {
        Optional.ofNullable(getItemByPathOrNull(path)).ifPresent(this::doDeleteItem);
    }

    @Override
    public void deleteItem(List<String> path) {
        doDeleteItem(getItemByPath(path));
    }

    @Override
    public void deleteItem(String documentId) {
        doDeleteItem(getItemById(documentId));
    }

    @Override
    public List<String> queryPathWithFulltext(String path, String textQuery, boolean orNull) {
        List<String> documentIds = list();
        try {
            String token = getAccessToken();
            String body = format("{\"requests\":[{\"entityTypes\":[\"driveItem\"],%s\"query\":{\"queryString\":\"%s AND isDocument=true AND path:\\\"%s/%s\\\"\"},\"fields\":[\"title\",\"id\"]}]}", config.getSharepointAuthProtocol().equals(MSAZUREOAUTH2_APPLICATION) ? "\"region\":\"" + tokenRegion + "\"," : "", textQuery, config.getSharepointUrl(), path);
            String fullQueryUrl = config.getSharepointGraphApiBaseUrl() + "search/query";
            HttpPost request = new HttpPost(fullQueryUrl);
            request.setHeader("Authorization", format("Bearer %s", token));
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
            logger.debug("post resource =< {} >", request);
            Map<String, Object> map = fromJson(checkStatusAndReadResponse(getHttpClient().execute(request)), MAP_OF_OBJECTS);
            logger.debug("got response = \n\n{}\n", mapToLoggableString(map));
            JSONObject jsonObject = new JSONObject(map).getJSONArray("value").getJSONObject(0).getJSONArray("hitsContainers").getJSONObject(0);
            if (jsonObject.has("hits")) {
                jsonObject.getJSONArray("hits").forEach(e -> {
                    documentIds.add(((JSONObject) e).getJSONObject("resource").getString("id"));
                });
            }
            logger.debug("Found < {} > attachments with matching content", documentIds.size());
        } catch (CmHttpRequestException ex) {
            if (toStringOrEmpty(getErrorCodeSafe(ex)).equalsIgnoreCase("itemNotFound") && orNull) {
                logger.debug("item not found for path =< {} >", path);
                return null;
            } else {
                throw ex;
            }
        } catch (IOException e) {
            throw new DmsException(e);
        }
        return documentIds;
    }

    @Nullable
    private Map<String, Object> doGetItemByPath(List<String> path, boolean orNull) {
        return Optional.ofNullable(doGetResource(format("drives/%s/root:/%s?$expand=listItem($expand=fields)", escapeUrlPart(getDriveId()), path.stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))), orNull)).map(CmInlineUtils::flattenMapsKeepOriginal).orElse(null);
    }

    private void doDeleteItem(Map<String, Object> item) {
        try {
            String documentId = toStringNotBlank(item.get("id"));
            String token = getAccessToken();
            HttpDelete request = new HttpDelete(format(getGraphApiBaseUrl() + "drives/%s/items/%s", escapeUrlPart(getDriveId()), escapeUrlPart(checkNotBlank(documentId))));
            request.setHeader("Authorization", format("Bearer %s", token));
            logger.debug("delete resource =< {} >", request);
            checkStatusAndClose(getHttpClient().execute(request));
            logger.debug("delete OK");
            String parentId = toStringOrNull(item.get("parentReference___id"));
            if (config.autodeleteEmptyDirectories() && isNotBlank(parentId) && toInt(flattenMaps(getResource(format("drives/%s/items/%s", escapeUrlPart(getDriveId()), escapeUrlPart(parentId)))).get("folder___childCount")) == 0) {
                deleteItem(parentId);
            }
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private DataHandler getContent(String path) {
        try {
            String token = getAccessToken();
            HttpGet request = new HttpGet(path);
            request.setHeader("Authorization", format("Bearer %s", token));
            logger.debug("get resource =< {} >", request);
            CloseableHttpResponse response = getHttpClient().execute(request);
            checkStatus(response);
            HttpEntity entity = checkNotNull(response.getEntity());
            byte[] data = CmIoUtils.toByteArray(entity.getContent());
            String contentType = Optional.ofNullable(entity.getContentType()).map(Header::getValue).orElseGet(() -> CmIoUtils.getContentType(data));
            logger.debug("got data = {} bytes {}", data.length, entity.getContentType());
            logger.debug("headers = \n\n{}\n", mapToLoggableStringLazy(map(list(response.getAllHeaders()), Header::getName, Header::getValue)));
            EntityUtils.consumeQuietly(entity);
            return CmIoUtils.newDataHandler(data, contentType);
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private byte[] getItemContent(List<String> path) {
        return checkNotNull(getItemContentOrNull(path), "item content not found for path =< %s >", path);
    }

    @Nullable
    private byte[] getItemContentOrNull(List<String> path) {
        try {
            String token = getAccessToken();
            HttpGet request = new HttpGet(format(getGraphApiBaseUrl() + "drives/%s/root:/%s:/content", escapeUrlPart(getDriveId()), path.stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))));
            request.setHeader("Authorization", format("Bearer %s", token));
            logger.debug("get resource =< {} >", request);
            CloseableHttpResponse response = getHttpClient().execute(request);
            checkStatus(response);
            HttpEntity entity = checkNotNull(response.getEntity());
            byte[] data = CmIoUtils.toByteArray(entity.getContent());
            logger.debug("got data = {} bytes {}", data.length, entity.getContentType());
            logger.debug("headers = \n\n{}\n", mapToLoggableStringLazy(map(list(response.getAllHeaders()), Header::getName, Header::getValue)));
            EntityUtils.consumeQuietly(entity);
            return data;
        } catch (CmHttpRequestException ex) {
            if (Strings.nullToEmpty(getErrorCodeSafe(ex)).equalsIgnoreCase("itemNotFound")) {
                logger.debug("item not found for path =< {} >", Joiner.on("/").join(path));
                return null;
            } else {
                throw ex;
            }
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private Map<String, Object> doSetItem(String path, DocumentData document) {
        try {
            String token = getAccessToken();
            Map<String, Object> item;
            if (!document.hasData()) {
                item = flattenMapsKeepOriginal(getResource(path + "?$expand=listItem($expand=fields)"));
            } else if (document.getData().length < UPLOAD_SPLIT_THRESHOLD) {
                HttpPut request = new HttpPut(getGraphApiBaseUrl() + path + "/content");
                request.setHeader("Authorization", format("Bearer %s", token));
                request.setEntity(new ByteArrayEntity(document.getData(), ContentType.parse(CmIoUtils.getContentType(document.getData()))));
                logger.debug("put resource =< {} >", request);
                item = fromJson(checkStatusAndReadResponse(getHttpClient().execute(request)), MAP_OF_OBJECTS);
                logger.debug("got response = \n\n{}\n", mapToLoggableStringLazy(item));
                item = getItemById(toStringNotBlank(item.get("id")));
            } else {
                HttpEntityEnclosingRequestBase request = new HttpPost(getGraphApiBaseUrl() + path + "/createUploadSession");
                request.setHeader("Authorization", format("Bearer %s", token));
                request.setEntity(new StringEntity(toJson(map("item", map("@microsoft.graph.conflictBehavior", "replace", "name", document.getFilename()))), ContentType.APPLICATION_JSON));
                logger.debug("post resource =< {} >", request);
                Map<String, Object> uploadInfo = fromJson(checkStatusAndReadResponse(getHttpClient().execute(request)), MAP_OF_OBJECTS);
                logger.debug("got response = \n\n{}\n", mapToLoggableStringLazy(uploadInfo));
                String uploadUrl = toStringNotBlank(uploadInfo.get("uploadUrl"));
                logger.debug("upload url =< {} >", uploadUrl);
                InputStream in = new ByteArrayInputStream(document.getData());
                byte[] buffer = new byte[UPLOAD_PART_SIZE];
                int offset = 0;
                int count;
                int size = document.getData().length;
                item = emptyMap();
                while (offset < size) {
                    count = in.read(buffer);
                    logger.debug("upload range {}-{} of total {} bytes", offset, offset + count - 1, size);
                    request = new HttpPut(uploadUrl);
                    request.setHeader("Content-Range", format("bytes %s-%s/%s", offset, offset + count - 1, size));
                    request.setEntity(new ByteArrayEntity(buffer, 0, count));
                    item = fromJson(checkStatusAndReadResponse(getHttpClient().execute(request)), MAP_OF_OBJECTS);
                    offset += count;
                }
                logger.debug("upload completed");
                item = getItemById(toStringNotBlank(item.get("id")));
            }
            String itemId = toStringNotBlank(item.get("id"));
            String listItemId = toStringNotBlank(item.get("listItem___id"));
            Map<String, Object> meta = mapOf(String.class, Object.class).accept(m -> customPropertySetter.accept(m, document));
            if (!meta.isEmpty()) {
                HttpPatch request = new HttpPatch(format(getGraphApiBaseUrl() + "sites/%s/lists/%s/items/%s/fields", escapeUrlPart(getSiteId()), escapeUrlPart(getListId()), escapeUrlPart(listItemId)));
                logger.debug("patch resource =< {} > with payload =\n\n{}\n", request, mapToLoggableStringLazy(meta));
                request.setHeader("Authorization", format("Bearer %s", token));
                request.setEntity(new StringEntity(toJson(meta), ContentType.APPLICATION_JSON));
                checkStatusAndClose(getHttpClient().execute(request));
            }
            return getItemById(itemId);
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private List<Map<String, Object>> listFolderContent(List<String> path, boolean createIfMissing) {
        try {
            Map<String, Object> resource = getResource(format("drives/%s/root:/%s:/children?$expand=listItem($expand=fields)", escapeUrlPart(getDriveId()), path.stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/"))));
            FluentList<Map<String, Object>> folderContent = list();
            folderContent = folderContent.with((List<Map<String, Object>>) resource.get("value"));
            while (resource.containsKey("@odata.nextLink")) {
                String nextPage = (String) resource.get("@odata.nextLink");
                resource = getResource(nextPage.replace(getGraphApiBaseUrl(), ""));
                folderContent = folderContent.with((List<Map<String, Object>>) resource.get("value"));
            }
            return folderContent.map(CmInlineUtils::flattenMapsKeepOriginal);
        } catch (CmHttpRequestException ex) {
            if (Strings.nullToEmpty(getErrorCodeSafe(ex)).equalsIgnoreCase("itemNotFound")) {
                logger.debug("folder not found for path =< {} >", Joiner.on("/").join(path));
                if (createIfMissing) {
                    createFolder(path);
                }
                return emptyList();
            } else {
                throw ex;
            }
        }
    }

    private void createFolder(List<String> path) {
        if (path.size() > 1) {
            listFolderContent(path.subList(0, path.size() - 1), true);
        }
        logger.debug("create folder =< {} >", Joiner.on("/").join(path));
        Object payload = map("name", path.get(path.size() - 1), "folder", emptyMap(), "@microsoft.graph.conflictBehavior", "fail");
        if (path.size() == 1) {
            postResource(payload, "drives/%s/root/children", escapeUrlPart(getDriveId()));
        } else {
            postResource(payload, "drives/%s/root:/%s:/children", escapeUrlPart(getDriveId()), path.subList(0, path.size() - 1).stream().map(SharepointDmsUtils::escapeUrlPart).collect(Collectors.joining("/")));
        }
    }

    @Nullable
    private String getErrorCodeSafe(CmHttpRequestException ex) {
        try {
            if (!ex.hasJsonContent()) {
                return null;
            } else {
                Map<String, Object> map = ex.getContentAsJsonSafe();
                if (map.containsKey("error") && map.get("error") instanceof Map errorCode) {
                    return toStringOrNull(errorCode.get("code"));
                } else {
                    return null;
                }
            }
        } catch (Exception exx) {
            logger.warn("error reading json response", exx);
            return null;
        }
    }

    private String getGraphApiBaseUrl() {
        return checkNotBlank(config.getSharepointGraphApiBaseUrl(), "missing sharepoint graph api base url");
    }

    private String getDriveId() {
        return driveId.get();
    }

    private String getListId() {
        return listId.get();
    }

    private String getSiteId() {
        return siteId.get();
    }

    private synchronized String getAccessToken() {
        if (StringUtils.isBlank(accessToken)) {
            acquireAccessToken();
        }
        return accessToken;
    }

    private String acquireDriveId() {
        Map<String, String> hostnameSiteWebUrl = getHostnameSiteWebUrl();
        String hostname = hostnameSiteWebUrl.get("hostname");
        String type = hostnameSiteWebUrl.get("type");
        String site = hostnameSiteWebUrl.get("site");
        String webUrl = hostnameSiteWebUrl.get("webUrl");
        Map<String, Object> driveInfo = ((List<Map<String, Object>>) getResource(format("sites/%s:/%s/%s:/drives", escapeUrlPart(hostname), escapeUrlPart(type), escapeUrlPart(site))).get("value")).stream().filter(d -> toStringOrEmpty(d.get("webUrl")).equalsIgnoreCase(webUrl)).collect(onlyElement("drive not found for hostname =< %s >, site =< %s >, weburl =< %s >", hostname, site, webUrl));
        logger.debug("drive info = \n\n{}\n", mapToLoggableString(driveInfo));
        String id = checkNotBlank(toStringOrNull(driveInfo.get("id")), "missing drive id");
        logger.debug("found drive id =< {} >", id);
        return id;
    }

    private String acquireListId() {
        Map<String, String> hostnameSiteWebUrl = getHostnameSiteWebUrl();
        String hostname = hostnameSiteWebUrl.get("hostname");
        String type = hostnameSiteWebUrl.get("type");
        String site = hostnameSiteWebUrl.get("site");
        String webUrl = hostnameSiteWebUrl.get("webUrl");
        Map<String, Object> driveInfo = ((List<Map<String, Object>>) getResource(format("sites/%s:/%s/%s:/lists", escapeUrlPart(hostname), escapeUrlPart(type), escapeUrlPart(site))).get("value")).stream().filter(d -> toStringOrEmpty(d.get("webUrl")).equalsIgnoreCase(webUrl)).collect(onlyElement("list not found for hostname =< %s >, site =< %s >, weburl =< %s >", hostname, site, webUrl));
        logger.debug("list info = \n\n{}\n", mapToLoggableString(driveInfo));
        String id = checkNotBlank(toStringOrNull(driveInfo.get("id")), "missing list id");
        logger.debug("found list id =< {} >", id);
        return id;
    }

    private String acquireSiteId() {
        Map<String, String> hostnameSiteWebUrl = getHostnameSiteWebUrl();
        String hostname = hostnameSiteWebUrl.get("hostname");
        String type = hostnameSiteWebUrl.get("type");
        String site = hostnameSiteWebUrl.get("site");
        Map<String, Object> info = getResource(format("sites/%s:/%s/%s:", escapeUrlPart(hostname), escapeUrlPart(type), escapeUrlPart(site)));
        logger.debug("site info = \n\n{}\n", mapToLoggableString(info));
        String id = checkNotBlank(toStringOrNull(info.get("id")), "missing site id");
        logger.debug("found site id =< {} >", id);
        return id;
    }

    private Map<String, String> getHostnameSiteWebUrl() {
        String sharepointUrl = trimAndCheckNotBlank(config.getSharepointUrl(), "missing required sharepoint url param");
        Matcher matcher = Pattern.compile("(https?://([^/]+)/(sites|teams)/([^/]+)(/([^/]+))?)/?").matcher(sharepointUrl);
        checkArgument(matcher.matches(), "invalid format for sharepoint url =< %s >", sharepointUrl);
        return map("hostname", checkNotBlank(matcher.group(2)),
                "type", checkNotBlank(matcher.group(3)),
                "site", checkNotBlank(matcher.group(4)),
                "webUrl", checkNotBlank(matcher.group(1)));
    }

    @Nullable
    private Map<String, Object> getResourceOrNull(String path) throws CmHttpRequestException {
        return doGetResource(path, true);
    }

    private Map<String, Object> getResource(String path) throws CmHttpRequestException {
        return doGetResource(path, false);
    }

    @Nullable
    private Map<String, Object> doGetResource(String path, boolean orNull) throws CmHttpRequestException {
        try {
            HttpGet request = HttpGet.class.cast(buildHttpRequest(path, HttpGet::new));
            logger.debug("get resource =< {} >", request);
            Map<String, Object> map = fromJson(checkStatusAndReadResponse(getHttpClient().execute(request)), MAP_OF_OBJECTS);
            logger.debug("got response = \n\n{}\n", mapToLoggableString(map));
            return map;
        } catch (CmHttpRequestException ex) {
            if (Strings.nullToEmpty(getErrorCodeSafe(ex)).equalsIgnoreCase("itemNotFound") && orNull) {
                logger.debug("item not found for path =< {} >", path);
                return null;
            } else {
                throw ex;
            }
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private Map<String, Object> postResource(Object payload, String path, Object... params) throws CmHttpRequestException {
        try {
            HttpPost request = HttpPost.class.cast(buildHttpRequest(format(path, params), HttpPost::new));
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(toJson(payload), ContentType.APPLICATION_JSON));
            logger.debug("post resource =< {} >", request);
            Map<String, Object> map = fromJson(checkStatusAndReadResponse(getHttpClient().execute(request)), MAP_OF_OBJECTS);
            logger.debug("got response = \n\n{}\n", mapToLoggableString(map));
            return map;
        } catch (IOException e) {
            throw new DmsException(e);
        }
    }

    private HttpUriRequest buildHttpRequest(String path, Function<String, HttpUriRequest> http) {
        String token = getAccessToken();
        HttpUriRequest request = http.apply(getGraphApiBaseUrl() + path);
        request.setHeader("Authorization", format("Bearer %s", token));
        request.setHeader("Accept", "application/json");
        return request;
    }

    private synchronized void acquireAccessToken() {
        logger.debug("get sharepoint access token with protocol =< {} >", config.getSharepointAuthProtocol());
        switch (config.getSharepointAuthProtocol()) {
            case MSAZUREOAUTH2_APPLICATION ->
                acquireMsApplicationAccessToken();
            case MSAZUREOAUTH2_DELEGATED ->
                acquireMsDelegatedAccessToken();
            case MSAZUREOAUTH2_PASSWORD ->
                acquireMsPasswordAccessToken();
            default ->
                throw unsupported("unsupported sharepoint auth protocol =< %s >", config.getSharepointAuthProtocol());
        }
    }

    private synchronized void refreshMsAccessToken() {
        acquireMsAccessToken(map("grant_type", "refresh_token", "refresh_token", checkNotBlank(refreshToken)));
    }

    private synchronized void acquireMsApplicationAccessToken() {
        acquireMsAccessToken(map("grant_type", "client_credentials"));
    }

    private synchronized void acquireMsDelegatedAccessToken() {
        acquireMsAccessToken(map("grant_type", "authorization_code", "code", checkNotBlank(config.getSharepointPassword(), "missing sharepoint code, use password configuration to set it")));
    }

    private synchronized void acquireMsPasswordAccessToken() {
        acquireMsAccessToken(map("grant_type", "password", "username", checkNotBlank(config.getSharepointUser(), "missing sharepoint auth usename"), "password", checkNotBlank(config.getSharepointPassword(), "missing sharepoint auth user password")));
    }

    private synchronized void acquireMsAccessToken(Map<String, String> payload) {
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost tokenRequest = new HttpPost(format("%s/%s/oauth2/v2.0/token", checkNotBlank(config.getSharepointAuthServiceUrl(), "missing sharepoint auth service url"), firstNotBlank(config.getSharepointAuthTenantId(), "common")));
            FluentMap<String, String> tokenRequestPayload = mapOf(String.class, String.class).with("client_id", checkNotBlank(config.getSharepointAuthClientId(), "missing sharepoint auth client id"), "client_secret", checkNotBlank(config.getSharepointAuthClientSecret(), "missing sharepoint auth client secret"), "scope", "openid offline_access https://graph.microsoft.com/.default").with(payload);
            tokenRequest.setEntity(new UrlEncodedFormEntity(tokenRequestPayload.toList(BasicNameValuePair::new), StandardCharsets.UTF_8.name()));
            logger.debug("execute sharepoint oauth token request =< {} > with payload =\n\n{}\n", tokenRequest, mapToLoggableStringLazy(tokenRequestPayload));
            Map<String, String> tokenResponse = fromJson(checkStatusAndReadResponse(client.execute(tokenRequest)), MAP_OF_STRINGS);
            logger.debug("received sharepoint oauth token response = \n\n{}\n", mapToLoggableStringLazy(tokenResponse));
            String token = checkNotBlank(tokenResponse.get("access_token"), "missing sharepoint oauth access token in token response");
            logger.debug("acquired sharepoint oauth access token =< {} >", token);
            DecodedJWT jwt = JWT.decode(token);
            Map<String, String> info = map(jwt.getClaims()).mapValues(Claim::asString);
            logger.debug("token info = \n\n{}\n", mapToLoggableStringLazy(info));
            Set<String> privileges = set(Splitter.on(" ").omitEmptyStrings().trimResults().split(Strings.nullToEmpty(info.get("scp"))));
            logger.debug("token privileges = {}", privileges);
            String refresh = "";
            String region = "";
            switch (config.getSharepointAuthProtocol()) {
                case MSAZUREOAUTH2_DELEGATED, MSAZUREOAUTH2_PASSWORD -> {
                    checkArgument(list(privileges).map(String::toLowerCase).contains("Files.ReadWrite.All".toLowerCase()), "missing required access privilege `Files.ReadWrite.All`");
                    checkArgument(list(privileges).map(String::toLowerCase).contains("Sites.Read.All".toLowerCase()), "missing required access privilege `Sites.Read.All`");
                    refresh = checkNotBlank(tokenResponse.get("refresh_token"), "missing refresh token");
                }
                case MSAZUREOAUTH2_APPLICATION -> {
                    region = checkNotBlank(info.get("tenant_region_scope"), "missing region token");
//                    checkArgument(list(privileges).map(String::toLowerCase).contains("Files.ReadWrite.All".toLowerCase()), "missing required access privilege `Files.ReadWrite.All`");
                }
            }
            ZonedDateTime release = now();
            ZonedDateTime expiration = release.plusSeconds(toInt(tokenResponse.get("expires_in")));
            logger.debug("token will expire at = {}", toIsoDateTime(expiration));
            logger.debug("access token ok");
            accessToken = token;
            refreshToken = refresh;
            tokenRegion = region;
            tokenRelease = release;
            tokenExpiration = expiration;
        } catch (Exception ex) {
            throw new DmsException(ex, "unable to authenticate sharepoint client");
        }
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

}
