/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WF_BASIC;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WF_LIFECYCLE;
import org.cmdbuild.auth.login.AuthenticationService;
import org.cmdbuild.auth.role.Role;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_READ;
import static org.cmdbuild.auth.role.RolePrivilege.RP_PROCESS_ALL_EXEC;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.auth.user.OperationUser.OPERATION_SCOPE;
import static org.cmdbuild.auth.user.OperationUser.OPERATION_SCOPE_SYSTEM;
import org.cmdbuild.auth.user.OperationUserImpl;
import org.cmdbuild.auth.user.OperationUserStore;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.classe.access.UserCardAccess;
import org.cmdbuild.classe.access.UserCardFileService;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.Constants.USER_CLASS_NAME;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.common.utils.PositionOfImpl;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NOTES;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PREV_EXECUTORS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_INSTANCE_ID;
import static org.cmdbuild.dao.core.q3.CompositeWhereOperator.OR;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import static org.cmdbuild.dao.core.q3.WhereOperator.INTERSECTS;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FILE;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.classNameOrVoid;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import static org.cmdbuild.workflow.FlowEvent.FlowEventType.FE_AFTER_ADVANCE;
import static org.cmdbuild.workflow.FlowEvent.FlowEventType.FE_BEFORE_ADVANCE;
import org.cmdbuild.workflow.FlowUpdatedEvent;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.WorkflowService;
import static org.cmdbuild.workflow.WorkflowService.WorkflowVariableProcessingStrategy.SET_ALL_CLASS_VARIABLES;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import org.cmdbuild.workflow.dao.CardToFlowCardWrapperService;
import org.cmdbuild.workflow.dao.ExtendedRiverPlanRepository;
import org.cmdbuild.workflow.dao.FlowPersistenceService;
import org.cmdbuild.workflow.dao.RiverFlowConversionService;
import static org.cmdbuild.workflow.dao.RiverPlanRepositoryImpl.ATTR_BIND_TO_CLASS;
import org.cmdbuild.workflow.dao.RiverPlanVersionInfo;
import static org.cmdbuild.workflow.inner.FlowConversionMode.CM_FULL;
import static org.cmdbuild.workflow.inner.FlowConversionMode.CM_LEAN;
import org.cmdbuild.workflow.jobs.WorkflowRiverBatchExecutorService;
import org.cmdbuild.workflow.model.AdvancedFlowStatus;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.FlowImpl;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.SimpleFlowAdvanceResponse;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.TaskAttribute;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.WfReferenceImpl;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_GROUP_NAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_PERFORMER_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_USER_USERNAME_VARIABLE;
import static org.cmdbuild.workflow.model.WorkflowConstants.CURRENT_USER_VARIABLE;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.model.XpdlInfo;
import org.cmdbuild.workflow.model.XpdlInfoImpl;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverFlowService;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import org.cmdbuild.workflow.river.engine.core.CompletedTaskImpl;
import org.cmdbuild.workflow.river.engine.core.RiverFlowImpl;
import org.cmdbuild.workflow.river.engine.core.RiverPlanImpl;
import org.cmdbuild.workflow.river.engine.core.Step;
import org.cmdbuild.workflow.river.engine.lock.AquiredLock;
import org.cmdbuild.workflow.river.engine.lock.RiverLockService;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.buildTaskDefinitionForClosedTask;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.buildTaskForClosedFlowAddWidgets;
import static org.cmdbuild.workflow.utils.RiverFlowUtils.getRiverFlowStatus;
import static org.cmdbuild.workflow.utils.WfRiverXpdlUtils.checkPlanUpgradeIsSafe;
import static org.cmdbuild.workflow.utils.WfRiverXpdlUtils.parseXpdlForCmdb;
import org.cmdbuild.workflow.utils.WorkflowUtils;
import static org.cmdbuild.workflow.utils.XpdlUtils.xpdlToDatasource;
import org.cmdbuild.workflow.xpdl.XpdlTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class WorkflowServiceImpl implements WorkflowService, WorkflowRiverBatchExecutorService, MinionComponent {

    public static final String UNNEST_EMPTY_VAL = "__empty__", BATCH_TASK_PERFORMER = "BATCH";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WorkflowConfiguration configuration;
    private final FlowCardRepository cardRepository;
    private final ProcessRepository classeRepository;
    private final XpdlTemplateService templateService;
    private final EventBusService eventService;
    private final OperationUserSupplier userStore;
    private final DaoService dao;
    private final WorkflowConfiguration workflowConfiguration;
    private final ExtendedRiverPlanRepository planRepository;
    private final RiverFlowService flowService;
    private final RiverFlowConversionService flowRepository;
    private final RiverLockService lockService;
    private final TaskConversionService taskConversionService;
    private final TaskDefinitionConversionService taskDefinitionConversionService;
    private final FlowPersistenceService persistenceService;
    private final WorkflowTypeConverter typeConverter;
    private final AuthenticationService authenticationService;
    private final OperationUserStore user;
    private final WidgetService widgetService;
    private final CardToFlowCardWrapperService wrapperService;
    private final UserCardService cardService;
    private final UserClassService userClassService;
    private final UserCardFileService userCardFileService;

    private final MinionHandler minionHandler;

    public WorkflowServiceImpl(
            WorkflowConfiguration configuration,
            FlowCardRepository cardRepository,
            ProcessRepository classeRepository,
            @Lazy XpdlTemplateService templateService,
            EventBusService eventService,
            OperationUserSupplier userStore,
            DaoService dao,
            WorkflowConfiguration workflowConfiguration,
            ExtendedRiverPlanRepository planRepository,
            RiverFlowService flowService,
            RiverFlowConversionService flowRepository,
            RiverLockService lockService,
            TaskConversionService taskConversionService,
            TaskDefinitionConversionService taskDefinitionConversionService,
            FlowPersistenceService persistenceService,
            WorkflowTypeConverter typeConverter,
            AuthenticationService authenticationService,
            OperationUserStore user,
            WidgetService widgetService,
            CardToFlowCardWrapperService wrapperService,
            UserCardService cardService,
            UserClassService userClassService,
            UserCardFileService userCardFileService) {
        this.configuration = checkNotNull(configuration);
        this.cardRepository = checkNotNull(cardRepository);
        this.classeRepository = checkNotNull(classeRepository);
        this.templateService = checkNotNull(templateService);
        this.eventService = checkNotNull(eventService);
        this.userStore = checkNotNull(userStore);
        this.dao = checkNotNull(dao);
        this.workflowConfiguration = checkNotNull(workflowConfiguration);
        this.planRepository = checkNotNull(planRepository);
        this.flowService = checkNotNull(flowService);
        this.flowRepository = checkNotNull(flowRepository);
        this.lockService = checkNotNull(lockService);
        this.taskConversionService = checkNotNull(taskConversionService);
        this.taskDefinitionConversionService = checkNotNull(taskDefinitionConversionService);
        this.persistenceService = checkNotNull(persistenceService);
        this.typeConverter = checkNotNull(typeConverter);
        this.authenticationService = checkNotNull(authenticationService);
        this.user = checkNotNull(user);
        this.widgetService = checkNotNull(widgetService);
        this.wrapperService = checkNotNull(wrapperService);
        this.cardService = checkNotNull(cardService);
        this.userClassService = checkNotNull(userClassService);
        this.userCardFileService = checkNotNull(userCardFileService);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Workflow Engine")
                .withConfigEnabler("org.cmdbuild.workflow.enabled")
                .withEnabledChecker(configuration::isEnabled)
                .withStatusChecker(() -> configuration.isEnabled() ? MRS_READY : MRS_NOTRUNNING)
                .reloadOnConfigs(WorkflowConfiguration.class)
                .build();
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public boolean isWorkflowEnabled() {
        return configuration.isEnabled();
    }

    @Override
    public TaskDefinition getEntryTaskForCurrentUser(String processId) {
        Process process = getProcess(processId);
        return WorkflowUtils.getEntryTaskForCurrentUser(process, userStore.getUser());
    }

    @Override
    public boolean hasEntryTaskForCurrentUser(String processId) {
        Process process = getProcess(processId);
        return WorkflowUtils.getEntryTaskForCurrentUserOrNull(process, userStore.getUser()) != null;
    }

    @Override
    public Process getProcess(String processClasseName) {
        return classeRepository.getProcessClassByName(processClasseName);
    }

    @Override
    public Collection<Process> getActiveProcessClasses() {
        return classeRepository.getAllPlanClassesForCurrentUser().stream().filter(p -> p.isActive() && p.isSuperclass() ? true : p.hasPlan()).collect(toList());//TODO check isActive filter
    }

    @Override
    public Collection<Process> getAllProcessClasses() {
        return classeRepository.getAllPlanClassesForCurrentUser();
    }

    @Override
    public Flow getFlowCardOrNull(Process classe, Long cardId) {
        return cardRepository.getFlowCardByPlanAndCardId(classe, cardId);
    }

    @Override
    public PagedElements<Flow> getUserFlowCardsByClasseIdAndQueryOptions(String className, DaoQueryOptions queryOptions) {
        return cardRepository.getUserCardsByClassIdAndQueryOptions(className, queryOptions);
    }

    @Override
    public Flow getUserFlowCard(String classId, Long cardId) {
        return cardRepository.getUserFlowCard(classId, cardId);
    }

    @Override
    public PagedElements<Flow> getFlowCardsByClasseAndQueryOptions(Classe processClass, DaoQueryOptions queryOptions) {
        return getUserFlowCardsByClasseIdAndQueryOptions(processClass.getName(), queryOptions);
    }

    @Override
    public boolean isWorkflowEnabledAndProcessRunnable(String className) {
        return configuration.isEnabled() && getProcess(className).isRunnable();
    }

    @Override
    public FlowAdvanceResponse startProcess(String processClassName, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        Process classe = classeRepository.getProcessClassByName(processClassName);
        FlowAdvanceResponse response = startProcess(classe, vars, variableProcessingStrategy, advance);
        eventService.getWorkflowEventBus().post(new FlowUpdatedEventImpl(response, advance));
        if (!cardRepository.userCanReadCard(processClassName, response.getCardId())) {
            response = SimpleFlowAdvanceResponse.copyOf(response).withTasklist(emptyList()).build();
        }
        return response;
    }

    @Override
    public FlowAdvanceResponse updateProcess(String planClasseId, Long flowCardId, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        Flow card = cardRepository.getFlowCardByClasseIdAndCardId(planClasseId, flowCardId);
        FlowAdvanceResponse response = updateProcess(card, taskId, vars, variableProcessingStrategy, advance);
        eventService.getWorkflowEventBus().post(new FlowUpdatedEventImpl(response, advance));
        if (!cardRepository.userCanReadCard(planClasseId, card.getId())) {
            response = SimpleFlowAdvanceResponse.copyOf(response).withTasklist(emptyList()).build();
        }
        return response;
    }

    @Override
    public FlowAdvanceResponse updateProcessWithOnlyTask(String classId, Long cardId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        Flow card = cardRepository.getFlowCardByClasseIdAndCardId(classId, cardId);
        Task task = getOnlyElement(getTaskList(card));
        FlowAdvanceResponse response = updateProcess(card, task.getId(), vars, variableProcessingStrategy, advance);
        eventService.getWorkflowEventBus().post(new FlowUpdatedEventImpl(response, advance));
        return response;
    }

    @Override
    public void suspendProcess(String classId, Long cardId) {
        suspendProcessInstance(getFlowCard(classId, cardId));
    }

    @Override
    public void resumeProcess(String classId, Long cardId) {
        resumeProcessInstance(getFlowCard(classId, cardId));
    }

    @Override
    public void abortProcess(String classId, Long cardId) {
        abortProcessInstance(getFlowCard(classId, cardId));
    }

    @Override
    public void abortProcessFromUser(String classId, Long cardId) {
        Flow flowCard = getFlowCard(classId, cardId);
        checkUserCanAbort(flowCard);
        abortProcessInstance(flowCard);
    }

    @Override
    public void suspendProcessFromUser(String classId, Long cardId) {
        Flow flowCard = getFlowCard(classId, cardId);
        checkUserCanSuspendResume(flowCard);
        suspendProcessInstance(flowCard);
    }

    @Override
    public void resumeProcessFromUser(String classId, Long cardId) {
        Flow flowCard = getFlowCard(classId, cardId);
        checkUserCanSuspendResume(flowCard);
        resumeProcessInstance(flowCard);
    }

    @Override
    public DataSource getXpdlTemplate(String planClasseId) {
        Process classe = classeRepository.getProcessClassByName(planClasseId);
        return templateService.getTemplate(classe);
    }

    @Override
    public DataSource getXpdlByClasseIdAndPlanId(String classId, String planId) {
        Process classe = classeRepository.getPlanClasseByClassAndPlanId(classId, planId);
        checkArgument(equal(classId, classe.getName()), "planId = %s does not bind to class = %s", planId, classId);
        return getXpdlForClasse(classe);
    }

    @Override
    public List<WidgetData> getWidgetsForUserTask(String classeId, Long cardId, String taskId) {
        Flow flowCard = getFlowCard(classeId, cardId);
        TaskDefinition taskDefinition = getTaskDefinition(flowCard, taskId);
        Map<String, Object> flowData = getFlowData(flowCard);
//		return widgetService.createWidgets(taskDefinition.getWidgets(), flowData); TODO
        return emptyList();
    }

    @Override
    public void abortProcessInstance(Flow flow) {
        RiverFlow riverFlow = flowRepository.cardToRiverFlow(flow);
        try (AquiredLock lock = lockService.aquireLock(riverFlow).aquired()) {
            riverFlow = flowService.terminateFlow(riverFlow);
            persistenceService.updateFlowCard(flow, riverFlow);
        }
    }

    @Override
    public void suspendProcessInstance(Flow flow) {
        RiverFlow riverFlow = flowRepository.cardToRiverFlow(flow);
        try (AquiredLock lock = lockService.aquireLock(riverFlow).aquired()) {
            riverFlow = flowService.suspendFlow(riverFlow);
            persistenceService.updateFlowCard(flow, riverFlow);
        }
    }

    @Override
    public void resumeProcessInstance(Flow flow) {
        RiverFlow riverFlow = flowRepository.cardToRiverFlow(flow);
        try (AquiredLock lock = lockService.aquireLock(riverFlow).aquired()) {
            riverFlow = flowService.resumeFlow(riverFlow);
            persistenceService.updateFlowCard(flow, riverFlow);
        }
    }

    @Override
    public List<Task> getTaskListForCurrentUserByClassIdAndCardId(String classId, Long cardId) {
        logger.debug("getTaskListForCurrentUser with classId = {} cardId = {}", classId, cardId);
        Flow card = cardRepository.getFlowCardByClasseIdAndCardId(classId, cardId);
        return taskConversionService.getTaskList(card);//TODO filter for user
    }

    @Override
    public PagedElements<Task> getTaskListForCurrentUserByClassIdSkipFlowData(String classId, DaoQueryOptions queryOptions) {
        UserCardAccess cardAccess = cardService.getUserCardAccess(classId);
        queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).and(cardAccess.getWholeClassFilter()).build();
        logger.debug("getTaskListForCurrentUserByClassIdSkipFlowData with classId = {} and groups = {}", classId, user.getActiveGroupNames());
        PositionOf positionOf = null;
        Classe classe = dao.getClasse(classId);
        if (queryOptions.hasPositionOf()) {
            long offset = firstNonNull(queryOptions.getOffset(), 0l);
            Long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, queryOptions.getPositionOf()).then()
                    .selectExpr("_unnested_taskid", buildUnnestQuery(ATTR_TASK_INSTANCE_ID))
                    .selectExpr("_unnested_groupname", buildUnnestQuery(ATTR_NEXT_EXECUTOR))
                    .from(classId)
                    .orderBy(queryOptions.getSorter())
                    .where(queryOptions.getFilter())
                    .accept(addUserPrevExFilter(classe))
                    .build().getFirstRowNumberOrNull();
            if (rowNumber == null) {
                positionOf = PositionOfImpl.builder()
                        .withFoundCard(false)
                        .withActualOffset(offset)
                        .build();
            } else {
                long positionInPage = rowNumber % queryOptions.getLimit();
                long pageOffset = rowNumber - positionInPage;
                if (queryOptions.getGoToPage()) {
                    offset = pageOffset;
                }
                positionOf = PositionOfImpl.builder()
                        .withFoundCard(true)
                        .withPositionInPage(positionInPage)
                        .withPositionInTable(rowNumber)
                        .withPageOffset(pageOffset)
                        .withActualOffset(offset)
                        .build();
                queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).withOffset(offset).build();
            }
        }
        QueryBuilder query = dao.selectAll()
                .selectExpr("_unnested_taskid", buildUnnestQuery(ATTR_TASK_INSTANCE_ID))
                .selectExpr("_unnested_groupname", buildUnnestQuery(ATTR_NEXT_EXECUTOR))
                .from(classId)
                .accept(addUserPrevExFilter(classe))
                .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor()::accept)
                .withOptions(queryOptions);
        List<Task> tasks = query.run().stream().map((r) -> {
            Card card = r.toCard();
            card = cardAccess.addCardAccessPermissionsFromSubfilterMark(card);
            Flow flowCard = wrapperService.cardToFlowCard(card);
            String taskId = checkNotBlank(toStringOrNull(r.asMap().get("_unnested_taskid")));
            if (equal(taskId, UNNEST_EMPTY_VAL)) {
                return buildTaskForClosedFlowAddWidgets(flowCard, widgetService);
            } else {
                return taskConversionService.getTask(flowCard, taskId, CM_LEAN);
            }
        }).collect(toList());
        long total;
        if (queryOptions.isPaged()) {
            total = dao.select(ATTR_ID)
                    .selectExpr("_unnested_taskid", buildUnnestQuery(ATTR_TASK_INSTANCE_ID))
                    .selectExpr("_unnested_groupname", buildUnnestQuery(ATTR_NEXT_EXECUTOR))
                    .select(ATTR_PREV_EXECUTORS)
                    .selectCount()
                    .from(classId)
                    .where(query)
                    .getCount();
            return new PagedElements<>(tasks, total, positionOf);
        } else {
            return new PagedElements<>(tasks);
        }
    }

    private Consumer<QueryBuilder> addUserPrevExFilter(Classe classe) {
        return (q) -> {
            if (!user.hasPrivileges(p -> p.hasPrivileges(RP_DATA_ALL_READ) || p.hasWriteAccess(classe))) {
                q.where(OR, (b) -> b.where("_unnested_groupname", IN, user.getActiveGroupNames()).where(ATTR_PREV_EXECUTORS, INTERSECTS, user.getActiveGroupNames()));//TODO handle dummy task for closed processes; improve query, remove literals (??)
            }
        };
    }

    private static String buildUnnestQuery(String attr) {
        attr = quoteSqlIdentifier(attr);
        return format("unnest(CASE cardinality(%s) WHEN 0 THEN ARRAY['%s'::varchar] ELSE %s END)", attr, UNNEST_EMPTY_VAL, attr);
    }

    @Override
    public Task getUserTask(Flow card, String userTaskId) {
        return taskConversionService.getTask(card, userTaskId);
    }

    @Override
    public FlowAdvanceResponse startProcess(Process process, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        return new StartProcessOperation(process, (Map) vars, variableProcessingStrategy, advance).startProcess();
    }

    @Override
    public FlowAdvanceResponse updateProcess(Flow flowCard, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
        return new UpdateProcessOperation(flowCard, taskId, (Map) vars, variableProcessingStrategy, advance).updateProcess();
    }

    @Override
    public List<XpdlInfo> getXpdlInfosOrderByVersionDesc(String classId) {
        List<RiverPlanVersionInfo> list = planRepository.getPlanVersionsByClassIdOrderByCreationDesc(classId);
        List<XpdlInfo> res = list();
        for (int i = 0; i < list.size(); i++) {
            RiverPlanVersionInfo riverPlan = list.get(i);
            int ver = list.size() - i;
            XpdlInfo xpdlInfo = XpdlInfoImpl.builder()
                    .withDefault(i == 0)
                    .withLastUpdate(riverPlan.getLastUpdate())
                    .withVersion(Integer.toString(ver))
                    .withProvider(RIVER)
                    .withPlanId(riverPlan.getPlanId())
                    .build();
            res.add(xpdlInfo);
        }
        return res;
    }

    @Override
    public DataSource getXpdlForClasse(Process classe) {
        RiverPlan plan = planRepository.getPlanById(classe.getPlanId());
        String xpdl = plan.toXpdl();
        return xpdlToDatasource(xpdl, format("%s_%s", classe.getName(), plan.getId()));
    }

    @Override
    public XpdlInfo addXpdl(String classId, DataSource dataSource) {
        return doAddXpdl(classId, dataSource, false);
    }

    @Override
    public XpdlInfo addXpdlReplaceCurrent(String classId, DataSource dataSource) {
        return doAddXpdl(classId, dataSource, true);
    }

    private XpdlInfo doAddXpdl(String classId, DataSource dataSource, boolean replaceCurrent) {
        String xpdlContent = readToString(dataSource);
        RiverPlan riverPlan = parseXpdlForCmdb(xpdlContent);
        String xpdlClassId = riverPlan.getAttr(ATTR_BIND_TO_CLASS);
        checkArgument(equal(xpdlClassId, classId), "xpdl binding mismatch, expected %s =< %s > but found %s", ATTR_BIND_TO_CLASS, classId, xpdlClassId);
        if (replaceCurrent) {
            RiverPlan currentPlan = planRepository.getPlanByClasseId(classId);
            checkPlanUpgradeIsSafe(currentPlan, riverPlan);
            riverPlan = planRepository.updatePlan(RiverPlanImpl.copyOf(riverPlan).withPlanId(currentPlan.getId()).build());
        } else {
            riverPlan = planRepository.createPlan(riverPlan);
        }
        String planId = riverPlan.getId();
        XpdlInfo xpdlInfo = getXpdlInfosOrderByVersionDesc(xpdlClassId).stream().filter((i) -> equal(i.getPlanId(), planId)).findFirst().get();
        logger.info("uploaded new xpdl version = {}", xpdlInfo);
        userClassService.invalidateCache();
        return xpdlInfo;
    }

    @Override
    public TaskDefinition getTaskDefinition(Flow flowCard, String taskId) {
        RiverPlan plan = planRepository.getPlanById(flowCard.getPlanId());
        RiverTask task = plan.getTask(taskId);
        return taskDefinitionConversionService.toTaskDefinition(task);
    }

    @Override
    public Map<String, Object> getFlowData(Flow flowCard) {
        RiverFlow flow = flowRepository.cardToRiverFlow(flowCard);
        return flow.getData();//TODO ensure that flow.getData contains all flow data
    }

    @Override
    public Task getTask(Flow flowCard, String taskId) {
        return getUserTask(flowCard, taskId);
    }

    @Override
    public List<Task> getTaskList(Flow flowCard) {
        return taskConversionService.getTaskList(flowCard);
    }

    @Override
    public List<Task> getTaskListLean(Flow flowCard) {
        return taskConversionService.getTaskListLean(flowCard);
    }

    @Override
    public Map<String, Object> getWidgetData(Collection<Task> taskList, Flow flow) {
        return extractWidgetData(flowRepository.cardToRiverFlow(flow), taskList);
    }

    @Override
    public Map<String, Object> getAllFlowData(String classId, long cardId) {
        Flow card = cardRepository.getFlowCardByClasseIdAndCardId(classId, cardId);
        return flowRepository.cardToRiverFlow(card, CM_FULL).getData();
    }

    private RiverFlow addCurrentPerformerDataInFlow(RiverFlow riverFlow, Task userTask) {
        OperationUser operationUser = user.getUser();//TODO the following lines are duplicated in shark impl; refactor and remove duplicate code
        Role performer = getTaskPerformer(userTask);
        ReferenceType groupAsReferenceType = typeConverter.rawValueToFlowValue(WorkflowUtils.workflowReferenceFromCmGroup(performer), ReferenceType.class);

        Map<String, Object> map = map(
                CURRENT_USER_USERNAME_VARIABLE, operationUser.getUsername(),
                CURRENT_GROUP_NAME_VARIABLE, performer.getName(),
                CURRENT_USER_VARIABLE, toReference(operationUser),
                CURRENT_PERFORMER_VARIABLE, groupAsReferenceType);
        logger.trace("add current performer data to flow, from current op user; data = \n\n{}\n", mapToLoggableStringLazy(map));
        return RiverFlowImpl.copyOf(riverFlow)
                .withData(map(riverFlow.getData()).with(map))
                .build();
    }

    private Role getTaskPerformer(Task userTask) {
        String taskPerformer = userTask.getPerformerName();
        return checkNotNull(authenticationService.getGroupWithNameOrNull(taskPerformer), "group not found for name =< %s > (task performer for task = %s)", taskPerformer, userTask);
    }

    @Nullable
    private ReferenceType toReference(OperationUser operationUser) {
        return typeConverter.rawValueToFlowValue(operationUser.getLoginUser().getId() == null ? null : new WfReferenceImpl(operationUser.getLoginUser().getIdNotNull(), USER_CLASS_NAME), ReferenceType.class);
    }

    @Override
    public List<TaskDefinition> getTaskDefinitions(String processId) {
        RiverPlan plan = planRepository.getPlanByClassIdOrNull(processId);
        if (plan == null) {
            logger.warn(marker(), "plan not found for process id = {}", processId);
            return emptyList();
        } else {
            List<TaskDefinition> taskDefinitions = plan.getSteps().stream().map(Step::getTask).filter(RiverTask::isUser).map(taskDefinitionConversionService::toTaskDefinition).sorted(Ordering.natural().onResultOf(TaskDefinition::getId)).collect(toList());
            taskDefinitions.add(buildTaskDefinitionForClosedTask());
            return taskDefinitions;
        }
    }

    @Override
    public void executeBatchTasks(Flow flow) {
        new BatchProcessOperation(flow).executeBatchTasks();
    }

    private class BatchProcessOperation extends ProcessOperation {

        public BatchProcessOperation(Flow flow) {
            super(flow.getType(), emptyMap(), SET_ALL_CLASS_VARIABLES, true);
            this.flow = checkNotNull(flow);
        }

        public void executeBatchTasks() {
            logger.debug("execute batch tasks for flow = {}", flow);
            riverFlow = flowRepository.cardToRiverFlow(flow);
            try (AquiredLock lock = lockService.aquireLockForBatchTask(riverFlow).aquired()) {//TODO wait?
                eventService.getWorkflowEventBus().post(new RiverFlowEventImpl(flow, FE_BEFORE_ADVANCE, riverFlow));
                riverFlow = flowService.executeBatchTasks(riverFlow);
                updateFlowCard(true);
                eventService.getWorkflowEventBus().post(new RiverFlowEventImpl(flow, FE_AFTER_ADVANCE, riverFlow));
            }
        }
    }

    private class StartProcessOperation extends ProcessOperation {

        private final RiverPlan riverPlan;
        private TaskDefinition entryTask;
        private String entryTaskId;

        public StartProcessOperation(Process process, Map<String, Object> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
            super(process, vars, variableProcessingStrategy, advance);
            riverPlan = planRepository.getPlanById(process.getPlanId());
        }

        public FlowAdvanceResponse startProcess() {
            logger.info("start process for classe = {}", process);
            prepareEntryTask();
            createFlow();
            try (AquiredLock lock = lockService.aquireLock(riverFlow).aquired()) {
                createCardAndUpdateFlowWithCardStuff();
                if (advance) {
                    advanceProcess();
                }
                return buildResponse();
            }
        }

        private void prepareEntryTask() {
            entryTask = WorkflowUtils.getEntryTaskForCurrentUser(process, user.getUser());
            entryTaskId = riverPlan.getEntryPointIdByTaskId(entryTask.getId());
        }

        private void createFlow() {
            riverFlow = flowService.createFlow(riverPlan);
            riverFlow = setInitialDataInFlow(riverFlow, process);
        }

        private void createCardAndUpdateFlowWithCardStuff() {
            TaskDefinition taskDefinition = WorkflowUtils.getEntryTaskForCurrentUser(process, user.getUser());

            Map<String, Object> fileAttrs = map(vars).filterKeys(k -> process.hasAttribute(k) && process.getAttribute(k).isOfType(FILE));
            fileAttrs.keySet().forEach(vars::remove);

            addTaskDataFromFormInFlow(vars, taskDefinition, widgetService.widgetDataToWidget(process.getName(), taskDefinition.getId(), taskDefinition.getWidgets(), vars));

            createFlowCard();

            if (!fileAttrs.isEmpty()) {
                vars.putAll(userCardFileService.prepareFileAttributes(process, map(vars).with(fileAttrs), null, flow.getId()));
                addTaskDataFromFormInFlow(vars, taskDefinition, widgetService.widgetDataToWidget(process.getName(), taskDefinition.getId(), taskDefinition.getWidgets(), vars));
                updateFlowCard(false);
            }

            riverFlow = flowService.startFlow(riverFlow, entryTaskId);//note: we create flow card and then start flow to allow triggers on card creation, before flow start
            updateFlowCard(false);

            List<Task> taskList = taskConversionService.getTaskList(flow);
            checkArgument(taskList.size() == 1, "we expected exactly one task for flow = %s at this time, found = %s", riverFlow.getId(), taskList);
            task = getOnlyElement(taskList);

            addTaskDataFromFormInFlow(vars, task.getDefinition(), task.getWidgets());
            task = getOnlyElement(taskConversionService.getTaskList(flow));//TODO avoid this refresh, retrieve only task definition before

            Map<String, Object> varsAndWidgetData = saveWidgets(task, riverFlow.getData());//TODO get data from flow, filtered for form; is this correct?
            addDataFromFormInFlow(varsAndWidgetData);
            updateFlowCard(true);
        }

        private void createFlowCard() {
            checkArgument(riverFlow != null && flow == null);
            setSystemScope();
            try {
                flow = persistenceService.createFlowCard(riverFlow);
                riverFlow = flowRepository.cardToRiverFlow(flow);
            } finally {
                unsetSystemScope();
            }
        }

        private RiverFlow setInitialDataInFlow(RiverFlow riverFlow, org.cmdbuild.workflow.model.Process classe) {
            logger.debug("set initial data in flow = {}", riverFlow);
            CmMapUtils.FluentMap<String, Object> data = map();
            RiverPlan plan = riverFlow.getPlan();

            plan.getGlobalVariables().forEach((String key, RiverVariableInfo var) -> {
                Object value = var.getDefaultValue().orElse(typeConverter.defaultValueForFlowInitialization(var.getJavaType()));
                value = typeConverter.rawValueToFlowValue(value, var.getJavaType());
                data.put(key, value);
            });

            if (logger.isTraceEnabled()) {
                logger.trace("initial flow data = \n\n{}\n", mapToLoggableString(data));
            }

            return RiverFlowImpl.copyOf(riverFlow)
                    .withData(data)
                    .build();
        }

    }

    private class UpdateProcessOperation extends ProcessOperation {

        private final String taskId, flowId;

        public UpdateProcessOperation(Flow flowCard, String taskId, Map<String, Object> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
            super(flowCard.getType(), vars, variableProcessingStrategy, advance);
            this.flow = checkNotNull(flowCard);
            this.taskId = checkNotBlank(taskId);
            flowId = flowCard.getFlowId();
        }

        public FlowAdvanceResponse updateProcess() {
            logger.info("update process for classeId = {} cardId = {}", flow.getType().getName(), flow.getCardId());
            try (AquiredLock lock = lockService.aquireLock(flowId).aquired()) {
                doUpdate();
                if (advance) {
                    advanceProcess();
                }
                return buildResponse();
            }
        }

        private void doUpdate() {//TODO fix this method
            vars.putAll(userCardFileService.prepareFileAttributes(process, vars, flow, flow.getId()));
            riverFlow = flowRepository.cardToRiverFlow(flow);
            task = taskConversionService.getTask(flow, taskId);
            addTaskDataFromFormInFlow(vars, task.getDefinition(), task.getWidgets());
            //TODO update flow card with river flow data
            task = taskConversionService.getTask(flow, taskId);//TODO avoid this refresh, retrieve only task definition before
            Map<String, Object> varsAndWidgetData = saveWidgets(task, riverFlow.getData());//TODO get data from flow, filtered for form; is this correct?
            addDataFromFormInFlow(varsAndWidgetData);
            updateFlowCard(true);
        }

    }

    private Map<String, Object> extractWidgetData(RiverFlow riverFlow, Collection<Task> taskList) {
        return taskList.stream().flatMap(t -> t.getWidgets().stream()).filter(w -> w.hasOutputKey() && !w.hasNoSelect()).map(Widget::getOutputKey).distinct().collect(toMap(identity(), riverFlow.getData()::get));
    }

    private abstract class ProcessOperation {

        protected final Process process;
        protected RiverFlow riverFlow;
        protected Flow flow;
        protected Task task;
        protected final Map<String, Object> vars;
        protected final boolean advance;
        protected final WorkflowVariableProcessingStrategy variableProcessingStrategy;

        public ProcessOperation(Process process, Map<String, Object> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance) {
            this.process = checkNotNull(process);
            this.vars = map(vars).mapKeys(process.getAliasToAttributeMap()::get);
            this.variableProcessingStrategy = checkNotNull(variableProcessingStrategy);
            this.advance = advance;
        }

        protected void validateTaksParameters(Map<String, ?> vars, Task task) {
            if (workflowConfiguration.isUserTaskParametersValidationEnabled()) {
                task.getDefinition().getVariables().stream().filter(TaskAttribute::isMandatory).forEach((var) -> {
                    Object formValue = vars.get(var.getName()), flowValue = riverFlow.getData().get(var.getName());
                    checkArgument(!isBlank(toStringOrNull(formValue)) || !isBlank(toStringOrNull(flowValue)), "missing mandatory value for var = %s task = %s", var.getName(), task.getDefinition());
                });
            }
        }

        protected void addCurrentPerformerDataInFlowAndCompleteTask() {
            checkNotNull(task);
            String taskId = task.getDefinition().getId();
            RiverTask riverTask = riverFlow.getPlan().getTask(taskId);
            riverFlow = addCurrentPerformerDataInFlow(riverFlow, task);
            riverFlow = flowService.completedTask(riverFlow, CompletedTaskImpl.of(riverFlow, riverTask));
            if (riverFlow.isRunning()) {
                riverFlow = RiverFlowImpl.copyOf(riverFlow)
                        .withFlowStatus(getRiverFlowStatus(cardRepository.getFlowCard(flow)))// read flow status from db, in order to detect if task script did change flow status (es: supend or abort flow)
                        .build();
            }
            flow = FlowImpl.copyOf(flow)
                    .withoutFlowActivity(a -> equal(a.getDefinitionId(), task.getDefinition().getId()))//remove completed activity, regenerate id
                    .withPreviousExecutors(list(flow.getPreviousExecutors()).with(task.getPerformerName())).build();
        }

        protected void addTaskDataFromFormInFlow(Map<String, Object> data, TaskDefinition taskDefinition, List<Widget> widgets) {
            logger.trace("adding data to flow, for task = {} raw (unfiltered) data = \n\n{}\n", taskDefinition.getId(), mapToLoggableStringLazy(data));
            Set<String> formVars = taskDefinition.getVariables().stream().map(TaskAttribute::getName).sorted().collect(toSet());
            Set<String> acceptedVars = set(formVars)
                    .with(ATTR_IDTENANT)
                    .with(ATTR_NOTES);//TODO improve this; add check for user privileges (??)
            logger.trace("variable processing strategy = {}, accepted vars = {}", variableProcessingStrategy, acceptedVars);
            Map dataToAdd = map().accept(m -> {
                switch (variableProcessingStrategy) {
                    case SET_ONLY_TASK_VARIABLES ->
                        m.putAll(Maps.filterKeys(data, acceptedVars::contains));
                    case SET_ALL_CLASS_VARIABLES ->
                        m.putAll(data);
                    default ->
                        throw unsupported("unsupported variable processing strategy = %s", variableProcessingStrategy);
                }
            }).accept((m) -> {
                widgets.stream().filter(Widget::hasOutputKey).forEach((w) -> {
                    if (data.containsKey(w.getOutputKey())) {
                        Object value = data.get(w.getOutputKey());
                        if (w.hasOutputType()) {
                            value = typeConverter.cardValueToFlowValue(value, w.getOutputType());
                        }
                        m.put(w.getOutputKey(), value);
                    }
                });
            });
            logger.trace("this data is not included in task or widgets, so it will be discarded = \n\n{}\n", lazyString(() -> mapToLoggableString(map(data).withoutKeys(dataToAdd.keySet()))));
            addDataFromFormInFlow(dataToAdd);
        }

        protected void addDataFromFormInFlow(Map data) {
            logger.trace("adding data to flow, raw data = \n\n{}\n", mapToLoggableStringLazy(data));
            data = convertFormDataToFlow(riverFlow.getPlan(), data);
            logger.trace("adding data to flow, processed data = \n\n{}\n", mapToLoggableStringLazy(data));
            riverFlow = RiverFlowImpl.copyOf(riverFlow)
                    .withData(map(riverFlow.getData()).with(data))
                    .build();
        }

        protected void updateFlowCard(boolean userScope) {
            checkNotNull(flow);
            checkNotNull(riverFlow);
            if (!userScope) {
                setSystemScope();
            }
            try {
                flow = persistenceService.updateFlowCard(flow, riverFlow);
                riverFlow = flowRepository.cardToRiverFlow(flow);
            } finally {
                if (!userScope) {
                    unsetSystemScope();
                }
            }
        }

        protected void advanceProcess() {
            try {
                doAdvanceProcess();
            } catch (Exception ex) {
                throw new WorkflowException(ex, "error advancing process = %s for task = %s", flow, task.getDefinition());
            }
        }

        protected void doAdvanceProcess() {
            logger.debug("advance flow, complete user task = {}", task);
            eventService.getWorkflowEventBus().post(new RiverFlowEventImpl(flow, FE_BEFORE_ADVANCE, riverFlow));
            validateTaksParameters(vars, task);
            addCurrentPerformerDataInFlowAndCompleteTask();
            updateFlowCard(false);
            eventService.getWorkflowEventBus().post(new RiverFlowEventImpl(flow, FE_AFTER_ADVANCE, riverFlow));
        }

        protected void setSystemScope() {
            user.pushUser(u -> OperationUserImpl.copyOf(u).withParam(OPERATION_SCOPE, OPERATION_SCOPE_SYSTEM).build());
        }

        protected void unsetSystemScope() {
            user.popUser();
        }

        //TODO refactor cose, use command bean
        protected FlowAdvanceResponse buildResponse() {
            logger.debug("build flow response for flow = {}", flow);
            boolean isCompleted = flow.isCompleted();
            List<Task> taskList = taskConversionService.getTaskList(flow);
            AdvancedFlowStatus status;
            if (isCompleted) {
                status = AdvancedFlowStatus.COMPLETED;
            } else if (taskList.isEmpty()) {
                status = AdvancedFlowStatus.PROCESSING_SCRIPT;
            } else {
                status = AdvancedFlowStatus.WAITING_FOR_USER_TASK;
            }
            Map<String, Object> widgetData = extractWidgetData(riverFlow, taskList);
            return SimpleFlowAdvanceResponse.builder()
                    .withFlowCard(FlowImpl.copyOf(flow).withWidgetData(widgetData).build())
                    .withAdvancedFlowStatus(status)
                    .withTasklist(taskList)
                    .build();
        }

    }

    private Map<String, Object> convertFormDataToFlow(RiverPlan plan, Map<String, Object> data) {
        Map<String, Object> converted = map();
        data.forEach((key, value) -> {
            RiverVariableInfo varInfo = plan.getGlobalVariables().get(key);
            if (varInfo != null) {
                try {
                    value = typeConverter.rawValueToFlowValue(value, varInfo.getJavaType());
                } catch (Exception ex) {
                    throw new WorkflowException(ex, "error converting form value = %s to flow var = %s", value, varInfo);
                }
            } else {
                logger.warn("received form data {} = {} ({}) not defined in flow plan global variables; skipping conversion", key, abbreviate(value), classNameOrVoid(value));
            }
            converted.put(key, value);
        });
        return converted;
    }

    private Map<String, Object> saveWidgets(Task activityInstance, Map<String, ?> vars) {
        logger.debug("save data for widgets");
        Map<String, Object> varsAndWidgetData = map(vars);
//        for (WidgetData widgetData : activityInstance.getWidgets()) {
//            if (widgetService.hasWidgetAction(widgetData.getType(), WIDGET_ACTION_SUBMIT)) {
////				Map<String, Object> params = checkNotNull((Map<String, Object>) allWidgetSubmission.get(widget.getId()),"missing submitted params for widget = %s",widget); TODO enable check once widget ui code is ready
////				Map<String, Object> widgetActionParams = firstNonNull((Map<String, Object>) allWidgetSubmission.get(widgetData.getId()), emptyMap());
//                Widget widget = widgetService.widgetDataToWidget(widgetData, (Map<String, Object>) vars); //TODO check context
//                logger.debug("save data for widget = {}", widget);
//                Map<String, Object> res = widgetService.executeWidgetAction(widget, WIDGET_ACTION_SUBMIT);
//                varsAndWidgetData.putAll(res);
//            }
//        }
        return typeConverter.widgetValuesToFlowValues(varsAndWidgetData);
    }

    private void checkUserCanAbort(Flow flow) {
        userStore.checkPrivileges(p -> p.hasPrivileges(RP_PROCESS_ALL_EXEC)
                || (p.hasServicePrivilege(GP_WF_BASIC, flow.getType()) && flow.getType().isWfUserStoppable())
                || p.hasServicePrivilege(GP_WF_LIFECYCLE, flow.getType()),
                "CM: user not authorized to abort flow = {}", flow);
    }

    private void checkUserCanSuspendResume(Flow flow) {
        userStore.checkPrivileges(p -> p.hasPrivileges(RP_PROCESS_ALL_EXEC)
                || (p.hasServicePrivilege(GP_WF_BASIC, flow.getType()) && flow.getType().isWfUserStoppable())
                || p.hasServicePrivilege(GP_WF_LIFECYCLE, flow.getType()),
                "CM: user not authorized to suspend/resume flow = {}", flow);
    }

    private static class FlowUpdatedEventImpl implements FlowUpdatedEvent {

        private final boolean isAdvanced;
        private final FlowAdvanceResponse response;

        public FlowUpdatedEventImpl(FlowAdvanceResponse response, boolean isAdvanced) {
            this.isAdvanced = isAdvanced;
            this.response = checkNotNull(response);
        }

        @Override
        public boolean isAdvanced() {
            return isAdvanced;
        }

        @Override
        public FlowAdvanceResponse getAdvanceResponse() {
            return response;
        }

    }
}
