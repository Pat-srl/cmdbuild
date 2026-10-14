package org.cmdbuild.gis.geoserver;

import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisAttributeRepository;
import static org.cmdbuild.gis.etl.CadEtlLoadHandler.CMDBUILD_DEFAULT_EPSG;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionUtils.buildMinionRuntimeStatusChecker;
import static org.cmdbuild.utils.cad.geo.GeoUtils.translateCoordinates;
import static org.cmdbuild.utils.cad.geo.ShapefileUtils.renameShapefileInners;
import org.cmdbuild.utils.cad.model.CadPoint;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.cmdbuild.utils.lang.LambdaExceptionUtils.Function_WithExceptions;
import static org.cmdbuild.utils.xml.CmXmlUtils.prettifyXmlLazy;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GeoserverServiceImpl implements GeoserverService, MinionComponent {

    private final static ComparableVersion GEOSERVER_VERSION_MIN = new ComparableVersion("2.16.2");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GisConfiguration config;
    private final GisAttributeRepository attributeRepository;

    private final MinionHandler minionHandler;
    private final Holder<List<GeoserverLayerInfo>> geoserverLayers;

    public GeoserverServiceImpl(GisConfiguration config, GisAttributeRepository attributeRepository, CacheService cacheService) {
        this.config = checkNotNull(config);
        this.attributeRepository = checkNotNull(attributeRepository);
        geoserverLayers = cacheService.newHolder("all_geoserver_layers");
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("GIS_ GeoServer client")
                .withEnabledChecker(config::isGeoServerEnabled)
                .withStatusChecker(buildMinionRuntimeStatusChecker(config::isGeoServerEnabled, this::isOk))
                .build();
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public boolean isEnabled() {
        return MinionComponent.super.isEnabled();
    }

    private void invalidateCache() {
        geoserverLayers.invalidate();
    }

    private boolean isOk() {
        try {
            checkGeoserverVersion();
            getWorkspaceOtherwiseCreate(format("%s/rest/workspaces/", config.getGeoServerUrl()));
            getGeoserverLayers();
            return true;
        } catch (Exception ex) {
            logger.debug("geoserver client service error", ex);
            logger.warn(marker(), "geoserver client service error: {}", ex.toString());
            return false;
        }
    }

    @ConfigListener(GisConfiguration.class)
    public void reload() {
        invalidateCache();
    }

    @Override
    public CadPoint getGeoserverLayerCenter(String layerName) {
        List<GeoserverLayerInfo> geoserverLayerInfo = getGeoserverLayers().stream().filter(e -> e.getLayerName().equals(layerName)).collect(toUnmodifiableList());
        if (!geoserverLayerInfo.isEmpty()) {
            return getOnlyElement(geoserverLayerInfo).getCenter();
        } else {
            return new CadPoint(0, 0);
        }
    }

    @Override
    public GeoserverLayer set(GeoserverLayer geoserverLayer, BigByteArray data) {
        checkIsEnabled();
        try {
            logger.info("set geoserver store/layer = {} : upload data = {}", geoserverLayer, byteCountToDisplaySize(data.length()));
            GisAttribute gisAttribute = getGisAttribute(geoserverLayer);
            String ext, contentType;
            switch (gisAttribute.getType()) {
                case GAT_SHAPE -> {
                    ext = "shp";
                    contentType = "application/zip";
                    data = renameShapefileInners(data, geoserverLayer.getGeoserverLayer());
                }
                case GAT_GEOTIFF -> {
                    ext = "geotiff";
                    contentType = "image/tiff";
                }
                default ->
                    throw unsupported("unsupported gis attribute type = %s", gisAttribute.getType());
            }
            put(format("%s/rest/workspaces/%s/%s/%s/file.%s?update=overwrite", config.getGeoServerUrl(), config.getGeoServerWorkspace(), getStoreTypeWsPart(geoserverLayer), geoserverLayer.getGeoserverStore(), ext), data, contentType);
            logger.debug("geoserver upload completed");
        } finally {
            invalidateCache();
        }
        GeoserverLayerInfo layerInfo = checkNotNull(getInfoByStoreOrNull(geoserverLayer.getGeoserverStore()), "layer not found for store name =< %s >", geoserverLayer.getGeoserverStore());
        return GeoserverLayerImpl.copyOf(geoserverLayer).withGeoserverLayer(layerInfo.getLayerName()).build();
    }

    @Override
    public void delete(GeoserverLayer geoserverLayer) {
        checkIsEnabled();
        if (getInfoByStoreOrNull(geoserverLayer.getGeoserverStore()) == null) {
            logger.warn("skip delete for geoserver store/layer = {} : layer not found", geoserverLayer);
        } else {
            try {
                logger.info("delete geoserver store/layer = {}", geoserverLayer);
                delete(format("%s/rest/workspaces/%s/%s/%s?recurse=true", config.getGeoServerUrl(), config.getGeoServerWorkspace(), getStoreTypeWsPart(geoserverLayer), geoserverLayer.getGeoserverStore()));
            } finally {
                invalidateCache();
            }
        }
    }

    @Nullable
    private GeoserverLayerInfo getInfoByStoreOrNull(String store) {
        checkNotBlank(store);
        return getGeoserverLayers().stream().filter(equal(GeoserverLayerInfo::getStoreName, store)).collect(onlyElementOrNull());
    }

    private GisAttribute getGisAttribute(GeoserverLayer geoserverLayer) {
        return attributeRepository.get(geoserverLayer.getOwnerClass(), geoserverLayer.getAttributeName());
    }

    private String getStoreTypeWsPart(GeoserverLayer geoserverLayer) {
        return getStoreTypeWsPart(getGisAttribute(geoserverLayer));
    }

    private String getStoreTypeWsPart(GisAttribute gisAttribute) {
        return switch (gisAttribute.getType()) {
            case GAT_SHAPE ->
                "datastores";
            case GAT_GEOTIFF ->
                "coveragestores";
            default ->
                throw unsupported("unsupported gis attribute type = %s", gisAttribute.getType());
        };
    }

    private void checkIsEnabled() {
        checkArgument(isEnabled(), "geoserver client not enabled");
    }

    private List<GeoserverLayerInfo> getGeoserverLayers() {
        return geoserverLayers.get(this::doGetGeoserverLayers);
    }

    private void checkGeoserverVersion() throws Exception {
        Document response = get(format("%s/rest/about/version", config.getGeoServerUrl()));
        ComparableVersion installedGeoserverVersion = new ComparableVersion(response.valueOf("about/resource[@name='GeoServer']/Version"));
        if (installedGeoserverVersion.compareTo(GEOSERVER_VERSION_MIN) < 0) {
            throw new GeoserverException("""
                                        The installed GeoServer version isn't the required one,
                                         installed : %s
                                         required: : %s or later
                                         This can cause GeoServer integration malfunctions, please install the suggested version""", installedGeoserverVersion, GEOSERVER_VERSION_MIN);
        }
    }

    private List<GeoserverLayerInfo> doGetGeoserverLayers() {
        checkIsEnabled();
        Document response = get(format("%s/rest/layers", config.getGeoServerUrl()));
        return (List) response.selectNodes("//layers/layer").stream().map(node -> {
            String layerNameWithWorkspace = ((Node) node).valueOf("name");
            if (layerNameWithWorkspace.startsWith(config.getGeoServerWorkspace() + ":")) {
                Document layer = get(format("%s/rest/layers/%s", config.getGeoServerUrl(), layerNameWithWorkspace));
                String layerName = checkNotBlank(layer.valueOf("/layer/name")),
                        storeName = layer.valueOf("/layer/resource[@class='featureType']/*[local-name()='link']/@href");
                Matcher matcher = Pattern.compile(".*/workspaces/" + Pattern.quote(config.getGeoServerWorkspace()) + "/datastores/([^/]+)/featuretypes/.*").matcher(storeName);                //<atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:13080/geoserver/rest/workspaces/cmdbuild/datastores/building_test_71808/featuretypes/test__2.xml" type="application/xml"/>
                checkArgument(matcher.matches(), "invalid store link pattern for value =< %s >", storeName);
                storeName = checkNotBlank(matcher.group(1));
                Document featureTypes = get(format("%s/rest/workspaces/%s/datastores/%s/featuretypes/%s.xml", config.getGeoServerUrl(), config.getGeoServerWorkspace(), layerName, layerName));
                double minx = featureTypes.numberValueOf("/featureType/nativeBoundingBox/minx").doubleValue();
                double miny = featureTypes.numberValueOf("/featureType/nativeBoundingBox/miny").doubleValue();
                double maxx = featureTypes.numberValueOf("/featureType/nativeBoundingBox/maxx").doubleValue();
                double maxy = featureTypes.numberValueOf("/featureType/nativeBoundingBox/maxy").doubleValue();
                String crs = featureTypes.valueOf("/featureType/nativeBoundingBox/crs");

                return new GeoserverLayerInfoImpl(storeName, layerName, translateCoordinates(new CadPoint((minx + maxx) / 2, (miny + maxy) / 2), crs, CMDBUILD_DEFAULT_EPSG));
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(toUnmodifiableList());
    }

    private void getWorkspaceOtherwiseCreate(String url) {
        logger.debug("try get geoserver workspace with configured name: {}", config.getGeoServerWorkspace());
        try {
            doWithClient((c) -> {
                HttpGet request = new HttpGet(url + config.getGeoServerWorkspace());
                request.setHeader("Accept", "application/xml");
                return checkNotNull(c.execute(request));
            });
        } catch (Exception ex) {
            logger.debug("error getting geoserver workspace with name {} : {}", config.getGeoServerWorkspace(), ex.toString());
            String data = "<workspace><name>" + config.getGeoServerWorkspace() + "</name></workspace>";
            post(url, data.getBytes(), "application/xml");
        }
    }

    @Nullable
    private Document post(String url, byte[] data, String contentType) {
        logger.debug("run geoserver request PUT {}", url);
        try {
            return doWithClient((c) -> {
                HttpPost request = new HttpPost(url);
                request.setHeader("Accept", "application/xml");
                request.setEntity(new ByteArrayEntity(data, ContentType.parse(checkNotBlank(contentType))));
                return c.execute(request);
            });
        } catch (Exception ex) {
            throw new GeoserverException(ex, "error processing geoserver request = PUT %s : %s", url, ex.toString());
        }
    }

    private Document get(String url) {
        logger.debug("run geoserver request GET {}", url);
        try {
            return doWithClient((c) -> {
                HttpGet request = new HttpGet(url);
                request.setHeader("Accept", "application/xml");
                return checkNotNull(c.execute(request));
            });
        } catch (Exception ex) {
            throw new GeoserverException(ex, "error processing geoserver request = GET %s : %s", url, ex.toString());
        }
    }

    @Nullable
    private Document put(String url, BigByteArray data, String contentType) {
        logger.debug("run geoserver request PUT {}", url);
        try {
            return doWithClient((c) -> {
                HttpPut request = new HttpPut(url);
                request.setHeader("Accept", "application/xml");
                request.setEntity(new ByteArrayEntity(data.toByteArray(), ContentType.parse(checkNotBlank(contentType))));
                return c.execute(request);
            });
        } catch (Exception ex) {
            throw new GeoserverException(ex, "error processing geoserver request = PUT %s : %s", url, ex.toString());
        }
    }

    private void delete(String url) {
        logger.debug("run geoserver request DELETE {}", url);
        try {
            doWithClient((c) -> {
                HttpDelete request = new HttpDelete(url);
                return c.execute(request);
            });
        } catch (Exception ex) {
            throw new GeoserverException(ex, "error processing geoserver request = DELETE %s : %s", url, ex.toString());
        }
    }

    @Nullable
    private Document doWithClient(Function_WithExceptions<CloseableHttpClient, HttpResponse, Exception> consumer) throws Exception {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(config.getGeoServerAdminUser(), config.getGeoServerAdminPassword());
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        try (CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build()) {
            HttpResponse response = consumer.apply(client);
            String content = EntityUtils.toString(response.getEntity());
            logger.trace("geoserver ws response = \n\n{}\n", prettifyXmlLazy(content));
            checkArgument(Integer.toString(response.getStatusLine().getStatusCode()).matches("20."), "error: %s %s", response.getStatusLine(), abbreviate(content));
            if (isResponseTextPlain(response) || isBlank(content)) {
                return null;
            } else {
                return DocumentHelper.parseText(content);
            }
        } finally {
            logger.trace("request completed");
        }
    }

    private boolean isResponseTextPlain(HttpResponse response) {
        if (response.getEntity().getContentType() != null) {
            return response.getEntity().getContentType().getValue().equals("text/plain");
        } else {
            return false;
        }
    }
}
