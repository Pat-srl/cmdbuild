package org.cmdbuild.gis;

import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Map;
import static org.cmdbuild.gis.GisAttributeType.GAT_POLYGON;

public interface GisAttribute {

    @Nullable
    Long getId();

    String getLayerName();

    int getIndex();

    int getMinimumZoom();

    int getDefaultZoom();

    int getMaximumZoom();

    String getVisibility();

    Map<String, Boolean> getVisibilityMap();

    ZonedDateTime getBeginDate();

    String getOwnerClassName();

    boolean isActive();

    String getDescription();

    String getMapStyle();

    Map<String, Object> getMapStyleMap();

    GisAttributeType getType();

    GisAttributeConfig getConfig();

    default boolean isVisible(String tableName) {
        return getVisibilityMap().containsKey(tableName);
    }

    default boolean isVisibleActive(String tableName) {
        return isVisible(tableName) ? getVisibilityMap().get(tableName) : false;
    }

    default boolean hasId() {
        return getId() != null;
    }

    default boolean isPostgis() {
        return switch (getType()) {
            case GAT_POINT, GAT_POLYGON, GAT_LINESTRING ->
                true;
            default ->
                false;
        };
    }

    default boolean isPolygon() {
        return equal(getType(), GAT_POLYGON);
    }

    default boolean isGeoserver() {
        return switch (getType()) {
            case GAT_GEOTIFF, GAT_SHAPE ->
                true;
            default ->
                false;
        };
    }

}
