/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.util.Collection;
import java.util.Map;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessSerializer;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.inner.ProcessRepository;
import org.cmdbuild.workflow.model.Process;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * <b>Note</b>: I refuse to use {@link WorkflowService}, that has 24 services as
 * dependencies in its implementation. And only loading is needed, because
 * {@link Process} are not created/modified/deleted in <i>mobile offline</i>.
 * Some method of {@link WorkflowService} is so replicated here.
 *
 * @author afelice
 */
@Component
public class ProcessLoaderImpl implements ProcessLoader {

    private final ProcessRepository processRepository;
    private final WorkflowService workflowService;
    private final ProcessSerializer processSerializer;

    public ProcessLoaderImpl(ProcessRepository processRepository, WorkflowService workflowService, @Qualifier("processSerializer") ProcessSerializer processSerializer) {
        this.processRepository = checkNotNull(processRepository);
        this.workflowService = checkNotNull(workflowService);
        this.processSerializer = checkNotNull(processSerializer);
    }

    /**
     * As was in <code>WorkflowService.getActiveProcessClasses()</code>
     *
     * @return
     */
    @Override
    public Collection<Process> getAllProcessColl() {
        //return processRepository.getAllPlanClassesForCurrentUser().stream().filter(p -> p.isActive() && p.isSuperclass() ? true : p.hasPlan()).collect(toList());
        return processRepository.getAllPlanClassesForCurrentUser();
    }

    /**
     * As was in <code>WorkflowService.getProcess(String processId)</code>
     *
     * @param processClasseName
     * @return
     */
    @Override
    public Process getProcess(String processClasseName) {
        return processRepository.getProcessClassByName(processClasseName);
    }

    /**
     * As in <code>ProcessWs.getXpdlVersionFile()</code>.
     *
     * @param processClasseName
     * @param planId
     * @return
     */
    @Override
    public DataHandler getProcessXpdl(String processClasseName, String planId) {
        DataSource dataSource = workflowService.getXpdlByClasseIdAndPlanId(processClasseName, planId);
        return new DataHandler(dataSource);
    }

    @Override
    public Map<String, Object> serializeProcess(Process curProcess) {
        return processSerializer.serializeProcess(curProcess);
    }

}
