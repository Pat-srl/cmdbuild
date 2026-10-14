/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.*;
import static java.util.Collections.emptyList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.*;
import static org.cmdbuild.auth.grant.GrantConstants.*;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.bim.BimService;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassDefinition;
import org.cmdbuild.classe.ExtendedClassDefinitionImpl;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.beans.CardIdAndClassNameUtils.serializeTypeAndCode;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.config.UiConfiguration;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl.ClassMetadataImplBuilder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.entrytype.*;
import static org.cmdbuild.dao.entrytype.AttributeGroupData.ATTRIBUTE_GROUP_DEFAULT_DISPLAY_MODE;
import static org.cmdbuild.dao.entrytype.ClassMultitenantModeUtils.parseClassMultitenantMode;
import static org.cmdbuild.dao.entrytype.ClassMultitenantModeUtils.serializeClassMultitenantMode;
import static org.cmdbuild.dao.entrytype.ClassPermission.*;
import static org.cmdbuild.dao.entrytype.ClassType.CT_STANDARD;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_SAFE;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.data.filter.SorterElementDirection;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.formstructure.FormStructureImpl;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.formtrigger.FormTriggerBinding;
import org.cmdbuild.formtrigger.FormTriggerImpl;
import org.cmdbuild.lookup.DmsAttachmentCountCheck;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.lookup.LookupValueData;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData.WsClassDataFormTrigger;
import org.cmdbuild.service.rest.common.serializationhelpers.ContextMenuSerializationHelper.WsClassDataContextMenuItem;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.date.CmDateUtils.*;
import static org.cmdbuild.utils.html.HtmlSanitizerUtils.sanitizeHtml;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.*;
import static org.cmdbuild.utils.lang.CmConvertUtils.*;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.*;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.widget.model.WidgetDbData;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_BUTTON_LABEL_KEY;
import static org.cmdbuild.widget.utils.WidgetUtils.serializeWidgetDataToString;
import static org.cmdbuild.widget.utils.WidgetUtils.toWidgetData;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.springframework.stereotype.Component;

@Component
public class ClassSerializationHelper {

    private final ObjectTranslationService translationService;
    private final BimService bimService;
    private final EasyuploadService easyuploadService;
    private final UserClassService classService;
    private final MultitenantConfiguration multitenantConfiguration;
    private final WorkflowConfiguration workflowConfiguration;
    private final CoreConfiguration coreConfiguration;
    private final UiConfiguration uiConfiguration;
    private final ContextMenuSerializationHelper contextMenuSerializationHelper;
    private final WidgetService widgetService;
    private final LookupService lookupService;
    private final DmsConfiguration dmsConfig;
    private final OperationUserSupplier operationUser;
    private final RoleRepository roleRepository;

    public ClassSerializationHelper(
            WidgetService widgetService,
            ObjectTranslationService translationService,
            BimService bimService,
            EasyuploadService easyuploadService,
            UserClassService classService,
            MultitenantConfiguration multitenantConfiguration,
            WorkflowConfiguration workflowConfiguration,
            CoreConfiguration coreConfiguration,
            UiConfiguration uiConfiguration,
            ContextMenuSerializationHelper contextMenuSerializationHelper,
            LookupService lookupService,
            DmsConfiguration dmsConfig,
            OperationUserSupplier operationUser,
            RoleRepository roleRepository) {
        this.translationService = checkNotNull(translationService);
        this.bimService = checkNotNull(bimService);
        this.easyuploadService = checkNotNull(easyuploadService);
        this.classService = checkNotNull(classService);
        this.multitenantConfiguration = checkNotNull(multitenantConfiguration);
        this.workflowConfiguration = checkNotNull(workflowConfiguration);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.uiConfiguration = checkNotNull(uiConfiguration);
        this.contextMenuSerializationHelper = checkNotNull(contextMenuSerializationHelper);
        this.widgetService = checkNotNull(widgetService);
        this.lookupService = checkNotNull(lookupService);
        this.dmsConfig = checkNotNull(dmsConfig);
        this.operationUser = checkNotNull(operationUser);
        this.roleRepository = checkNotNull(roleRepository);
    }

