/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface WaterwayDescriptorMeta {

    @Nullable
    String getCode();

    @Nullable
    String getDescription();

    boolean isEnabled();

    Set<String> getDisabledItems();

    Map<String, String> getParams();

    default boolean hasCode() {
        return isNotBlank(getCode());
    }
}
