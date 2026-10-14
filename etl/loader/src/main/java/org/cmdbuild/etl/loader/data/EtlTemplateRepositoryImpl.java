/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader.data;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.Map;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_TEMPLATE;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateConfig;
import static org.cmdbuild.etl.loader.EtlTemplateConfig.EnableCreate.EC_TRUE;
import org.cmdbuild.etl.loader.EtlTemplateDynamicImpl;
import org.cmdbuild.etl.loader.EtlTemplateImpl;
import org.cmdbuild.etl.loader.EtlTemplateLoaderHelper;
import org.cmdbuild.etl.loader.EtlTemplateReference;
import org.cmdbuild.etl.loader.EtlTemplateRepository;
import org.cmdbuild.etl.loader.EtlTemplateTarget;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_CLASS;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_DOMAIN;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_PROCESS;
import static org.cmdbuild.etl.utils.EtlTemplateUtils.serializeImportExportTemplateTarget;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenListOfMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.view.ViewDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class EtlTemplateRepositoryImpl implements EtlTemplateRepository, EtlTemplateLoaderHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final ViewDefinitionService viewService;
    private final WaterwayDescriptorService service;
    private final CmCache<List<EtlTemplate>> templatesByTarget;
    private final CmCache<EtlTemplateReference> templatesByName;
    private final Holder<List<EtlTemplateReference>> templates;

    public EtlTemplateRepositoryImpl(WaterwayDescriptorService service, ViewDefinitionService viewService, DaoService dao, CacheService cacheService, EventBusService busService) {
        this.service = checkNotNull(service);
        this.dao = checkNotNull(dao);
        this.viewService = checkNotNull(viewService);
        templatesByTarget = cacheService.newCache("ietemplates_by_target", CacheConfig.SYSTEM_OBJECTS);
        templatesByName = cacheService.newCache("ietemplates_by_name", CacheConfig.SYSTEM_OBJECTS);
        templates = cacheService.newHolder("ietemplates_all", CacheConfig.SYSTEM_OBJECTS);
        busService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                invalidateCaches();
            }
        });
    }

    private void invalidateCaches() {
        templatesByTarget.invalidateAll();
        templatesByName.invalidateAll();
        templates.invalidate();
    }

    @Override
    public List<EtlTemplateReference> getAll() {
        return templates.get(() -> service.getAllItems(WYCIT_TEMPLATE).stream().map(this::dataToTemplate).collect(toImmutableList()));
    }

    @Override
    public List<EtlTemplate> getTemplates() {
        return list(getAll()).filter(EtlTemplate.class);
    }

    @Override
    public List<EtlTemplate> getAllForTarget(EtlTemplateTarget type, String name) {
        checkNotNull(type);
        checkNotBlank(name);
        return templatesByTarget.get(key(serializeImportExportTemplateTarget(type), name), () -> getTemplates().stream().filter(t -> equal(t.getTargetType(), type) && equal(t.getTargetName(), name)).collect(toImmutableList()));
    }

    @Override
    public List<EtlTemplate> getAllForTargetClassAndRelatedDomains(String classId) {
        Classe classe = dao.getClasse(classId);
        List<Domain> domains = dao.getDomainsForClasse(classe);
        return list(getAllForTarget(ET_CLASS, classe.getName())).with(getAllForTarget(ET_PROCESS, classe.getName())).accept(l -> domains.stream().map(d -> getAllForTarget(ET_DOMAIN, d.getName())).forEach(l::addAll));
    }

    @Override
    public EtlTemplateReference getByName(String templateName) {
        return templatesByName.get(templateName, () -> doGetTemplateByName(templateName));
    }

    @Override
    public EtlTemplate getTemplateByName(String templateName) {
        return (EtlTemplate) getByName(templateName);
    }

    @Override
    public EtlTemplate create(EtlTemplate template) {
        service.createUpdateDescriptor(templateToFile(template.getCode(), template), WaterwayDescriptorMetaImpl.builder().withEnabled(template.isActive()).build(), false);
        invalidateCaches();
        return getTemplateByName(template.getCode());
    }

    @Override
    public EtlTemplate update(EtlTemplate template) {
        WaterwayDescriptorRecord record = service.getDescriptorForSingleItemUpdate(getTemplateByName(template.getCode()).getCode());
        service.createUpdateDescriptor(templateToFile(record.getCode(), template), WaterwayDescriptorMetaImpl.builder().withEnabled(template.isActive()).build());
        invalidateCaches();
        return getTemplateByName(template.getCode());
    }

    @Override
    public void delete(String templateCode) {
        service.deleteDescriptor(service.getDescriptorForSingleItemUpdate(getTemplateByName(templateCode).getCode()).getCode());
        invalidateCaches();
    }

    private EtlTemplateReference doGetTemplateByName(String templateName) {
        return getAll().stream().filter(t -> equal(t.getCode(), templateName)).collect(onlyElement("import/export template not found for name = %s", templateName));
    }

    private EtlTemplateReference dataToTemplate(WaterwayItem data) {
        try {
            if (toBooleanOrDefault(data.getConfig("dynamic"), false)) {
                return EtlTemplateDynamicImpl.builder()
                        .withActive(data.isEnabled())
                        .withCode(data.getCode())
                        .withDescription(data.getDescription())
                        .withData(map(data.getConfig()).with("columns", firstNotNull(data.getConfig("columns"), unflattenListOfMaps(data.getConfig(), "columns")), serializeEnum(WYCIT_TEMPLATE), data.getCode(), "description", data.getDescription()))
                        .build();
            } else {
                EtlTemplateConfig config = fromJson(toJson(map(data.getConfig()).with("columns", unflattenListOfMaps(data.getConfig(), "columns"))), EtlTemplateConfigImpl.class);
                EtlTemplate template = EtlTemplateImpl.builder()
                        .withActive(data.isEnabled())
                        .withCode(data.getCode())
                        .withDescription(data.getDescription())
                        .withConfig(config)
                        .build();
                validateTemplateSafe(template);
                return template;
            }
        } catch (Exception ex) {
            throw new EtlException(ex, "error processing template = %s", data);
        }
    }

    @Override
    public EtlTemplate jsonToTemplate(String data) {
        try {
            Map<String, Object> map = fromJson(data, MAP_OF_OBJECTS);
//            EtlTemplateConfig config = fromJson(toJson(map(map).with("columns", unflattenListOfMaps(map, "columns"))), EtlTemplateConfigImpl.class);
            EtlTemplateConfig config = fromJson(data, EtlTemplateConfigImpl.class);
            EtlTemplate template = EtlTemplateImpl.builder()
                    .withActive(true)
                    .withCode(toStringNotBlank(map.get(serializeEnum(WYCIT_TEMPLATE))))
                    .withDescription(toStringOrNull(map.get("description")))
                    .withConfig(config)
                    .build();
            validateTemplateSafe(template);
            return template;
        } catch (Exception ex) {
            throw new EtlException(ex, "error processing template =< %s >", data);
        }
    }

    private String templateToFile(String code, EtlTemplate template) {
        validateTemplate(template);
        return toJson(map("descriptor", code, "description", template.getDescription(), "tag", "standalone", "items", list(map(fromJson(toJson(EtlTemplateConfigImpl.copyOf(template).build()), MAP_OF_OBJECTS))
                .with(serializeEnum(WYCIT_TEMPLATE), template.getCode(), "description", template.getDescription()))));
    }

    private void validateTemplateSafe(EtlTemplate template) {
        try {
            validateTemplate(template);
        } catch (Exception ex) {
            logger.warn(marker(), "invalid template = {}", template, ex);
        }
    }

    private void validateTemplate(EtlTemplate template) {
        switch (template.getTargetType()) {
            case ET_CLASS -> {
                Classe classe = dao.getClasse(template.getTargetName());
                checkArgument(!classe.isProcess());
                if (template.isImportTemplate()) {
                    checkArgument(!equal(template.getEnableCreate(), EC_TRUE) || !classe.isSuperclass(), "cannot create an import (create) template on a super class");
                }
            }
            case ET_PROCESS -> {
                Classe process = dao.getClasse(template.getTargetName());
                checkArgument(process.isProcess() && !template.isImportTemplate());
            }
            case ET_DOMAIN ->
                dao.getDomain(template.getTargetName());
            case ET_VIEW ->
                viewService.getSharedByName(template.getTargetName());
            case ET_RECORD -> {
                //nothing to do
            }
            default ->
                throw new IllegalArgumentException("unsupported target type = " + template.getTargetType());
        }
//        template.getColumns().forEach(c -> { TODO fix this, handle geo attributes
//            Attribute attribute = entryType.getAttribute(c.getAttributeName());
//            if (attribute.isOfType(REFERENCE, LOOKUP, FOREIGNKEY) || set(ATTR_IDOBJ1, ATTR_IDOBJ2).contains(attribute.getName())) {
//                checkArgument(!equal(c.getMode(), ETCM_DEFAULT), "invalid column mode = %s for attribute = %s", c.getMode(), attribute);
//            } else {
//                checkArgument(equal(c.getMode(), ETCM_DEFAULT), "invalid column mode = %s for attribute = %s", c.getMode(), attribute);
//            }
//        });
    }

}