    public CmMapUtils.FluentMap<String, Object> buildBasicResponse(Classe classe) {
        return CmMapUtils.<String, Object, Object>map(
                "_id", classe.getName(),
                "name", classe.getName(),
                "description", classe.getDescription(),
                "_description_translation", translationService.translateClassDescription(classe),
                "_description_plural_translation", translationService.translateClassDescriptionPlural(classe),
                "prototype", classe.isSuperclass(),
                "parent", classe.getParentOrNull(),
                "active", classe.isActive(),
                "type", serializeEnum(classe.getClassType()),
                "speciality", serializeEnum(classe.getClassSpeciality()),
                "_can_read", classe.hasUiPermission(CP_READ),
                "_can_create", classe.hasUiPermission(CP_CREATE),
                "_can_update", classe.hasUiPermission(CP_UPDATE),
                "_can_clone", classe.hasUiPermission(CP_CLONE),
                "_can_delete", classe.hasUiPermission(CP_DELETE),
                "_can_modify", classe.hasUiPermission(CP_MODIFY),
                "_can_print", classe.hasUiPermission(CP_PRINT)
        ).accept(m -> {
            Role currentUserRole = roleRepository.getByNameOrIdOrNull(operationUser.getCurrentGroup());

            if (currentUserRole != null && currentUserRole.getConfig().getFullTextSearch() != null) {
                m.put("_can_search", currentUserRole.getConfig().getFullTextSearch() && classe.hasUiPermission(CP_SEARCH));
            } else {
                m.put("_can_search", uiConfiguration.isFullTextSearchEnabled() && classe.hasUiPermission(CP_SEARCH));
            }

            Map<String, Object> customPermissions = (Map) map(GDCP_BULK_UPDATE, coreConfiguration.isBulkUpdateEnabledDefault() ? classe.hasUiWritePermission() : false,
                    GDCP_BULK_DELETE, coreConfiguration.isBulkDeleteEnabledDefault() ? classe.hasUiWritePermission() : false,
                    GDCP_BULK_ABORT, workflowConfiguration.isBulkAbortEnabledDefault() ? classe.hasUiWritePermission() : false,
                    GDCP_FLOW_CLOSED_MODIFY_ATTACHMENT, workflowConfiguration.enableAddAttachmentOnClosedActivities()
            ).accept((e) -> {
                if (currentUserRole != null) {
                    if (currentUserRole.getConfig().getBulkAbort() != null) {
                        e.put(GDCP_BULK_ABORT, currentUserRole.getConfig().getBulkAbort());
                    }
                    if (currentUserRole.getConfig().getBulkUpdate() != null) {
                        e.put(GDCP_BULK_UPDATE, currentUserRole.getConfig().getBulkUpdate());
                    }
                    if (currentUserRole.getConfig().getBulkDelete() != null) {
                        e.put(GDCP_BULK_DELETE, currentUserRole.getConfig().getBulkDelete());
                    }
                }
            }).skipNullValues().with(classe.getOtherPermissions());
            if (classe.isProcess()) {
                m.put("_can_start", toBooleanOrNull(customPermissions.get(GDCP_FLOW_START)));
                list(GDCP_BULK_ABORT, GDCP_FLOW_CLOSED_MODIFY_ATTACHMENT).forEach(k -> m.put(format("_can_%s", k), toBooleanOrDefault(customPermissions.get(k), true)));
            } else {
                list(GDCP_BULK_UPDATE, GDCP_BULK_DELETE).forEach(k -> m.put(format("_can_%s", k), toBooleanOrDefault(customPermissions.get(k), true)));
            }
            list(GDCP_ATTACHMENT, GDCP_DETAIL, GDCP_EMAIL, GDCP_HISTORY, GDCP_NOTE, GDCP_RELATION).accept(l -> {
                if (!classe.isProcess()) {
                    l.add(GDCP_SCHEDULE);
                }
            }).forEach(c -> {
                m.put(format("_%s_access_read", c), toBooleanOrNull(customPermissions.get(format("%s_read", c))));
                m.put(format("_%s_access_write", c), toBooleanOrNull(customPermissions.get(format("%s_write", c))));
            });
            m.put(format("_%s_access", GDCP_RELGRAPH), toBooleanOrNull(customPermissions.get(GDCP_RELGRAPH)));
        }).with(
                "defaultFilter", classe.getMetadata().getDefaultFilterOrNull(),
                "defaultImportTemplate", serializeTypeAndCode(classe.getMetadata().getDefaultImportTemplateOrNull()),
                "defaultExportTemplate", classe.getMetadata().hasDefaultExportTemplate() ? classe.getMetadata().getDefaultExportTemplate().getCode() : null,
                "description_attribute_name", ATTR_DESCRIPTION,
                "metadata", classe.getMetadata().getCustomMetadata(),
                "_icon", classe.getMetadata().hasIcon() ? Optional.ofNullable(easyuploadService.getByPathOrNull(classe.getMetadata().getIcon())).map(EasyuploadItem::getId).orElse(null) : null,
                "uiRouting_mode", serializeEnum(classe.getMetadata().getUiRoutingMode()),
                "uiRouting_target", classe.getMetadata().getUiRoutingTarget(),
                "uiRouting_custom", fromJson(firstNotBlank(classe.getMetadata().getUiRoutingCustom(), "{}"), MAP_OF_STRINGS)
        ).accept((m) -> {
            if (multitenantConfiguration.isMultitenantEnabled()) {
                m.put("multitenantMode", serializeClassMultitenantMode(classe.getMultitenantMode()));
            }
        });
    }

