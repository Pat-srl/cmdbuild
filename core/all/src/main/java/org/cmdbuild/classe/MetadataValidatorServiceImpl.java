/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.cardfilter.StoredFilter;
import org.cmdbuild.classe.access.MetadataValidatorService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.gate.inner.EtlGateRepository;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateRepository;
import org.springframework.stereotype.Component;

@Component
public class MetadataValidatorServiceImpl implements MetadataValidatorService {

    private final DaoService dao;
    private final CardFilterService filterService;
    private final EtlTemplateRepository importExportTemplateService;
    private final EtlGateRepository gateRepository;

    public MetadataValidatorServiceImpl(DaoService dao, CardFilterService filterService, EtlTemplateRepository importExportTemplateService, EtlGateRepository gateRepository) {
        this.dao = checkNotNull(dao);
        this.filterService = checkNotNull(filterService);
        this.importExportTemplateService = checkNotNull(importExportTemplateService);
        this.gateRepository = checkNotNull(gateRepository);
    }

    @Override
    public void validateMetadata(String classId, ClassMetadata metadata) {
        if (metadata.getDefaultFilterOrNull() != null) {
            StoredFilter filter = filterService.getById(metadata.getDefaultFilterOrNull());
            checkArgument(filter.isShared());
//            checkArgument(filter.isShared()&&filter.isForClass(dao.getClass))
        }
        if (metadata.getDefaultImportTemplateOrNull() != null) {
            switch (metadata.getDefaultImportTemplate().getType()) {
                case ClassMetadata.TEMPLATE_TYPE_TEMPLATE -> {
                    EtlTemplate template = importExportTemplateService.getTemplateByName(metadata.getDefaultImportTemplate().getCode());
                    checkArgument(template.isImportTemplate() && template.hasTarget(dao.getClasse(classId)), "invalid import template = %s for class = %s", template, classId);
                }
                case ClassMetadata.TEMPLATE_TYPE_GATE -> {
                    EtlGate gate = gateRepository.getByCode(metadata.getDefaultImportTemplate().getCode());
//                    checkArgument(template.isImportTemplate() && template.hasTarget(dao.getClasse(classId)), "invalid import template = %s for class = %s", template, classId); TODO
                }
                default ->
                    throw new UnsupportedOperationException();
            }
        }
        if (metadata.getDefaultExportTemplateOrNull() != null) {
            switch (metadata.getDefaultExportTemplate().getType()) {
                case ClassMetadata.TEMPLATE_TYPE_TEMPLATE -> {
                    EtlTemplate template = importExportTemplateService.getTemplateByName(metadata.getDefaultExportTemplate().getCode());
                    checkArgument(template.isExportTemplate() && template.hasTarget(dao.getClasse(classId)), "invalid export template = %s for class = %s", template, classId);
                }
                default ->
                    throw new UnsupportedOperationException();
            }
//                case ClassMetadata.TEMPLATE_TYPE_GATE:
//                    EtlGate gate = gateRepository.getById(metadata.getDefaultImportTemplate().getId());
////                    checkArgument(template.isImportTemplate() && template.hasTarget(dao.getClasse(classId)), "invalid import template = %s for class = %s", template, classId); TODO
//                    break;
        }
    }

}
