/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.Streams;
import static com.google.common.collect.Streams.stream;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.annotation.Nullable;
import java.io.InputStream;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.client.rest.api.WokflowApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.model.AttributeDetail;
import org.cmdbuild.client.rest.model.FlowDataImpl;
import org.cmdbuild.client.rest.model.SimpleAttributeDetail;
import org.cmdbuild.client.rest.model.SimpleFlowDataAndStatus;
import org.cmdbuild.client.rest.model.SimplePlanVersionInfo;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.model.AdvancedFlowStatus;

public class WorkflowApiImpl extends AbstractServiceClientImpl implements WokflowApi {

    private final Gson gson = new Gson();

    public WorkflowApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public WokflowServiceWithFlowDataAndStatus start(String processId, FlowData process) {
        checkNotBlank(processId);
        checkNotNull(process);
        logger.info("start process id = {}", processId);
        Map map = map()
                .with("_advance", true)
                .with(process.getAttributes());
        JsonElement response = post("processes/" + processId + "/instances", map).asJson();
        return parseFlowActionResponse(response);
    }

    @Override
    public WokflowServiceWithFlowDataAndStatus advance(String processId, String instanceId, String activityId, FlowData processInstance) {
        checkNotBlank(processId);
        checkNotBlank(instanceId);
        checkNotBlank(activityId);
        checkNotNull(processInstance);
        logger.info("advance process processId = {} instanceId = {} activityId = {}", processId, instanceId, activityId);
        Map map = map()
                .with("_activity", activityId)
                .with("_advance", true)
                .with(processInstance.getAttributes());
        JsonElement response = put("processes/" + processId + "/instances/" + instanceId, map).asJson();
        return parseFlowActionResponse(response);
    }

    private WokflowServiceWithProcessInfoImpl parseFlowActionResponse(JsonElement response) {
        JsonObject jsonData = response.getAsJsonObject().getAsJsonObject("data");
        Map<String, Object> data = gson.fromJson(jsonData, new TypeToken<Map<String, Object>>() {
        }.getType());
        List<TaskDetail> tasklist = stream(jsonData.get("_tasklist").getAsJsonArray()).map(JsonElement::getAsJsonObject).map(this::parseTaskDetail).collect(toList());
        FlowDataAndStatus flowDataAndStatus = SimpleFlowDataAndStatus.builder()
                .withFlowId(toString(jsonData.get("_flowId")))
                .withFlowStatus(AdvancedFlowStatus.valueOf(toString(jsonData.get("_flowStatus"))))
                .withFlowCardId(toString(jsonData.get("_id")))
                .withAttributes(data)
                .withTasklist(tasklist)
                .build();
        return new WokflowServiceWithProcessInfoImpl(flowDataAndStatus);
    }

    @Override
    public FlowData get(String planId, String walkId) {
        checkNotBlank(planId);
        checkNotBlank(walkId);
        logger.debug("get process detail for planId = {} walkId = {}", planId, walkId);
        JsonElement response = get("processes/" + planId + "/instances/" + walkId).asJson();
        JsonObject responseData = response.getAsJsonObject().getAsJsonObject("data");
        Map<String, Object> attributes = map();
        responseData.entrySet().stream().forEach((entry) -> {
            String key = entry.getKey();
            String value = toString(entry.getValue());
            switch (key) {
                case "_id":
                    checkArgument(equal(value, walkId));
            }
            attributes.put(key, value);
        });
        String status = (String) attributes.get("_status_description");
        return FlowDataImpl.builder().withInstanceId(walkId).withAttributes(attributes).withStatus(status).build();
    }

    @Override
    public List<TaskInfo> getTaskList(String processId, String instanceId) {
        checkNotBlank(processId);
        checkNotBlank(instanceId);
        logger.debug("get process instance activities for processId = {} instanceId = {}", processId, instanceId);
        JsonNode response = get("processes/" + processId + "/instances/" + instanceId + "/activities").asJackson();
        return Streams.stream(response.get("data")).map((record) -> fromJson(record, SimpleTaskInfo.class)).collect(toList());
    }

