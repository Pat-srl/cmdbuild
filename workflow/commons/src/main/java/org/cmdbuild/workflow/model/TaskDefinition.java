package org.cmdbuild.workflow.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.MoreCollectors.toOptional;
import static com.google.common.collect.Streams.stream;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.widget.model.WidgetData;

public interface TaskDefinition {

    final static String TASKDEF_METADATA_DESCRIPTION_ATTR_NAME = "AdditionalActivityLabel";
    final static String TASKDEF_METADATA_ACTIVITY_SUBSET_ID = "ActivitySubsetId";

    String getId();

    String getDescription();

    String getInstructions();

    List<TaskPerformer> getPerformers();

    TaskPerformer getFirstNonAdminPerformer();

    List<TaskAttribute> getVariables();

    Iterable<TaskMetadata> getMetadata();

    List<WidgetData> getWidgets();

    @Nullable
    default String getMetadata(String key) {
        return stream(getMetadata()).filter(m -> equal(key, m.getName())).map(TaskMetadata::getValue).collect(toOptional()).orElse(null);
    }

    @Nullable
    default String getTaskDescriptionAttrName() {
        return getMetadata(TASKDEF_METADATA_DESCRIPTION_ATTR_NAME);
    }

    @Nullable
    default String getTaskActivitySubsetId() {
        return getMetadata(TASKDEF_METADATA_ACTIVITY_SUBSET_ID);
    }

    default boolean hasTaskDescriptionAttrName() {
        return isNotBlank(getTaskDescriptionAttrName());
    }

    default boolean hasActivitySubsetId() {
        return isNotBlank(getTaskActivitySubsetId());
    }

    default WidgetData getWidgetById(String widgetId) {
        return getWidgets().stream().filter(w -> equal(w.getId(), checkNotBlank(widgetId))).collect(onlyElement("widget not found for id =< %s >", widgetId));
    }

}
