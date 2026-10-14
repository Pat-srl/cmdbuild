package org.cmdbuild.workflow;

import jakarta.annotation.Nullable;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.workflow.model.Process;

public interface WorkflowTypeConverter {

    @Nullable
    Object cardValueToFlowValue(@Nullable Object value, Attribute attribute);

    @Nullable
    Object cardValueToFlowValue(@Nullable Object value, CardAttributeType attributeType);

    @Nullable
    <T> T rawValueToFlowValue(@Nullable Object defaultValue, Class<T> javaType);

    Map<String, Object> flowValuesToCardValues(Process classe, Map<String, Object> data);

    @Nullable
    Object flowValueToCardValue(Process classe, String key, @Nullable Object value);

    Map<String, Object> widgetValuesToFlowValues(Map<String, Object> varsAndWidgetData);

    <T> T defaultValueForFlowInitialization(Class<T> javaType);

    @Nullable
    Object inflateFlowValueToCardValue(Object value);

}
