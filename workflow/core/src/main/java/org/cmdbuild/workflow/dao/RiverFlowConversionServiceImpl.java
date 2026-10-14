/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Lists.transform;
import com.google.common.collect.Ordering;
import com.google.common.collect.Streams;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_DATA;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PLAN_INFO;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_DEFINITION_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_INSTANCE_ID;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import static org.cmdbuild.workflow.dao.FlowCardRepositoryImpl.buildFlowCardCode;
import static org.cmdbuild.workflow.dao.FlowCardRepositoryImpl.isSystemAttrAndShouldBeSkipped;
import static org.cmdbuild.workflow.dao.RiverPlanRepositoryImpl.ATTR_BIND_TO_CLASS;
import org.cmdbuild.workflow.inner.FlowConversionMode;
import org.cmdbuild.workflow.inner.ProcessRepository;
import org.cmdbuild.workflow.inner.TaskDefinitionConversionService;
import static org.cmdbuild.workflow.inner.TaskDefinitionConversionServiceImpl.getFirstNonAdminPerformerOrNull;
import static org.cmdbuild.workflow.inner.TaskDefinitionConversionServiceImpl.getTaskPerformerForUserOrNull;
import static org.cmdbuild.workflow.inner.WorkflowServiceImpl.BATCH_TASK_PERFORMER;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.FlowActivity;
import org.cmdbuild.workflow.model.FlowActivityImpl;
import org.cmdbuild.workflow.model.FlowImpl;
import org.cmdbuild.workflow.model.FlowStatus;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskPerformer;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_CARD_ID_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_CLASSNAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.PROCESS_INSTANCE_ID_VARIABLE;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverFlowStatus;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.core.RiverFlowImpl;
import org.cmdbuild.workflow.river.engine.data.RiverPlanRepository;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.RIVER_FLOW_STATUS_ATTR;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.serializeRiverFlowData;
import static org.cmdbuild.workflow.utils.RiverFlowUtils.getRiverFlowData;
import static org.cmdbuild.workflow.utils.WfRiverXpdlUtils.riverPlanIdToLegacyUniqueProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RiverFlowConversionServiceImpl implements RiverFlowConversionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OperationUserSupplier operationUser;
    private final WorkflowTypeConverter typeConverter;
    private final ProcessRepository classeRepository;
    private final RiverPlanRepository planRepository;
    private final TaskDefinitionConversionService taskDefinitionConversionService;

    public RiverFlowConversionServiceImpl(OperationUserSupplier operationUser, TaskDefinitionConversionService taskDefinitionConversionService, WorkflowTypeConverter typeConverter, ProcessRepository classeRepository, RiverPlanRepository planRepository) {
        this.typeConverter = checkNotNull(typeConverter);
        this.classeRepository = checkNotNull(classeRepository);
        this.planRepository = checkNotNull(planRepository);
        this.taskDefinitionConversionService = checkNotNull(taskDefinitionConversionService);
        this.operationUser = checkNotNull(operationUser);
    }

    @Override
    public RiverFlow cardToRiverFlow(Flow flowCard, FlowConversionMode mode) {
        RiverPlan riverPlan = planRepository.getPlanById(flowCard.getPlanId());
        return flowCardToRiverFlow(riverPlan, flowCard, mode);
    }

    @Override
    public Flow copyRiverFlowDataToNewCard(Process process, RiverFlow riverFlow) {
        return doCopyRiverFlowDataToCard(FlowImpl.builder().withCard(process, map(
                ATTR_TASK_INSTANCE_ID, emptyList(),
                ATTR_TASK_DEFINITION_ID, emptyList(),
                ATTR_NEXT_EXECUTOR, emptyList(),
                ATTR_PLAN_INFO, riverPlanIdToLegacyUniqueProcessDefinition(riverFlow.getPlanId()),
                ATTR_FLOW_ID, riverFlow.getId()
        )).withFlowStatus(riverStatusToFlowStatus(riverFlow.getStatus())).build(), riverFlow);
    }

    @Override
    public Flow copyRiverFlowDataToCard(Flow flowCard, RiverFlow riverFlow) {
        return doCopyRiverFlowDataToCard(flowCard, riverFlow);
    }

    private Flow doCopyRiverFlowDataToCard(Flow flowCard, RiverFlow riverFlow) {
        Process process = flowCard.getType();
        String serializedFlowData = riverFlowDataToFlowCardSerializedData(riverFlow);

        Set<String> newTaskIds = riverFlow.getTaskIds();
        Set<String> oldTaskIds = flowCard.getFlowActivities().stream().map(FlowActivity::getDefinitionId).collect(toSet());

        List<FlowActivity> flowActivities = listOf(FlowActivity.class).accept(l -> {
            flowCard.getFlowActivities().stream().filter((a) -> newTaskIds.contains(a.getDefinitionId())).map(t -> {
                String taskPerformer = getProcessedAssignedGroupForTask(riverFlow.getPlan().getTask(t.getDefinitionId()), riverFlow.getData());
                return new FlowActivityImpl(t.getInstanceId(), t.getDefinitionId(), taskPerformer, t.getDescription());
            }).forEach(l::add);
            newTaskIds.stream().filter(not(oldTaskIds::contains)).map((taskId) -> {
                String taskPerformer = getProcessedAssignedGroupForTask(riverFlow.getPlan().getTask(taskId), riverFlow.getData());
                return new FlowActivityImpl(randomId(), taskId, taskPerformer, taskId);
            }).forEach(l::add);
        });

//        Collection<String> previousExecutors = set(flowCard.getPreviousExecutors()).accept(l -> {
//            oldTaskIds.stream().filter(not(newTaskIds::contains)).map(flowCard::getFlowActivityByDefinitionId).map(FlowActivity::getPerformerGroup).forEach(l::add);
//        });
        logger.debug("convert river flow to card: old task ids = {}, new task ids = {}", oldTaskIds, newTaskIds);

        logger.debug("updating values from flow to card");
        Map<String, Object> attributesNotInCard = map(), systemAttributes = map(), attributesToUpdate = map();
        riverFlow.getData().entrySet().stream().sorted(Ordering.natural().onResultOf(Map.Entry::getKey)).forEach((entry) -> {
            String key = entry.getKey();
            Attribute attribute = process.getAttributeOrNull(key);
            if (attribute == null || attribute.isVirtual()) {
                attributesNotInCard.put(key, entry.getValue());
            } else if (isSystemAttrAndShouldBeSkipped(attribute)) {
                systemAttributes.put(key, entry.getValue());
            } else {
                attributesToUpdate.put(key, typeConverter.flowValueToCardValue(process, entry.getKey(), entry.getValue()));
            }
        });

        logger.trace("flow values that are system attributes in card, not updated:\n\n{}\n", mapToLoggableStringLazy(systemAttributes));
        logger.trace("flow values not present in card, not updated:\n\n{}\n", mapToLoggableStringLazy(attributesNotInCard));
        logger.trace("updating flow values in card:\n\n{}\n", mapToLoggableStringLazy(attributesToUpdate));

        return FlowImpl.copyOf(flowCard)
                .withCard(CardImpl.copyOf(flowCard).withAttributes(attributesToUpdate).withAttributes(map(ATTR_CODE, buildFlowCardCode(transform(flowActivities, FlowActivity::getDefinitionId), flowCard.getType()),//TODO check this 
                        ATTR_FLOW_DATA, serializedFlowData
                )).build())
                .withFlowStatus(riverStatusToFlowStatus(riverFlow.getStatus()))
                .withFlowActivities(flowActivities)
                //                .withPreviousExecutors(previousExecutors)
                .build();
    }

    private RiverFlow flowCardToRiverFlow(RiverPlan riverPlan, Flow flowCard, FlowConversionMode mode) {
        Map<String, Object> data = getRiverFlowData(riverPlan, flowCard, mode);

        Map<String, Object> dataFromCard = Streams.stream(flowCard.getRawValues()).filter((e) -> flowCard.getType().isUserAttribute(e.getKey())).collect(toMap(Map.Entry::getKey, (entry) -> {//note: we add values even if they're not in flow global variables
            String key = entry.getKey();
            Object value = entry.getValue();
            return typeConverter.cardValueToFlowValue(value, flowCard.getType().getAttribute(key));
        }));

        data = map(data).with(dataFromCard).with(
                PROCESS_CARD_ID_VARIABLE, flowCard.getCardId(),
                PROCESS_CLASSNAME_VARIABLE, flowCard.getType().getName(),
                PROCESS_INSTANCE_ID_VARIABLE, flowCard.getFlowId()
        );

        logger.trace("loaded flow with data = \n\n{}\n", mapToLoggableStringLazy(data));

        return RiverFlowImpl.builder()
                .withFlowId(flowCard.getFlowId())
                .withPlan(riverPlan)
                .withFlowStatus(convert(data.get(RIVER_FLOW_STATUS_ATTR), RiverFlowStatus.class))
                .withTasks(flowCard.getFlowActivities().stream().map(FlowActivity::getDefinitionId).map(riverPlan::getTask).collect(toImmutableList()))
                .withData(data)
                .build();
    }

    private String riverFlowDataToFlowCardSerializedData(RiverFlow flow) {
        org.cmdbuild.workflow.model.Process classe = classeRepository.getPlanClasseByClassAndPlanId(flow.getPlan().getAttr(ATTR_BIND_TO_CLASS), flow.getPlan().getId());
        return serializeRiverFlowData(flow.getData(), classe, flow.getPlan(), flow.getStatus());
    }

    private String getProcessedAssignedGroupForTask(RiverTask task, Map<String, Object> flowData) {
        if (task.isBatch()) {
            return BATCH_TASK_PERFORMER;
        } else {
            logger.debug("getProcessedAssignedGroupForTask task = {}", task);
            List<TaskPerformer> list = taskDefinitionConversionService.getProcessedTaskPerformersForTask(task, flowData);
            logger.debug("processing performers = {}", list);
            TaskPerformer taskPerformer = getTaskPerformerForUserOrNull(list, operationUser.getUser());//TODO use this for entry task, and getSingleNonAdminPerformerOrNull for other tasks
            if (taskPerformer == null || taskPerformer.getValue() == null) {
                taskPerformer = getFirstNonAdminPerformerOrNull(list);
            }
            logger.debug("selected task performer = {}", taskPerformer);
            return checkNotBlank(Optional.ofNullable(taskPerformer).map(TaskPerformer::getValue).orElse(null), "task performer value is blank for performer = %s task = %s", taskPerformer, task);
        }
    }

    private static FlowStatus riverStatusToFlowStatus(RiverFlowStatus status) {
        return switch (status) {
            case COMPLETE ->
                FlowStatus.COMPLETED;
//            case ERROR://TODO remove this ??
//                return FlowStatus.TERMINATED;//TODO remove this ??
            case READY, RUNNING ->
                FlowStatus.OPEN;
            case SUSPENDED ->
                FlowStatus.SUSPENDED;
            case ABORTED ->
                FlowStatus.ABORTED;
            default ->
                throw new IllegalStateException();
        };
    }
}
