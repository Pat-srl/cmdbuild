/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import java.util.List;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface WaterwayItem extends WaterwayItemInfo, WaterwayItemBaseExt {

    final String WY_ITEM_STORAGE = "storage", WY_ITEM_TAG = "tag";

    List<String> getItems();

    default boolean hasStorage() {
        return isNotBlank(getStorage());
    }

    @Nullable
    default String getStorage() {
        return getConfig(WY_ITEM_STORAGE);
    }

    @Nullable
    default String getTag() {
        return getConfig(WY_ITEM_TAG);
    }
}
