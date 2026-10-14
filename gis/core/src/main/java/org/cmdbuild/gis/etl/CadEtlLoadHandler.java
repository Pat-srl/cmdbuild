/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.etl;

import com.google.common.base.Joiner;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.collect.Iterables.getLast;
import jakarta.annotation.Nullable;
import static java.lang.Math.round;
import static java.lang.String.format;
import java.util.Collections;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.postgres.q3.RefAttrHelperService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.data.filter.beans.ContextFilterImpl;
import org.cmdbuild.etl.EtlException;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_CAD;
import org.cmdbuild.etl.job.EtlLoadHandler;
import org.cmdbuild.etl.job.EtlLoaderApi;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_LEAVE_MISSING;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_NO_MERGE;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfig;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfigImpl;
import org.cmdbuild.etl.loader.EtlTemplateColumnMode;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_DEFAULT;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_RECORDID;
import org.cmdbuild.etl.loader.EtlTemplateImpl;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.etl.loader.EtlTemplateWithData;
import org.cmdbuild.etl.loader.EtlTemplateWithDataImpl;
import org.cmdbuild.etl.loader.inner.EtlProcessingResultErrorImpl;
import org.cmdbuild.etl.loader.inner.EtlProcessingResultImpl;
import static org.cmdbuild.etl.loader.inner.EtlProcessingResultImpl.emptyResult;
import static org.cmdbuild.etl.loader.inner.EtlTemplateProcessorServiceImpl.CM_IMPORT_RECORD_ID;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_PROCESSING_REPORT;
import org.cmdbuild.etl.waterway.message.WaterwayMessageDataImpl;
import static org.cmdbuild.fault.FaultUtils.exceptionToUserMessage;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.Linestring;
import org.cmdbuild.gis.Polygon;
import static org.cmdbuild.gis.etl.CadEtlLoadHandler.MasterCardFilterMode.MCFM_FROMSHAPE;
import org.cmdbuild.gis.model.LinestringImpl;
import org.cmdbuild.gis.model.PointImpl;
import org.cmdbuild.gis.model.PolygonImpl;
import static org.cmdbuild.gis.utils.GisUtils.cmGeometryToPostgisSql;
import org.cmdbuild.utils.cad.CadGeometryUtils;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getBoundingBox;
import static org.cmdbuild.utils.cad.CadGeometryUtils.getSurfaceArea;
import static org.cmdbuild.utils.cad.CadUtils.parseCadFile;
import org.cmdbuild.utils.cad.dxfparser.model.DxfDocument;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntity;
import org.cmdbuild.utils.cad.geo.DxfToShapefileHelper;
import static org.cmdbuild.utils.cad.geo.GeoUtils.parseTransformationRules;
import org.cmdbuild.utils.cad.model.CadEntity;
import org.cmdbuild.utils.cad.model.CadEntityImpl;
import org.cmdbuild.utils.cad.model.CadPoint;
import org.cmdbuild.utils.cad.model.CadPolyline;
import org.cmdbuild.utils.cad.model.CadRectangle;
import static org.cmdbuild.utils.cad.model.CadRectangle.rectangle;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.distinctOn;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDouble;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToMessage;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ETL load handler for CAD (DWG/DXF) file imports in the GIS module.
 * <p>
 * This component processes CAD files, extracts geometric and metadata
 * information,
 * and imports records and shapes into the CMDBuild system using configured
 * templates.
 * It supports automatic master card filtering, shape import to GeoServer, and
 * relative position mapping between CAD entities and target classes.
 * </p>
 *
 * <p>
 * Key features:
 * <ul>
 * <li>Parses CAD files and extracts entities, geometry, and metadata</li>
 * <li>Imports records using ETL templates, with support for auto master card
 * filtering</li>
 * <li>Imports shapes to GeoServer and manages existing layers</li>
 * <li>Handles relative position attributes for CAD entities</li>
 * <li>Supports configuration for shape import, master card filtering, and layer
 * inclusion/exclusion</li>
 * </ul>
 * </p>
 *
 * <p>
 * Usage: This handler is typically invoked by the ETL loader framework when a
 * CAD import job is triggered.
 * </p>
 *
 * @author CMDBuild Team
 */
@Component
public class CadEtlLoadHandler implements EtlLoadHandler {

    public final static String CAD_METADATA_PREFIX = "meta.",
            CAD_LAYER_MASTER = "CM_MASTER",
            CAD_LAYER = "layer",
            CAD_AREA = "area",
            CAD_POSITION = "position",
            CAD_PERIMETER = "perimeter",
            CAD_LINE = "line",
            CAD_BOX = "box",
            CAD_ENTITY = "entity",
            CAD_IMPORT_RELATIVE_LOCATION_COLUMN_NAME = "CM_RELATIVE_LOCATION",
            CMDBUILD_DEFAULT_EPSG = "EPSG:3857"; // same as EPSG:900913

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EtlTemplateService importService;
    private final GisService gisService;
    private final GisConfiguration gisConfiguration;
    private final DaoService dao;
    private final RefAttrHelperService referenceHelper;