    public CmMapUtils.FluentMap<String, Object> buildFullDetailResponse(Classe classe) {
        return buildBasicResponse(classe).with(
                "dmsCategory", classe.hasDmsCategory() ? classe.getDmsCategory() : null,
                "noteInline", classe.getMetadata().getNoteInline(),
                "noteInlineClosed", classe.getMetadata().getNoteInlineClosed(),
                "noteInGrid", classe.getMetadata().getNoteInGrid(),
                "attachmentsInline", classe.getMetadata().getAttachmentsInline(),
                "attachmentsInlineClosed", classe.getMetadata().getAttachmentsInlineClosed(),
                "validationRule", classe.getMetadata().getValidationRuleOrNull(),
                "stoppableByUser", classe.getMetadata().isWfUserStoppable(),
                "barcodeSearchAttr", classe.getMetadata().getBarcodeSearchAttr(),
                "barcodeSearchRegex", classe.getMetadata().getBarcodeSearchRegex(),
                "icon_font", classe.getMetadata().getIconFont(),
                "defaultOrder", classe.getDefaultOrder().getElements().stream().map((o) -> map(ATTRIBUTE, o.getProperty(), DIRECTION, o.getDirection().equals(SorterElementDirection.ASC) ? ASCENDING : DESCENDING)).collect(toList()),//TODO replace this with standard sorter serialization
                "domainOrder", classe.getMetadata().getDomainOrder(),
                "help", classe.getMetadata().getHelpMessage(),
                "autoValue", classe.getMetadata().getAutoValue(),
                "_help_translation", translationService.translateClassHelp(classe)
        ).accept((m) -> {
            if (bimService.isEnabled()) {
                m.put("hasBimLayer", bimService.hasBim(classe));
            }
            if (classe.isDmsModel()) {
                m.put(
                        "allowedExtensions", Joiner.on(",").join(nullToEmpty(classe.getMetadata().getDmsAllowedExtensions())),
                        "checkCount", serializeEnum(classe.getMetadata().getDmsCheckCount()),
                        "checkCountNumber", classe.getMetadata().getDmsCheckCountNumber(),
                        "maxFileSize", classe.getMetadata().getMaxFileSize());
            }
        });
    }

    public CmMapUtils.FluentMap<String, Object> buildFullDetailExtendedResponse(ExtendedClass extendedClass) {
        Classe input = extendedClass.getClasse();
        List<FormTrigger> triggers = extendedClass.getFormTriggers();
        List<ContextMenuItem> contextMenuItems = extendedClass.getContextMenuItems();
        AtomicInteger attrGroupIndex = new AtomicInteger(0);
        return buildFullDetailResponse(input)
                .with("widgets", widgetsToResponse(extendedClass.getWidgets(), input)).skipNullValues()
                .with("formTriggers", triggers == null ? null : triggers.stream().map((t) -> {
                    Map map = map("script", t.getJsScript(), "active", t.isActive());
                    for (FormTriggerBinding binding : FormTriggerBinding.values()) {
                        map.put(binding.name(), t.getBindings().contains(binding));
                    }
                    return map;
                }).collect(toList()))
                .with("contextMenuItems", contextMenuItems == null ? null : contextMenuSerializationHelper.contextMenuItemsToResponse(contextMenuItems, extendedClass.getClasse().getName()))
                .with("attributeGroups", input.getAttributeGroups().stream()
                        .map(g -> map(
                        "_id", g.getName(),
                        "name", g.getName(),
                        "description", g.getDescription(),
                        "_description_translation", input.isView() ? translationService.translateViewAttributeGroupDescription(g) : translationService.translateAttributeGroupDescription(input, g),
                        "index", attrGroupIndex.incrementAndGet()
                ).accept(m -> {
                    m.with(g.getConfig()); //TODO improve this (??)
                })).collect(toList())).accept(m -> {
            if (extendedClass.hasForm()) {
                m.put("formStructure", fromJson(extendedClass.getFormStructure().getData(), JsonNode.class));
            }
        }).accept(d -> {
            List<Map<String, Object>> categories = list();
            String dmsCategory = extendedClass.getClasse().hasDmsCategory() ? extendedClass.getClasse().getDmsCategory() : dmsConfig.getDefaultDmsCategory();
            List<LookupValue> allCategoryValues = lookupService.getAllLookup(dmsCategory).stream().filter(LookupValueData::isActive).collect(toList());
            mergeDmsPermissions(allCategoryValues, extendedClass.getClasse().getDmsPermissions()).forEach((k, e) -> {
                if (!e.contains(GAP_NONE)) {
                    String categoryValue = k.replace(format("%s_", dmsCategory), "");
                    Map<String, Object> category = map();
                    category.put("category", categoryValue);
                    if (e.contains(GAP_READ)) {
                        category.put("_can_create", e.contains(GAP_WRITE));
                    } else if (e.contains(GAP_DEFAULT)) {
                        category.put("_can_create", extendedClass.getClasse().isProcess() ? extendedClass.getClasse().hasUiPermission(CP_WF_BASIC) : extendedClass.getClasse().hasUiPermission(CP_WRITE)); //extendedClass.getClasse().hasUiPermission(CP_WRITE));
                    }
                    categories.add(category);
                }
            });
            d.put("dmsCategories", categories);
            d.put("lookupValues", list(extendedClass.getLookupValuesByAttr().entrySet()).map(e -> map("attribute", e.getKey(), "values", list(e.getValue()).map(l -> l.getId()))).sorted(m -> (String) m.get("attribute")));
        }).then();
    }

