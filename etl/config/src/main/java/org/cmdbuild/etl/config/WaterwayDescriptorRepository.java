/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface WaterwayDescriptorRepository {

    WaterwayDescriptorRecord createUpdateDescriptor(DataSource config, @Nullable WaterwayDescriptorMeta meta, boolean overwriteExisting);

    WaterwayDescriptorRecord updateDescriptorMeta(String code, WaterwayDescriptorMeta meta);

    @Nullable
    WaterwayDescriptorRecord getDescriptorOrNull(String code);

    void deleteDescriptor(String code);

    List<WaterwayDescriptorRecord> getAllDescriptors();

    List<WaterwayItem> getAllItems();

    default List<WaterwayItem> getAllItems(WaterwayItemType... itemType) {
        return getAllItems().stream().filter(i -> i.isOfType(itemType)).collect(toList());
    }

    default List<WaterwayItem> getAllItemsEnabled() {
        return getAllItems().stream().filter(WaterwayItem::isEnabled).collect(toList());
    }

    default List<WaterwayItem> getAllItemsEnabled(WaterwayItemType... itemType) {
        return getAllItemsEnabled().stream().filter(i -> i.isOfType(itemType)).collect(toList());
    }

    default WaterwayDescriptorRecord createUpdateDescriptor(String config, @Nullable WaterwayDescriptorMeta meta) {
        return createUpdateDescriptor(config, meta, true);
    }

    default WaterwayDescriptorRecord createUpdateDescriptor(String config, @Nullable WaterwayDescriptorMeta meta, boolean overwriteExisting) {
        return createUpdateDescriptor(newDataSource(config), meta, overwriteExisting);
    }

    default WaterwayDescriptorRecord createUpdateDescriptor(DataSource config, @Nullable WaterwayDescriptorMeta meta) {
        return createUpdateDescriptor(config, meta, true);
    }

    default WaterwayDescriptorRecord getDescriptor(String code) {
        return checkNotNull(getDescriptorOrNull(code), "descriptor not found for code =< %s >", code);
    }

    default WaterwayDescriptorRecord createUpdateDescriptor(DataSource config) {
        return createUpdateDescriptor(config, null);
    }

    default WaterwayItem getItemByCode(String code) {
        checkNotBlank(code);
        return getAllItems().stream().filter(i -> equal(i.getCode(), code)).collect(onlyElement("item not found for code =< %s >", code));
    }

}
