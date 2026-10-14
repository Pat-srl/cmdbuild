package org.cmdbuild.gis;

import jakarta.annotation.Nullable;

public interface GisValue<T extends CmGeometry> {

    String getLayerName();

    String getOwnerClassId();

    long getOwnerCardId();

    @Nullable
    String getOwnerCardDescription();

    T getGeometry();

    default GisValueType getType() {
        return getGeometry().getType();
    }

    default <E> E getGeometry(Class<E> type) {
        return (E) getGeometry();
    }

}
