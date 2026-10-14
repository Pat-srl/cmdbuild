package org.cmdbuild.widget.model;

import org.apache.commons.lang3.math.NumberUtils;

public interface WidgetInfo {

    String getType();

    boolean isActive();

    String getId();

    String getLabel();

    default boolean isCustomComponent() {
        return NumberUtils.isCreatable(getId());//TODO check this !! is id? is type?
    }

}