    /**
     * Constructs a CAD ETL load handler with required services and configuration.
     *
     * @param importService    the ETL template service for importing records
     * @param gisService       the GIS service for shape import and layer management
     * @param dao              the DAO service for database access
     * @param referenceHelper  the reference attribute helper service
     * @param gisConfiguration the GIS configuration
     */
    public CadEtlLoadHandler(EtlTemplateService importService, GisService gisService, DaoService dao,
            RefAttrHelperService referenceHelper, GisConfiguration gisConfiguration) {
        this.importService = checkNotNull(importService);
        this.gisService = checkNotNull(gisService);
        this.dao = checkNotNull(dao);
        this.referenceHelper = checkNotNull(referenceHelper);
        this.gisConfiguration = checkNotNull(gisConfiguration);
    }

    /**
     * Returns the handler type identifier for CAD ETL jobs.
     *
     * @return the handler type string
     */
    @Override
    public String getType() {
        return ETLHT_CAD;
    }

    /**
     * Loads records from the provided ETL loader API, processing the CAD file and
     * importing data.
     *
     * @param api the ETL loader API containing input data and configuration
     * @return a {@link WaterwayMessageData} report of the import process, or
     *         {@code null} if not applicable
     */
    @Override
    @Nullable
    public WaterwayMessageData load(EtlLoaderApi api) {
        return new CadImportHelper(api).loadRecords();
    }

    /**
     * Builds a PostGIS polygon string from the perimeter of the given CAD entity,
     * or returns {@code null} if the perimeter is a point.
     *
     * @param entity the CAD entity
     * @return the PostGIS polygon string or {@code null}
     */
    @Nullable
    private static String buildGisPerimeter(CadEntity entity) {
        return entity.getPerimeter().isPoint() ? null : cmGeometryToPostgisSql(toPostgisPolygon(entity.getPerimeter()));
    }

    /**
     * Builds a PostGIS linestring string from the polyline of the given CAD entity,
     * or returns {@code null} if the perimeter is a point.
     *
     * @param entity the CAD entity
     * @return the PostGIS linestring string or {@code null}
     */
    @Nullable
    private static String buildGisLinestring(CadEntity entity) {
        return entity.getPerimeter().isPoint() ? null : cmGeometryToPostgisSql(toPostgisLinestring(entity));
    }

    /**
     * Builds a PostGIS polygon string from the bounding box of the given CAD
     * entity,
     * or returns {@code null} if the perimeter is a point.
     *
     * @param entity the CAD entity
     * @return the PostGIS polygon string or {@code null}
     */
    @Nullable
    private static String buildGisBox(CadEntity entity) {
        return entity.getPerimeter().isPoint() ? null
                : cmGeometryToPostgisSql(toPostgisPolygon(entity.getBoundingBox()));
    }

    /**
     * Builds a PostGIS point string from the position of the given CAD entity.
     *
     * @param entity the CAD entity
     * @return the PostGIS point string
     */
    private static String buildGisPosition(CadEntity entity) {
        return cmGeometryToPostgisSql(new PointImpl(entity.getPosition().getX(), entity.getPosition().getY()));
    }

    /**
     * Converts a {@link CadPolyline} to a {@link Polygon} for PostGIS.
     * Ensures the polygon is closed by repeating the first vertex if needed.
     *
     * @param cadPolyline the CAD polyline
     * @return the corresponding {@link Polygon}
     */
    private static Polygon toPostgisPolygon(CadPolyline cadPolyline) {
        return new PolygonImpl(
                (List) list(cadPolyline.getVertexes()).map(p -> new PointImpl(p.getX(), p.getY())).accept(l -> {
                    if (!Objects.equals(l.get(0), getLast(l))) {
                        l.add(l.get(0));
                    }
                }));
    }

    /**
     * Converts a {@link CadEntity} to a {@link Linestring} for PostGIS.
     * Ensures the linestring is closed if the entity is closed.
     *
     * @param cadPolyline the CAD entity
     * @return the corresponding {@link Linestring}
     */
    private static Linestring toPostgisLinestring(CadEntity cadPolyline) {
        return new LinestringImpl((List) list(cadPolyline.getPolyline().getVertexes())
                .map(p -> new PointImpl(p.getX(), p.getY())).accept(l -> {
                    if (cadPolyline.isClosed() && !Objects.equals(l.get(0), getLast(l))) {
                        l.add(l.get(0));
                    }
                }));
    }

    /**
     * Helper class for managing the CAD import process, including record
     * extraction,
     * template mapping, master card filtering, and shape import.
     */
    private class CadImportHelper {

        private final EtlLoaderApi api;
        private final CadImportConfig config;
        private final boolean enableAngleDisplacementProcessing;
        private final DxfDocument document;
        private final Map<String, String> globalMetadata;
        private final List<EtlTemplate> templates;
        private List<Map<String, Object>> records;

        public CadImportHelper(EtlLoaderApi api) {
            this.api = checkNotNull(api);
            config = new CadImportConfig(api);
            document = parseCadFile(gisConfiguration, api.getData());
            globalMetadata = map(document.getMetadata()).mapKeys(k -> format("global.%s", k).toLowerCase())
                    .with(map(document.getMetadata()).mapKeys(String::toLowerCase));
            templates = list(api.getTemplates()).filter(EtlTemplate::isActive).immutable();
            this.enableAngleDisplacementProcessing = gisConfiguration.enableAngleDisplacementProcessing();
        }