    @Override
    public TaskDetail getStartProcessTask(String processId) {
        checkNotBlank(processId);
        logger.debug("get start process task detail for processId = {}", processId);
        JsonElement response = get("processes/" + processId + "/start_activities/").asJson();
        JsonArray list = response.getAsJsonObject().getAsJsonArray("data");
        checkArgument(list.size() == 1);
        JsonObject data = list.get(0).getAsJsonObject();
        return parseTaskDetail(data);
    }

    @Override
    public TaskDetail getTask(String processId, String instanceId, String taskId) {
        checkNotBlank(processId);
        checkNotBlank(instanceId);
        checkNotBlank(taskId);
        logger.debug("get task detail for processId = {} instanceId = {} taskId = {}", processId, instanceId, taskId);
        JsonElement response = get("processes/" + processId + "/instances/" + instanceId + "/activities/" + taskId).asJson();
        JsonObject data = response.getAsJsonObject().getAsJsonObject("data");
        return parseTaskDetail(data);
    }

    private TaskDetail parseTaskDetail(JsonObject data) {
        String description = toString(data.get("description"));
        String taskId = toString(data.get("_id"));
        JsonArray attributes = data.getAsJsonArray("attributes");
        List<TaskParam> params = Streams.stream(attributes).map(JsonElement::getAsJsonObject).map((attr) -> {
            JsonElement detailElement = attr.get("detail");
            AttributeDetail attributeDetail = fromJson(detailElement.toString(), SimpleAttributeDetail.class);//TODO avoid tostring here
            return new SimpleTaskParam(toString(attr.get("_id")), toBoolean(attr.get("writable")), toBoolean(attr.get("mandatory")), toBoolean(attr.get("action")), attributeDetail);
        }).collect(toList());
        return new SimpleTaskDetail(params, taskId, description);
    }

    @Override
    public List<PlanInfo> getPlans() {
        logger.debug("get all plans");
        JsonElement response = get("processes").asJson();
        JsonArray plans = response.getAsJsonObject().get("data").getAsJsonArray();
        return Streams.stream(plans).map(JsonElement::getAsJsonObject).map(WorkflowApiImpl::readPlanInfo).collect(toList());
    }

    @Override
    public PlanInfo getPlan(String processId) {
        checkNotBlank(processId, "process id cannot be null");
        JsonElement response = get(format("processes/%s", encodeUrlPath(processId))).asJson();
        JsonObject plan = response.getAsJsonObject().getAsJsonObject("data");
        return readPlanInfo(plan);
    }

    private static PlanInfo readPlanInfo(JsonObject plan) {
        return new SimplePlanInfo(toString(plan.get("_id")), toString(plan.get("description")), toString(plan.get("engine")));
    }

    @Override
    public WokflowServiceWithPlanVersionInfo uploadPlanVersion(String processId, InputStream data) {
        return doUploadPlanVersion(processId, data, false);
    }

    @Override
    public WokflowServiceWithPlanVersionInfo replacePlanVersion(String processId, InputStream data) {
        return doUploadPlanVersion(processId, data, true);
    }

    @Override
    public String downloadPlanVersion(String classId, String planId) {
        checkNotBlank(classId, "class id cannot be null");
        checkNotBlank(planId, "plan id cannot be null");
        return new String(getBytes(format("processes/%s/versions/%s/file", encodeUrlPath(classId), encodeUrlPath(planId))), Charsets.UTF_8);
    }

    @Override
    public byte[] downloadFlowGraph(String classId, long cardId) {
        checkNotBlank(classId, "class id cannot be null");
        return getBytes(format("processes/%s/instances/%s/graph", encodeUrlPath(classId), cardId));
    }

    @Override
    public byte[] downloadSimplifiedFlowGraph(String classId, long cardId) {
        checkNotBlank(classId, "class id cannot be null");
        return getBytes(format("processes/%s/instances/%s/graph?simplified=true", encodeUrlPath(classId), cardId));
    }

    @Override
    public List<PlanVersionInfo> getPlanVersions(String processId) {
        checkNotBlank(processId, "process id cannot be null");
        JsonNode response = get(format("processes/%s/versions", encodeUrlPath(processId))).asJackson();
        return Streams.stream(response.get("data")).map((version) -> fromJson(version, SimplePlanVersionInfo.class)).collect(toList());
    }

