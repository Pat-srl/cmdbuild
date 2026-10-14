/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface WaterwayDescriptor extends WaterwayDescriptorInfo {

    final String WY_DESCRIPTOR_CODE = "descriptor", WY_DESCRIPTOR_DESCRIPTION = "description";

    List<WaterwayItemConfig> getItems();

    Map<String, String> getConfig();

    default WaterwayItemConfig getItem(String code) {
        checkNotBlank(code);
        return getItems().stream().filter(i -> equal(i.getCode(), code)).collect(onlyElement("item not found for code =< %s >", code));
    }

    default boolean hasSingleItem() {
        return getItems().size() == 1;
    }

    default WaterwayItemConfig getSingleItem() {
        return getOnlyElement(getItems());
    }

    @Nullable
    default String getConfig(String key) {
        return getConfig().get(checkNotBlank(key));
    }
}