    //TODO move the merging with missing permissions in an earlier step
    private Map<String, Set<GrantAttributePrivilege>> mergeDmsPermissions(List<LookupValue> lookups, Map<String, Set<GrantAttributePrivilege>> dmsPrivileges) {
        Map<String, Set<GrantAttributePrivilege>> mergedPrivileges = map();
        dmsPrivileges.forEach((k, e) -> {
            if (lookupService.getLookupByTypeAndCode(k.replaceAll("_.*", ""), k.replaceAll(".*_", "")).isActive()) {
                mergedPrivileges.put(k, e);
            }
        });
        lookups.forEach(l -> {
            if (!dmsPrivileges.containsKey(format("%s_%s", l.getLookupType(), l.getCode()))) {
                mergedPrivileges.put(format("%s_%s", l.getLookupType(), l.getCode()), set(GAP_DEFAULT));
            }
        });
        return mergedPrivileges;
    }

    private Object widgetsToResponse(Collection<WidgetData> widgets, Classe classe) {
        return widgets.stream().map((widgetData) -> {
            Map<String, Object> widgetDataAsMapWithoutLabel = map(widgetData.getData()).withoutKey(WIDGET_BUTTON_LABEL_KEY);
            Widget widget = widgetService.widgetDataToWidget(widgetData, classe);
            return serializeWidget(widget)
                    .with("_label_translation", translationService.translateClassWidgetDescription(((WidgetDbData) widgetData).getOwner(), widgetData.getId(), widgetData.getLabel()))
                    .with("_config", serializeWidgetDataToString(widgetDataAsMapWithoutLabel));
        }).collect(toList());
    }

    public ExtendedClassDefinition extendedClassDefinitionForNewClass(WsClassData data) {
        return addExtendedClassData(classDefinitionForNewClass(data), data);
    }

    public ExtendedClassDefinition extendedClassDefinitionForExistingClass(String classId, WsClassData data) {
        return addExtendedClassData(classDefinitionForExistingClass(classId, data), data);
    }

    /**
     * Used by <code>model-diff-schema</code>, to handle
     * <code>reserved</code>/<code>protected</code> {@link Classe}s.
     *
     * @param currentClass
     * @param data
     * @return
     */
    public ExtendedClassDefinition extendedClassDefinitionForExistingClass(Classe currentClass, WsClassData data) {
        // as done in classDefinitionForExistingClass, without permission mode check (that fails for a reserved Classe)
        ClassDefinition classDefinition = ClassDefinitionImpl.copyOf(currentClass)
                .withMetadata(ClassMetadataImpl.copyOf(currentClass.getMetadata()).accept(data.metadataFillerForClassDataUpdate(coreConfiguration.hasDefaultTextContentSecurity(TCS_HTML_SAFE))).accept(addIcon(data)).build())
                .build();

        return addExtendedClassData(classDefinition, data);
    }

    public CmMapUtils.FluentMap serializeWidget(WidgetData widgetData) {
        return serializeWidget(widgetData, null);
    }

