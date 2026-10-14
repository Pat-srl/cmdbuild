/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface Item {

    @Nullable
    Long getId();

    String getTypeName();

    Map<String, Object> getData();

    @Nullable
    default Object get(String key) {
        return getData().get(checkNotBlank(key));
    }

    default boolean hasId() {
        return isNotNullAndGtZero(getId());
    }
}