        @Nullable
        public WaterwayMessageData loadRecords() {
            logger.info("loading cad file = {} {}", byteCountToDisplaySize(countBytes(api.getData())),
                    getContentType(api.getData()));

            logger.debug("global metadata = \n\n{}\n", mapToLoggableStringLazy(globalMetadata));

            EtlProcessingResult cardImportResult;

            if (!api.getTemplates().isEmpty()) {
                List<CadEntity> entities = document.getCadEntities(CMDBUILD_DEFAULT_EPSG,
                        enableAngleDisplacementProcessing);
                if (logger.isDebugEnabled()) {
                    entities.forEach(e -> logger.debug("found source cad entity = {}", e));
                }
                CadEntity master = CadEntityImpl.builder().withLayer(CAD_LAYER_MASTER).withSurface(0d).accept(m -> {
                    CadRectangle box;
                    if (entities.isEmpty()) {
                        box = rectangle(0, 0, 0, 0);
                    } else {
                        Iterator<CadEntity> iterator = entities.iterator();
                        box = iterator.next().getBoundingBox();
                        while (iterator.hasNext()) {
                            box = getBoundingBox(
                                    list(box.getVertexes()).with(iterator.next().getPerimeter().getVertexes()));
                        }
                    }
                    m.withBoundingBox(box).withPerimeter(box).withPosition(box.getCenter());
                    logger.debug("master record location = {} perimeter = {}", box.getCenter(), box);
                }).build();
                records = (List) list(entities).with(master).stream()
                        .map(e -> map(globalMetadata).with(CAD_LAYER, e.getLayer(),
                                CAD_AREA, toStringNotBlank(e.getSurface()), // TODO format/unit ??
                                CAD_POSITION, buildGisPosition(e),
                                CAD_PERIMETER, buildGisPerimeter(e),
                                CAD_LINE, buildGisLinestring(e),
                                CAD_BOX, buildGisBox(e),
                                CAD_ENTITY, e,
                                CM_IMPORT_RECORD_ID, randomId()).accept(m -> {
                                    e.getMetadata().forEach((k, v) -> {
                                        m.put(CAD_METADATA_PREFIX + k.toLowerCase(), v);
                                        m.putIfAbsent(k, v);
                                    });
                                }))
                        .collect(toList());

                // List<EtlTemplate> templates = api.getTemplates().stream().filter(t ->
                // t.isActive()).collect(toList());
                // if (enableShapeImport && config.enableAutoMasterCardFilter) {
                // List<EtlTemplate> list = templates.stream().filter(t ->
                // equal(t.getTargetName(), config.classId)).collect(toList());
                // if (!list.isEmpty()) {
                // int index = list.stream().mapToInt(templates::lastIndexOf).max().getAsInt();
                // list = templates.subList(0, index + 1);
                // templates = templates.subList(index + 1, templates.size());
                // cardImportResult = cardImportResult.and(importTemplates(records, list));
                // }
                // }
                // cardImportResult =
                // cardImportResult.and(importTemplates(templates));
                // TODO fix auto master card filter (when master card is imported by templates
                // here) !!
                try {
                    List<EtlTemplateWithData> data = templates.stream().map(t -> new EtlTemplateWithDataImpl(
                            EtlTemplateImpl.copyOf(t).withColumns(t.getColumns().stream().map(c -> {
                                return switch (c.getColumnName()) {
                                    case CAD_IMPORT_RELATIVE_LOCATION_COLUMN_NAME ->
                                        EtlTemplateColumnConfigImpl.copyOf(c).withColumnName(c.getAttributeName())
                                                .withMode(ETCM_RECORDID).build();
                                    default ->
                                        EtlTemplateColumnConfigImpl.copyOf(c).withColumnName(c.getAttributeName())
                                                .build();
                                };
                            }).collect(toUnmodifiableList())).accept(b -> {
                                if (config.enableAutoMasterCardFilter
                                        && !Objects.equals(config.masterCardClassId, t.getTargetName())) {
                                    Attribute attr = getAttrForMasterCardFilterOrNull(t.getTargetName());
                                    if (attr != null) {
                                        Object masterCardIdOrRecordId = getMasterCardIdOrRecordId();
                                        logger.info(
                                                "add template filter from shape master card = {}[{}] to template = {} using reference attr = {}",
                                                config.masterCardClassId, masterCardIdOrRecordId, t, attr);
                                        CmdbFilter filter = AttributeFilterConditionImpl
                                                .eq(attr.getName(), masterCardIdOrRecordId).toAttributeFilter()
                                                .toCmdbFilters();
                                        b
                                                .withFilter(filter.and(t.getFilter()))
                                                .withReferenceFilter(t.getReferenceFilter()
                                                        .and(CmdbFilterImpl.builder()
                                                                .withContextFilter(new ContextFilterImpl(
                                                                        referenceHelper.getTargetClassForAttribute(attr)
                                                                                .getName(),
                                                                        masterCardIdOrRecordId.toString()))
                                                                .build()));
                                    } else {
                                        checkArgument(t.hasMergeMode(EM_LEAVE_MISSING, EM_NO_MERGE),
                                                "missing reference attr for master card filter, for template with active merge mode = %s",
                                                t);
                                        logger.info(
                                                "skip master card filter for template = {} (no unique reference attr found)",
                                                t);
                                    }
                                }
                            }).build(), () -> {
                                TemplateRecordsHelper helper = new TemplateRecordsHelper(t);
                                logger.info("load records for template = {} with layer =< {} > code metadata =< {} >",
                                        t, helper.layer, helper.codeAttrs.stream().collect(joining(", ")));
                                Map<String, String> attributeMapping = map(t.getColumns().stream()
                                        .collect(toImmutableMap(EtlTemplateColumnConfig::getAttributeName,
                                                c -> c.getColumnName().toLowerCase())))
                                        .with(CM_IMPORT_RECORD_ID, CM_IMPORT_RECORD_ID);
                                logger.debug("using column mapping for target =< {} > mapping = \n\n{}\n",
                                        t.getTargetName(), mapToLoggableStringLazy(attributeMapping));
                                CadRelativePositionHelper relativePositionHelper = new CadRelativePositionHelper(t);
                                return helper.getRecordStreamForTemplate()
                                        .map(r -> map(attributeMapping).mapValues(r::get)
                                                .accept(m -> relativePositionHelper.handleRelativePositionAttrs(r,
                                                        m::put)))
                                        .collect(toUnmodifiableList());
                            })).collect(toUnmodifiableList());
                    cardImportResult = importService.importDataWithTemplates(data);
                } catch (Exception ex) {
                    logger.error(marker(), "error preparing data for template processing", ex);
                    cardImportResult = new EtlProcessingResultImpl(0, 0, 0, 0, 0,
                            singletonList(new EtlProcessingResultErrorImpl(0l, 0l, emptyMap(),
                                    exceptionToUserMessage(ex), exceptionToMessage(ex))));
                }

                logger.info("completed data import for document = {} : {}", document,
                        cardImportResult.getResultDescription());
            } else {
                logger.info("no template configured, skip card import");
                cardImportResult = emptyResult();
            }

            if (config.enableShapeImport) {
                EtlProcessingResult shapeImportResult;
                try {
                    checkArgument(gisService.isGeoserverEnabled(), "geoserver is not enabled");

                    logger.debug("shape import begin, selecting target card");
                    Card shapeTargetCard = checkNotNull(
                            dao.select(ATTR_ID).from(config.shapeImportClassId)
                                    .accept(addKeyFilter(config.shapeImportClassId,
                                            config.getShapeImportKeySourceAndAttrs()))
                                    .getCardOrNull(),
                            "unable to retrieve master card: card not found for key attrs =< %s > with values =< %s >",
                            Joiner.on(",").join(config.shapeImportKeyAttr),
                            config.shapeImportKeySource.stream().map(globalMetadata::get).collect(joining(",")));
                    Predicate<DxfEntity> filter = config.includeLayers.isEmpty() && config.excludeLayers.isEmpty()
                            ? alwaysTrue()
                            : (e) -> (config.includeLayers.isEmpty()
                                    || config.includeLayers.contains(e.getLayer().trim().toLowerCase()))
                                    && (config.excludeLayers.isEmpty()
                                            || !config.excludeLayers.contains(e.getLayer().trim().toLowerCase()));
                    logger.info("build shapefile ( filter layers =< {} > )",
                            config.includeLayers.isEmpty() ? "NO FILTER" : config.includeLayers);
                    DxfToShapefileHelper helper = DxfToShapefileHelper.withDocument(document)
                            .withEntityFilter(filter)
                            .withTargetReferenceSystem(config.targetReferenceSystem)
                            .withTransformationRules(isBlank(config.transformationRules) ? null
                                    : parseTransformationRules(config.transformationRules))
                            .withEnableAngleDisplacementProcessing(enableAngleDisplacementProcessing)
                            .build();
                    BigByteArray shapeFile = helper.toShapeFile();
                    logger.info("load shapefile on geoserver, shapefile element count = {} location = {}",
                            helper.getShapeFileElementCount(), helper.getShapeFileLocation());
                    boolean hasPreviousShape = gisService.getGeoserverLayerByCodeOrNull(config.shapeImportClassId,
                            config.shapeAttrName, shapeTargetCard.getId()) != null;
                    if (config.replaceExisting && hasPreviousShape) {// TODO improve this, move in gisAttribute config
                                                                     // (??)
                        gisService.deleteGeoServerLayer(config.shapeImportClassId, config.shapeAttrName,
                                shapeTargetCard.getId());
                    }
                    gisService.setGeoserverLayer(config.shapeImportClassId, config.shapeAttrName,
                            shapeTargetCard.getId(), newDataHandler(shapeFile));
                    shapeImportResult = new EtlProcessingResultImpl(hasPreviousShape ? 0 : 1, hasPreviousShape ? 1 : 0,
                            0, 0, 1, emptyList());
                } catch (Exception ex) {
                    logger.error(marker(), "error importing shape from cad file", ex);// TODO throw exception ??
                    shapeImportResult = new EtlProcessingResultImpl(0, 0, 0, 0, 0,
                            list(new EtlProcessingResultErrorImpl(0l, 0l, emptyMap(), exceptionToUserMessage(ex),
                                    exceptionToMessage(ex))));// TODO improve this, record info

                }
                cardImportResult = cardImportResult.and(shapeImportResult);
            }

            return WaterwayMessageDataImpl.build(WY_PROCESSING_REPORT, cardImportResult, api.getMeta());
        }