    @Override
    public String getXpdlTemplate(String processId) {
        checkNotBlank(processId, "process id cannot be null");
        byte[] bytes = getBytes(format("processes/%s/template", encodeUrlPath(processId)));
        return checkNotBlank(new String(bytes, Charsets.UTF_8));
    }

    private WokflowServiceWithPlanVersionInfo doUploadPlanVersion(String processId, InputStream data, boolean replace) {
        checkNotBlank(processId, "process id cannot be null");
        checkNotNull(data, "data param cannot be null");
        HttpEntity multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", data, ContentType.APPLICATION_OCTET_STREAM, "file.xpdl")
                .build();
        String url = format("processes/%s/versions", encodeUrlPath(processId));
        if (replace) {
            url += "?replace=true";
        }
        JsonNode response = post(url, multipart).asJackson();
        PlanVersionInfo planInfo = fromJson(response.get("data"), SimplePlanVersionInfo.class);
        return new WokflowServiceWithPlanInfoImpl(planInfo);
    }

    private static class SimplePlanInfo implements PlanInfo {

        private final String id, description, provider;

        public SimplePlanInfo(String id, String description, @Nullable String provider) {
            this.id = checkNotNull(id);
            this.description = checkNotNull(description);
            this.provider = provider;
        }

        @Override
        @Nullable
        public String getProvider() {
            return provider;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "SimplePlanInfo{" + "id=" + id + ", description=" + description + '}';
        }

    }

    private static class SimpleTaskDetail extends SimpleTaskInfo implements TaskDetail {

        private final List<TaskParam> params;

        public SimpleTaskDetail(List<TaskParam> params, String activityId, String description) {
            super(activityId, description);
            this.params = checkNotNull(params);
        }

        @Override
        public List<TaskParam> getParams() {
            return params;
        }

    }

    private static class SimpleTaskParam implements TaskParam {

        private final String name;
        private final boolean writable, required, action;
        private final AttributeDetail detail;

        public SimpleTaskParam(String name, boolean writable, boolean required, boolean action, AttributeDetail detail) {
            this.name = checkNotNull(name);
            this.writable = writable;
            this.required = required;
            this.action = action;
            this.detail = checkNotNull(detail);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isWritable() {
            return writable;
        }

        @Override
        public boolean isAction() {
            return action;
        }

        @Override
        public boolean isRequired() {
            return required;
        }

        @Override
        public AttributeDetail getDetail() {
            return detail;
        }

    }

    @JsonDeserialize(builder = SimpleTaskInfoBuilder.class)
    private static class SimpleTaskInfo implements TaskInfo {

        private final String id, description;

        public SimpleTaskInfo(String activityId, String description) {
            this.id = checkNotBlank(activityId);
            this.description = nullToEmpty(description);
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getDescription() {
            return description;
        }

    }

    private static class SimpleTaskInfoBuilder implements Builder<SimpleTaskInfo, SimpleTaskInfoBuilder> {

        private String id, description;

        @JsonProperty("_id")
        public SimpleTaskInfoBuilder withId(String id) {
            this.id = id;
            return this;
        }

        @JsonProperty("description")
        public SimpleTaskInfoBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        @Override
        public SimpleTaskInfo build() {
            return new SimpleTaskInfo(id, description);
        }

    }

    private class WokflowServiceWithProcessInfoImpl implements WokflowServiceWithFlowDataAndStatus {

        private final FlowDataAndStatus processInfo;

        public WokflowServiceWithProcessInfoImpl(FlowDataAndStatus processInfo) {
            this.processInfo = checkNotNull(processInfo);
        }

        @Override
        public FlowDataAndStatus getFlowData() {
            return processInfo;
        }

        @Override
        public WokflowApi then() {
            return WorkflowApiImpl.this;
        }

    }

    private class WokflowServiceWithPlanInfoImpl implements WokflowServiceWithPlanVersionInfo {

        private final PlanVersionInfo planInfo;

        public WokflowServiceWithPlanInfoImpl(PlanVersionInfo planInfo) {
            this.planInfo = checkNotNull(planInfo);
        }

        @Override
        public WokflowApi then() {
            return WorkflowApiImpl.this;
        }

        @Override
        public PlanVersionInfo getPlanVersionInfo() {
            return planInfo;
        }

    }

}
