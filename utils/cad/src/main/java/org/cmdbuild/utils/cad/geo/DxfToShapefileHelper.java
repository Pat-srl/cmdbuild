/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.geo;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicates;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.utils.cad.dxfparser.CadException;
import org.cmdbuild.utils.cad.dxfparser.model.DxfDocument;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntity;
import static org.cmdbuild.utils.cad.geo.GeoUtils.serializeTransformationRules;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmZipUtils.dirToZip;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.data.Transaction;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DxfToShapefileHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DxfDocument document;
    private final Predicate<DxfEntity> entityFilter;
    private final CadPointTransformationHelper helper;
    private final boolean enableAngleDisplacementProcessing;

    private DxfToShapefileHelper(DxfToShapefileHelperBuilder builder, boolean enableAngleDisplacementProcessing) {
        this.document = checkNotNull(builder.document);
        this.entityFilter = firstNotNull(builder.entityFilter, Predicates.alwaysTrue());
        this.enableAngleDisplacementProcessing = enableAngleDisplacementProcessing;
        helper = builder.transformationRules == null ? CadPointTransformationHelper.fromDocument(document, builder.targetReferenceSystem, enableAngleDisplacementProcessing) : new CadPointTransformationHelper(builder.transformationRules);
    }

    public DxfDocument getDocument() {
        return document;
    }

    public Predicate<DxfEntity> getEntityFilter() {
        return entityFilter;
    }

    public CadPoint getShapeFileLocation() { //TODO improve this (?) use single stream aggregator
        List<CadPoint> points = streamEntitiesForShapeFile().flatMap(e -> e.getPerimeter().getVertexes().stream()).map(helper::cadPointToGeoPoint).collect(toImmutableList());
        return point(points.stream().mapToDouble(CadPoint::getX).average().orElse(0), points.stream().mapToDouble(CadPoint::getY).average().orElse(0));
    }

    public long getShapeFileElementCount() {
        return streamEntitiesForShapeFile().count();
    }

    public BigByteArray toShapeFile() {
        try {
            logger.debug("start conversion of dxf content to shape file, using helper = {}", helper);
            logger.debug("transformation rules = {}", serializeTransformationRules(helper.getTransformationRules()));
            String targetReferenceSystem = helper.getTargetCoordinateSystem();
            CoordinateReferenceSystem crs = CRS.decode(targetReferenceSystem);

            SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
            builder.setName("MyFeature");
            builder.setCRS(crs);
            builder.add("the_geom", LineString.class);
            SimpleFeatureType MY_FEATURE_TYPE = builder.buildFeatureType();

            DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

            streamEntitiesForShapeFile().map(e -> {
                logger.trace("add feature from entity = {}", e);
                SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(MY_FEATURE_TYPE);
                LineString lineString = geometryFactory.createLineString(list(e.getPerimeter().getVertexes()).accept(l -> {
                    if (e.isClosedPerimeter()) {
                        l.add(e.getPerimeter().getVertexes().iterator().next());
                    }
                }).stream().map(helper::cadPointToGeoPoint).map(p -> new Coordinate(p.getX(), p.getY())).collect(toList()).toArray(Coordinate[]::new));
                featureBuilder.add(lineString);
                return featureBuilder.buildFeature(null);
            }).forEach(featureCollection::add);

            logger.debug("processed {} features; build shape file", featureCollection.size());

            File dir = tempDir(), file = new File(dir, "file.shp");
            try {
                ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
                ShapefileDataStore dataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(map(
                        "url", file.toURI().toURL(),
                        "create spatial index", Boolean.TRUE
                ));
                dataStore.createSchema(MY_FEATURE_TYPE);
                dataStore.forceSchemaCRS(crs);
                try (Transaction transaction = new DefaultTransaction("create")) {
                    String typeName = getOnlyElement(list(dataStore.getTypeNames()));
                    SimpleFeatureStore featureStore = (SimpleFeatureStore) dataStore.getFeatureSource(typeName);
                    featureStore.setTransaction(transaction);
                    featureStore.addFeatures(featureCollection);
                    transaction.commit();
                }

                return new BigByteArray(dirToZip(dir));
            } finally {
                deleteQuietly(dir);
            }
        } catch (FactoryException | IOException ex) {
            throw new CadException(ex);
        }
    }

    private Stream<DxfEntity> streamEntitiesForShapeFile() {
        return document.getEntities().stream().filter(entityFilter).filter(e -> !e.getPerimeter().isPoint());
    }

    public static DxfToShapefileHelperBuilder builder() {
        return new DxfToShapefileHelperBuilder();
    }

    public static DxfToShapefileHelperBuilder withDocument(DxfDocument dxfDocument) {
        return builder().withDocument(dxfDocument);
    }

    public static BigByteArray toShapeFile(DxfDocument dxfDocument) {
        return withDocument(dxfDocument).toShapeFile();
    }

    public static class DxfToShapefileHelperBuilder implements Builder<DxfToShapefileHelper, DxfToShapefileHelperBuilder> {

        private DxfDocument document;
        private Predicate<DxfEntity> entityFilter;
        private String targetReferenceSystem;
        private List<PointTransformationRule> transformationRules;
        private boolean enableAngleDisplacementProcessing;

        public DxfToShapefileHelperBuilder withDocument(DxfDocument document) {
            this.document = document;
            return this;
        }

        public DxfToShapefileHelperBuilder withEntityFilter(Predicate<DxfEntity> entityFilter) {
            this.entityFilter = entityFilter;
            return this;
        }

        public DxfToShapefileHelperBuilder withTransformationRules(List<PointTransformationRule> transformationRules) {
            this.transformationRules = transformationRules;
            return this;
        }

        public DxfToShapefileHelperBuilder withTargetReferenceSystem(String targetReferenceSystem) {
            this.targetReferenceSystem = targetReferenceSystem;
            return this;
        }

        public DxfToShapefileHelperBuilder withEnableAngleDisplacementProcessing(boolean enableAngleDisplacementProcessing) {
            this.enableAngleDisplacementProcessing = enableAngleDisplacementProcessing;
            return this;
        }

        @Override
        public DxfToShapefileHelper build() {
            return new DxfToShapefileHelper(this, firstNotNull(enableAngleDisplacementProcessing, true));
        }

        public BigByteArray toShapeFile() {
            return build().toShapeFile();
        }

    }
}