        /**
         * Returns the master card ID or record ID for use in template filtering.
         * Tries to find the card in the database, or falls back to imported records.
         *
         * @return the master card ID or record ID
         * @throws DaoException if the card cannot be found
         */
        private Object getMasterCardIdOrRecordId() {
            try {
                Object masterCardIdOrRecordId = Optional.ofNullable(dao.select(ATTR_ID).from(config.masterCardClassId)
                        .accept(addKeyFilter(config.masterCardClassId, config.getMasterCardKeySourceAndAttrs()))
                        .getCardOrNull()).map(Card::getId).orElse(null);
                if (masterCardIdOrRecordId == null) {
                    List<Map<String, Object>> list = templates.stream()
                            .filter(equal(EtlTemplate::getTargetName, config.masterCardClassId))
                            .flatMap(tt -> new TemplateRecordsHelper(tt).getRecordStreamForTemplate()
                                    .filter(addKeyFilter(tt, config.getMasterCardKeySourceAndAttrs())))
                            .distinct().collect(toList());
                    checkArgument(list.size() <= 1,
                            "too many records ( %s ) found for master class =< %s > records = < %s >", list.size(),
                            config.masterCardClassId, list(list).map(r -> r.get(CAD_ENTITY)));
                    masterCardIdOrRecordId = Optional.ofNullable(getOnlyElement(list, null))
                            .map(m -> m.get(CM_IMPORT_RECORD_ID)).orElse(null);
                }
                return checkNotNull(masterCardIdOrRecordId, "card or record not found");
            } catch (Exception ex) {
                throw new DaoException(ex,
                        "unable to retrieve master card for type = %s key attrs =< %s > with values =< %s >",
                        config.masterCardClassId, Joiner.on(",").join(config.masterCardKeyAttr),
                        config.masterCardKeySource.stream().map(globalMetadata::get).collect(joining(",")));
            }
        }