    public CmMapUtils.FluentMap serializeWidget(WidgetData widgetData, @Nullable String className) {
        return map("_id", widgetData.getId(),
                "_label", widgetData.getLabel(),
                "_type", widgetData.getType(),
                "_active", widgetData.isActive(),
                "_required", widgetData.isRequired(),
                "_alwaysenabled", widgetData.isAlwaysEnabled(),
                "_hideincreation", widgetData.hideInCreation(),
                "_hideinedit", widgetData.hideInEdit(),
                "_inline", widgetData.isInline(),
                "_inlineclosed", widgetData.isInlineClosed(),
                "_inlinebefore", widgetData.getInlineBefore(),
                "_inlineafter", widgetData.getInlineAfter(),
                "_output", widgetData.getOutputParameterOrNull()).accept(m -> {
            String descriptionTranslation;
            if (widgetData instanceof WidgetDbData widgetDbData) {//TODO improve this, translate wf widgets
                descriptionTranslation = translationService.translateClassWidgetDescription(widgetDbData.getOwner(), widgetData.getId(), widgetData.getLabel());
            } else if (isNotBlank(className) && isNotBlank(widgetData.getId())) {
                descriptionTranslation = translationService.translateClassWidgetDescription(className, widgetData.getId(), widgetData.getLabel());
            } else {
                descriptionTranslation = widgetData.getLabel();
            }
            m.put("_label_translation", descriptionTranslation);
        }).with(map(widgetData.getExtendedData()).mapValues(ClassSerializationHelper::serializeWidgetExtendedValue));
    }

    @Nullable
    public static Object serializeWidgetExtendedValue(@Nullable Object value) {
        if (value == null || isPrimitiveOrWrapper(value)) {
            return value;
        } else if (isDateTime(value)) {
            return toIsoDateTimeUtc(value);
        } else if (isDate(value)) {
            return toIsoDate(value);
        } else if (isTime(value)) {
            return toIsoTime(value);
        } else {
            return value;
        }
    }

    public CmMapUtils.FluentMap serializeWorkflowWidget(Process process, TaskDefinition task, WidgetData widgetData) {
        return serializeWidget(widgetData).with("_label_translation", translationService.translateWorkflowWidgetDescription(process.getName(), task.getId(), widgetData.getId(), widgetData.getLabel()));
    }

    private ClassDefinition classDefinitionForNewClass(WsClassData data) {
        Classe parent = Optional.ofNullable(trimToNull(data.parentId)).map(classService::getUserClass).orElse(null);
        return ClassDefinitionImpl.builder()
                .withParent(applyOrNull(parent, Classe::getName))
                .withName(data.name)
                .withMetadata(ClassMetadataImpl.builder().accept(data.metadataFillerForClassDataCreate(coreConfiguration.hasDefaultTextContentSecurity(TCS_HTML_SAFE))).accept(addIcon(data)).build())
                .build();
    }

    private ClassDefinition classDefinitionForExistingClass(String classId, WsClassData data) {
        Classe currentClass = classService.getUserClass(classId);
        return ClassDefinitionImpl.copyOf(currentClass)
                .withMetadata(ClassMetadataImpl.copyOf(currentClass.getMetadata()).accept(data.metadataFillerForClassDataUpdate(coreConfiguration.hasDefaultTextContentSecurity(TCS_HTML_SAFE))).accept(addIcon(data)).build())
                .build();
    }

    private Consumer<ClassMetadataImplBuilder> addIcon(WsClassData data) {
        return b -> {
            if (isNotNullAndGtZero(data.iconId)) {
                b.withIconPath(easyuploadService.getById(data.iconId).getPath());
            } else {
                b.withIconPath(null);
            }
        };
    }

    private ExtendedClassDefinition addExtendedClassData(ClassDefinition classDefinition, WsClassData data) {
        return ExtendedClassDefinitionImpl.builder()
                .withClassDefinition(classDefinition)
                .withContextMenuItems(contextMenuSerializationHelper.toContextMenuItems(data.contextMenuItems))
                .withFormTriggers(toFormTriggers(data.formTriggers))
                .withDefaultClassOrdering(data.defaultOrder == null ? emptyList() : data.defaultOrder.stream()
                        .map((o) -> Pair.of(o.attribute, parseDirection(o.direction)))
                        .collect(toList()))
                .withWidgets(data.widgets.stream().map((w) -> toWidgetData(w.type, w.active, w.config, w.label)).collect(toList()))
                .withAttributeGroups(data.attributeGroups.stream().map(g -> {
                    return new AttributeGroupInfoImpl(g.name, g.description, (Map) map(ATTRIBUTE_GROUP_DEFAULT_DISPLAY_MODE, g.defaultDisplayMode).withoutValues(Objects::isNull));//TODO improve this (??)
                }).collect(toList()))
                .withFormStructure(applyOrNull(data.formStructure, fs -> new FormStructureImpl(toJson(fs))))
                .build();
    }

    private ExtendedClassDefinition.Direction parseDirection(String direction) {
        return switch (checkNotBlank(direction).toLowerCase()) {
            case ASCENDING ->
                ExtendedClassDefinition.Direction.ASC;
            case DESCENDING ->
                ExtendedClassDefinition.Direction.DESC;
            default ->
                throw new UnsupportedOperationException("unsupported order direction = " + direction);
        };
    }

