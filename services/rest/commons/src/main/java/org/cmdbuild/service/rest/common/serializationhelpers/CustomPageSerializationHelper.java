/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author ldare
 */
public class CustomPageSerializationHelper {

    public static Object serializeCustomPage(UiComponentInfo customPage, ObjectTranslationService translationService) {
        return map(
                "_id", customPage.getId(),
                "active", customPage.isActive(),
                "name", customPage.getName(),
                "description", customPage.getDescription(),
                "_description_translation", translationService.translateCustomPageDesciption(customPage.getName(), customPage.getDescription()),
                "alias", customPage.getExtjsAlias(),
                "componentId", customPage.getExtjsComponentId(),
                "devices", CmCollectionUtils.list(customPage.getTargetDevices()).map(CmConvertUtils::serializeEnum),
                "_updated", toIsoDateTime(customPage.getLastUpdated()),
                "icon_font", customPage.getIconFont());
    }

    public static Stream<Object> serializeCustomPageList(List<UiComponentInfo> listUiComponent, ObjectTranslationService objectTranslationService) {
        return listUiComponent.stream().map(c -> serializeCustomPage(c, objectTranslationService));
    }
}
