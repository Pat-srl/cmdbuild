/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;

import org.cmdbuild.corecomponents.CoreComponent;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author ldare
 */
public class CoreComponentSerializationHelper {

    public static Object applySerializationToCoreComponent(CoreComponent coreComponent) {
        return serializeDetails(coreComponent);
    }

    public static FluentMap<String, Object> serializeInfo(CoreComponent component) {
        return map(
                "_id", component.getCode(),
                "active", component.isActive(),
                "name", component.getCode(),
                "description", component.getDescription(),
                "type", serializeEnum(component.getType()));
    }

    public static FluentMap<String, Object> serializeDetails(CoreComponent component) {
        return serializeInfo(component).with("data", component.getData());
    }
}
