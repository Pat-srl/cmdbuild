package org.cmdbuild.etl.config.itemkey;

public interface ItemKey {

    String getCode();

    String getDescriptorKey();

    default String getDescriptorCode() {
        return ItemKeyUtils.getDescriptorCodeFromKey(getDescriptorKey());
    }
}
