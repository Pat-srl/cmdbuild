/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.filterKeys;
import jakarta.annotation.Nullable;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.entrytype.AttrEditorType;
import static org.cmdbuild.dao.entrytype.AttrEditorType.ET_HTML;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.ShowPassword.SP_ALWAYS;
import org.cmdbuild.dao.entrytype.AttributePermission;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_DEFAULT;
import org.cmdbuild.dao.entrytype.AttributePermissions;
import org.cmdbuild.dao.entrytype.AttributePermissionsImpl;
import org.cmdbuild.dao.entrytype.CascadeAction;
import static org.cmdbuild.dao.entrytype.CascadeAction.CA_RESTRICT;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.getDefaultPermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.parseAttributePermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializeAttributePermissionMode;
import static org.cmdbuild.dao.entrytype.EntryTypeMetadata.ENTRY_TYPE_MODE;
import static org.cmdbuild.dao.entrytype.EntryTypeMetadata.PERMISSIONS;
import org.cmdbuild.dao.entrytype.PermissionScope;
import org.cmdbuild.dao.entrytype.TextContentSecurity;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_SAFE;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_PLAINTEXT;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FileAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LinkAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeLanguage;
import static org.cmdbuild.dao.entrytype.attributetype.TextAttributeLanguage.TAL_HTML;
import static org.cmdbuild.dao.entrytype.attributetype.TextAttributeLanguage.TAL_MARKDOWN;
import static org.cmdbuild.dao.entrytype.attributetype.TextAttributeLanguage.TAL_PLAINTEXT;
import org.cmdbuild.dao.entrytype.attributetype.UnitOfMeasureLocation;
import static org.cmdbuild.dao.entrytype.attributetype.UnitOfMeasureLocation.UML_AFTER;
import static org.cmdbuild.dao.entrytype.attributetype.UnitOfMeasureLocationUtils.serializeUnitOfMeasureLocation;
import static org.cmdbuild.dao.utils.RelationDirectionUtils.serializeRelationDirection;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnumUpper;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class AttributeMetadataImpl extends AbstractMetadataImpl implements AttributeMetadata {

    private static final Set<String> ATTRIBUTE_METADATA_KEYS = ImmutableSet.of(ENTRY_TYPE_MODE, PERMISSIONS, LOOKUP_TYPE, REFERENCE_DOMAIN, REFERENCE_DIRECTION, BASEDSP, MANDATORY, UNIQUE, FK_TARGET_CLASS, CASCADE,
            INHERITED, INDEX, DEFAULT, GROUP, CLASSORDER, EDITOR_TYPE, FILTER, USE_DOMAIN_FILTER, SHOW_IN_REDUCED_GRID, DOMAINKEY, GISATTR,
            FORMAT_PATTERN, PRESELECT_IF_UNIQUE, UNIT_OF_MEASURE, UNIT_OF_MEASURE_LOCATION, VISIBLE_DECIMALS, SHOW_SEPARATORS,
            HELP_MESSAGE, SHOW_IF_EXPR, VALIDATION_RULES_EXPR, AUTO_VALUE_EXPR, SHOW_THOUSANDS_SEPARATOR, SHOW_SECONDS, UI_ALIAS, TEXT_LANGUAGE,
            PASSWORD, TEXT_CONTENT_SECURITY, ITEMS, SHOW_LABEL, LABEL_REQUIRED, SHOW_PREVIEW, DMS_CATEGORY, FORMULA_TYPE, FORMULA_CODE, SHOW_PASSWORD,
            LINK, ENCRYPT_ON_DB, VIRTUAL, MOBILE_EDITOR, MOBILE_EDITOR_REGEX, UI_SORTABLE, TRIM_ENABLED, ANONYMIZABLE);

    private static final AttributeMetadataImpl EMPTY_INSTANCE = new AttributeMetadataImpl();

    private final boolean isVirtual, encryptOnDb, isLink, showPreview, showLabel, labelRequired, showInGrid, showInReducedGrid, isMandatory, isUnique, isInherited, preselectIfUnique, showSeparators, domainKey, showThousandsSeparator, showSeconds, isMasterDetail, isPassword, isMultiline, helpAlwaysVisible, hideInGrid, hideInFilter, isSortable, useDomainFilter, isTrimEnabled, isAnonymizable, isShowOnlyIconIfPresent;

    private final String mobileEditorRegex, syncToDmsAttr, formulaCode, dmsCategory, uiAlias, lookupType, domain, foreignKeyDestinationClassName, defaultValue, group, filter, unitOfMeasure, formatPattern, helpMessage, showIfExpr, validationRulesExpr, autoValueExpr, masterDetailDescription;
    private final MobileEditor mobileEditor;
    private final int index, classOrder;
    private final Integer visibleDecimals, maxLength;
    private final AttributePermissionMode mode;
    private final AttributePermissions permissions;
    private final RelationDirection direction;
    private final UnitOfMeasureLocation unitOfMeasureLocation;
    private final CascadeAction cascadeAction;
    private final TextContentSecurity textContentSecurity;
    private final AttrEditorType editorType;
    private final TextAttributeLanguage textAttributeLanguage;
    private final String gisAttr;
    private final FormulaType formulaType;
    private final ShowPassword showPassword;
    private final List<String> items;

    public AttributeMetadataImpl(Map<String, String> map) {
        super(map, filterKeys(map, not(ATTRIBUTE_METADATA_KEYS::contains)));
        lookupType = defaultIfBlank(map.get(LOOKUP_TYPE), null);
        domain = defaultIfBlank(map.get(REFERENCE_DOMAIN), null);
        direction = parseEnumOrNull(map.get(REFERENCE_DIRECTION), RelationDirection.class);
        showInGrid = toBooleanOrDefault(map.get(BASEDSP), false);
        showLabel = toBooleanOrDefault(map.get(SHOW_LABEL), true);
        labelRequired = toBooleanOrDefault(map.get(LABEL_REQUIRED), false);
        showPreview = toBooleanOrDefault(map.get(SHOW_PREVIEW), false);
        isLink = toBooleanOrDefault(map.get(LINK), false);
        showInReducedGrid = toBooleanOrDefault(map.get(SHOW_IN_REDUCED_GRID), false);
        helpAlwaysVisible = toBooleanOrDefault(map.get(HELP_ALWAYS_VISIBLE), false);
        isMandatory = toBooleanOrDefault(map.get(MANDATORY), false);
        isUnique = toBooleanOrDefault(map.get(UNIQUE), false);
        foreignKeyDestinationClassName = map.get(FK_TARGET_CLASS);
        cascadeAction = parseEnumOrDefault(map.get(CASCADE), CA_RESTRICT);
        mode = parseEnumOrDefault(map.get(ENTRY_TYPE_MODE), APM_DEFAULT);
        isInherited = toBooleanOrDefault(map.get(INHERITED), false);
        domainKey = toBooleanOrDefault(map.get(DOMAINKEY), false);
        isMultiline = toBooleanOrDefault(map.get(MULTILINE), false);
        gisAttr = map.get(GISATTR);
        index = toIntegerOrDefault(map.get(INDEX), -1);
        defaultValue = map.get(DEFAULT);
        group = map.get(GROUP);
        dmsCategory = map.get(DMS_CATEGORY);
        formulaType = parseEnumOrNull(map.get(FORMULA_TYPE), FormulaType.class);
        formulaCode = map.get(FORMULA_CODE);
        showPassword = parseEnumOrDefault(map.get(SHOW_PASSWORD), SP_ALWAYS);
        classOrder = toIntegerOrDefault(map.get(CLASSORDER), 0);
        maxLength = toIntegerOrNull(map.get(LENGTH));
        filter = map.get(FILTER);
        useDomainFilter = toBooleanOrDefault(map.get(USE_DOMAIN_FILTER), false);
        hideInFilter = toBooleanOrDefault(map.get(HIDE_IN_FILTER), false);
        hideInGrid = toBooleanOrDefault(map.get(HIDE_IN_GRID), false);
        if (isNotBlank(map.get(PERMISSIONS))) {
            permissions = AttributePermissionsImpl
                    .copyOf(getDefaultPermissions(mode))
                    .addPermissions(parseAttributePermissions(map.get(PERMISSIONS)))
                    .build();
        } else {
            permissions = getDefaultPermissions(mode);
        }
        formatPattern = map.get(FORMAT_PATTERN);
        unitOfMeasure = map.get(UNIT_OF_MEASURE);
        checkArgument(nullToEmpty(unitOfMeasure).length() <= 10, "unit of measure param can be at most 10 chars long");
        preselectIfUnique = toBooleanOrDefault(map.get(PRESELECT_IF_UNIQUE), false);
        showSeparators = toBooleanOrDefault(map.get(SHOW_SEPARATORS), true);
        visibleDecimals = toIntegerOrNull(map.get(VISIBLE_DECIMALS));
        unitOfMeasureLocation = parseEnumOrDefault(map.get(UNIT_OF_MEASURE_LOCATION), UML_AFTER);
        helpMessage = map.get(HELP_MESSAGE);
        showIfExpr = map.get(SHOW_IF_EXPR);
        validationRulesExpr = map.get(VALIDATION_RULES_EXPR);
        autoValueExpr = map.get(AUTO_VALUE_EXPR);
        showThousandsSeparator = toBooleanOrDefault(map.get(SHOW_THOUSANDS_SEPARATOR), false);
        showSeconds = toBooleanOrDefault(map.get(SHOW_SECONDS), false);
        masterDetailDescription = map.get(MASTER_DETAIL_DESCRIPTION);
        isMasterDetail = toBooleanOrDefault(map.get(IS_MASTER_DETAIL), false);
        uiAlias = map.get(UI_ALIAS);
        editorType = parseEnumOrNull(map.get(EDITOR_TYPE), AttrEditorType.class);
        if (isNotBlank(map.get(TEXT_LANGUAGE))) {
            textAttributeLanguage = parseEnum(map.get(TEXT_LANGUAGE), TextAttributeLanguage.class);
        } else if (editorType != null) {
            textAttributeLanguage = switch (editorType) {
                case ET_HTML ->
                    TAL_HTML;
                case ET_MARKDOWN ->
                    TAL_MARKDOWN;
                case ET_PLAIN ->
                    TAL_PLAINTEXT;
                default ->
                    null;
            };
        } else {
            textAttributeLanguage = null;
        }
        isPassword = toBooleanOrDefault(map.get(PASSWORD), false);
        encryptOnDb = toBooleanOrDefault(map.get(ENCRYPT_ON_DB), isPassword);
        textContentSecurity = parseEnumOrDefault(map.get(TEXT_CONTENT_SECURITY), equal(editorType, ET_HTML) ? TCS_HTML_SAFE : TCS_PLAINTEXT);
        items = ImmutableList.copyOf(Splitter.on(Pattern.compile("[ ,]")).omitEmptyStrings().trimResults().splitToList(nullToEmpty(map.get(ITEMS))));
        syncToDmsAttr = map.get(SYNC_TO_DMS_ATTR);
        isVirtual = toBooleanOrDefault(map.get(VIRTUAL), false);
        if (isFile()) {
            checkNotBlank(dmsCategory, "dms category is required for file attribute type");
        }
        mobileEditorRegex = map.get(MOBILE_EDITOR_REGEX);
        mobileEditor = parseEnumOrNull(map.get(MOBILE_EDITOR), MobileEditor.class);
        isSortable = toBooleanOrDefault(map.get(UI_SORTABLE), true);
        isTrimEnabled = toBooleanOrDefault(map.get(TRIM_ENABLED), false);
        isAnonymizable = toBooleanOrDefault(map.get(ANONYMIZABLE), false);
        isShowOnlyIconIfPresent = toBooleanOrDefault(map.get(SHOW_ONLY_ICON_IF_PRESENT), false);
    }

    public AttributeMetadataImpl() {
        this(emptyMap());
    }

    @Override
    public boolean isTrimEnabled() {
        return isTrimEnabled;
    }

    @Override
    public boolean isAnonymizable() {
        return isAnonymizable;
    }

    @Override
    @Nullable
    public String getMobileEditorRegex() {
        return mobileEditorRegex;
    }

    @Override
    @Nullable
    public MobileEditor getMobileEditor() {
        return mobileEditor;
    }

    @Override
    public boolean isSortable() {
        return isSortable;
    }

    @Override
    public boolean isVirtual() {
        return isVirtual;
    }

    @Override
    public boolean encryptOnDb() {
        return encryptOnDb;
    }

    @Override
    public boolean isLink() {
        return isLink;
    }

    @Override
    public ShowPassword getShowPassword() {
        return showPassword;
    }

    @Override
    @Nullable
    public String getFormulaCode() {
        return formulaCode;
    }

    @Override
    @Nullable
    public FormulaType getFormulaType() {
        return formulaType;
    }

    @Override
    public boolean showPreview() {
        return showPreview;
    }

    @Override
    @Nullable
    public String getDmsCategory() {
        return dmsCategory;
    }

    @Override
    public boolean showLabel() {
        return showLabel;
    }

    @Override
    public boolean labelRequired() {
        return labelRequired;
    }

    @Override
    public boolean isMultiline() {
        return isMultiline;
    }

    @Override
    @Nullable
    public Integer getMaxLength() {
        return maxLength;
    }

    @Override
    public List<String> getItemTypes() {
        return items;
    }

    @Override
    @Nullable
    public String getGisAttr() {
        return gisAttr;
    }

    @Override
    @Nullable
    public String getSyncToDmsAttr() {
        return syncToDmsAttr;
    }

    @Override
    public TextContentSecurity getTextContentSecurity() {
        return textContentSecurity;
    }

    @Override
    public boolean isPassword() {
        return isPassword;
    }

    @Override
    public CascadeAction getCascadeAction() {
        return cascadeAction;
    }

    @Override
    @Nullable
    public TextAttributeLanguage getTextAttributeLanguage() {
        return textAttributeLanguage;
    }

    @Override
    @Nullable
    public String getUiAlias() {
        return uiAlias;
    }

    @Override
    public boolean showThousandsSeparator() {
        return showThousandsSeparator;
    }

    @Override
    public boolean showSeconds() {
        return showSeconds;
    }

    @Nullable
    @Override
    public String getHelpMessage() {
        return helpMessage;
    }

    @Nullable
    @Override
    public String getShowIfExpr() {
        return showIfExpr;
    }

    @Nullable
    @Override
    public String getValidationRulesExpr() {
        return validationRulesExpr;
    }

    @Nullable
    @Override
    public String getAutoValueExpr() {
        return autoValueExpr;
    }

    @Override
    public boolean preselectIfUnique() {
        return preselectIfUnique;
    }

    @Override
    public boolean showSeparators() {
        return showSeparators;
    }

    @Override
    public boolean helpAlwaysVisible() {
        return helpAlwaysVisible;
    }

    @Override
    public boolean hideInFilter() {
        return hideInFilter;
    }

    @Override
    public boolean hideInGrid() {
        return hideInGrid;
    }

    @Nullable
    @Override
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    @Nullable
    @Override
    public String getFormatPattern() {
        return formatPattern;
    }

    @Override
    @Nullable
    public String getMasterDetailDescription() {
        return masterDetailDescription;
    }

    @Override
    public boolean isMasterDetail() {
        return isMasterDetail;
    }

    @Nullable
    @Override
    public Integer getVisibleDecimals() {
        return visibleDecimals;
    }

    @Override
    public UnitOfMeasureLocation getUnitOfMeasureLocation() {
        return unitOfMeasureLocation;
    }

    @Override
    public Map<PermissionScope, Set<AttributePermission>> getPermissionMap() {
        return permissions.getPermissionMap();
    }

    @Override
    public boolean showInGrid() {
        return showInGrid;
    }

    @Override
    public boolean showInReducedGrid() {
        return showInReducedGrid;
    }

    @Override
    public boolean isMandatory() {
        return isMandatory;
    }

    @Override
    public boolean isUnique() {
        return isUnique;
    }

    @Override
    public boolean isInherited() {
        return isInherited;
    }

    @Override
    public String getLookupType() {
        return lookupType;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public RelationDirection getDirection() {
        return direction;
    }

    @Override
    public String getForeignKeyDestinationClassName() {
        return foreignKeyDestinationClassName;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    @Nullable
    public String getGroup() {
        return group;
    }

    @Override
    public AttrEditorType getEditorType() {
        return editorType;
    }

    @Override
    public boolean isDomainKey() {
        return domainKey;
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public boolean isUseDomainFilter() {
        return useDomainFilter;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getClassOrder() {
        return classOrder;
    }

    @Override
    public AttributePermissionMode getMode() {
        return mode;
    }

    @Nullable
    @Override
    public boolean isShowOnlyIconIfPresent() {
        return isShowOnlyIconIfPresent;
    }

    public static AttributeMetadata emptyAttributeMetadata() {
        return EMPTY_INSTANCE;
    }

    public static AttributeMetadataImplBuilder builder() {
        return new AttributeMetadataImplBuilder();
    }

    public static AttributeMetadataImplBuilder copyOf(AttributeMetadata source) {
        return builder().withMetadata(source.getAll());
    }

    public static class AttributeMetadataImplBuilder implements Builder<AttributeMetadataImpl, AttributeMetadataImplBuilder> {

        private final FluentMap<String, String> map = map();

        public AttributeMetadataImplBuilder withMetadata(String... meta) {
            map.putAll(map(meta));
            return this;
        }

        public AttributeMetadataImplBuilder withMetadata(Map<String, String> metadata) {
            map.putAll(metadata);
            return this;
        }

        public AttributeMetadataImplBuilder withSortable(Boolean value) {
            map.put(UI_SORTABLE, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withMobileEditor(MobileEditor mobileEditor) {
            map.put(MOBILE_EDITOR, serializeEnum(mobileEditor));
            return this;
        }

        public AttributeMetadataImplBuilder withMobileEditorRegex(String mobileEditorRegex) {
            map.put(MOBILE_EDITOR_REGEX, mobileEditorRegex);
            return this;
        }

        public AttributeMetadataImplBuilder withFormatPattern(String value) {
            map.put(FORMAT_PATTERN, value);
            return this;
        }

        public AttributeMetadataImplBuilder withPreselectIfUnique(Boolean value) {
            map.put(PRESELECT_IF_UNIQUE, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withUnitOfMeasure(String value) {
            map.put(UNIT_OF_MEASURE, value);
            return this;
        }

        public AttributeMetadataImplBuilder withUnitOfMeasureLocation(UnitOfMeasureLocation value) {
            map.put(UNIT_OF_MEASURE_LOCATION, value == null ? null : serializeUnitOfMeasureLocation(value));
            return this;
        }

        public AttributeMetadataImplBuilder withVisibleDecimal(Integer value) {
            map.put(VISIBLE_DECIMALS, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withShowSeparators(Boolean value) {
            map.put(SHOW_SEPARATORS, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withShowThousandsSeparators(Boolean value) {
            map.put(SHOW_THOUSANDS_SEPARATOR, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withShowSeconds(Boolean value) {
            map.put(SHOW_SECONDS, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withLink(Boolean value) {
            map.put(LINK, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withShowLabel(Boolean value) {
            map.put(SHOW_LABEL, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withLabelRequired(Boolean value) {
            map.put(LABEL_REQUIRED, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withHelpAlwaysVisible(Boolean value) {
            map.put(HELP_ALWAYS_VISIBLE, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withHideInFilter(Boolean value) {
            map.put(HIDE_IN_FILTER, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withHideInGrid(Boolean value) {
            map.put(HIDE_IN_GRID, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withShowPreview(Boolean value) {
            map.put(SHOW_PREVIEW, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withDmsCategory(String value) {
            map.put(DMS_CATEGORY, value);
            return this;
        }

        public AttributeMetadataImplBuilder withFormulaType(FormulaType value) {
            map.put(FORMULA_TYPE, serializeEnum(value));
            return this;
        }

        public AttributeMetadataImplBuilder withFormulaCode(String value) {
            map.put(FORMULA_CODE, value);
            return this;
        }

        public AttributeMetadataImplBuilder withShowPassword(ShowPassword value) {
            map.put(SHOW_PASSWORD, serializeEnum(value));
            return this;
        }

        public AttributeMetadataImplBuilder withPassword(Boolean value) {
            map.put(PASSWORD, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withMasterDetail(Boolean value) {
            map.put(IS_MASTER_DETAIL, toStringOrNull(value));
            return this;
        }

        public AttributeMetadataImplBuilder withMasterDetailDescription(String value) {
            map.put(MASTER_DETAIL_DESCRIPTION, value);
            return this;
        }

        public AttributeMetadataImplBuilder withDescription(String description) {
            map.put(DESCRIPTION, description);
            return this;
        }

        public AttributeMetadataImplBuilder withDefaultValue(String defaultValue) {
            map.put(DEFAULT, defaultValue);
            return this;
        }

        public AttributeMetadataImplBuilder withHelpMessage(String value) {
            map.put(HELP_MESSAGE, value);
            return this;
        }

        public AttributeMetadataImplBuilder withShowIfExpr(String value) {
            map.put(SHOW_IF_EXPR, value);
            return this;
        }

        public AttributeMetadataImplBuilder withValidationRulesExpr(String value) {
            map.put(VALIDATION_RULES_EXPR, value);
            return this;
        }

        public AttributeMetadataImplBuilder withAutoValueExpr(String value) {
            map.put(AUTO_VALUE_EXPR, value);
            return this;
        }

        public AttributeMetadataImplBuilder withGroup(String group) {
            map.put(GROUP, group);
            return this;
        }

        public AttributeMetadataImplBuilder witSyncToDmsAttr(String syncToDmsAttr) {
            map.put(SYNC_TO_DMS_ATTR, syncToDmsAttr);
            return this;
        }

        public AttributeMetadataImplBuilder withEditorType(AttrEditorType editorType) {
            map.put(EDITOR_TYPE, serializeEnumUpper(editorType));
            return this;
        }

        public AttributeMetadataImplBuilder withDomainKey(Boolean domainKey) {
            map.put(DOMAINKEY, toStringOrNull(domainKey));
            return this;
        }

        public AttributeMetadataImplBuilder withItems(List<String> items) {
            map.put(ITEMS, isNullOrEmpty(items) ? null : Joiner.on(" ").join(items));
            return this;
        }

        public AttributeMetadataImplBuilder withFilter(String filter) {
            map.put(FILTER, filter);
            return this;
        }

        public AttributeMetadataImplBuilder withUseDomainFilter(Boolean useDomainFilter) {
            map.put(USE_DOMAIN_FILTER, useDomainFilter);
            return this;
        }

        public AttributeMetadataImplBuilder withTargetClass(String targetClass) {
            map.put(FK_TARGET_CLASS, targetClass);
            return this;
        }

        public AttributeMetadataImplBuilder withCascadeAction(CascadeAction cascadeAction) {
            map.put(CASCADE, serializeEnum(cascadeAction));
            return this;
        }

        public AttributeMetadataImplBuilder withShowInGrid(Boolean showInGrid) {
            map.put(BASEDSP, toStringOrNull(showInGrid));
            return this;
        }

        public AttributeMetadataImplBuilder withShowInReducedGrid(Boolean showInReducedGrid) {
            map.put(SHOW_IN_REDUCED_GRID, toStringOrNull(showInReducedGrid));
            return this;
        }

        public AttributeMetadataImplBuilder withRequired(Boolean required) {
            map.put(MANDATORY, toStringOrNull(required));
            return this;
        }

        public AttributeMetadataImplBuilder withUnique(Boolean unique) {
            map.put(UNIQUE, toStringOrNull(unique));
            return this;
        }

        public AttributeMetadataImplBuilder withActive(Boolean active) {
            map.put(ACTIVE, toStringOrNull(active));
            return this;
        }

        public AttributeMetadataImplBuilder withMode(AttributePermissionMode mode) {
            map.put(ENTRY_TYPE_MODE, serializeAttributePermissionMode(mode));
            return this;
        }

        public AttributeMetadataImplBuilder withIndex(Integer index) {
            map.put(INDEX, toStringOrNull(ltZeroToNull(index)));
            return this;
        }

        public AttributeMetadataImplBuilder withClassOrder(Integer classOrder) {
            map.put(CLASSORDER, toStringOrNull(classOrder));
            return this;
        }

        public AttributeMetadataImplBuilder withMaxLength(Integer maxLength) {
            map.put(LENGTH, toStringOrNull(maxLength));
            return this;
        }

        public AttributeMetadataImplBuilder withTextContentSecurity(TextContentSecurity textContentSecurity) {
            map.put(TEXT_CONTENT_SECURITY, serializeEnum(textContentSecurity));
            return this;
        }

        public AttributeMetadataImplBuilder withTrimEnabled(Boolean trimEnabled) {
            map.put(TRIM_ENABLED, toStringOrNull(trimEnabled));
            return this;
        }

        public AttributeMetadataImplBuilder withAnonymizable(Boolean anonymizable) {
            map.put(ANONYMIZABLE, toStringOrNull(anonymizable));
            return this;
        }

        public AttributeMetadataImplBuilder withShowOnlyIconIfPresent(Boolean showOnlyIconIfPresent) {
            if (showOnlyIconIfPresent == null) {
                return this;
            }
            // only lookup and lookuparray have defined showOnlyIconIfPresent
            map.put(SHOW_ONLY_ICON_IF_PRESENT, showOnlyIconIfPresent);
            return this;
        }

        public AttributeMetadataImplBuilder withType(CardAttributeType type) {
            type.accept(new NullAttributeTypeVisitor() {
                @Override
                public void visit(ReferenceAttributeType attributeType) {
                    map.put(
                            REFERENCE_DOMAIN, attributeType.getDomainName(),
                            REFERENCE_DIRECTION, serializeRelationDirection(attributeType.getDirection()));
                }

                @Override
                public void visit(LookupAttributeType attributeType) {
                    map.put(LOOKUP_TYPE, attributeType.getLookupTypeName());
                }

                @Override
                public void visit(LookupArrayAttributeType attributeType) {
                    map.put(LOOKUP_TYPE, attributeType.getLookupTypeName());
                }

                @Override
                public void visit(IpAddressAttributeType attributeType) {
                    map.put(IP_TYPE, serializeEnum(attributeType.getType()));//TODO check this
                }

                @Override
                public void visit(ForeignKeyAttributeType attributeType) {
                    map.put(FK_TARGET_CLASS, attributeType.getForeignKeyDestinationClassName());
                    map.put(CASCADE, serializeEnum(attributeType.getForeignKeyCascadeAction()));
                }

                @Override
                public void visit(FileAttributeType attributeType) {
                    map.put(FK_TARGET_CLASS, DMS_MODEL_PARENT_CLASS);
                }

                @Override
                public void visit(LinkAttributeType attributeType) {
                    map.put(LINK, TRUE);
                }

            });
            return this;
        }

        @Override
        public AttributeMetadataImpl build() {
            return new AttributeMetadataImpl(map);
        }

    }
}
