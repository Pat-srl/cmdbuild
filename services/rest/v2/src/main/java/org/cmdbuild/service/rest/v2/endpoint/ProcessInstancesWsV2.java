package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.joining;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.annotation.Nullable;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.common.beans.IdAndDescription;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;

import org.cmdbuild.service.rest.v4.endpoint.CardWs;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.service.rest.v2.serializationhelpers.FlowConverterServicev2;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.collectionType;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Task;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;

@Path("processes/{processId}/instances/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstancesWsV2 {

    private final WorkflowService workflowService;
    private final FlowConverterServicev2 converterService;
    private final CardFilterService filterService;

    public ProcessInstancesWsV2(WorkflowService workflowService, FlowConverterServicev2 converterService, CardFilterService filterService) {
        this.workflowService = checkNotNull(workflowService);
        this.converterService = checkNotNull(converterService);
        this.filterService = checkNotNull(filterService);
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("processId") String processId, WsFlowData processInstance) {
        Process processClass = workflowService.getProcess(processId);
        FlowAdvanceResponse response = workflowService.startProcess(
                processId,
                convertInputValuesForFirstStep(processClass, processInstance),
                processInstance.isAdvance());
        return map("data", response.getFlowCard().getCardId(), "meta", map());
    }

    @GET
    @Path("{processInstanceId}")
    public Object readOne(@PathParam(PROCESS_ID) String planClasseId, @PathParam(PROCESS_INSTANCE_ID) Long flowCardId) {
        Flow card = workflowService.getFlowCard(planClasseId, flowCardId);
        Map metaRef = map();
        for (Map.Entry<String, Object> attribute : card.getAttributeValues()) {
            if (card.get(attribute.getKey()) instanceof IdAndDescription idAndDesc) {
                metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
            }
        }
        return map("data", converterService.serializeFlow(card), "meta", map("total", null, "references", metaRef));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam(PROCESS_ID) String processId,
            @QueryParam(FILTER) String filter,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(POSITION_OF) Long instanceId) {

        Process found = workflowService.getProcess(processId);
        if (instanceId != null && limit == null) {
            limit = Integer.MAX_VALUE;
        }
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withFilter(CmFilterUtils.parseFilter(getFilterOrNull(filter)))
                .withSorter(CmSorterUtils.parseSorter(sort))
                .withPaging(offset, limit)
                .withPositionOf(instanceId, Boolean.TRUE)
                .build().mapAttrNames(map("_status", "FlowStatus"));
        PagedElements<? extends Flow> elements;

        elements = workflowService.getUserFlowCardsByClasseIdAndQueryOptions(found.getName(), queryOptions);
        Map metaRef = map();
        for (Card card : elements) {
            for (Map.Entry<String, Object> attribute : card.getAttributeValues()) {
                if (card.get(attribute.getKey()) instanceof IdAndDescription idAndDesc) {
                    metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
                }
            }
        }
        return map("data", elements.stream().map(converterService::serializeFlow).toList(), "meta", map("total", elements.totalSize(), "positions", handlePositionOfAndGetMeta(queryOptions, elements)));
    }

    @PUT
    @Path("{processInstanceId}")
    public Object update(@PathParam("processId") String processId, @PathParam("processInstanceId") Long instanceId, WsFlowData processInstance) {
        Flow flowCard = workflowService.getFlowCard(processId, instanceId);
        Task task = workflowService.getTask(flowCard, checkNotBlank(processInstance.getActivity(), "must set 'activity' param"));

        Map<String, Object> map = convertInputValuesForFlow(flowCard.getType(), processInstance);
        map = map(map).with(getWidgetValues(task, processInstance.widgets));

        FlowAdvanceResponse response = workflowService.updateProcess(processId,
                instanceId,
                task.getId(),
                map,
                processInstance.isAdvance());

        return map("data", toData(response));
    }

    @DELETE
    @Path("{processInstanceId}")
    public Object delete(@PathParam("processId") String processId, @PathParam("processInstanceId") Long instanceId) {
        return null;
    }

    public static Map<String, Object> handlePositionOfAndGetMeta(DaoQueryOptions queryOptions, PagedElements paged) {
        if (paged.hasPositionOf()) {
            PositionOf positionOf = paged.getPositionOf();
            Map positionMeta;
            if (positionOf.foundCard()) {
                positionMeta = map("hasPosition", true,
                        "position", positionOf.getPositionInTable());
            } else {
                positionMeta = map("found", false);
            }
            return map(queryOptions.getPositionOf(), positionMeta);
        } else {
            return emptyMap();
        }
    }

    private Object toData(FlowAdvanceResponse response) {
        List tasklist = response.getTasklist().stream().map(task -> converterService.taskToTaskResponseWithFullDetail(response.getFlowCard(), task)).toList();
        return converterService.serializeFlow(response.getFlowCard()).with("_flowStatus", response.getAdvancedFlowStatus().name(), "_flowId", response.getFlowId(), "_tasklist", tasklist);
    }

    private Map<String, Object> getWidgetValues(Task task, List<WsWidgetData> widgets) {
        Map<String, Object> widgetMap = map(widgets, WsWidgetData::getWidgetId, WsWidgetData::getOutput);
        Map<String, Object> res = map();

        task.getWidgets().forEach(w -> {

            Object value = widgetMap.get(w.getId());
            if ((value != null) && (w.getType().equals("customForm") && w.hasOutputKey())) {
                List<String> values = convert(value, LIST_OF_STRINGS.getType());
                Map<String, String> valueMap = map();
                values.forEach(v -> valueMap.putAll(fromJson(v, MAP_OF_STRINGS)));
                String stringValue = valueMap.entrySet().stream().map(e -> format("%s=%s", e.getKey(), e.getValue())).collect(joining(","));
                res.put(w.getOutputKey(), stringValue);
            }
        });
        return res;
    }

    @Nullable
    private String getFilterOrNull(@Nullable String filter) {
        return CardWs.getFilterOrNull(filter, id -> filterService.getById(id).getConfiguration());
    }

    private Map<String, Object> convertInputValuesForFlow(Process userProcessClass, WsFlowData processInstanceAdvanceable) {
        return convertValues(userProcessClass, firstNonNull(processInstanceAdvanceable.getValues(), emptyMap()));
    }

    private Map<String, Object> convertInputValuesForFirstStep(Process userProcessClass, WsFlowData processInstanceAdvanceable) {
        return convertValuesForFirstStep(userProcessClass, firstNonNull(processInstanceAdvanceable.getValues(), emptyMap()));
    }

    private Map<String, Object> convertValues(Process planClasse, Map<String, Object> values) {
        Map<String, Object> map = map();
        values.forEach((key, value) -> {
            if (planClasse.hasAttribute(key)) {
                value = rawToSystem(planClasse.getAttribute(key).getType(), value);
            }
            map.put(key, value);
        });
        return map;
    }

    private Map<String, Object> convertValuesForFirstStep(Process planClasse, Map<String, Object> values) {
        Map<String, Object> map = map();
        List<WidgetData> widgets = workflowService.getEntryTaskForCurrentUser(planClasse.getName()).getWidgets().stream().filter(w -> w.getOutputParameterOrNull() != null && !w.getType().equals("customForm")).toList();
        values.forEach((key, value) -> {
            if (planClasse.hasAttribute(key)) {
                value = rawToSystem(planClasse.getAttribute(key).getType(), value);
            }
            if (key.equals("_widgets")) {
                ((List<Map<String, Object>>) value).forEach(w -> {
                    Optional<WidgetData> optWidget = widgets.stream().filter(wi -> w.get("_id").equals(wi.getId())).collect(toOptional());
                    if (!optWidget.isEmpty()) {
                        List<Map<String, Object>> valuesList = list();
                        ((Map<Object, Object>) w.get("output")).forEach((k, v) -> valuesList.add(map("_id", k)));
                        map.put(optWidget.get().getOutputParameter().replace("\"", ""), valuesList);
                    }
                });
            }
            map.put(key, value);
        });
        return map;
    }

    public static class WsFlowData {

        private final Map<String, Object> values;
        private final List<WsWidgetData> widgets;
        private final boolean advance;
        private final String taskId;

        @JsonCreator
        public WsFlowData(Map<String, Object> values) {
            this.values = values;
            advance = toBooleanOrDefault(values.get("_advance"), false);
            taskId = emptyToNull(toStringOrNull(values.get("_activity")));
            widgets = applyOrDefault(values.get("_widgets"), w -> fromJson(toJson(w), collectionType(WsWidgetData.class)), emptyList());
        }

        public Map<String, Object> getValues() {
            return values;
        }

        public boolean isAdvance() {
            return advance;
        }

        public @Nullable
        String getActivity() {
            return taskId;
        }

    }

    public static class WsWidgetData {

        private final String widgetId;
        private final Object output;

        public WsWidgetData(@JsonProperty("_id") String widgetId, @JsonProperty("output") Object output) {
            this.widgetId = checkNotBlank(widgetId);
            this.output = output;
        }

        public String getWidgetId() {
            return widgetId;
        }

        public Object getOutput() {
            return output;
        }

    }
}