        /**
         * Helper class for extracting template records for a specific layer and key
         * attributes.
         */
        private class TemplateRecordsHelper {

            private final String layer;
            private final Set<String> codeAttrs;

            public TemplateRecordsHelper(EtlTemplate template) {
                if (Objects.equals(checkNotBlank(template.getSource(), "missing source for template = {}", template),
                        CAD_LAYER_MASTER)) {
                    layer = CAD_LAYER_MASTER;
                } else {
                    layer = checkNotBlank(template.getSource(), "missing source for template = {}", template)
                            .toLowerCase();
                }
                codeAttrs = template.getImportKeyAttributes().stream().map(template::getColumnByAttrName)
                        .map(c -> c.getColumnName().toLowerCase()).collect(toUnmodifiableSet());
            }

            /**
             * Returns a stream of records for the template's layer, where all code
             * attributes are present.
             *
             * @return a stream of matching records
             */
            private Stream<Map<String, Object>> getRecordStreamForTemplate() {
                return records.stream()
                        .filter(r -> Objects.equals(r.get(CAD_LAYER).toString().toLowerCase(), layer.toLowerCase())
                                && codeAttrs.stream().allMatch(codeAttr -> isNotBlank((String) r.get(codeAttr))));
            }

        }

        /**
         * Returns the reference attribute for master card filtering, or {@code null} if
         * not found.
         *
         * @param targetClassName the target class name
         * @return the reference attribute or {@code null}
         */
        @Nullable
        private Attribute getAttrForMasterCardFilterOrNull(String targetClassName) {
            Classe target = dao.getClasse(targetClassName);
            logger.info("add template filter from master card of type = {}", config.masterCardClassId);
            Classe masterClassType = dao.getClasse(config.masterCardClassId);
            return referenceHelper.getAttrForMasterCardFilterOrNull(target, masterClassType);
        }

        /**
         * Helper class for handling relative position attributes in CAD import records.
         */
        private class CadRelativePositionHelper {

            private final EtlTemplate template;
            private final Classe classe;

            public CadRelativePositionHelper(EtlTemplate template) {
                this.template = checkNotNull(template);
                classe = dao.getClasse(template.getTargetName());
            }

