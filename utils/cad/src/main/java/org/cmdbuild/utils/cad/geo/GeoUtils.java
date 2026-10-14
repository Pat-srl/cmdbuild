/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.geo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;
import static java.lang.Integer.parseInt;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.utils.cad.dxfparser.CadException;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDouble;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static CadPoint translateCoordinates(CadPoint cadPoint, String from, String to) {
        try {
            checkNotNull(cadPoint);
            LOGGER.trace("translate point {} from {} to {}", cadPoint, from, to);
            CoordinateReferenceSystem sourceCRS = CRS.decode(checkNotBlank(from), true);
            CoordinateReferenceSystem targetCRS = CRS.decode(checkNotBlank(to), true);
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, false);
            int srid = parseInt(to.replaceFirst("EPSG:([0-9]+)", "$1"));//TODO check this
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), srid);
            Point point = geometryFactory.createPoint(new Coordinate(cadPoint.getX(), cadPoint.getY()));
            Point targetPoint = (Point) JTS.transform(point, transform);
            CadPoint geoPoint = point(targetPoint.getX(), targetPoint.getY());
            LOGGER.trace("translated point {} from {} to {} as {}", cadPoint, from, to, geoPoint);
            return geoPoint;
        } catch (MismatchedDimensionException | TransformException | FactoryException ex) {
            throw new CadException(ex);
        }
    }

    public static String serializeTransformationRules(List<PointTransformationRule> rules) {
        return toJson(rules.stream().map(rule -> map().skipNullValues().with(
                "translation_x", rule.getTranslation() == null ? null : rule.getTranslation().getX(),
                "translation_y", rule.getTranslation() == null ? null : rule.getTranslation().getY(),
                "rotation_angle", rule.getRotationAngle(),
                "rotation_center_x", rule.getRotationCenter() == null ? null : rule.getRotationCenter().getX(),
                "rotation_center_y", rule.getRotationCenter() == null ? null : rule.getRotationCenter().getY(),
                "scaling_x", rule.getScaling() == null ? null : rule.getScaling().getX(),
                "scaling_y", rule.getScaling() == null ? null : rule.getScaling().getY(),
                "scaling_center_x", rule.getScalingCenter() == null ? null : rule.getScalingCenter().getX(),
                "scaling_center_y", rule.getScalingCenter() == null ? null : rule.getScalingCenter().getY(),
                "source_crs", rule.getSourceCoordinateReferenceSystem(),
                "target_crs", rule.getTargetCoordinateReferenceSystem())).collect(toImmutableList()));
    }

    public static List<PointTransformationRule> parseTransformationRules(String rules) {
        ArrayNode array = fromJson(checkNotBlank(rules), ArrayNode.class);
        return stream(array.elements()).map(e -> fromJson((ObjectNode) e, MAP_OF_STRINGS)).map((Map<String, String> e) -> new PointTransformationRuleImpl(
                isBlank(e.get("translation_x")) ? null : point(toDouble(e.get("translation_x")), toDouble(e.get("translation_y"))),
                isBlank(e.get("rotation_angle")) ? null : toDouble(e.get("rotation_angle")),
                isBlank(e.get("rotation_center_x")) ? null : point(toDouble(e.get("rotation_center_x")), toDouble(e.get("rotation_center_y"))),
                isBlank(e.get("scaling_x")) ? null : point(toDouble(e.get("scaling_x")), toDouble(e.get("scaling_y"))),
                isBlank(e.get("scaling_center_x")) ? null : point(toDouble(e.get("scaling_center_x")), toDouble(e.get("scaling_center_y"))),
                e.get("source_crs"),
                e.get("target_crs")
        )).collect(toImmutableList());
    }
}
