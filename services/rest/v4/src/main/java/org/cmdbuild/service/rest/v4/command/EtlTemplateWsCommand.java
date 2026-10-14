/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.firebase.database.utilities.Pair;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateInlineProcessorService;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.service.rest.v4.model.WsEtlTemplateData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static org.cmdbuild.etl.loader.EtlTemplateTarget.*;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class EtlTemplateWsCommand {

    private final EtlTemplateService etlTemplateService;
    private final EtlTemplateInlineProcessorService etlTemplateInlineProcessorService;

    public EtlTemplateWsCommand(EtlTemplateService etlTemplateService, EtlTemplateInlineProcessorService etlTemplateInlineProcessorService) {
        this.etlTemplateService = checkNotNull(etlTemplateService);
        this.etlTemplateInlineProcessorService = checkNotNull(etlTemplateInlineProcessorService);
    }

    public List<EtlTemplate> doReadAll(Predicate<EtlTemplate> predicate) {
        return list(etlTemplateService.getAllForUser()).withOnly(predicate);
    }

    public List<EtlTemplate> doReadAllForClass(String classId, Boolean includeRelatedDomains, Predicate<EtlTemplate> predicate) {
        return list(includeRelatedDomains ? etlTemplateService.getForUserForTargetClassAndRelatedDomains(classId) : etlTemplateService.getForUserForTarget(ET_CLASS, classId)).withOnly(predicate);
    }

    public List<EtlTemplate> doReadAllForProcess(String classId, Boolean includeRelatedDomains, Predicate<EtlTemplate> predicate) {
        return list(includeRelatedDomains ? etlTemplateService.getForUserForTargetClassAndRelatedDomains(classId) : etlTemplateService.getForUserForTarget(ET_PROCESS, classId)).withOnly(predicate);
    }

    public List<EtlTemplate> doReadAllForView(String viewId, Predicate<EtlTemplate> predicate) {
        return list(etlTemplateService.getForUserForTarget(ET_VIEW, viewId)).withOnly(predicate);
    }

    public EtlTemplate doReadOne(String idOrCode) {
        return etlTemplateService.getForUserByCode(idOrCode);
    }

    public DataSource doExecuteExportTemplate(String idOrCode, String filterStr) {
        return etlTemplateService.exportForUserDataWithTemplateAndFilter(idOrCode, filterStr);
    }

    public Pair<EtlProcessingResult, DataSource> doExecuteImportTemplate(String idOrCode, DataHandler dataHandler) {
        EtlTemplate template = etlTemplateService.getForUserByCode(idOrCode);
        EtlProcessingResult result = etlTemplateService.importForUserDataWithTemplate(toDataSource(dataHandler), template);
        DataSource report = etlTemplateService.buildImportResultReport(result, template);
        return new Pair<>(result, report);
    }

    public DataSource doExecuteInlineExportTemplate(String data, WsEtlTemplateData config) {
        return etlTemplateInlineProcessorService.exportDataInline(fromJson(data, LIST_OF_MAP_OF_OBJECTS), config.toInlineModel(), config.toInlineTemplate());
    }

    public List<Card> doExecuteInlineImportTemplate(DataHandler data, WsEtlTemplateData config) {
        return etlTemplateInlineProcessorService.importDataInline(toDataSource(data), config.toInlineModel(), config.toInlineTemplate());
    }

    public EtlTemplate doCreate(WsEtlTemplateData data) {
        return etlTemplateService.create(data.toImportExportTemplate().build());
    }

    public EtlTemplate doUpdate(String templateId, WsEtlTemplateData data) {
        return etlTemplateService.update(data.toImportExportTemplate().withCode(templateId).build());
    }

    public void doDelete(String templateName) {
        etlTemplateService.delete(templateName);
    }

}
