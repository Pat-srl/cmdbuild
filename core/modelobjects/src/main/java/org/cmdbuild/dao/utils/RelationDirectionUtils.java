/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

public class RelationDirectionUtils {

    public static String serializeRelationDirection(RelationDirection direction) {
        return direction.name().toLowerCase().replaceFirst("rd_", "");
    }

    public static RelationDirection parseRelationDirection(String value) {
        return parseEnum(value, RelationDirection.class);
    }

    @Nullable
    public static RelationDirection parseRelationDirectionOrNull(@Nullable String value) {
        return parseEnumOrNull(value, RelationDirection.class);
    }

}
