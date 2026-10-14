/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataHandler;
import java.util.Collection;
import java.util.Map;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.XpdlInfo;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class ProcessSyncImpl implements ProcessSync {

    private final ProcessLoader processLoader;
    private final WorkflowService workflowService;

    public ProcessSyncImpl(ProcessLoader processLoader, WorkflowService workflowService) {
        this.processLoader = checkNotNull(processLoader);
        this.workflowService = checkNotNull(workflowService);
    }

    @Override
    public Collection<Process> getAllProcessColl() {
        return processLoader.getAllProcessColl();
    }

    @Override
    public Process getProcess(String processClasseName) {
        return processLoader.getProcess(processClasseName);
    }

    @Override
    public DataHandler getProcessXpdl(String processClasseName, String planId) {
        return processLoader.getProcessXpdl(processClasseName, planId);
    }

    @Override
    public Map<String, Object> serializeProcess(Process curProcess) {
        return processLoader.serializeProcess(curProcess);
    }

    @Override
    public String addXpld(String processClasseName, DataHandler xpdlContentDataHandler, boolean bReplace) {
        // As in ProcessWs.uploadNewXpdlVersion()
        XpdlInfo xpdlInfo;
        if (bReplace) {
            xpdlInfo = workflowService.addXpdlReplaceCurrent(processClasseName, toDataSource(xpdlContentDataHandler));
        } else {
            xpdlInfo = workflowService.addXpdl(processClasseName, toDataSource(xpdlContentDataHandler));
        }

        return xpdlInfo.getPlanId();
    }

}
