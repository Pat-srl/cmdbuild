/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.etl.gate.EtlGateService;
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.service.rest.v4.model.WsImportExportGateData;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Component
public class EtlGateWsCommand {

    private final EtlGateService etlGateService;

    public EtlGateWsCommand(EtlGateService etlGateService) {

        this.etlGateService = checkNotNull(etlGateService);
    }

    public List<EtlGate> doReadAll() {
        return etlGateService.getAllForCurrentUser();
    }

    public CmCollectionUtils.FluentList<EtlGate> doReadAllForClass(String classId) {
        return list(etlGateService.getAllForCurrentUser()).withOnly(e -> e.getShowOnClasses().contains(checkNotBlank(classId)));
    }

    public EtlGate doRead(String code) {
        return etlGateService.getByCodeForCurrentUser(code);
    }

    public EtlGate doCreate(WsImportExportGateData data) {
        return etlGateService.create(data.toEtlGate().build());
    }

    public EtlGate doUpdate(String code, WsImportExportGateData data) {
        return etlGateService.update(data.toEtlGate().withCode(code).build());
    }

    public void doDelete(String code) {
        etlGateService.delete(code);
    }

}
