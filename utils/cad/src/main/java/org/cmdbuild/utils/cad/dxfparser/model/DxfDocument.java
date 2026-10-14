/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.cad.dxfparser.DxfToCadHelper;
import org.cmdbuild.utils.cad.dxfparser.utils.DxfDocumentUtils;
import org.cmdbuild.utils.cad.model.CadEntity;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface DxfDocument {

    static final String HEADER_VARIABLE_COMMENTS = "$COMMENTS";

    Map<String, DxfVariable> getHeaderVariables();

    List<DxfEntity> getEntities();

    List<DxfObject> getObjects();

    @Nullable
    default String getHeaderValue(String key) {
        return Optional.ofNullable(getHeaderVariables().get(checkNotBlank(key))).map(DxfVariable::getStringValue).orElse(null);
    }

    default Map<String, String> getHeaderValues() {
        return map(getHeaderVariables()).withValues(DxfVariable::hasStringValue).mapValues(DxfVariable::getStringValue);
    }

    default String getAcadVersion() {
        return format("%s %s", nullToEmpty(getHeaderValue("$ACADVER")), nullToEmpty(getHeaderValue("$ACADMAINTVER"))).trim();
    }

    default Map<String, String> getMetadataFromComments() {
        return Optional.ofNullable(getHeaderVariables().get(HEADER_VARIABLE_COMMENTS)).map(DxfVariable::getStringValue).map(DxfDocumentUtils::parseMetadata).orElse(emptyMap());
    }

    default Map<String, String> getMetadata() {
        return map(getHeaderVariables()).withValues(DxfVariable::hasStringValue).mapValues(DxfVariable::getStringValue).with(getMetadataFromComments());
    }

    default <T extends DxfObject> T getObject(Class<T> type) {
        return getObjects().stream().filter(type::isInstance).collect(onlyElement("object not found for type = %s", type));
    }

    default List<CadEntity> getCadEntities(String targetCoordinateSystem, boolean enableAngleDisplacementProcessing) {
        return new DxfToCadHelper(this, targetCoordinateSystem, enableAngleDisplacementProcessing).extractCadEntities();
    }

    default <T extends DxfGenericObject> List<T> getObjects(Class<T> type) {
        return (List) getObjects().stream().filter(type::isInstance).collect(toImmutableList());
    }

    default DxfGenericObject getObjectByType(String type) {
        return getObjects().stream().filter(DxfGenericObject.class::isInstance).map(DxfGenericObject.class::cast)
                .filter(o -> o.getType().equalsIgnoreCase(type)).collect(onlyElement("object not found for type = %s", type));
    }
}
