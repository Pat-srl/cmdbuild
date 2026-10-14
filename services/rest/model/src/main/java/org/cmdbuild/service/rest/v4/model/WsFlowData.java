/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.annotation.Nullable;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * @author ldare
 */
public class WsFlowData {

    private final Map<String, Object> values;
    private final boolean advance;
    private final String taskId;

    @JsonCreator
    public WsFlowData(Map<String, Object> values) {
        this.values = map(checkNotNull(values)).immutable();
        advance = toBooleanOrDefault(values.get("_advance"), false);
        taskId = emptyToNull(toStringOrNull(values.get("_activity")));
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public boolean isAdvance() {
        return advance;

    }

    @Nullable
    public String getActivity() {
        return taskId;
    }

}
