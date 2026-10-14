/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceArrayAttributeType;
import org.cmdbuild.exception.WidgetException;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_CLASS_NAME;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_OUTPUT_TYPE;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_TYPE_LINK_CARDS;
import static org.cmdbuild.widget.utils.WidgetValueUtils.parseWidgetStringValueOrRawValue;

public interface Widget extends WidgetData {

    Map<String, Object> getContext();

    default boolean has(String key) {
        return isNotBlank(getStringOrNull(key));
    }

    default String getNotBlank(String key) {
        return checkNotBlank(getStringOrNull(key));
    }

    @Nullable
    default String getStringOrNull(String key) {
        return toStringOrNull(getData().get(key));
    }

    @Nullable
    default String getOutputKeyOrNull() {
        return getOutputParameterOrNull();
    }

    default String getOutputKey() {
        return checkNotBlank(getOutputKeyOrNull());
    }

    default boolean hasOutputKey() {
        return isNotBlank(getOutputParameterOrNull());
    }

    default boolean hasNoSelect() {
        return equal(getStringOrNull("NoSelect"), "1");
    }

    default boolean hasOutputType() {
        return getOutputTypeOrNull() != null;
    }

    default CardAttributeType getOutputType() {
        return checkNotNull(getOutputTypeOrNull());
    }

    @Nullable
    default CardAttributeType getOutputTypeOrNull() {
        try {
            switch (getType()) {
                case WIDGET_TYPE_LINK_CARDS: {
                    String className = checkNotBlank(parseWidgetStringValueOrRawValue(toStringOrNull(getData().get(WIDGET_CLASS_NAME))), "missing ClassName widget attr"); //TODO fix bugs, use parseWidgetStringValue()
                    return new ReferenceArrayAttributeType(className);
                }
                default:
                    String outputType = getStringOrNull(WIDGET_OUTPUT_TYPE);
                    if (isBlank(outputType)) {
                        return null;
                    } else {
                        //TODO better
                        switch (parseEnum(outputType, AttributeTypeName.class)) {
                            case REFERENCEARRAY: {
                                String className = checkNotBlank(parseWidgetStringValueOrRawValue(toStringOrNull(getData().get(WIDGET_CLASS_NAME))), "missing ClassName widget attr"); //TODO fix bugs, use parseWidgetStringValue()
                                return new ReferenceArrayAttributeType(className);
                            }
                            default:
                                throw new UnsupportedOperationException(format("unsupported output type =< %s >", outputType));
                        }
                    }
            }
        } catch (Exception ex) {
            throw new WidgetException(ex, "error processing output type for widget = %s", this);
        }
    }

}
