/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;

import org.cmdbuild.cardfilter.StoredFilter;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author ldare
 */
public class ClassFilterSerializationHelper {

    public static FluentMap<String, Object> serializeFilter(StoredFilter filter, ObjectTranslationService translationService) {
        return map(
                "_id", filter.getId(),
                "name", filter.getName(),
                "description", filter.getDescription(),
                "_description_translation", filter.isShared() ? translationService.translateFilterDescription(filter.getOwnerName(), filter.getName(), filter.getDescription()) : filter.getDescription(),
                "target", filter.getOwnerName(),
                "ownerType", filter.getOwnerType(),
                "active", filter.isActive(),
                "configuration", filter.getConfiguration(),
                "shared", filter.isShared()
        );
    }
}
