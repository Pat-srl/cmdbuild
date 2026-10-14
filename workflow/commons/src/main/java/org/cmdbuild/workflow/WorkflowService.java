package org.cmdbuild.workflow;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.workflow.WorkflowService.WorkflowVariableProcessingStrategy.SET_ONLY_TASK_VARIABLES;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.XpdlInfo;

public interface WorkflowService {

    final String CONTEXT_SCOPE_WORKFLOW = "workflow";

    Task getUserTask(Flow card, String userTaskId);

    Map<String, Object> getFlowData(Flow flowCard);

    TaskDefinition getTaskDefinition(Flow flowCard, String taskId);

    FlowAdvanceResponse startProcess(Process classe, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    FlowAdvanceResponse updateProcess(Flow card, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    DataSource getXpdlForClasse(Process classe);

    List<TaskDefinition> getTaskDefinitions(String processId);

    List<XpdlInfo> getXpdlInfosOrderByVersionDesc(String classId);

    XpdlInfo addXpdl(String classId, DataSource dataSource);

    XpdlInfo addXpdlReplaceCurrent(String classId, DataSource dataSource);

    List<Task> getTaskListForCurrentUserByClassIdAndCardId(String classId, Long cardId); //TODO move this to facade?

    PagedElements<Task> getTaskListForCurrentUserByClassIdSkipFlowData(String classId, DaoQueryOptions queryOptions); //TODO move this to facade?

    Task getTask(Flow flowCard, String taskId);

    Collection<Task> getTaskList(Flow flowCard);

    void abortProcessInstance(Flow flowCard);

    void suspendProcessInstance(Flow flowCard);

    void resumeProcessInstance(Flow flowCard);

    TaskDefinition getEntryTaskForCurrentUser(String processId);

    boolean hasEntryTaskForCurrentUser(String processId);

    boolean isWorkflowEnabled();

    boolean isWorkflowEnabledAndProcessRunnable(String classId);

    PagedElements<Flow> getUserFlowCardsByClasseIdAndQueryOptions(String classId, DaoQueryOptions queryOptions);

    PagedElements<Flow> getFlowCardsByClasseAndQueryOptions(Classe classe, DaoQueryOptions queryOptions);

    @Nullable
    Flow getFlowCardOrNull(Process classe, Long cardId);

    Process getProcess(String classId);

    DataSource getXpdlByClasseIdAndPlanId(String classId, String planId);

    Collection<Process> getActiveProcessClasses();

    Collection<Process> getAllProcessClasses();

    List<WidgetData> getWidgetsForUserTask(String classeId, Long cardId, String taskId);

    FlowAdvanceResponse startProcess(String classId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    FlowAdvanceResponse updateProcess(String classId, Long cardId, String taskId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    FlowAdvanceResponse updateProcessWithOnlyTask(String classId, Long cardId, Map<String, ?> vars, WorkflowVariableProcessingStrategy variableProcessingStrategy, boolean advance);

    DataSource getXpdlTemplate(String classId);

    void suspendProcess(String classId, Long cardId);

    void resumeProcess(String classId, Long cardId);

    void abortProcess(String classId, Long cardId);

    void abortProcessFromUser(String classId, Long cardId);

    void suspendProcessFromUser(String classId, Long cardId);

    void resumeProcessFromUser(String classId, Long cardId);

    Flow getUserFlowCard(String classId, Long cardId);

    default List<Flow> getUserFlowCardsByClasseId(String classId) {
        return getUserFlowCardsByClasseIdAndQueryOptions(classId, DaoQueryOptionsImpl.emptyOptions()).elements();
    }

    default Flow getFlowCard(Process classe, Long cardId) {
        return checkNotNull(getFlowCardOrNull(classe, cardId), "flow card not found for classe = %s cardId = %s", classe, cardId);
    }

    default Flow getFlowCard(CardIdAndClassName card) {
        return getFlowCard(card.getClassName(), card.getId());
    }

    default Flow getFlowCard(String classId, Long cardId) {
        Process classe = getProcess(classId);
        return getFlowCard(classe, cardId);
    }

    default FlowAdvanceResponse startProcess(String classId, Map<String, ?> vars, boolean advance) {
        return startProcess(classId, vars, SET_ONLY_TASK_VARIABLES, advance);
    }

    default FlowAdvanceResponse startProcess(Classe process, Map<String, ?> vars, boolean advance) {
        return startProcess(process.getName(), vars, SET_ONLY_TASK_VARIABLES, advance);
    }

    default FlowAdvanceResponse startProcess(Classe process, Object... vars) {
        return startProcess(process.getName(), map(vars), SET_ONLY_TASK_VARIABLES, true);
    }

    default FlowAdvanceResponse updateProcess(CardIdAndClassName card, Object... vars) {
        return updateProcess(card.getClassName(), card.getId(), getTask(getFlowCard(card)).getId(), map(vars), false);
    }

    default FlowAdvanceResponse advanceProcess(CardIdAndClassName card, Object... vars) {
        return updateProcess(card.getClassName(), card.getId(), getTask(getFlowCard(card)).getId(), map(vars), true);
    }

    default FlowAdvanceResponse updateProcess(String classId, Long cardId, String taskId, Map<String, ?> vars, boolean advance) {
        return updateProcess(classId, cardId, taskId, vars, SET_ONLY_TASK_VARIABLES, advance);
    }

    default FlowAdvanceResponse updateProcessWithOnlyTask(String classId, Long cardId, Map<String, ?> vars, boolean advance) {
        return updateProcessWithOnlyTask(classId, cardId, vars, SET_ONLY_TASK_VARIABLES, advance);
    }

    default FlowAdvanceResponse updateProcessWithOnlyTask(CardIdAndClassName flow, Object... vars) {
        return updateProcessWithOnlyTask(flow.getClassName(), flow.getId(), map(vars), SET_ONLY_TASK_VARIABLES, true);
    }

    default Task getTask(Flow flowCard) {
        return getOnlyElement(getTaskList(flowCard));
    }

    default TaskDefinition getTaskDefinition(String processId, String taskId) {
        return getTaskDefinitions(processId).stream().filter(t -> equal(t.getId(), taskId)).collect(onlyElement("task def not found for processId =< %s > and taskId =< %s >", processId, taskId));
    }

    default Collection<Task> getTaskListLean(Flow flowCard) {
        return getTaskList(flowCard);
    }

    default Map<String, Object> getWidgetData(Collection<Task> taskList, Flow flowCard) {
        throw new UnsupportedOperationException();
    }

    default Map<String, Object> getAllFlowData(String classId, long cardId) {
        throw new UnsupportedOperationException();
    }

    default Task getTaskByDefinitionId(Flow card, String taskDefinitionId) {
        return getTaskList(card).stream().filter(t -> equal(t.getDefinition().getId(), taskDefinitionId)).collect(onlyElement("task not found for flow = %s definition id =< %s >", card, taskDefinitionId));
    }

    default XpdlInfo addXpdl(String classId, String xpdlData) {
        return addXpdl(classId, newDataSource(xpdlData, "text/xml"));
    }

    default XpdlInfo addXpdl(Classe process, String xpdlData) {
        return addXpdl(process.getName(), xpdlData);
    }

    default XpdlInfo addXpdlReplaceCurrent(String classId, String xpdlData) {
        return addXpdlReplaceCurrent(classId, newDataSource(xpdlData, "text/xml"));
    }

    default PagedElements<Task> getTaskListForCurrentUserByClassId(String classId, @Nullable Long offset, @Nullable Long limit, CmdbSorter sort, CmdbFilter filter, @Nullable Long positionOfCard, @Nullable Boolean goToPage) {
        return getTaskListForCurrentUserByClassIdSkipFlowData(classId, DaoQueryOptionsImpl.builder()
                .withOffset(offset)
                .withLimit(limit)
                .withSorter(sort)
                .withFilter(filter)
                .withPositionOf(positionOfCard, goToPage)
                .build());
    }

    enum WorkflowVariableProcessingStrategy {
        SET_ONLY_TASK_VARIABLES, SET_ALL_CLASS_VARIABLES
    }

}
