package org.cmdbuild.common.beans;

import static org.cmdbuild.common.beans.CardIdAndClassNameUtils.serializeTypeAndCode;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public interface TypeAndCode {

    String getType();

    String getCode();

    default boolean hasType(String... oneof) {
        return set(oneof).contains(getType());
    }

    default String serialize() {
        return serializeTypeAndCode(this);
    }
}
