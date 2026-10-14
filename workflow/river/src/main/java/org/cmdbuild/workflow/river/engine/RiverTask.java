package org.cmdbuild.workflow.river.engine;

import static com.google.common.base.Objects.equal;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.Multimap;
import static java.util.Collections.emptyList;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_ATTR_PERFORMER_TYPE;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_ATTR_PERFORMER_VALUE;

public interface RiverTask<T> {

    String getPlanId();

    String getId();

    RiverTaskType getTaskType();

    Multimap<String, String> getExtendedAttributes();

    T getTaskTypeData();

    default boolean isOfType(RiverTaskType... types) {
        return set(types).contains(getTaskType());
    }

    @Nullable
    default String getExtendedAttribute(String key) {
        return getOnlyElement(firstNonNull(getExtendedAttributes().get(key), emptyList()), null);
    }

    @Nullable
    default String getAttr(String key) {
        return Iterables.getOnlyElement(getExtendedAttributes().get(key), null);
    }

    default boolean isInline() {
        return equal(getTaskType(), RiverTaskType.SCRIPT_INLINE);
    }

    default boolean isNoop() {
        return equal(getTaskType(), RiverTaskType.NOP);
    }

    default boolean isUser() {
        return equal(getTaskType(), RiverTaskType.USER);
    }

    default boolean isBatch() {
        return equal(getTaskType(), RiverTaskType.SCRIPT_BATCH);
    }

    default String getPerformerValue() {
        return checkNotBlank(getAttr(TASK_ATTR_PERFORMER_VALUE));
    }

    default String getPerformerType() {
        return checkNotBlank(getAttr(TASK_ATTR_PERFORMER_TYPE));
    }

}
