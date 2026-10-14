/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface AbstractMetadata {

    public static final String ACTIVE = "cm_active";
    public static final String DESCRIPTION = "cm_description";

    String getDescription();

    boolean isActive();

    @Nullable
    String get(String key);

    Map<String, String> getAll();

    Map<String, String> getCustomMetadata();

    default boolean hasValue(String key) {
        return isNotBlank(get(key));
    }

}