    private List<FormTrigger> toFormTriggers(List<WsClassData.WsClassDataFormTrigger> formTriggers) {
        return formTriggers.stream().map((t) -> {
            List<FormTriggerBinding> bindings = list();
            try {
                list(WsClassDataFormTrigger.class.getDeclaredFields()).filter(field -> list(FormTriggerBinding.class.getEnumConstants()).map(FormTriggerBinding::name).contains(field.getName())).forEach(declaredField -> {
                    try {
                        if (declaredField.getBoolean(t)) {
                            bindings.add(parseEnum(declaredField.getName(), FormTriggerBinding.class));
                        }
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw runtime(ex);
                    }
                });
            } catch (IllegalArgumentException | SecurityException ex) {
                throw runtime(ex);
            }
            return FormTriggerImpl.builder()
                    .withActive(t.active)
                    .withJsScript(t.script)
                    .withBindings(bindings)
                    .build();
        }).collect(toList());
    }

    public CmMapUtils.FluentMap<String, Object> serialize(Classe classe, Set<UserClassService.ClassQueryFeatures> features, Boolean detailed) {
        ExtendedClass extendedClass = classService.getExtendedClass(classe, features.toArray(UserClassService.ClassQueryFeatures[]::new));
        if (detailed) {
            return buildFullDetailExtendedResponse(extendedClass);
        }
        return buildBasicResponse(classe);
    }

    public List<CmMapUtils.FluentMap<String, Object>> serialize(List<Classe> classes, Set<UserClassService.ClassQueryFeatures> features, Boolean detailed) {
        return classes.stream()
                .map(c -> serialize(c, features, detailed))
                .collect(toList());
    }

    public List<CmMapUtils.FluentMap<String, Object>> serialize(List<Classe> classes, Boolean detailed) {
        return classes.stream().map(detailed ? c -> buildFullDetailExtendedResponse(classService.getExtendedClass(c)) : this::buildBasicResponse).collect(toList());
    }

