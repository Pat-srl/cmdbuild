/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.filterKeys;
import jakarta.annotation.Nullable;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.common.beans.CardIdAndClassNameUtils.parseTypeAndCode;
import org.cmdbuild.common.beans.TypeAndCode;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_ICON;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_DEFAULT;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DEFAULT_FILTER;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DMS_CATEGORY;
import static org.cmdbuild.dao.entrytype.ClassMetadata.NOTE_INLINE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.NOTE_INLINE_CLOSED;
import static org.cmdbuild.dao.entrytype.ClassMetadata.PROCESS_ENGINE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.SUPERCLASS;
import static org.cmdbuild.dao.entrytype.ClassMetadata.USER_STOPPABLE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.VALIDATION_RULE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.WORKFLOW_ENABLE_SAVE_BUTTON;
import static org.cmdbuild.dao.entrytype.ClassMetadata.WORKFLOW_PROVIDER;
import static org.cmdbuild.dao.entrytype.ClassMetadata.WORKFLOW_STATUS_ATTR;
import org.cmdbuild.dao.entrytype.ClassMultitenantMode;
import static org.cmdbuild.dao.entrytype.ClassMultitenantMode.CMM_NEVER;
import org.cmdbuild.dao.entrytype.ClassMultitenantModeUtils;
import static org.cmdbuild.dao.entrytype.ClassMultitenantModeUtils.serializeClassMultitenantMode;
import org.cmdbuild.dao.entrytype.ClassPermissionMode;
import org.cmdbuild.dao.entrytype.ClassType;
import static org.cmdbuild.dao.entrytype.ClassType.CT_SIMPLE;
import static org.cmdbuild.dao.entrytype.ClassType.CT_STANDARD;
import org.cmdbuild.dao.entrytype.ClassUiRoutingMode;
import static org.cmdbuild.dao.entrytype.ClassUiRoutingMode.CURM_CUSTOM;
import static org.cmdbuild.dao.entrytype.ClassUiRoutingMode.CURM_CUSTOMPAGE;
import static org.cmdbuild.dao.entrytype.ClassUiRoutingMode.CURM_DEFAULT;
import static org.cmdbuild.dao.entrytype.ClassUiRoutingMode.CURM_VIEW;
import org.cmdbuild.lookup.DmsAttachmentCountCheck;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class ClassMetadataImpl extends EntryTypeMetadataImpl implements ClassMetadata {

    private static final BiMap<String, ClassType> CLASS_META_TO_CLASS_TYPE = ImmutableBiMap.of(CLASS_TYPE_SIMPLE, CT_SIMPLE, CLASS_TYPE_STANDARD, CT_STANDARD);

    private static final Set<String> CLASS_METADATA_KEYS = set(WORKFLOW_PROVIDER, SUPERCLASS, CLASS_TYPE, USER_STOPPABLE, WORKFLOW_STATUS_ATTR,
            WORKFLOW_ENABLE_SAVE_BUTTON, DMS_CATEGORY,
            DEFAULT_FILTER, NOTE_INLINE, NOTE_INLINE_CLOSED, NOTE_INGRID, ATTACHMENTS_INLINE, ATTACHMENTS_INLINE_CLOSED, VALIDATION_RULE, PROCESS_ENGINE, MULTITENANT_MODE,
            DEFAULT_IMPORT_TEMPLATE, DEFAULT_EXPORT_TEMPLATE, DOMAIN_ORDER, CLASS_SPECIALITY).immutable();

    private final boolean isSuperclass, isUserStoppable, noteInline, noteInlineClosed, noteInGrid, attachmentsInline, attachmentsInlineClosed;
    private final Boolean isFlowSaveButtonEnabled;
    private final String helpMessage, autoValue, flowStatusAttr, messageAttr, dmsCategory, validationRule, flowProvider, uiRoutingTarget, uiRoutingCustom, barcodeSearchAttr, barcodeSearchRegex, iconFont;
    private final ClassUiRoutingMode uiRoutingMode;
    private final Long defaultFilter;
    private final ClassType classType;
    private final ClassMultitenantMode multitenantMode;
    private final List<String> domainOrder;
    private final Set<String> allowedExtensions;
    private final Integer maxFileSize;
    private final DmsAttachmentCountCheck countCheck;
    private final Integer countCheckNumber;
    private final ClassSpeciality classSpeciality;
    private final TypeAndCode defaultImportTemplate, defaultExportTemplate;

    public ClassMetadataImpl(Map<String, String> map) {
        super(map, filterKeys(map, not(CLASS_METADATA_KEYS::contains)));
        isSuperclass = toBooleanOrDefault(map.get(SUPERCLASS), false);
        classType = checkNotNull(CLASS_META_TO_CLASS_TYPE.get(firstNotBlank(map.get(CLASS_TYPE), CLASS_TYPE_STANDARD).toLowerCase()), "unsupported class type =< %s >", map.get(CLASS_TYPE));
        isUserStoppable = toBooleanOrDefault(map.get(USER_STOPPABLE), false);
        flowStatusAttr = emptyToNull(map.get(WORKFLOW_STATUS_ATTR));
        messageAttr = emptyToNull(map.get(WORKFLOW_MESSAGE_ATTR));
        isFlowSaveButtonEnabled = toBooleanOrNull(map.get(WORKFLOW_ENABLE_SAVE_BUTTON));
        dmsCategory = emptyToNull(map.get(DMS_CATEGORY));
        noteInline = toBooleanOrDefault(map.get(NOTE_INLINE), false);
        noteInlineClosed = toBooleanOrDefault(map.get(NOTE_INLINE_CLOSED), false);
        noteInGrid = toBooleanOrDefault(map.get(NOTE_INGRID), false);
        attachmentsInline = toBooleanOrDefault(map.get(ATTACHMENTS_INLINE), false);
        attachmentsInlineClosed = toBooleanOrDefault(map.get(ATTACHMENTS_INLINE_CLOSED), true);
        defaultFilter = toLongOrNull(map.get(DEFAULT_FILTER));
        defaultImportTemplate = parseTypeAndCode(toStringOrNull(map.get(DEFAULT_IMPORT_TEMPLATE)), TEMPLATE_TYPE_TEMPLATE);
        checkArgument(defaultImportTemplate == null || defaultImportTemplate.hasType(TEMPLATE_TYPE_TEMPLATE, TEMPLATE_TYPE_GATE), "invalid import template class = %s", defaultImportTemplate);
        defaultExportTemplate = parseTypeAndCode(toStringOrNull(map.get(DEFAULT_EXPORT_TEMPLATE)), TEMPLATE_TYPE_TEMPLATE);
//        checkArgument(defaultExportTemplate == null || defaultExportTemplate.hasType(TEMPLATE_TYPE_TEMPLATE, TEMPLATE_TYPE_GATE), "invalid export template class = %s", defaultExportTemplate);
        checkArgument(defaultExportTemplate == null || defaultExportTemplate.hasType(TEMPLATE_TYPE_TEMPLATE), "invalid export template class = %s", defaultExportTemplate);
        validationRule = emptyToNull(map.get(VALIDATION_RULE));
        flowProvider = trimToNull(map.get(WORKFLOW_PROVIDER));
//        checkArgument(isBlank(flowProvider) || equal(flowProvider, RIVER), "invalid process engine =< %s >", flowProvider);
        multitenantMode = Optional.ofNullable(trimToNull(map.get(MULTITENANT_MODE))).map(ClassMultitenantModeUtils::parseClassMultitenantMode).orElse(CMM_NEVER);
        domainOrder = isBlank(map.get(DOMAIN_ORDER)) ? emptyList() : Splitter.on(",").trimResults().omitEmptyStrings().splitToList(map.get(DOMAIN_ORDER)).stream().distinct().collect(toImmutableList());
        classSpeciality = parseEnumOrDefault(map.get(CLASS_SPECIALITY), CS_DEFAULT);
        allowedExtensions = ImmutableSet.copyOf(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(nullToEmpty(map.get(DMS_ALLOWED_EXTENSIONS))));
        countCheck = parseEnumOrNull(map.get(DMS_CHECK_COUNT), DmsAttachmentCountCheck.class);
        countCheckNumber = toIntegerOrNull(map.get(DMS_CHECK_COUNT_NUMBER));
        maxFileSize = toIntegerOrNull(map.get(DMS_MAX_FILE_SIZE));
        helpMessage = map.get(HELP_MESSAGE);
        autoValue = map.get(AUTO_VALUE);
        uiRoutingMode = parseEnumOrDefault(map.get(UI_ROUTING_MODE), CURM_DEFAULT);
        switch (uiRoutingMode) {
            case CURM_DEFAULT -> {
                uiRoutingTarget = null;
                uiRoutingCustom = null;
            }
            case CURM_CUSTOMPAGE, CURM_VIEW -> {
                uiRoutingTarget = checkNotBlank(map.get(UI_ROUTING_TARGET), "missing ui routing target");
                uiRoutingCustom = null;
            }
            case CURM_CUSTOM -> {
                uiRoutingTarget = null;
                uiRoutingCustom = firstNotBlank(map.get(UI_ROUTING_CUSTOM));
                fromJson(uiRoutingCustom, MAP_OF_STRINGS);
            }
            default ->
                throw unsupported("unsupported ui routing mode = %s", uiRoutingMode);
        }
        barcodeSearchAttr = map.get(BARCODE_SEARCH_ATTR);
        barcodeSearchRegex = map.get(BARCODE_SEARCH_REGEX);
        iconFont = map.get(ICON_FONT);
    }

    public ClassMetadataImpl() {
        this(emptyMap());
    }

    @Override
    @Nullable
    public String getBarcodeSearchAttr() {
        return barcodeSearchAttr;
    }

    @Override
    @Nullable
    public String getBarcodeSearchRegex() {
        return barcodeSearchRegex;
    }

    @Override
    @Nullable
    public String getIconFont() {
        return iconFont;
    }

    @Override
    @Nullable
    public String getUiRoutingTarget() {
        return uiRoutingTarget;
    }

    @Nullable
    @Override
    public String getUiRoutingCustom() {
        return uiRoutingCustom;
    }

    @Override
    public ClassUiRoutingMode getUiRoutingMode() {
        return uiRoutingMode;
    }

    @Override
    public ClassSpeciality getClassSpeciality() {
        return classSpeciality;
    }

    @Nullable
    @Override
    public String getHelpMessage() {
        return helpMessage;
    }

    @Nullable
    @Override
    public String getAutoValue() {
        return autoValue;
    }

    @Override
    public Set<String> getDmsAllowedExtensions() {
        return allowedExtensions;
    }

    @Override
    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    @Override
    @Nullable
    public DmsAttachmentCountCheck getDmsCheckCount() {
        return countCheck;
    }

    @Override
    @Nullable
    public Integer getDmsCheckCountNumber() {
        return countCheckNumber;
    }

    @Override
    public List<String> getDomainOrder() {
        return domainOrder;
    }

//    @Override
//    public boolean isProcess() {
//        return isProcess;
//    }
    @Override
    public boolean isSuperclass() {
        return isSuperclass;
    }

    @Override
    public ClassType getClassType() {
        return classType;
    }

    @Override
    public boolean isWfUserStoppable() {
        return isUserStoppable;
    }

    @Override
    @Nullable
    public String getFlowStatusAttr() {
        return flowStatusAttr;
    }

    @Override
    @Nullable
    public String getMessageAttr() {
        return messageAttr;
    }

    @Override
    @Nullable
    public Boolean isFlowSaveButtonEnabled() {
        return isFlowSaveButtonEnabled;
    }

    @Override
    @Nullable
    public String getDmsCategoryOrNull() {
        return dmsCategory;
    }

    @Override
    public boolean getNoteInline() {
        return noteInline;
    }

    @Override
    public boolean getNoteInlineClosed() {
        return noteInlineClosed;
    }

    @Override
    public boolean getNoteInGrid() {
        return noteInGrid;
    }

    @Override
    public boolean getAttachmentsInline() {
        return attachmentsInline;
    }

    @Override
    public boolean getAttachmentsInlineClosed() {
        return attachmentsInlineClosed;
    }

    @Override
    @Nullable
    public Long getDefaultFilterOrNull() {
        return defaultFilter;
    }

    @Override
    @Nullable
    public TypeAndCode getDefaultImportTemplateOrNull() {
        return defaultImportTemplate;
    }

    @Override
    @Nullable
    public TypeAndCode getDefaultExportTemplateOrNull() {
        return defaultExportTemplate;
    }

    @Override
    @Nullable
    public String getValidationRuleOrNull() {
        return validationRule;
    }

    @Override
    @Nullable
    public String getFlowProviderOrNull() {
        return flowProvider;
    }

    @Override
    public ClassMultitenantMode getMultitenantMode() {
        return multitenantMode;
    }

    public static ClassMetadataImplBuilder builder() {
        return new ClassMetadataImplBuilder();
    }

    public static ClassMetadataImplBuilder copyOf(ClassMetadata source) {
        return new ClassMetadataImplBuilder(source.getAll());
    }

    public static class ClassMetadataImplBuilder implements Builder<ClassMetadataImpl, ClassMetadataImplBuilder> {

        private final Map<String, String> metadata = map();

        public ClassMetadataImplBuilder() {
        }

        public ClassMetadataImplBuilder(Map map) {
            this.metadata.putAll(map);
        }

        private ClassMetadataImplBuilder with(String key, @Nullable Object value) {
            metadata.put(key, toStringOrNull(value));
            return this;
        }

        public ClassMetadataImplBuilder withBarcodeSearchAttr(String value) {
            return with(BARCODE_SEARCH_ATTR, value);
        }

        public ClassMetadataImplBuilder withBarcodeSearchRegex(String value) {
            return with(BARCODE_SEARCH_REGEX, value);
        }

        public ClassMetadataImplBuilder withUiRoutingMode(ClassUiRoutingMode routingMode) {
            return this.with(UI_ROUTING_MODE, serializeEnum(routingMode));
        }

        public ClassMetadataImplBuilder withUiRoutingTarget(String uiRoutingTarget) {
            return this.with(UI_ROUTING_TARGET, uiRoutingTarget);
        }

        public ClassMetadataImplBuilder withUiRoutingCustom(String uiRoutingCustom) {
            return this.with(UI_ROUTING_CUSTOM, uiRoutingCustom);
        }

        public ClassMetadataImplBuilder withDescription(String description) {
            return this.with(DESCRIPTION, description);
        }

        public ClassMetadataImplBuilder withActive(Boolean isActive) {
            return this.with(ACTIVE, isActive);
        }

        public ClassMetadataImplBuilder withIconFont(String iconFont) {
            return this.with(ICON_FONT, iconFont);
        }

        public ClassMetadataImplBuilder withMode(ClassPermissionMode mode) {
            return this.with(ENTRY_TYPE_MODE, mode.name().toLowerCase());
        }

        public ClassMetadataImplBuilder withSuperclass(Boolean isSuperclass) {
            return this.with(SUPERCLASS, isSuperclass);
        }

        public ClassMetadataImplBuilder withClassType(ClassType classType) {
            return this.with(CLASS_TYPE, checkNotNull(CLASS_META_TO_CLASS_TYPE.inverse().get(checkNotNull(classType)), "unsupported class type = %s", classType));
        }

        public ClassMetadataImplBuilder withIsUserStoppable(Boolean isUserStoppable) {
            return this.with(USER_STOPPABLE, isUserStoppable);
        }

        public ClassMetadataImplBuilder withIsFlowSaveButtonEnabled(Boolean isFlowSaveButtonEnabled) {
            return this.with(WORKFLOW_ENABLE_SAVE_BUTTON, isFlowSaveButtonEnabled);
        }

        public ClassMetadataImplBuilder withNoteInline(Boolean noteInline) {
            return this.with(NOTE_INLINE, noteInline);
        }

        public ClassMetadataImplBuilder withNoteInlineClosed(Boolean noteInlineClosed) {
            return this.with(NOTE_INLINE_CLOSED, noteInlineClosed);
        }

        public ClassMetadataImplBuilder withNoteInGrid(Boolean noteInGrid) {
            return this.with(NOTE_INGRID, noteInGrid);
        }

        public ClassMetadataImplBuilder withAttachmentsInline(Boolean attachmentsInline) {
            return this.with(ATTACHMENTS_INLINE, attachmentsInline);
        }

        public ClassMetadataImplBuilder withAttachmentsInlineClosed(Boolean attachmentsInlineClosed) {
            return this.with(ATTACHMENTS_INLINE_CLOSED, attachmentsInlineClosed);
        }

        public ClassMetadataImplBuilder withFlowStatusAttr(String flowStatusAttr) {
            return this.with(WORKFLOW_STATUS_ATTR, flowStatusAttr);
        }

        public ClassMetadataImplBuilder withMessageAttr(String messageAttr) {
            return this.with(WORKFLOW_MESSAGE_ATTR, messageAttr);
        }

        public ClassMetadataImplBuilder withDmsCategory(String dmsCategory) {
            return this.with(DMS_CATEGORY, dmsCategory);
        }

        public ClassMetadataImplBuilder withDefaultFilter(Long defaultFilter) {
            return this.with(DEFAULT_FILTER, defaultFilter);
        }

        public ClassMetadataImplBuilder withIconPath(String iconPath) {
            return this.with(CLASS_ICON, iconPath);
        }

        public ClassMetadataImplBuilder withDefaultImportTemplate(String defaultImportTemplate) {
            return this.with(DEFAULT_IMPORT_TEMPLATE, defaultImportTemplate);
        }

        public ClassMetadataImplBuilder withDefaultExportTemplate(String defaultExportTemplate) {
            return this.with(DEFAULT_EXPORT_TEMPLATE, defaultExportTemplate);
        }

        public ClassMetadataImplBuilder withValidationRule(String validationRule) {
            return this.with(VALIDATION_RULE, validationRule);
        }

        public ClassMetadataImplBuilder withMultitenantMode(@Nullable ClassMultitenantMode multitenantMode) {
            return this.with(MULTITENANT_MODE, multitenantMode == null ? null : serializeClassMultitenantMode(multitenantMode));
        }

        public ClassMetadataImplBuilder withFlowProvider(String flowProvider) {
            return this.with(WORKFLOW_PROVIDER, flowProvider);
        }

        public ClassMetadataImplBuilder withDmsAllowedExtensions(@Nullable Collection<String> allowedExtensions) {
            return this.with(DMS_ALLOWED_EXTENSIONS, allowedExtensions == null ? null : Joiner.on(",").join(allowedExtensions));
        }

        public ClassMetadataImplBuilder withDmsCountCheck(@Nullable DmsAttachmentCountCheck countCheck) {
            return this.with(DMS_CHECK_COUNT, serializeEnum(countCheck));
        }

        public ClassMetadataImplBuilder withDmsCountCheckNumber(@Nullable Integer countCheckNumber) {
            return this.with(DMS_CHECK_COUNT_NUMBER, toStringOrNull(countCheckNumber));
        }

        public ClassMetadataImplBuilder withMaxFileSize(@Nullable Integer maxFileSize) {
            return this.with(DMS_MAX_FILE_SIZE, toIntegerOrNull(maxFileSize));
        }

        public ClassMetadataImplBuilder withHelpMessage(String value) {
            return this.with(HELP_MESSAGE, value);
        }

        public ClassMetadataImplBuilder withAutoValue(String value) {
            return this.with(AUTO_VALUE, value);
        }

        public ClassMetadataImplBuilder withDomainOrder(@Nullable List<String> domainOrder) {
            return this.with(DOMAIN_ORDER, domainOrder == null || domainOrder.isEmpty() ? null : Joiner.on(",").join(domainOrder));
        }

        public ClassMetadataImplBuilder withOther(Map other) {
            metadata.putAll(map(other).withoutKeys(CLASS_METADATA_KEYS::contains));
            return this;
        }

        @Override
        public ClassMetadataImpl build() {
            return new ClassMetadataImpl(metadata);
        }

    }
}