            /**
             * Handles relative position attributes for a record, updating the target
             * attribute with the related record ID.
             *
             * @param record   the import record
             * @param consumer a consumer to accept attribute name and value
             */
            public void handleRelativePositionAttrs(Map<String, ?> record, BiConsumer<String, String> consumer) {
                CadEntity cadEntity = (CadEntity) checkNotNull(record.get(CAD_ENTITY));
                template.getColumns().stream()
                        .filter(equal(EtlTemplateColumnConfig::getColumnName, CAD_IMPORT_RELATIVE_LOCATION_COLUMN_NAME))
                        .forEach(column -> {
                            Classe target = referenceHelper
                                    .getTargetClassForAttribute(classe.getAttribute(column.getAttributeName()));
                            logger.debug("lookup relative position for entity = {} attr =< {} > target class =< {} >",
                                    cadEntity, column.getAttributeName(), target.getName());
                            List<Map<String, ?>> values = relPosRecords(cadEntity, target,
                                    r -> CadGeometryUtils.contains(((CadEntity) r.get(CAD_ENTITY)).getPerimeter(),
                                            cadEntity.getPerimeter(), 0.8d));
                            if (values.isEmpty()) {
                                logger.debug("no relative position card found for record = {}",
                                        lazyString(() -> logRecord(record, template)));
                                relPosRecords(cadEntity, target,
                                        r -> CadGeometryUtils.intersects(((CadEntity) r.get(CAD_ENTITY)).getPerimeter(),
                                                cadEntity.getPerimeter()))
                                        .forEach(r -> {
                                            logger.debug("intersecting record = {} ( overlap = {}% )", r,
                                                    round(100
                                                            * CadGeometryUtils.intersectionSize(
                                                                    ((CadEntity) r.get(CAD_ENTITY)).getPerimeter(),
                                                                    cadEntity.getPerimeter())
                                                            / getSurfaceArea(cadEntity.getPerimeter())));
                                        });
                            } else {
                                if (values.size() > 1) {
                                    logger.debug("more than one value found, get record with least surface area");
                                    double minArea = values.stream().mapToDouble(v -> toDouble(v.get(CAD_AREA))).min()
                                            .getAsDouble();
                                    values = list(values).filter(v -> toDouble(v.get(CAD_AREA)) <= minArea);
                                }
                                if (values.size() == 1) {
                                    logger.debug("found relative position card = {} for record = {}",
                                            toStringNotBlank(getOnlyElement(values).get(CM_IMPORT_RECORD_ID)),
                                            lazyString(() -> logRecord(record, template)));
                                    consumer.accept(column.getAttributeName(),
                                            toStringNotBlank(getOnlyElement(values).get(CM_IMPORT_RECORD_ID)));
                                } else {
                                    logger.warn(
                                            "more than one value found for relative position for record = {}, unable to select one (values = {})",
                                            lazyString(() -> logRecord(record, template)), values);
                                }
                            }
                        });
            }

            /**
             * Finds records related by spatial containment or intersection for relative
             * position mapping.
             *
             * @param cadEntity the CAD entity
             * @param target    the target class
             * @param filter    a predicate to filter records
             * @return a list of related records
             */
            private List<Map<String, ?>> relPosRecords(CadEntity cadEntity, Classe target,
                    Predicate<Map<String, ?>> filter) {
                return api.getTemplates().stream()
                        .filter(t -> t.isTargetClass() && target.equalToOrAncestorOf(dao.getClasse(t.getTargetName())))
                        .flatMap(t -> new TemplateRecordsHelper(t).getRecordStreamForTemplate()
                                .filter(r -> CadGeometryUtils.contains(((CadEntity) r.get(CAD_ENTITY)).getPerimeter(),
                                        cadEntity.getPerimeter(), 0.95d))
                                .peek(r -> logger.trace("found record = {}", lazyString(() -> logRecord(r, t)))))
                        .filter(distinctOn(r -> toStringNotBlank(r.get(CM_IMPORT_RECORD_ID))))
                        .collect(toUnmodifiableList());
            }
        }

        private Consumer<QueryBuilder> addKeyFilter(String classeId, List<EtlTemplateColumnConfig> keyConfig) {
            Classe classe = dao.getClasse(classeId);
            return q -> {
                keyConfig.forEach(k -> {
                    Object value = globalMetadata.get(k.getColumnName());
                    try {
                        Attribute attribute = classe.getAttribute(k.getAttributeName());
                        if (attribute.isOfType(REFERENCE, FOREIGNKEY, LOOKUP)) {
                            value = switch (k.getMode()) {
                                case ETCM_CODE ->
                                    isNullOrBlank(value) ? null
                                            : dao.select(ATTR_ID)
                                                    .from(referenceHelper.getTargetClassForAttribute(attribute))
                                                    .where(ATTR_CODE, EQ, value).getCardIdOrNull();
                                case ETCM_DESCRIPTION ->
                                    isNullOrBlank(value) ? null
                                            : dao.select(ATTR_ID)
                                                    .from(referenceHelper.getTargetClassForAttribute(attribute))
                                                    .where(ATTR_DESCRIPTION, EQ, value).getCardIdOrNull();
                                case ETCM_ID ->
                                    toLongOrNull(value);
                                default ->
                                    throw new EtlException("invalid column mode = %s for attr = %s", k.getMode(),
                                            attribute);
                            };
                        }
                        q.where(k.getAttributeName(), EQ, value);
                    } catch (Exception ex) {
                        throw new EtlException(ex, "key filter error for config = %s value =< %s >", k, value);
                    }
                });
            };
        }