    public static List<CmMapUtils.FluentMap<String, Object>> filterAttributes(List<CmMapUtils.FluentMap<String, Object>> attributeSerializations, String filterStr) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasFilter()) {
            attributeSerializations = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, m) -> toStringOrNull(m.get(k))).withFilter(filter.getAttributeFilter()).filter(attributeSerializations);
        }
        return attributeSerializations;
    }

    public static class WsClassData {

        public final String name;
        public final String description;
        public final String help, autoValue, validationRule, flowStatusAttr, messageAttr, flowProvider;
        public final ClassType type;
        public final String parentId, uiRoutingTarget, uiRoutingCustom, barcodeSearchAttr, barcodeSearchRegex;
        public final ClassMultitenantMode multitenantMode;
        public final boolean isActive;
        public final boolean isSuperclass;
        public final Boolean noteInline, noteInlineClosed, noteInGrid;
        public final Boolean attachmentsInlineClosed, attachmentsInline;
        public final Boolean stoppableByUser;
        public final Boolean enableSaveButton;
        public final String defaultImportTemplate, defaultExportTemplate, dmsCategory;
        public final List<WsClassDataDefaultOrder> defaultOrder;
        public final List<WsClassDataFormTrigger> formTriggers;
        public final List<WsClassDataContextMenuItem> contextMenuItems;
        public final List<WsClassDataWidget> widgets;
        public final List<String> domainOrder;
        public final List<WsClassDataAttributeGroup> attributeGroups;
        public final Long defaultFilter, iconId;
        public final JsonNode formStructure;
        private final Collection<String> allowedExtensions;
        private final DmsAttachmentCountCheck checkCount;
        private final Integer checkCountNumber, maxFileSize;
        private final ClassUiRoutingMode uiRoutingMode;
        private final String iconFont;

        public WsClassData(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("defaultFilter") Long defaultFilter,
                @JsonProperty("defaultImportTemplate") String defaultImportTemplate,
                @JsonProperty("defaultExportTemplate") String defaultExportTemplate,
                @JsonProperty("_icon") Long iconId,
                @JsonProperty("validationRule") String validationRule,
                @JsonProperty("type") String type,
                @JsonProperty("allowedExtensions") String allowedExtensions,
                @JsonProperty("checkCount") String checkCount,
                @JsonProperty("checkCountNumber") Integer checkCountNumber,
                @JsonProperty("maxFileSize") Integer maxFileSize,
                @JsonProperty("messageAttr") String messageAttr,
                @JsonProperty("flowStatusAttr") String flowStatusAttr,
                @JsonProperty("engine") String engine,
                @JsonProperty("parent") String parentId,
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("prototype") Boolean isSuperclass,
                @JsonProperty("noteInline") Boolean noteInline,
                @JsonProperty("noteInlineClosed") Boolean noteInlineClosed,
                @JsonProperty("noteInGrid") Boolean noteInGrid,
                @JsonProperty("attachmentsInline") Boolean attachmentsInline,
                @JsonProperty("attachmentsInlineClosed") Boolean attachmentsInlineClosed,
                @JsonProperty("enableSaveButton") Boolean enableSaveButton,
                @JsonProperty("dmsCategory") String dmsCategory,
                @JsonProperty("multitenantMode") String multitenantMode,
                @JsonProperty("stoppableByUser") Boolean stoppableByUser,
                @JsonProperty("defaultOrder") List<WsClassDataDefaultOrder> defaultOrder,
                @JsonProperty("formTriggers") List<WsClassDataFormTrigger> formTriggers,
                @JsonProperty("contextMenuItems") List<WsClassDataContextMenuItem> contextMenuItems,
                @JsonProperty("widgets") List<WsClassDataWidget> widgets,
                @JsonProperty("attributeGroups") List<WsClassDataAttributeGroup> attributeGroups,
                @JsonProperty("domainOrder") List<String> domainOrder,
                @JsonProperty("help") String help,
                @JsonProperty("autoValue") String autoValue,
                @JsonProperty("uiRouting_mode") String uiRoutingMode,
                @JsonProperty("uiRouting_target") String uiRoutingTarget,
                @JsonProperty("uiRouting_custom") JsonNode uiRoutingCustom,
                @JsonProperty("barcodeSearchAttr") String barcodeSearchAttr,
                @JsonProperty("barcodeSearchRegex") String barcodeSearchRegex,
                @JsonProperty("formStructure") JsonNode formStructure,
                @JsonProperty("icon_font") String iconFont) {
            this.name = checkNotBlank(name, "class name cannot be blank");
            this.description = description;
            this.defaultFilter = defaultFilter;
            this.defaultImportTemplate = defaultImportTemplate;
            this.defaultExportTemplate = defaultExportTemplate;
            this.validationRule = validationRule;
            this.type = parseEnumOrDefault(type, CT_STANDARD);
            this.allowedExtensions = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(Strings.nullToEmpty(allowedExtensions));
            this.checkCount = parseEnumOrNull(checkCount, DmsAttachmentCountCheck.class);
            this.checkCountNumber = checkCountNumber;
            this.maxFileSize = maxFileSize;
            this.messageAttr = messageAttr;
            this.flowStatusAttr = flowStatusAttr;
            this.flowProvider = engine;
            this.parentId = parentId;
            this.isActive = firstNotNull(isActive, true);
            this.isSuperclass = firstNotNull(isSuperclass, false);
            this.noteInline = noteInline;
            this.noteInlineClosed = noteInlineClosed;
            this.noteInGrid = noteInGrid;
            this.enableSaveButton = enableSaveButton;
            this.stoppableByUser = stoppableByUser;
            this.attachmentsInline = attachmentsInline;
            this.attachmentsInlineClosed = attachmentsInlineClosed;
            this.dmsCategory = dmsCategory;
            this.defaultOrder = nullToEmpty(defaultOrder);
            this.formTriggers = nullToEmpty(formTriggers);
            this.contextMenuItems = nullToEmpty(contextMenuItems);
            this.widgets = nullToEmpty(widgets);
            this.multitenantMode = isBlank(multitenantMode) ? null : parseClassMultitenantMode(multitenantMode);
            this.attributeGroups = nullToEmpty(attributeGroups);
            this.domainOrder = nullToEmpty(domainOrder);
            this.iconId = iconId;
            this.formStructure = formStructure;
            this.help = help;
            this.autoValue = autoValue;
            this.uiRoutingMode = parseEnumOrNull(uiRoutingMode, ClassUiRoutingMode.class);
            this.uiRoutingTarget = uiRoutingTarget;
            this.uiRoutingCustom = applyOrNull(uiRoutingCustom, CmJsonUtils::toJson);
            this.barcodeSearchAttr = barcodeSearchAttr;
            this.barcodeSearchRegex = barcodeSearchRegex;
            this.iconFont = iconFont;
        }

        public Consumer<ClassMetadataImpl.ClassMetadataImplBuilder> metadataFillerForClassDataCreate(boolean sanitizeHtml) {
            return (b) -> b
                    .withSuperclass(isSuperclass)
                    .withClassType(type)
                    .accept(metadataFillerForClassDataUpdate(sanitizeHtml));
        }

        public Consumer<ClassMetadataImpl.ClassMetadataImplBuilder> metadataFillerForClassDataUpdate(boolean sanitizeHtml) {
            return (b) -> b.withActive(isActive)
                    .withDescription(description)
                    .withIsUserStoppable(stoppableByUser)
                    .withIsFlowSaveButtonEnabled(enableSaveButton)
                    .withDmsCategory(dmsCategory)
                    .withDefaultFilter(defaultFilter)
                    .withDefaultImportTemplate(defaultImportTemplate)
                    .withDefaultExportTemplate(defaultExportTemplate)
                    .withNoteInline(noteInline)
                    .withNoteInlineClosed(noteInlineClosed)
                    .withNoteInGrid(noteInGrid)
                    .withAttachmentsInline(attachmentsInline)
                    .withAttachmentsInlineClosed(attachmentsInlineClosed)
                    .withValidationRule(validationRule)
                    .withMultitenantMode(multitenantMode)
                    .withFlowStatusAttr(flowStatusAttr)
                    .withFlowProvider(flowProvider)
                    .withMessageAttr(messageAttr)
                    .withDomainOrder(domainOrder)
                    .withDmsAllowedExtensions(allowedExtensions)
                    .withDmsCountCheck(checkCount)
                    .withDmsCountCheckNumber(checkCountNumber)
                    .withMaxFileSize(maxFileSize)
                    .withHelpMessage(sanitizeHtml ? sanitizeHtml(help) : help)
                    .withAutoValue(autoValue)
                    .withUiRoutingMode(uiRoutingMode)
                    .withUiRoutingTarget(uiRoutingTarget)
                    .withUiRoutingCustom(uiRoutingCustom)
                    .withBarcodeSearchAttr(barcodeSearchAttr)
                    .withBarcodeSearchRegex(barcodeSearchRegex)
                    .withIconFont(iconFont);
        }

        @Override
        public String toString() {
            return "WsClassData{" + "name=" + name + ", description=" + description + ", type=" + type + ", parentId=" + parentId + ", isActive=" + isActive + ", isSuperclass=" + isSuperclass + '}';
        }

        public static class WsClassDataDefaultOrder {

            public final String attribute;
            public final String direction;

            public WsClassDataDefaultOrder(@JsonProperty("attribute") String attribute, @JsonProperty("direction") String direction) {
                this.attribute = attribute;
                this.direction = direction;
            }
        }

        public static class WsClassDataFormTrigger {

            public final String script;
            public final boolean active;
            public final boolean beforeView;
            public final boolean beforeInsert;
            public final boolean beforeEdit;
            public final boolean beforeClone;
            public final boolean afterInsert;
            public final boolean afterEdit;
            public final boolean afterClone;
            public final boolean afterDelete;
            public final boolean afterInsertExecute;
            public final boolean afterEditExecute;

            public WsClassDataFormTrigger(
                    @JsonProperty("script") String script,
                    @JsonProperty("active") Boolean active,
                    @JsonProperty("beforeView") Boolean beforeView,
                    @JsonProperty("beforeInsert") Boolean beforeInsert,
                    @JsonProperty("beforeEdit") Boolean beforeEdit,
                    @JsonProperty("beforeClone") Boolean beforeClone,
                    @JsonProperty("afterInsert") Boolean afterInsert,
                    @JsonProperty("afterEdit") Boolean afterEdit,
                    @JsonProperty("afterClone") Boolean afterClone,
                    @JsonProperty("afterDelete") Boolean afterDelete,
                    @JsonProperty("afterInsertExecute") Boolean afterInsertExecute,
                    @JsonProperty("afterEditExecute") Boolean afterEditExecute) {
                this.script = script;
                this.active = active;
                this.beforeView = beforeView;
                this.beforeInsert = beforeInsert;
                this.beforeEdit = beforeEdit;
                this.beforeClone = beforeClone;
                this.afterInsert = afterInsert;
                this.afterEdit = afterEdit;
                this.afterClone = afterClone;
                this.afterDelete = afterDelete;
                this.afterInsertExecute = afterInsertExecute;
                this.afterEditExecute = afterEditExecute;
            }
        }

        public static class WsClassDataWidget {

            private final String label, type, config;
            private final Boolean active;

            public WsClassDataWidget(
                    @JsonProperty("_label") String label,
                    @JsonProperty("_type") String type,
                    @JsonProperty("_config") String config,
                    @JsonProperty("_active") Boolean active) {
                this.label = label;
                this.type = type;
                this.config = config;
                this.active = active;
            }
        }

        public static class WsClassDataAttributeGroup {

            protected final String name, description, defaultDisplayMode;

            public WsClassDataAttributeGroup(
                    @JsonProperty("name") String name,
                    @JsonProperty("description") String description,
                    @JsonProperty(ATTRIBUTE_GROUP_DEFAULT_DISPLAY_MODE) String defaultDisplayMode) {//TODO improve this (??)
                this.name = name;
                this.description = description;
                this.defaultDisplayMode = defaultDisplayMode;
            }

        }
    }
}
