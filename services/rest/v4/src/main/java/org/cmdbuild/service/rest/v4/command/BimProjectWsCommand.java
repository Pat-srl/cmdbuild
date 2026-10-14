/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.collect.Ordering;
import jakarta.activation.DataHandler;
import org.cmdbuild.bim.*;
import org.cmdbuild.service.rest.v4.model.WsProjectData;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;

/**
 * @author ldare
 */
@Component
public class BimProjectWsCommand {

    private final BimService bimService;

    public BimProjectWsCommand(BimService bimService) {
        this.bimService = bimService;
    }

    public List<BimProjectExt> doGetAll() {
        return bimService.getAllProjectsAndObjects().stream().sorted(Ordering.natural().onResultOf(BimProjectExt::getName)).toList();
    }

    public BimObject doGetValue(Long projectId, String globalId) {
        return bimService.getBimObjectForProjectGlobalIdOrNull(bimService.getProjectById(projectId), globalId);
    }

    public BimProjectExt doGetOne(Long id) {
        return bimService.getProjectExt(id);
    }

    public DataHandler doDownloadIfcFile(Long id, String ifcFormat, String bimFormat) {
        return switch (bimFormat) {
            case "ifc" -> bimService.downloadIfcFile(id, ifcFormat);
            case "xkt" -> bimService.downloadXktFile(id);
            default -> throw unsupported("Invalid bim format %s", bimFormat);
        };
    }

    public BimProjectExt doCreateProjectWithFile(DataHandler dataHandler, WsProjectData data) {
        if (dataHandler == null) {
            return bimService.createProjectExt(data.toBimProject().build(), data.toOwnerOrNull());
        } else {
            String schema;
            BimProjectExt project = bimService.createProjectExt(new BimProjectExtImpl(data.toBimProject().build(), data.toOwnerOrNull()));
            bimService.uploadXktFile(project.getId(), dataHandler, true);
            return bimService.getProjectExt(project.getId());
        }
    }

    public BimProjectExt doUpdate(Long id, DataHandler dataHandler, WsProjectData data) {
        bimService.updateProjectExt(data.toBimProject().withId(id).build(), data.toOwnerOrNull());
        if (dataHandler != null) {
            bimService.uploadXktFile(id, dataHandler, false);
        }
        return bimService.getProjectExt(id);
    }

    public BimProject doUploadIfcFile(Long id, DataHandler dataHandler) {
        return bimService.uploadXktFile(id, dataHandler, false);
    }

    public void doDelete(Long id) {
        bimService.deleteProject(id);
    }
}