        private Predicate<Map<String, Object>> addKeyFilter(EtlTemplate template,
                List<EtlTemplateColumnConfig> keyConfig) {
            Classe classe = dao.getClasse(template.getTargetName());
            return r -> keyConfig.stream().allMatch(k -> {
                Object value1 = globalMetadata.get(k.getColumnName());
                try {
                    Attribute attribute = classe.getAttribute(k.getAttributeName());
                    EtlTemplateColumnConfig templateColumn = template.getColumnByAttrName(attribute.getName());
                    Object value2 = r.get(templateColumn.getColumnName());
                    return Objects.equals(toStringOrEmpty(value1), toStringOrEmpty(value2));// TODO conversion
                    // if (equal(toStringOrEmpty(value1), toStringOrEmpty(value2))) {
                    // return true;
                    // } else if (attribute.isOfType(REFERENCE, FOREIGNKEY, LOOKUP)) {
                    // return switch (k.getMode()) {
                    // case ETCM_CODE ->
                    // equal(dao.select(ATTR_ID).from(referenceHelper.getTargetClassForAttribute(attribute)).where(ATTR_CODE,
                    // EQ, value1).getCardIdOrNull(),
                    // dao.select(ATTR_ID).from(referenceHelper.getTargetClassForAttribute(attribute)).where(ATTR_CODE,
                    // EQ, value2).getCardIdOrNull());
                    // case ETCM_DESCRIPTION ->
                    // equal(dao.select(ATTR_ID).from(referenceHelper.getTargetClassForAttribute(attribute)).where(ATTR_DESCRIPTION,
                    // EQ, value1).getCardIdOrNull(),
                    // dao.select(ATTR_ID).from(referenceHelper.getTargetClassForAttribute(attribute)).where(ATTR_DESCRIPTION,
                    // EQ, value2).getCardIdOrNull());
                    // case ETCM_ID ->
                    // equal(toLongOrNull(value1), toLongOrNull(value2));
                    // default ->
                    // throw new EtlException("invalid column mode = %s for attr = %s", k.getMode(),
                    // attribute);
                    // };
                    // }
                    // return equal(rawToSystem(attribute, value1), rawToSystem(attribute, value2));
                } catch (Exception ex) {
                    throw new EtlException(ex, "key filter error for config = %s value =< %s >", k, value1);
                }
            });
        }
    }

    /**
     * Configuration holder for CAD import settings, including shape import, master
     * card filtering,
     * layer inclusion/exclusion, and transformation rules.
     */
    private class CadImportConfig {

        private final String shapeImportClassId, shapeAttrName, transformationRules, targetReferenceSystem,
                masterCardClassId;
        private final boolean replaceExisting, enableAutoMasterCardFilter, enableShapeImport;// note: it is necessary to
                                                                                             // force this if crs
                                                                                             // changes ! (test &
                                                                                             // confirm)
        private final Set<String> includeLayers, excludeLayers;
        private final List<String> shapeImportKeySource, shapeImportKeyAttr, masterCardKeySource, masterCardKeyAttr;
        private final List<EtlTemplateColumnMode> shapeImportKeyMode, masterCardKeyMode;

