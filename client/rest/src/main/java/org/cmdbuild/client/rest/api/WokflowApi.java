/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.client.rest.core.RestServiceClient;
import org.cmdbuild.client.rest.model.AttributeDetail;
import org.cmdbuild.client.rest.model.FlowDataImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.model.AdvancedFlowStatus;

public interface WokflowApi extends RestServiceClient {

    WokflowServiceWithFlowDataAndStatus start(String processId, FlowData process);

    WokflowServiceWithFlowDataAndStatus advance(String processId, String instanceId, String taskId, FlowData simpleProcessInstance);

    FlowData get(String processId, String instanceId);

    List<TaskInfo> getTaskList(String processId, String instanceId);

    TaskDetail getTask(String processId, String instanceId, String taskId);

    List<PlanInfo> getPlans();

    PlanInfo getPlan(String processId);

    WokflowServiceWithPlanVersionInfo uploadPlanVersion(String processId, InputStream data);

    WokflowServiceWithPlanVersionInfo replacePlanVersion(String processId, InputStream data);

    String downloadPlanVersion(String classId, String planId);

    byte[] downloadFlowGraph(String classId, long toLong);

    byte[] downloadSimplifiedFlowGraph(String classId, long toLong);

    List<PlanVersionInfo> getPlanVersions(String processId);

    String getXpdlTemplate(String processId);

    TaskDetail getStartProcessTask(String processId);

    default WokflowServiceWithFlowDataAndStatus start(String processId, Map<String, Object> data) {
        return start(processId, FlowDataImpl.builder().withAttributes(data).build());
    }

    default WokflowServiceWithFlowDataAndStatus start(String processId, Object... data) {
        return start(processId, map(data));
    }

    default WokflowServiceWithFlowDataAndStatus advance(String processId, String instanceId, String taskId, Map<String, Object> data) {
        return advance(processId, instanceId, taskId, FlowDataImpl.builder().withAttributes(data).build());
    }

    default WokflowServiceWithFlowDataAndStatus advance(String processId, long instanceId, String taskId, Map<String, Object> data) {
        return advance(processId, String.valueOf(instanceId), taskId, data);
    }

    default WokflowServiceWithPlanVersionInfo uploadPlanVersion(String processId, String data) {
        return uploadPlanVersion(processId, new ByteArrayInputStream(data.getBytes(UTF_8)));
    }

    interface WokflowServiceWithFlowDataAndStatus {

        FlowDataAndStatus getFlowData();

        WokflowApi then();

        default long getFlowCardId() {
            return toLong(getFlowData().getFlowCardId());
        }
    }

    interface WokflowServiceWithPlanVersionInfo {

        PlanVersionInfo getPlanVersionInfo();

        WokflowApi then();
    }

    interface PlanVersionInfo {

        String getId();

        String getVersion();

        String getProvider();

        String getPlanId();

        boolean isDefault();
    }

    interface PlanInfo {

        String getId();

        String getDescription();

        @Nullable
        String getProvider();

    }

    interface TaskInfo {

        String getId();

        String getDescription();
    }

    interface TaskDetail extends TaskInfo {

        List<TaskParam> getParams();

    }

    interface TaskParam {

        String getName();

        boolean isWritable();

        boolean isRequired();

        boolean isAction();

        AttributeDetail getDetail();
    }

    interface FlowDataAndStatus extends FlowData {

        String getFlowCardId();

        AdvancedFlowStatus getFlowStatus();

        List<TaskDetail> getTaskList();
    }

    interface FlowData {

        @Nullable
        String getFlowId();

        Map<String, Object> getAttributes();

        String getStatus();

        default boolean hasId() {
            return !isBlank(getFlowId());
        }

    }

}
