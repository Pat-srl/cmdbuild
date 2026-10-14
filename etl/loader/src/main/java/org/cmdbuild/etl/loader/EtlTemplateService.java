/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import java.util.List;
import jakarta.activation.DataSource;

public interface EtlTemplateService extends EtlTemplateRepository, EtlTemplateProcessorService, EtlTemplateDynamicProcessor {

    EtlTemplate getForUserByCode(String code);

    EtlTemplate getForUserByCodeWithFilter(String code, String filter);

    List<EtlTemplate> getAllForUser();

    List<EtlTemplate> getForUserForTargetClassAndRelatedDomains(String classId);

    List<EtlTemplate> getForUserForTarget(EtlTemplateTarget target, String classId);

    DataSource buildImportResultReport(EtlProcessingResult result, EtlTemplate template);

    EtlProcessingResult importForUserDataWithTemplate(DataSource toDataSource, EtlTemplate template);

    default DataSource exportForUserDataWithTemplate(String idOrCode) {
        return exportDataWithTemplate(getForUserByCode(idOrCode));
    }

    default DataSource exportForUserDataWithTemplateAndFilter(String idOrCode, String filter) {
        return exportDataWithTemplate(getForUserByCodeWithFilter(idOrCode, filter));
    }

    default DataSource exportDataWithTemplate(String templateCode) {
        return exportDataWithTemplate(getTemplateByName(templateCode));
    }

    default EtlProcessingResult importDataWithTemplate(DataSource data, String templateCode) {
        return importDataWithTemplate(data, getTemplateByName(templateCode));
    }

}
