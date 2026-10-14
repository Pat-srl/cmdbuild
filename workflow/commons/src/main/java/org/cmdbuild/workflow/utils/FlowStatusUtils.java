/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.cmdbuild.common.beans.LookupValue;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.cmdbuild.workflow.model.FlowStatus;
import static org.cmdbuild.workflow.model.FlowStatus.ABORTED;
import static org.cmdbuild.workflow.model.FlowStatus.COMPLETED;
import static org.cmdbuild.workflow.model.FlowStatus.OPEN;
import static org.cmdbuild.workflow.model.FlowStatus.SUSPENDED;

public class FlowStatusUtils {

    public static boolean isCompleted(FlowStatus status) {
        return switch (status) {
            case COMPLETED, ABORTED ->
                true;
            case OPEN, SUSPENDED ->
                false;
            default ->
                throw unsupported("unsupported flow status = %s", status);
        };
    }

    public static final String STATE_OPEN_RUNNING = "open.running",
            STATE_OPEN_NOT_RUNNING_SUSPENDED = "open.not_running.suspended",
            STATE_CLOSED_COMPLETED = "closed.completed",
            //            STATE_CLOSED_TERMINATED = "closed.terminated",
            STATE_CLOSED_ABORTED = "closed.aborted",
            FLOW_STATUS_LOOKUP = "FlowStatus";

    private static final BiMap<String, FlowStatus> stateByFlowStatusCode;

    static {
        stateByFlowStatusCode = HashBiMap.create();
        stateByFlowStatusCode.put(STATE_OPEN_RUNNING, FlowStatus.OPEN);
        stateByFlowStatusCode.put(STATE_OPEN_NOT_RUNNING_SUSPENDED, FlowStatus.SUSPENDED);
        stateByFlowStatusCode.put(STATE_CLOSED_COMPLETED, FlowStatus.COMPLETED);
        stateByFlowStatusCode.put(STATE_CLOSED_ABORTED, FlowStatus.ABORTED);
    }

    public static String toFlowStatusLookupCode(FlowStatus status) {
        return checkNotNull(stateByFlowStatusCode.inverse().get(checkNotNull(status)));
    }

    public static FlowStatus toFlowStatus(LookupValue value) {
        checkArgument(value.hasCode(), "invalid flow status lookup = %s (missing lookup code)", value);
        return checkNotNull(stateByFlowStatusCode.get(value.getCode()), "invalid flow status lookup = %s", value);
    }
}
