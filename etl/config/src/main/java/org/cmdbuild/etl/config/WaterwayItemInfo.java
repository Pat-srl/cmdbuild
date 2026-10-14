/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.configItemKey;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.getDescriptorCodeFromKey;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public interface WaterwayItemInfo extends WaterwayItemBase {

    String getDescriptorKey();

    default String getKey() {
        return configItemKey(getDescriptorKey(), getCode());
    }

    default String getDescriptorCode() {
        return getDescriptorCodeFromKey(getDescriptorKey());
    }

    default boolean isOfType(WaterwayItemType type) {
        return equal(getType(), type);
    }

    default boolean isOfType(WaterwayItemType... type) {
        return set(type).contains(getType());
    }
}
