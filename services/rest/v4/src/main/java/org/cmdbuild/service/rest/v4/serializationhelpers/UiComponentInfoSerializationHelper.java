/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.serializationhelpers;


import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;

import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class UiComponentInfoSerializationHelper {

    public static Object serializeInfo(UiComponentInfo component) {
        return map(
                "_id", component.getId(),
                "active", component.isActive(),
                "name", component.getName(),
                "description", component.getDescription(),
                "alias", component.getExtjsAlias(),
                "componentId", component.getExtjsComponentId(),
                "devices", CmCollectionUtils.list(component.getTargetDevices()).map(CmConvertUtils::serializeEnum));
    }
}
