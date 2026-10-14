package org.cmdbuild.widget.model;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.Maps;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_ALWAYS_ENABLED_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_OUTPUT_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_REQUIRED_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_BUTTON_LABEL_KEY;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_HIDE_IN_CREATION;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_HIDE_IN_EDIT;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_INDEX;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_INLINE;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_INLINE_AFTER;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_INLINE_BEFORE;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_INLINE_CLOSED;

public interface WidgetData extends WidgetInfo {

    Map<String, Object> getData();

    default Map<String, Object> getExtendedData() {
        return Maps.filterKeys(getData(), not(in(set(WIDGET_REQUIRED_KEY, WIDGET_OUTPUT_KEY, WIDGET_BUTTON_LABEL_KEY, WIDGET_ALWAYS_ENABLED_KEY, WIDGET_INLINE, WIDGET_INLINE_CLOSED, WIDGET_INLINE_BEFORE, WIDGET_INLINE_AFTER))));
    }

    default boolean isRequired() {
        return toBooleanOrDefault(getData().get(WIDGET_REQUIRED_KEY), false);
    }

    default boolean hideInCreation() {
        return toBooleanOrDefault(getData().get(WIDGET_HIDE_IN_CREATION), false);
    }

    default boolean hideInEdit() {
        return toBooleanOrDefault(getData().get(WIDGET_HIDE_IN_EDIT), false);
    }

    default boolean isAlwaysEnabled() {
        return toBooleanOrDefault(getData().get(WIDGET_ALWAYS_ENABLED_KEY), false);
    }

    default boolean isInline() {
        return toBooleanOrDefault(getData().get(WIDGET_INLINE), false);
    }

    default boolean isInlineClosed() {
        return toBooleanOrDefault(getData().get(WIDGET_INLINE_CLOSED), false);
    }

    @Nullable
    default String getInlineBefore() {
        return trimToNull(toStringOrNull(getData().get(WIDGET_INLINE_BEFORE)));
    }

    @Nullable
    default String getInlineAfter() {
        return trimToNull(toStringOrNull(getData().get(WIDGET_INLINE_AFTER)));
    }

    @Nullable
    default String getOutputParameterOrNull() {
        return trimToNull(toStringOrNull(getData().get(WIDGET_OUTPUT_KEY)));
    }

    default String getOutputParameter() {
        return checkNotBlank(getOutputParameterOrNull());
    }

    default int getIndex() {
        return toInt(firstNotNull(getData().get(WIDGET_INDEX), Integer.MAX_VALUE));
    }

}
