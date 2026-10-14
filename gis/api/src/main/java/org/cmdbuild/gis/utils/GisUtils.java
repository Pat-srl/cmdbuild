/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.utils;

import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.Geometry;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.gis.CmGeometry;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisException;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.gis.Linestring;
import org.cmdbuild.gis.Point;
import org.cmdbuild.gis.Polygon;
import org.cmdbuild.gis.model.GisValueImpl;
import org.cmdbuild.gis.model.LinestringImpl;
import org.cmdbuild.gis.model.PointImpl;
import org.cmdbuild.gis.model.PolygonImpl;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;

public class GisUtils {

    public static String buildGisTableName(String baseClassName, String layerName) {
        return format("gis.Gis_%s_%s", checkNotBlank(baseClassName), checkNotBlank(layerName));
    }

    public static String buildGisTableNameForQuery(String baseClassName, String layerName) {
        return format("gis.\"Gis_%s_%s\"", checkNotBlank(baseClassName), checkNotBlank(layerName));
    }

    public static String gisTableNameToBaseClassName(String tableName) {
        return checkNotBlank(tableName.replaceFirst("gis.Gis_(.+)_.+", "$1"));
    }

    public static String gisTableNameToAttributeName(String tableName) {
        return checkNotBlank(tableName.replaceFirst("gis.Gis_.+_(.+)", "$1"));
    }

    public static CmGeometry parseGeometry(String value) {
        try {
            Geometry postgisGeometry = new PGgeometry(checkNotBlank(value)).getGeometry();
            return switch (postgisGeometry.getType()) {
                case net.postgis.jdbc.geometry.Geometry.POINT ->
                    new PointImpl(postgisGeometry.getFirstPoint().getX(), postgisGeometry.getFirstPoint().getY());
                case net.postgis.jdbc.geometry.Geometry.LINESTRING ->
                    new LinestringImpl(IntStream.range(0, postgisGeometry.numPoints()).mapToObj(postgisGeometry::getPoint).map(p -> new PointImpl(p.getX(), p.getY())).collect(toList()));
                case net.postgis.jdbc.geometry.Geometry.POLYGON ->
                    new PolygonImpl(IntStream.range(0, postgisGeometry.numPoints()).mapToObj(postgisGeometry::getPoint).map(p -> new PointImpl(p.getX(), p.getY())).collect(toList()));
                default ->
                    throw illegalArgument("unsupported postgis geometry = %s", postgisGeometry);
            };
        } catch (SQLException ex) {
            throw new GisException(ex, "error parsing geometry from string value = '%s'", abbreviate(value));
        }
    }

    @Nullable
    public static GisValue postgisSqlToCmGeometryOrNull(GisAttribute attribute, long ownerCardId, @Nullable String sqlGeometryValue, @Nullable String ownerDescription) {
        return postgisSqlToCmGeometryOrNull(attribute.getLayerName(), attribute.getOwnerClassName(), ownerCardId, sqlGeometryValue, ownerDescription);
    }

    @Nullable
    public static GisValue postgisSqlToCmGeometryOrNull(String layerName, String ownerClassName, long ownerCardId, @Nullable String sqlGeometryValue, @Nullable String ownerDescription) {
        if (isBlank(sqlGeometryValue)) {
            return null;
        } else {
            CmGeometry cmGeometry = parseGeometry(sqlGeometryValue);
            return GisValueImpl.builder()
                    .withLayerName(layerName)
                    .withOwnerCardId(ownerCardId)
                    .withOwnerClassId(ownerClassName)
                    .withGeometry(cmGeometry)
                    .withOwnerCardDescription(ownerDescription)
                    .build();
        }
    }

    public static String cmGeometryToPostgisSql(CmGeometry geometry) {
        return switch (geometry.getType()) {
            case POINT ->
                format("POINT(%s %s)", serializePostgisDouble(((Point) geometry).getX()), serializePostgisDouble(((Point) geometry).getY()));
            case LINESTRING ->
                format("LINESTRING(%s)", ((Linestring) geometry).getPoints().stream().map((p) -> format("%s %s", serializePostgisDouble(p.getX()), serializePostgisDouble(p.getY()))).collect(joining(",")));
            case POLYGON ->
                format("POLYGON((%s))", ((Polygon) geometry).getPoints().stream().map((p) -> format("%s %s", serializePostgisDouble(p.getX()), serializePostgisDouble(p.getY()))).collect(joining(",")));
            default ->
                throw unsupported("unsupported geometry type = %s", geometry.getType());
        };
    }

    private static String serializePostgisDouble(double value) {
        return new BigDecimal(value, new MathContext(15, RoundingMode.HALF_UP)).stripTrailingZeros().toPlainString();
    }
}
