/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.etl.config.WaterwayItemType.isNestedType;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

public interface WaterwayDescriptorService extends WaterwayDescriptorRepository {

    default WaterwayDescriptorRecord getDescriptorForSingleItemUpdate(String itemCode) {
        try {
            String descriptorCode = getItemByCode(itemCode).getDescriptorCode();
            WaterwayDescriptorRecord descriptor = getDescriptor(descriptorCode);
            List<WaterwayItem> items = list(getAllItems()).filter(i -> equal(descriptor.getKey(), i.getDescriptorKey()) && !isNestedType(i.getType()));
            checkArgument(items.size() == 1, "expected single first-level item, but got = %s", list(items).map(WaterwayItem::getCode).collect(joining(", ")));
//            checkArgument(equal(getOnlyElement(items).getCode(), code), "descriptor/item code mismatch for descriptor = %s item = %s", descriptor, getOnlyElement(items));
            return descriptor;
        } catch (Exception ex) {
            throw runtime(ex, "CM: cannot modify (supposed) single item descriptor with item code = < %s >", itemCode);
        }
    }

//    default boolean isSingleItemDescriptor(String code) {
//        WaterwayDescriptorRecord descriptor = getDescriptor(code);
//        List<WaterwayItem> items = list(getAllItems()).filter(i -> equal(descriptor.getKey(), i.getDescriptorKey()) && !isNestedType(i.getType()));
//        return items.size() == 1 && equal(getOnlyElement(items).getCode(), code);
//    }

}
