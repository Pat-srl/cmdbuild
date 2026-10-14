package org.cmdbuild.cluster;

import java.time.ZonedDateTime;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;

public interface ClusterMessage {

    final String THIS_INSTANCE_ID = "THIS";

    String getSourceInstanceId();

    String getMessageId();

    ZonedDateTime getTimestamp();

    String getMessageType();

    Map<String, Object> getMessageData();

    @Nullable
    String getTargetNodeId();

    @Nullable
    default <T> T getValue(String key, Class<T> type) {
        return convert(getMessageData().get(key), type);
    }

    @Nullable
    default String getValue(String key) {
        return getValue(key, String.class);
    }

    default boolean hasTarget() {
        return isNotBlank(getTargetNodeId());
    }
}