        public CadImportConfig(EtlLoaderApi api) {
            logger.debug("load cad import config =\n\n{}\n", mapToLoggableStringLazy(api.getConfig()));
            enableShapeImport = toBooleanOrDefault(api.getConfig("shape_import_enabled"), false);
            shapeImportClassId = api.getConfig("shape_import_target_class");
            shapeAttrName = api.getConfig("shape_import_target_attr");
            shapeImportKeySource = list(toListOfStrings(api.getConfig("shape_import_key_source")))
                    .map(String::toLowerCase);
            shapeImportKeyAttr = toListOfStrings(api.getConfig("shape_import_key_attr"));
            shapeImportKeyMode = isBlank(api.getConfig("shape_import_key_mode"))
                    ? Collections.nCopies(shapeImportKeyAttr.size(), ETCM_DEFAULT)
                    : list(toListOfStrings(api.getConfig("shape_import_key_mode")))
                            .map(s -> parseEnumOrDefault(s, ETCM_DEFAULT));
            transformationRules = unpackIfPacked(api.getConfig("shape_import_transformation_rules"));
            targetReferenceSystem = firstNotBlank(api.getConfig("shape_import_target_reference_system"), "EPSG:4326");
            replaceExisting = toBooleanOrDefault(api.getConfig("shape_import_replace_existing"), true);// note: it is
                                                                                                       // necessary to
                                                                                                       // force this if
                                                                                                       // crs changes !
                                                                                                       // (test &
                                                                                                       // confirm)
            includeLayers = list(toListOfStrings(api.getConfig("shape_import_source_layers_include")))
                    .map(String::toLowerCase).toSet().immutable();
            excludeLayers = list(toListOfStrings(api.getConfig("shape_import_source_layers_exclude")))
                    .map(String::toLowerCase).toSet().immutable();
            if (enableShapeImport) {
                checkNotBlank(shapeImportClassId, "missing target shape class");
                checkNotBlank(shapeAttrName, "missing target cad (shapefile) attr name");
                checkNotEmpty(shapeImportKeySource, "missing shape import key source");
                checkNotEmpty(shapeImportKeyAttr, "missing shape import key attr");
                checkArgument(
                        shapeImportKeySource.size() == shapeImportKeyAttr.size()
                                && shapeImportKeySource.size() == shapeImportKeyMode.size(),
                        "mismatching shape import key source/attrs config");
            }
            MasterCardFilterMode masterCardFilterMode = parseEnumOrDefault(api.getConfig("master_card_filter_mode"),
                    MCFM_FROMSHAPE);
            switch (masterCardFilterMode) {
                case MCFM_DISABLED -> {
                    enableAutoMasterCardFilter = false;
                    masterCardClassId = null;
                    masterCardKeySource = null;
                    masterCardKeyAttr = null;
                    masterCardKeyMode = null;
                }
                case MCFM_CUSTOM -> {
                    enableAutoMasterCardFilter = true;
                    masterCardClassId = api.getConfig("master_card_target_class");
                    masterCardKeySource = toListOfStrings(api.getConfig("master_card_key_source"));
                    masterCardKeyAttr = toListOfStrings(api.getConfig("master_card_key_attr"));
                    masterCardKeyMode = isBlank(api.getConfig("master_card_key_mode"))
                            ? Collections.nCopies(masterCardKeyAttr.size(), ETCM_DEFAULT)
                            : list(toListOfStrings(api.getConfig("master_card_key_mode")))
                                    .map(s -> parseEnumOrDefault(s, ETCM_DEFAULT));
                }
                case MCFM_FROMSHAPE -> {
                    enableAutoMasterCardFilter = enableShapeImport;
                    masterCardClassId = shapeImportClassId;
                    masterCardKeySource = shapeImportKeySource;
                    masterCardKeyAttr = shapeImportKeyAttr;
                    masterCardKeyMode = shapeImportKeyMode;
                }
                default ->
                    throw unsupported("unsupported master card filter mode = %s", masterCardFilterMode);
            }
            if (enableAutoMasterCardFilter) {
                checkNotBlank(masterCardClassId, "missing target master card class");
                checkNotEmpty(masterCardKeySource, "missing master card import key source");
                checkNotEmpty(masterCardKeyAttr, "missing master card import key attr");
                checkArgument(
                        masterCardKeySource.size() == masterCardKeyAttr.size()
                                && masterCardKeySource.size() == masterCardKeyMode.size(),
                        "mismatching master card import key source/attrs config");
            }
        }

        /**
         * Returns the combined shape import key source and attribute configuration.
         *
         * @return a list of column configs for shape import keys
         */
        public List<EtlTemplateColumnConfig> getShapeImportKeySourceAndAttrs() {
            return IntStream.range(0, shapeImportKeySource.size())
                    .mapToObj(i -> EtlTemplateColumnConfigImpl.builder().withAttributeName(shapeImportKeyAttr.get(i))
                            .withColumnName(shapeImportKeySource.get(i)).withMode(shapeImportKeyMode.get(i)).build())
                    .collect(toUnmodifiableList());
        }

        /**
         * Returns the combined master card key source and attribute configuration.
         *
         * @return a list of column configs for master card keys
         */
        public List<EtlTemplateColumnConfig> getMasterCardKeySourceAndAttrs() {
            return IntStream.range(0, masterCardKeySource.size())
                    .mapToObj(i -> EtlTemplateColumnConfigImpl.builder().withAttributeName(masterCardKeyAttr.get(i))
                            .withColumnName(masterCardKeySource.get(i)).withMode(masterCardKeyMode.get(i)).build())
                    .collect(toUnmodifiableList());
        }

    }

    /**
     * Enum for master card filter modes.
     */
    public enum MasterCardFilterMode {
        MCFM_DISABLED, MCFM_FROMSHAPE, MCFM_CUSTOM
    }

    /**
     * Formats a record for logging, showing only relevant columns.
     *
     * @param record   the record map
     * @param template the ETL template
     * @return a string representation for logging
     */
    private static String logRecord(Map<String, ?> record, EtlTemplate template) {
        return mapToLoggableStringInline(map(record).withKeys(
                list(template.getColumns()).map(EtlTemplateColumnConfig::getColumnName).with(CM_IMPORT_RECORD_ID)));
    }

    /**
     * Converts a {@link Polygon} to a {@link CadPolyline}.
     *
     * @param polygon the polygon
     * @return the corresponding CAD polyline
     */
    public static CadPolyline toCadPolyline(Polygon polygon) {
        return new CadPolyline(
                polygon.getPoints().stream().map(p -> new CadPoint(p.getX(), p.getY())).collect(toUnmodifiableList()));
    }

    /**
     * Builds a bounding box string from coordinates.
     *
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @return a comma-separated bounding box string
     */
    public static String buildBoundingBoxStr(double x1, double y1, double x2, double y2) {
        return format("%s,%s,%s,%s", x1, y1, x2, y2);// TODO check this
    }

}
