package org.cmdbuild.gis;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import static java.lang.Long.parseLong;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.navtree.NavTreeNode;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface GisService {

    boolean isGisEnabled();

    boolean isGeoserverEnabled();

    List<GisAttribute> getGisAttributes();

    List<GisAttribute> getGisAttributesByOwnerClassIncludeInherited(String classId);

    List<GisAttribute> getGisAttributesVisibleFromClass(String classId);

    GisAttribute getGisAttributeIncludeInherited(String classId, String attributeName);

    GisAttribute getGisAttributeWithCurrentUser(long attributeId);

    GisAttribute createGisAttribute(GisAttribute gisAttribute);

    GisAttribute updateGisAttribute(GisAttribute gisAttribute);

    void deleteGisAttribute(String classId, String attributeName);

    List<GisValue> getGisValues(String classId, long cardId);

    List<GisValue> getGisValuesForCurrentUser(String classId, long cardId);

    List<GisValue> getGisValues(Collection<Long> attrs, String bbox, CmdbFilter filter, String forOwner);

    GisValue setGisValue(GisValue value);

    GisValue setGisValueWithCurrentUser(GisValue value);

    void deleteGisValueWithCurrentUser(String classId, long cardId, String attrName);

    GisValuesAndNavTree getGisValuesAndNavTree(Collection<Long> attrs, String bbox, CmdbFilter filter, String forOwner);

    GeoserverLayer setGeoserverLayer(String classId, String attrName, long cardId, DataHandler file);

    @Nullable
    GeoserverLayer getGeoserverLayerByCodeOrNull(String classId, String attrName, long cardId);

    @Nullable
    GeoserverLayer getGeoserverLayerByIdOrNull(String classId, Long attrName, long cardId);

    void deleteGeoServerLayer(long layerId);

    List<GeoserverLayer> getGeoServerLayers();

    List<GeoserverLayer> getGeoServerLayersForCard(String classId, Long cardId);

    NavTreeNode getGisNavTree();

    void updateGeoAttributesVisibilityForClass(String classId, Map<Long, Boolean> newVisibility);

    List<GisAttribute> updateGisAttributesOrder(List<Long> attrIdsInOrder);

    @Nullable
    Area getAreaForValues(Collection<Long> attrs, CmdbFilter filter, String forOwner);

    @Nullable
    Area getUserAreaForValues(Collection<Long> attrs, CmdbFilter filter);

    GeoserverLayer updateGeoserverLayer(GeoserverLayer build);

    default GeoserverLayer getGeoserverLayer(String classId, String codeOrId, long cardId) {
        if (NumberUtils.isCreatable(codeOrId)) {
            return checkNotNull(getGeoserverLayerByIdOrNull(classId, toLong(codeOrId), cardId), "geoserver layer not found for class = %s attr = %s card = %s", classId, codeOrId, cardId);
        } else {
            return checkNotNull(getGeoserverLayerByCodeOrNull(classId, codeOrId, cardId), "geoserver layer not found for class = %s attr = %s card = %s", classId, codeOrId, cardId);
        }
    }

    default void deleteGeoServerLayer(String classId, String attrName, long cardId) {
        deleteGeoServerLayer(getGeoserverLayer(classId, attrName, cardId).getId());
    }

    default boolean hasGeoAttributes(String classId) {
        return !getGisAttributesByOwnerClassIncludeInherited(classId).isEmpty();
    }

    default GisAttribute getGisAttributeWithCurrentUserByClassAndNameOrId(String classId, String attributeNameOrLayerId) {
        if (NumberUtils.isCreatable(attributeNameOrLayerId)) {
            GisAttribute gisAttribute = getGisAttributeWithCurrentUser(parseLong(attributeNameOrLayerId));
            return gisAttribute;
        } else {
            return GisService.this.getGisAttributeIncludeInherited(classId, attributeNameOrLayerId);//TODO fix this, priilege check
        }
    }

    @Nullable
    default GisValue getGisValueForCurrentUserOrNull(String classId, long cardId, String attrId) {
        checkNotBlank(attrId);
        return getGisValuesForCurrentUser(classId, cardId).stream().filter((v) -> equal(v.getLayerName(), attrId)).collect(toOptional()).orElse(null);
    }

    @Nullable
    default GisValue getGisValueOrNull(String classId, long cardId, String attrId) {
        checkNotBlank(attrId);
        return getGisValues(classId, cardId).stream().filter((v) -> equal(v.getLayerName(), attrId)).collect(toOptional()).orElse(null);
    }

    default GisValue getGisValueForCurrentUser(String classId, long cardId, String attrId) {
        return checkNotNull(getGisValueForCurrentUserOrNull(classId, cardId, attrId), "geo value not found for classId = %s cardId = %s attrId = %s", classId, cardId, attrId);
    }

    default GisValue getGisValue(String classId, long cardId, String attrId) {
        return checkNotNull(getGisValueOrNull(classId, cardId, attrId), "geo value not found for classId = %s cardId = %s attrId = %s", classId, cardId, attrId);
    }

    void checkAttributePermission(String classId, String attrName, GrantAttributePrivilege grantType);
}
