/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsFlowData;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.WorkflowGraphService;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FILE;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;

/**
 * @author ldare
 */
@Component
public class ProcessInstancesWsCommand {

    private final WorkflowGraphService workflowGraphService;
    private final WorkflowService workflowService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ProcessInstancesWsCommand(
            WorkflowGraphService workflowGraphService,
            WorkflowService workflowService) {
        this.workflowGraphService = checkNotNull(workflowGraphService);
        this.workflowService = checkNotNull(workflowService);
    }

    public FlowAdvanceResponse doCreate(String processId, WsFlowData processInstance) {
        Process processClass = workflowService.getProcess(processId);
        return workflowService.startProcess(
                processId,
                convertInputValuesForFlow(processClass, processInstance),
                //				adaptWidgets(processInstance.getWidgets()),
                processInstance.isAdvance());
    }

    public FlowAdvanceResponse doUpdate(String planClassId, Long flowCardId, WsFlowData processInstance) {
        Flow flowCard = workflowService.getFlowCard(planClassId, flowCardId);
        Task task = workflowService.getTask(flowCard, checkNotBlank(processInstance.getActivity(), "must set 'activity' param"));

        Map<String, Object> map = convertInputValuesForFlow(flowCard.getType(), processInstance);
        map = convertTaskValues(task, map);

        return workflowService.updateProcess(planClassId, flowCardId, task.getId(), map, processInstance.isAdvance());
    }

    public Flow doRead(String planClassId, Long flowCardId) {
        return workflowService.getUserFlowCard(planClassId, flowCardId);
    }

    public DataHandler doPlotGraph(String processId, Long cardId, Boolean simplified) {
        Flow card = workflowService.getFlowCard(processId, cardId);
        DataSource graph;
        if (simplified) {
            graph = workflowGraphService.getSimplifiedGraphImageForFlow(card);
        } else {
            graph = workflowGraphService.getGraphImageForFlow(card);
        }
        return new DataHandler(graph);
    }

    public PagedElements<Flow> doReadMany(String name, DaoQueryOptions queryOptions) {
        return workflowService.getUserFlowCardsByClasseIdAndQueryOptions(name, queryOptions);
    }

    public void doDelete(String processId, Long instanceId) {
        workflowService.abortProcessFromUser(processId, instanceId);
    }

    public void doSuspend(String processId, Long instanceId) {
        workflowService.suspendProcessFromUser(processId, instanceId);
    }

    public void doResume(String processId, Long instanceId) {
        workflowService.resumeProcessFromUser(processId, instanceId);
    }

    public void doDeleteMany(String processId, WsQueryOptions wsQueryOptions) {
        // TODO access control (can_bulk), bulk query
        workflowService.getUserFlowCardsByClasseIdAndQueryOptions(processId, wsQueryOptions.getQuery()).forEach(c -> workflowService.abortProcessFromUser(c.getClassName(), c.getId()));
    }

    private Map<String, Object> convertInputValuesForFlow(Process userProcessClass, WsFlowData processInstanceAdvanceable) {
        return convertValues(userProcessClass, firstNonNull(processInstanceAdvanceable.getValues(), emptyMap()));
    }

    private Map<String, Object> convertValues(Process type, Map<String, Object> values) {
        return map(values).mapValues((key, value) -> {
            try {
                if (type.hasAttribute(key) && !type.getAttribute(key).isOfType(FILE)) {//TODO improve this
                    value = rawToSystem(type.getAttribute(key).getType(), value);
                }
                return value;
            } catch (Exception ex) {
                throw runtime(ex, "error converting attr =< {} > type = {} value =< {} >", key, type, abbreviate(value));
            }
        });
    }

    private Map<String, Object> convertTaskValues(org.cmdbuild.workflow.model.Task task, Map<String, Object> values) {
        return map(values).accept(m -> {
            task.getWidgets().forEach((w) -> {
                if (w.hasOutputKey() && w.hasOutputType()) {
                    Object rawValue = values.get(w.getOutputKey());
                    Object value = rawToSystem(w.getOutputType(), rawValue);
                    m.put(w.getOutputKey(), value);
                }
            });
        });
    }
}
