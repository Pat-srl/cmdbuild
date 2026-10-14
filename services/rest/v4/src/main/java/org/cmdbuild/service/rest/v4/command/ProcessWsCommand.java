/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.collect.Ordering;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.XpdlInfo;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Component
public class ProcessWsCommand {

    private final WorkflowService workflowService;//TODO replace with user wf service
    private final UserClassService userClassService;
    private final ClassSerializationHelper classSerializationHelper;

    public ProcessWsCommand(WorkflowService workflowService, UserClassService userClassService, ClassSerializationHelper classSerializationHelper) {
        this.workflowService = checkNotNull(workflowService);
        this.userClassService = checkNotNull(userClassService);
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
    }

    public PagedElements<Process> doReadAll(Supplier<Collection<Process>> function, Long limit, Long offset) {
        Collection<Process> all = function.get();
        List<Process> ordered = Ordering.natural().onResultOf(Process::getName).sortedCopy(all);
        return paged(ordered, offset, limit);
    }

    public Process doRead(String processId) {
        return workflowService.getProcess(processId);
    }

    public XpdlInfo doUploadNewXpdlVersion(String processId, DataHandler dataHandler, Boolean replace) {
        if (replace) {
            return workflowService.addXpdlReplaceCurrent(processId, toDataSource(dataHandler));
        } else {
            return workflowService.addXpdl(processId, toDataSource(dataHandler));
        }
    }

    public List<XpdlInfo> doGetAllXpdlVersions(String processId) {
        if (workflowService.isWorkflowEnabled()) {
            return workflowService.getXpdlInfosOrderByVersionDesc(processId);
        } else {
            return emptyList();
        }
    }

    public DataHandler doGetXpdlVersionFile(String processId, String planId) {
        DataSource dataSource = workflowService.getXpdlByClasseIdAndPlanId(processId, planId);
        return new DataHandler(dataSource);
    }

    public DataHandler doGetXpdlTemplateFile(String processId) {
        DataSource dataSource = workflowService.getXpdlTemplate(processId);
        return new DataHandler(dataSource);
    }

    public ExtendedClass doCreate(ClassSerializationHelper.WsClassData data) {
        return userClassService.createClass(classSerializationHelper.extendedClassDefinitionForNewClass(data));
    }

    public ExtendedClass doUpdate(String classId, ClassSerializationHelper.WsClassData data) {
        return userClassService.updateClass(classSerializationHelper.extendedClassDefinitionForExistingClass(classId, data));
    }

    public void doDelete(String classId) {
        userClassService.deleteClass(classId);
    }
}
