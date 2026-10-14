/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim.etl;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.bim.BimObjectImpl;
import org.cmdbuild.bim.BimProjectExt;
import org.cmdbuild.bim.BimProjectImpl;
import org.cmdbuild.bim.BimService;
import static org.cmdbuild.bim.BimService.BIMSERVER_SOURCE_PROJECT_ID;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.config.BimConfiguration;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.etl.EtlException;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_IFC;
import org.cmdbuild.etl.job.EtlLoadHandler;
import org.cmdbuild.etl.job.EtlLoaderApi;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlProcessingResultFault;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfig;
import org.cmdbuild.etl.loader.EtlTemplateImpl;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.etl.loader.EtlTemplateWithData;
import org.cmdbuild.etl.loader.EtlTemplateWithDataImpl;
import org.cmdbuild.etl.loader.inner.CardProcessedEvent;
import org.cmdbuild.etl.loader.inner.EtlProcessingResultErrorImpl;
import org.cmdbuild.etl.loader.inner.EtlProcessingResultImpl;
import static org.cmdbuild.etl.loader.inner.EtlProcessingResultImpl.emptyResult;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_PROCESSING_REPORT;
import org.cmdbuild.etl.waterway.message.WaterwayMessageDataImpl;
import static org.cmdbuild.fault.FaultUtils.exceptionToUserMessage;
import org.cmdbuild.utils.ifc.IfcEntry;
import org.cmdbuild.utils.ifc.IfcModel;
import static org.cmdbuild.utils.ifc.IfcModel.RECORD_SOURCE_ENTRY;
import org.cmdbuild.utils.ifc.utils.IfcUtils;
import static org.cmdbuild.utils.io.CmIoUtils.toDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.zip;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToMessage;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IfcEtlLoaderHandler implements EtlLoadHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EtlTemplateService importService;
    private final BimService bimService;
    private final DaoService dao;

    public IfcEtlLoaderHandler(
            EtlTemplateService importService,
            BimService bimService,
            DaoService daoService,
            BimConfiguration bimConfiguration) {
        this.importService = checkNotNull(importService);
        this.bimService = checkNotNull(bimService);
        this.dao = checkNotNull(daoService);
    }

    @Override
    public String getType() {
        return ETLHT_IFC;
    }

    @Override
    @Nullable
    public WaterwayMessageData load(EtlLoaderApi api) {
        return new IfcEtlLoaderHandlerHelper(api).load();
    }

    private enum IfcProjectMode {
        AUTO, NONE
    }

    private enum IfcMasterCardMode {
        AUTO, STATIC
    }

    private class IfcEtlLoaderHandlerHelper {

        private final EtlLoaderApi api;
        private final boolean enableProjectManagement, enableAutoCard;

        private final boolean loadBimObjects, hasParent;
        private final String importProjectCode, masterCardClassId, subprojectCodeSuffix;
        private final List<String> masterCardKeySource, masterCardKeyAttr;
        private final Long masterCardIdParam;

        private CardIdAndClassName masterCard;
        private BimProjectExt project;
        private EtlProcessingResult importResult = emptyResult();
        private IfcModel ifc;

        public IfcEtlLoaderHandlerHelper(EtlLoaderApi api) {
            this.api = checkNotNull(api);

            enableProjectManagement = equal(IfcProjectMode.AUTO, parseEnumOrDefault(api.getConfig("bimserver_project_mode"), IfcProjectMode.AUTO));
            importProjectCode = api.getParam("bimserver_project_import_code");
            masterCardIdParam = toLongOrNull(api.getParam("bimserver_project_master_card_id"));
            enableAutoCard = equal(IfcMasterCardMode.AUTO, parseEnumOrDefault(api.getConfig("bimserver_project_master_card_mode"), IfcMasterCardMode.AUTO));

            hasParent = toBooleanOrDefault(api.getConfig("bimserver_project_has_parent"), false);
            subprojectCodeSuffix = firstNotBlank(api.getConfig("bimserver_subproject_code_suffix"), format("_%s", api.getGateCode()));

            masterCardClassId = api.getParam("bimserver_project_master_card_target_class");
            masterCardKeySource = toListOfStrings(api.getConfig("bimserver_project_master_card_key_source"));
            masterCardKeyAttr = toListOfStrings(api.getConfig("bimserver_project_master_card_key_attr"));

            loadBimObjects = toBooleanOrDefault(api.getConfig("bimserver_project_objects_import_enabled"), enableProjectManagement);

            checkArgument(!loadBimObjects || enableProjectManagement, "cannot enable bim object load if bimserver project management is not enabled");
        }

        public WaterwayMessageData load() {

            Long sourceProjectId = toLongOrNull(api.getParam(BIMSERVER_SOURCE_PROJECT_ID));
            if (isNotNullAndGtZero(sourceProjectId)) {
                project = bimService.getProjectExt(sourceProjectId);
                if (project.hasParent()) {
                    masterCard = bimService.getProjectExt(project.getParentId()).getOwner();
                } else {
                    masterCard = project.getOwner();
                }
                logger.info("loading ifc from project = {}", project);
                ifc = IfcUtils.loadIfc(toDataSource(bimService.downloadIfcFile(project.getId(), null)));
            } else {
                logger.info("loading ifc = {}", api.getData());
                ifc = IfcUtils.loadIfc(api.getData());
                masterCard = null;
                project = null;
            }

            List<EtlTemplateWithData> data = api.getTemplates().stream().filter(EtlTemplate::isActive).map(t -> new EtlTemplateWithDataImpl(EtlTemplateImpl.copyOf(t).withColumnNameFromAttributeName().build(), () -> {
                String context = checkNotBlank(t.getSource(), "missing source for template = {}", t);
                logger.info("load records for template = {} with source =< {} >", t, context);
                Map<String, String> attributeMapping = t.getColumns().stream().collect(toImmutableMap(EtlTemplateColumnConfig::getAttributeName, EtlTemplateColumnConfig::getColumnName));
                logger.debug("using column mapping = \n\n{}\n", mapToLoggableStringLazy(attributeMapping));
                List<Map<String, ?>> records = (List) ifc.extractRecords(context, attributeMapping);
                return records;
            })).collect(toImmutableList());

            List<EtlTemplateWithData> masterTemplates = list(data).withOnly(d -> equal(d.getTemplate().getTargetName(), masterCardClassId));
            if (!masterTemplates.isEmpty()) {
                logger.debug("execute master card template");
                data = list(data).without(masterTemplates);
                importResult = importResult.and(importService.importDataWithTemplates(masterTemplates));
            }

            if (enableProjectManagement) {
                logger.debug("project management enabled, check bim project");
                if (masterCard == null) {
                    try {
                        checkNotBlank(masterCardClassId, "missing master card class id");
                        if (isNotNullAndGtZero(masterCardIdParam)) {
                            masterCard = dao.getCard(masterCardClassId, masterCardIdParam);
                        } else if (enableAutoCard) {
                            checkNotEmpty(masterCardKeySource, "missing master card import key source");
                            checkNotEmpty(masterCardKeyAttr, "missing master card import key attr");
                            checkArgument(masterCardKeySource.size() == masterCardKeyAttr.size(), "mismatching master card import key source/attrs config");
                            masterCard = dao.select(ATTR_ID).from(masterCardClassId).accept(q -> zip(masterCardKeySource, masterCardKeyAttr, Pair::of).forEach(p -> q.where(p.getRight(), EQ, ifc.queryString(p.getLeft())))).getCard();
                        } else {
                            throw new EtlException("missing valid master card configuration (static card id or other)");
                        }
                    } catch (Exception ex) {
                        throw new EtlException(ex, "unable to retrieve bim project master card");
                    }
                }
                logger.debug("master card = {}", masterCard);
                if (project != null) {
                    logger.debug("bim project already provided, skip create/update");
                } else {
                    logger.debug("create/update bim project");
                    project = bimService.getProjectByMasterCardOrNull(masterCard);
                    String code = firstNotBlank(importProjectCode, format("%s_%s", masterCard.getClassName(), firstNotBlank(masterCard.getCode(), masterCard.getId().toString())));
                    Long parentId;
                    CardIdAndClassName projectOwner;
                    if (hasParent) {
                        BimProjectExt parent = project;
                        if (parent == null) {
                            parent = bimService.createProjectExt(BimProjectImpl.builder().withName(code).build(), masterCard);
                        }
                        logger.debug("handle sub project, parent project = {}, subproject code suffix =< {} >", parent, subprojectCodeSuffix);
                        project = bimService.getProjectsForParent(parent).stream().filter(p -> p.getName().endsWith(subprojectCodeSuffix)).collect(toOptional()).orElse(null);
                        code += subprojectCodeSuffix;
                        parentId = parent.getId();
                        projectOwner = null;
                    } else {
                        parentId = null;
                        projectOwner = masterCard;
                    }
                    if (project == null) {
                        project = bimService.createProjectExt(BimProjectImpl.builder().withName(code).withParentId(parentId).build(), projectOwner);
                        importResult = importResult.and(new EtlProcessingResultImpl(1, 0, 0, 0, 1, emptyList()));
                    } else {
                        importResult = importResult.and(new EtlProcessingResultImpl(0, 1, 0, 0, 1, emptyList()));
                    }
                    bimService.uploadXktFile(project.getId(), toDataHandler(api.getData()));
                }
                logger.debug("bim project = {}", project);
            }

            //TODO master card filter !!
            List<Pair<CardIdAndClassName, Map<String, Object>>> cardsAndGuids = list();

            data = list(data).map(d -> new EtlTemplateWithDataImpl(d.getTemplate(), (Supplier) d::getData, new Object() {

                @Subscribe
                public void handleCardProcessedEvent(CardProcessedEvent event) {
                    cardsAndGuids.add(Pair.of((Card) event.getCard(), event.getRecord()));
                }

            }));

            importResult = importResult.and(importService.importDataWithTemplates(data));

            if (loadBimObjects) {
                logger.info("load bim objects mapping, processing {} records", cardsAndGuids.size());
                AtomicLong created = new AtomicLong(0), updated = new AtomicLong(0);
                List<EtlProcessingResultFault> errors = list();
                try {
                    checkNotNull(project, "missing bimserver project");
                    cardsAndGuids.stream().filter(c -> masterCard == null || !equal(c.getLeft().getId(), masterCard.getId())).forEach(p -> {
                        Map<String, Object> record = p.getRight();
                        CardIdAndClassName card = p.getLeft();
                        try {
                            String globalId = ((IfcEntry) record.get(RECORD_SOURCE_ENTRY)).getGlobalId();
                            bimService.updateBimObject(BimObjectImpl.builder().withOwnerCardId(card.getId()).withOwnerClassId(card.getClassName())
                                    .withProjectId(project.getProjectId())
                                    .withGlobalId(checkNotBlank(globalId))
                                    .build());

//                                    created.incrementAndGet(); //TODO check if created or updated
                            updated.incrementAndGet();//TODO check if created or updated

                        } catch (Exception ex) {
                            logger.warn(marker(), "error importing bim obj = {} with card = {}", record, card, ex);
                            errors.add(new EtlProcessingResultErrorImpl(0l, 0l, record, exceptionToUserMessage(ex), exceptionToMessage(ex)));
                        }
                    });
                } catch (Exception ex) {
                    logger.warn(marker(), "error importing bimserver objects", ex);
                    errors.add(new EtlProcessingResultErrorImpl(0l, 0l, emptyMap(), exceptionToUserMessage(ex), exceptionToMessage(ex)));
                }
                importResult = importResult.and(new EtlProcessingResultImpl(created.get(), updated.get(), 0, 0, 0, errors));
            }

            logger.info("completed import for ifc = {} : {}", ifc, importResult.getResultDescription());
            return WaterwayMessageDataImpl.build(WY_PROCESSING_REPORT, importResult, api.getMeta());
        }

    }

}
