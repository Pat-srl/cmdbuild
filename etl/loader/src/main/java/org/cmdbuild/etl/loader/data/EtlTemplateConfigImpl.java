/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader.data;

import org.cmdbuild.etl.loader.EtlTemplateDateTimeMode;
import org.cmdbuild.etl.loader.EtlFileFormat;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfig;
import org.cmdbuild.etl.loader.EtlTemplateType;
import org.cmdbuild.etl.loader.EtlMergeMode;
import org.cmdbuild.etl.loader.EtlTemplateConfig;
import org.cmdbuild.etl.loader.EtlTemplateTarget;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.loader.*;
import org.cmdbuild.etl.loader.data.EtlTemplateConfigImpl.EtlTemplateConfigImplBuilder;
import org.cmdbuild.utils.lang.Builder;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Collections.emptyList;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.utils.CmFilterUtils.*;
import static org.cmdbuild.data.filter.FilterType.CONTEXT;
import static org.cmdbuild.etl.loader.EtlFileFormat.EFF_OTHER;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_LEAVE_MISSING;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_NO_MERGE;
import static org.cmdbuild.etl.loader.EtlTemplateConfig.EnableCreate.EC_TRUE;
import static org.cmdbuild.etl.loader.EtlTemplateConfig.EtlTemplateRecordError.ETE_LOG_WARNING;
import static org.cmdbuild.etl.loader.EtlTemplateDateTimeMode.ETDT_EXTJS;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.*;
import static org.cmdbuild.etl.loader.EtlTemplateType.ETT_IMPORT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.*;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@JsonDeserialize(builder = EtlTemplateConfigImplBuilder.class)
public class EtlTemplateConfigImpl implements EtlTemplateConfig {

    private final EtlTemplateTarget targetType;
    private final List<EtlTemplateColumnConfig> columns;
    private final List<String> importKeyAttributes;
    private final EtlMergeMode mergeMode;
    private final EtlTemplateType type;
    private final EtlFileFormat format;
    private final EtlTemplateDateTimeMode dateTimeMode;
    private final List<String> availableAttrsForScriptOnMissing;
    private final String targetName, source, attributeNameForUpdateAttrOnMissing, attributeValueForUpdateAttrOnMissing, attributeModeForUpdateAttrOnMissing, csvSeparator, charset, errorTemplate, errorAccount, notificationTemplate, notificationAccount, scriptForUpdateAttrOnMissing;
    private final String scriptOnCreated, scriptOnModified, scriptOnUnmodified;
    private final String dateFormat, timeFormat, dateTimeFormat, decimalSeparator, thousandsSeparator, classpath, callback;
    private final EnableCreate enableCreate;
    private final boolean useHeader, ignoreColumnOrder, handleMissingRecordsOnError;
    private final Integer headerRow, dataRow, firstCol;
    private final CmdbFilter exportFilter, importFilter, filter, referenceFilter;
    private final EtlTemplateRecordError onRecordError;

    private EtlTemplateConfigImpl(EtlTemplateConfigImplBuilder builder) {
        this.targetType = firstNotNull(builder.targetType, isBlank(builder.targetName) ? ET_RECORD : ET_CLASS);
        this.type = firstNotNull(builder.type, ETT_IMPORT);
        this.format = firstNotNull(builder.format, EFF_OTHER);
        this.targetName = switch (targetType) {
            case ET_RECORD ->
                builder.targetName;//TODO check this
            default ->
                checkNotBlank(builder.targetName, "target name is null");
        };
        this.columns = ImmutableList.copyOf(firstNotNull(builder.columns, emptyList()));
        Set<String> attrs = uniqueIndex(columns, EtlTemplateColumnConfig::getAttributeName).keySet();
        this.csvSeparator = builder.csvSeparator;
        this.enableCreate = firstNotNull(builder.enableCreate, EC_TRUE);
        if (isImportTemplate()) {
            this.mergeMode = firstNotNull(builder.mergeMode, EM_LEAVE_MISSING);
            switch (mergeMode) {
                case EM_UPDATE_ATTR_ON_MISSING -> {
                    scriptForUpdateAttrOnMissing = builder.scriptForUpdateAttrOnMissing;
                    availableAttrsForScriptOnMissing = builder.availableAttrsForScriptOnMissing;
                    attributeNameForUpdateAttrOnMissing = builder.attributeNameForUpdateAttrOnMissing;
                    attributeValueForUpdateAttrOnMissing = builder.attributeValueForUpdateAttrOnMissing;
                    attributeModeForUpdateAttrOnMissing = builder.attributeModeForUpdateAttrOnMissing;
                    checkArgument(isNotBlank(scriptForUpdateAttrOnMissing) || isNotBlank(attributeNameForUpdateAttrOnMissing));
                }
                default -> {
                    attributeNameForUpdateAttrOnMissing = null;
                    attributeValueForUpdateAttrOnMissing = null;
                    attributeModeForUpdateAttrOnMissing = null;
                    scriptForUpdateAttrOnMissing = null;
                    availableAttrsForScriptOnMissing = null;
                }
            }
            checkArgument(!equal(mergeMode, EM_NO_MERGE) || equal(enableCreate, EC_TRUE), "cannot have a non-merge non-insert import template!");
            switch (targetType) {
                case ET_DOMAIN -> {
                    checkArgument(attrs.contains(ATTR_IDOBJ1), "missing required attr = %s for domain template", ATTR_IDOBJ1);
                    checkArgument(attrs.contains(ATTR_IDOBJ2), "missing required attr = %s for domain template", ATTR_IDOBJ2);
                    this.importKeyAttributes = emptyList();
                }
                case ET_CLASS -> {
                    switch (mergeMode) {
                        case EM_NO_MERGE:
                            this.importKeyAttributes = emptyList();
                            break;
                        default:
                            this.importKeyAttributes = set(nullToEmpty(builder.importKeyAttributes)).with(builder.importKeyAttribute).stream().filter(StringUtils::isNotBlank).collect(toImmutableList());
                            checkArgument(!importKeyAttributes.isEmpty(), "import key attr[s] is null");
                            importKeyAttributes.forEach(a -> checkArgument(columns.stream().map(EtlTemplateColumnConfig::getAttributeName).anyMatch(equalTo(a)), "invalid key attr = %s", a));
                    }
                }
                case ET_RECORD -> {
                    this.importKeyAttributes = emptyList();
                }
                case ET_VIEW, ET_PROCESS ->
                    throw new EtlException("target type %s not supported for import template", targetType);
                default ->
                    throw new EtlException("unsupported target type =< %s >", targetType);
            }
        } else {
            this.importKeyAttributes = emptyList();
            this.mergeMode = EM_NO_MERGE;
            attributeNameForUpdateAttrOnMissing = null;
            availableAttrsForScriptOnMissing = null;
            attributeValueForUpdateAttrOnMissing = null;
            attributeModeForUpdateAttrOnMissing = null;
            scriptForUpdateAttrOnMissing = null;
        }
        this.source = builder.source;
        this.charset = builder.charset;
        this.errorTemplate = builder.errorTemplate;
        this.errorAccount = builder.errorAccount;
        this.notificationTemplate = builder.notificationTemplate;
        this.notificationAccount = builder.notificationAccount;
        this.headerRow = ltEqZeroToNull(builder.headerRow);
        this.dataRow = ltEqZeroToNull(builder.dataRow);
        this.firstCol = ltEqZeroToNull(builder.firstCol);
        this.useHeader = firstNotNull(builder.useHeader, columns.isEmpty() || columns.stream().anyMatch(c -> isNotBlank(c.getColumnName())));
        this.ignoreColumnOrder = firstNotNull(builder.ignoreColumnOrder, false);
        checkArgument(ignoreColumnOrder == false || (useHeader == true && columns.stream().allMatch(c -> isNotBlank(c.getColumnName()))), "invalid param ignoreColumnOrder with incomplete/missing header config");
        this.exportFilter = firstNotNull(isExportTemplate() ? builder.exportFilter : null, noopFilter());
        this.importFilter = firstNotNull(isImportTemplate() ? builder.importFilter : null, noopFilter());
        this.filter = firstNotNull(builder.filter, noopFilter());
        this.dateTimeMode = firstNotNull(builder.dateTimeMode, ETDT_EXTJS);
        this.dateFormat = builder.dateFormat;
        this.timeFormat = builder.timeFormat;
        this.dateTimeFormat = builder.dateTimeFormat;
        this.decimalSeparator = builder.decimalSeparator;
        this.thousandsSeparator = builder.thousandsSeparator;
        this.handleMissingRecordsOnError = firstNotNull(builder.handleMissingRecordsOnError, false);
        this.referenceFilter = firstNotNull(builder.referenceFilter, noopFilter());
        referenceFilter.checkHasOnlySupportedFilterTypes(CONTEXT);
        this.classpath = builder.classpath;
        this.callback = builder.callback;
        this.onRecordError = firstNotNull(builder.onRecordError, ETE_LOG_WARNING);
        this.scriptOnCreated = builder.scriptOnCreated;
        this.scriptOnModified = builder.scriptOnModified;
        this.scriptOnUnmodified = builder.scriptOnUnmodified;
    }

    @Override
    @Nullable
    public String getClasspath() {
        return classpath;
    }

    @Override
    @Nullable
    public String getCallback() {
        return callback;
    }

    @Override
    @Nullable
    public String getErrorTemplate() {
        return errorTemplate;
    }

    @Override
    @Nullable
    public String getErrorAccount() {
        return errorAccount;
    }

    @Override
    @Nullable
    public String getNotificationTemplate() {
        return notificationTemplate;
    }

    @Override
    @Nullable
    public String getNotificationAccount() {
        return notificationAccount;
    }

    @Override
    public EnableCreate getEnableCreate() {
        return enableCreate;
    }

    @Override
    @Nullable
    public EtlTemplateDateTimeMode getDateTimeMode() {
        return dateTimeMode;
    }

    @Override
    @Nullable
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    @Nullable
    public String getTimeFormat() {
        return timeFormat;
    }

    @Override
    @Nullable
    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    @Override
    @Nullable
    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    @Override
    @Nullable
    public String getThousandsSeparator() {
        return thousandsSeparator;
    }

    @Override
    @Nullable
    public String getSource() {
        return source;
    }

    @Override
    public boolean getUseHeader() {
        return useHeader;
    }

    @Override
    public boolean getIgnoreColumnOrder() {
        return ignoreColumnOrder;
    }

    @Override
    @Nullable
    public Integer getHeaderRow() {
        return headerRow;
    }

    @Override
    @Nullable
    public Integer getDataRow() {
        return dataRow;
    }

    @Override
    @Nullable
    public Integer getFirstCol() {
        return firstCol;
    }

    @Override
    public EtlTemplateTarget getTargetType() {
        return targetType;
    }

    @Override
    @JsonProperty("format")
    public EtlFileFormat getFileFormat() {
        return format;
    }

    @Override
    public String getTargetName() {
        return targetName;
    }

    @Override
    public List<EtlTemplateColumnConfig> getColumns() {
        return columns;
    }

    @Override
    public EtlMergeMode getMergeMode() {
        return mergeMode;
    }

    @Override
    @Nullable
    public String getAttributeNameForUpdateAttrOnMissing() {
        return attributeNameForUpdateAttrOnMissing;
    }

    @Override
    @Nullable
    public String getAttributeValueForUpdateAttrOnMissing() {
        return attributeValueForUpdateAttrOnMissing;
    }

    @Override
    @Nullable
    public String getAttributeModeForUpdateAttrOnMissing() {
        return attributeModeForUpdateAttrOnMissing;
    }

    @Override
    @Nullable
    public String getScriptForUpdateAttrOnMissing() {
        return scriptForUpdateAttrOnMissing;
    }

    @Override
    @Nullable
    public List<String> getAvailableAttrsForScriptOnMissing() {
        return availableAttrsForScriptOnMissing;
    }

    @Override
    @Nullable
    public String getScriptOnCreated() {
        return scriptOnCreated;
    }

    @Override
    @Nullable
    public String getScriptOnModified() {
        return scriptOnModified;
    }

    @Override
    @Nullable
    public String getScriptOnUnmodified() {
        return scriptOnUnmodified;
    }

    @Override
    @JsonProperty("mode")
    public EtlTemplateType getType() {
        return type;
    }

    @Override
    @Nullable
    public String getCharset() {
        return charset;
    }

    @Override
    @Nullable
    public String getCsvSeparator() {
        return csvSeparator;
    }

    @Override
    public List<String> getImportKeyAttributes() {
        return importKeyAttributes;
    }

    @Override
    public boolean getHandleMissingRecordsOnError() {
        return handleMissingRecordsOnError;
    }

    @JsonIgnore
    @Override
    public CmdbFilter getFilter() {
        return filter;
    }

    @JsonProperty("filter")
    public String getFilterAsString() {
        return serializeFilter(filter);
    }

    @JsonIgnore
    @Override
    public CmdbFilter getExportFilter() {
        return exportFilter;
    }

    @JsonIgnore
    @Override
    public CmdbFilter getImportFilter() {
        return importFilter;
    }

    @JsonProperty("exportFilter")
    public String getExportFilterAsString() {
        return serializeFilter(exportFilter);
    }

    @JsonProperty("importFilter")
    public String getImportFilterAsString() {
        return serializeFilter(importFilter);
    }

    @Override
    public CmdbFilter getReferenceFilter() {
        return referenceFilter;
    }

    @JsonProperty("referenceFilter")
    public String getReferenceFilterAsString() {
        return serializeFilter(referenceFilter);
    }

    @Override
    @JsonProperty("onRecordError")
    public EtlTemplateRecordError getOnRecordError() {
        return onRecordError;
    }

    public static EtlTemplateConfigImplBuilder builder() {
        return new EtlTemplateConfigImplBuilder();
    }

    public static EtlTemplateConfigImplBuilder copyOf(EtlTemplateConfig source) {
        return new EtlTemplateConfigImplBuilder()
                .withHandleMissingRecordsOnError(source.getHandleMissingRecordsOnError())
                .withTargetType(source.getTargetType())
                .withTargetName(source.getTargetName())
                .withColumns(source.getColumns())
                .withMergeMode(source.getMergeMode())
                .withAvailableAttrsForScriptOnMissing(source.getAvailableAttrsForScriptOnMissing())
                .withAttributeNameForUpdateAttrOnMissing(source.getAttributeNameForUpdateAttrOnMissing())
                .withAttributeValueForUpdateAttrOnMissing(source.getAttributeValueForUpdateAttrOnMissing())
                .withAttributeModeForUpdateAttrOnMissing(source.getAttributeModeForUpdateAttrOnMissing())
                .withScriptForUpdateAttrOnMissing(source.getScriptForUpdateAttrOnMissing())
                .withScriptOnCreated(source.getScriptOnCreated())
                .withScriptOnModified(source.getScriptOnModified())
                .withScriptOnUnmodified(source.getScriptOnUnmodified())
                .withExportFilter(source.getExportFilter())
                .withImportFilter(source.getImportFilter())
                .withFileFormat(source.getFileFormat())
                .withType(source.getType())
                .withCsvSeparator(source.getCsvSeparator())
                .withImportKeyAttributes(source.getImportKeyAttributes())
                .withUseHeader(source.getUseHeader())
                .withIgnoreColumnOrder(source.getIgnoreColumnOrder())
                .withHeaderRow(source.getHeaderRow())
                .withDataRow(source.getDataRow())
                .withFirstCol(source.getFirstCol())
                .withSource(source.getSource())
                .withFilter(source.getFilter())
                .withCharset(source.getCharset())
                .withDateTimeMode(source.getDateTimeMode())
                .withDateFormat(source.getDateFormat())
                .withTimeFormat(source.getTimeFormat())
                .withDateTimeFormat(source.getDateTimeFormat())
                .withDecimalSeparator(source.getDecimalSeparator())
                .withThousandsSeparator(source.getThousandsSeparator())
                .withEnableCreate(source.getEnableCreate())
                .withErrorTemplate(source.getErrorTemplate())
                .withErrorAccount(source.getErrorAccount())
                .withNotificationTemplate(source.getNotificationTemplate())
                .withNotificationAccount(source.getNotificationAccount())
                .withClasspath(source.getClasspath())
                .withCallback(source.getCallback())
                .withOnRecordError(source.getOnRecordError());
    }

    public static class EtlTemplateConfigImplBuilder implements Builder<EtlTemplateConfigImpl, EtlTemplateConfigImplBuilder> {

        private EtlTemplateTarget targetType;
        private String targetName, source, charset;
        private List<? extends EtlTemplateColumnConfig> columns;
        private List<String> importKeyAttributes;
        private List<String> availableAttrsForScriptOnMissing;
        private EtlMergeMode mergeMode;
        private String attributeNameForUpdateAttrOnMissing, attributeValueForUpdateAttrOnMissing, attributeModeForUpdateAttrOnMissing, scriptForUpdateAttrOnMissing;
        private String scriptOnCreated, scriptOnModified, scriptOnUnmodified;
        private String csvSeparator, importKeyAttribute;
        private EtlTemplateType type;
        private EtlFileFormat format;
        private EnableCreate enableCreate;
        private Boolean useHeader, ignoreColumnOrder, handleMissingRecordsOnError;
        private Integer headerRow, dataRow, firstCol;
        private CmdbFilter exportFilter, importFilter, filter, referenceFilter;
        private EtlTemplateDateTimeMode dateTimeMode;
        private String timeFormat, dateFormat, decimalSeparator, dateTimeFormat, thousandsSeparator, errorTemplate, errorAccount, notificationTemplate, notificationAccount, classpath, callback;
        private EtlTemplateRecordError onRecordError;

        public EtlTemplateConfigImplBuilder withOnRecordError(EtlTemplateRecordError onRecordError) {
            this.onRecordError = onRecordError;
            return this;
        }

        public EtlTemplateConfigImplBuilder withCallback(String callback) {
            this.callback = callback;
            return this;
        }

        public EtlTemplateConfigImplBuilder withClasspath(String classpath) {
            this.classpath = classpath;
            return this;
        }

        public EtlTemplateConfigImplBuilder withErrorTemplate(String errorTemplate) {
            this.errorTemplate = errorTemplate;
            return this;
        }

        public EtlTemplateConfigImplBuilder withErrorAccount(String errorAccount) {
            this.errorAccount = errorAccount;
            return this;
        }

        public EtlTemplateConfigImplBuilder withNotificationTemplate(String notificationTemplate) {
            this.notificationTemplate = notificationTemplate;
            return this;
        }

        public EtlTemplateConfigImplBuilder withNotificationAccount(String notificationAccount) {
            this.notificationAccount = notificationAccount;
            return this;
        }

        public EtlTemplateConfigImplBuilder withEnableCreate(EnableCreate enableCreate) {
            this.enableCreate = enableCreate;
            return this;
        }

        public EtlTemplateConfigImplBuilder withHandleMissingRecordsOnError(Boolean handleMissingRecordsOnError) {
            this.handleMissingRecordsOnError = handleMissingRecordsOnError;
            return this;
        }

        public EtlTemplateConfigImplBuilder withDateTimeMode(EtlTemplateDateTimeMode dateTimeMode) {
            this.dateTimeMode = dateTimeMode;
            return this;
        }

        public EtlTemplateConfigImplBuilder withDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public EtlTemplateConfigImplBuilder withDateTimeFormat(String dateTimeFormat) {
            this.dateTimeFormat = dateTimeFormat;
            return this;
        }

        public EtlTemplateConfigImplBuilder withTimeFormat(String timeFormat) {
            this.timeFormat = timeFormat;
            return this;
        }

        public EtlTemplateConfigImplBuilder withDecimalSeparator(String decimalSeparator) {
            this.decimalSeparator = decimalSeparator;
            return this;
        }

        public EtlTemplateConfigImplBuilder withThousandsSeparator(String thousandsSeparator) {
            this.thousandsSeparator = thousandsSeparator;
            return this;
        }

        public List<? extends EtlTemplateColumnConfig> getColumns() {
            return columns;
        }

        public EtlTemplateConfigImplBuilder withCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public EtlTemplateConfigImplBuilder withTargetType(EtlTemplateTarget targetType) {
            this.targetType = targetType;
            return this;
        }

        public EtlTemplateConfigImplBuilder withUseHeader(Boolean useHeader) {
            this.useHeader = useHeader;
            return this;
        }

        public EtlTemplateConfigImplBuilder withIgnoreColumnOrder(Boolean ignoreColumnOrder) {
            this.ignoreColumnOrder = ignoreColumnOrder;
            return this;
        }

        public EtlTemplateConfigImplBuilder withHeaderRow(Integer headerRow) {
            this.headerRow = headerRow;
            return this;
        }

        public EtlTemplateConfigImplBuilder withDataRow(Integer dataRow) {
            this.dataRow = dataRow;
            return this;
        }

        public EtlTemplateConfigImplBuilder withFirstCol(Integer firstCol) {
            this.firstCol = firstCol;
            return this;
        }

        public EtlTemplateConfigImplBuilder withType(EtlTemplateType type) {
            this.type = type;
            return this;
        }

        public EtlTemplateConfigImplBuilder withFileFormat(EtlFileFormat format) {
            this.format = format;
            return this;
        }

        public EtlTemplateConfigImplBuilder withTarget(EntryType entryType) {
            return switch (entryType.getEtType()) {
                case ET_CLASS ->
                    this.withTargetType(ET_CLASS).withTargetName(entryType.getName());
                case ET_DOMAIN ->
                    this.withTargetType(ET_DOMAIN).withTargetName(entryType.getName());
                default ->
                    throw new EtlException("invalid target = %s", entryType);
            };
        }

        public EtlTemplateConfigImplBuilder withTargetName(String targetName) {
            this.targetName = targetName;
            return this;
        }

        public EtlTemplateConfigImplBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public EtlTemplateConfigImplBuilder withCsvSeparator(String csvSeparator) {
            this.csvSeparator = csvSeparator;
            return this;
        }

        public EtlTemplateConfigImplBuilder withImportKeyAttributes(List<String> importKeyAttributes) {
            this.importKeyAttributes = importKeyAttributes;
            return this;
        }

        public EtlTemplateConfigImplBuilder withImportKeyAttributes(String importKeyAttributes) {
            this.importKeyAttributes = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(importKeyAttributes);//TODO improve this
            return this;
        }

        public EtlTemplateConfigImplBuilder withColumns(EtlTemplateColumnConfig... columns) {
            return this.withColumns(ImmutableList.copyOf(columns));
        }

        public EtlTemplateConfigImplBuilder withColumnNameFromAttributeName() {
            return this.withColumns(columns.stream().map(c -> EtlTemplateColumnConfigImpl.copyOf(c).withColumnName(c.getAttributeName()).build()).collect(toImmutableList()));
        }

        @JsonDeserialize(contentAs = EtlTemplateColumnConfigImpl.class)
        public EtlTemplateConfigImplBuilder withColumns(List<? extends EtlTemplateColumnConfig> columns) {
            this.columns = columns;
            return this;
        }

        public EtlTemplateConfigImplBuilder withMergeMode(EtlMergeMode mergeMode) {
            this.mergeMode = mergeMode;
            return this;
        }

        @JsonSetter("mergeMode")
        public EtlTemplateConfigImplBuilder withMergeModeAsString(String mergeMode) {
            return this.withMergeMode(parseEnum(mergeMode, EtlMergeMode.class));
        }

        @JsonSetter("mode")
        public EtlTemplateConfigImplBuilder withTypeAsString(String type) {
            return this.withType(parseEnum(type, EtlTemplateType.class));
        }

        @JsonSetter("targetType")
        public EtlTemplateConfigImplBuilder withTargetTypeAsString(String targetType) {
            return this.withTargetType(parseEnum(targetType, EtlTemplateTarget.class));
        }

        @JsonSetter("format")
        public EtlTemplateConfigImplBuilder withFileFormatAsString(String format) {
            return this.withFileFormat(parseEnum(format, EtlFileFormat.class));
        }

        public EtlTemplateConfigImplBuilder withScriptForUpdateAttrOnMissing(String scriptForUpdateAttrOnMissing) {
            this.scriptForUpdateAttrOnMissing = scriptForUpdateAttrOnMissing;
            return this;
        }

        public EtlTemplateConfigImplBuilder withAvailableAttrsForScriptOnMissing(List<String> availableAttrsForScriptOnMissing) {
            this.availableAttrsForScriptOnMissing = availableAttrsForScriptOnMissing;
            return this;
        }

        public EtlTemplateConfigImplBuilder withAvailableAttrsForScriptOnMissing(String availableAttrsForScriptOnMissing) {
            this.availableAttrsForScriptOnMissing = applyOrNull(availableAttrsForScriptOnMissing, attrs -> Splitter.on(",").omitEmptyStrings().trimResults().splitToList(attrs));
            return this;
        }

        public EtlTemplateConfigImplBuilder withScriptOnCreated(String scriptOnCreated) {
            this.scriptOnCreated = scriptOnCreated;
            return this;
        }

        public EtlTemplateConfigImplBuilder withScriptOnModified(String scriptOnModified) {
            this.scriptOnModified = scriptOnModified;
            return this;
        }

        public EtlTemplateConfigImplBuilder withScriptOnUnmodified(String scriptOnUnmodified) {
            this.scriptOnUnmodified = scriptOnUnmodified;
            return this;
        }

        public EtlTemplateConfigImplBuilder withAttributeNameForUpdateAttrOnMissing(String attributeNameForUpdateAttrOnMissing) {
            this.attributeNameForUpdateAttrOnMissing = attributeNameForUpdateAttrOnMissing;
            return this;
        }

        public EtlTemplateConfigImplBuilder withAttributeValueForUpdateAttrOnMissing(String attributeValueForUpdateAttrOnMissing) {
            this.attributeValueForUpdateAttrOnMissing = attributeValueForUpdateAttrOnMissing;
            return this;
        }

        public EtlTemplateConfigImplBuilder withAttributeModeForUpdateAttrOnMissing(String attributeModeForUpdateAttrOnMissing) {
            this.attributeModeForUpdateAttrOnMissing = attributeModeForUpdateAttrOnMissing;
            return this;
        }

        public EtlTemplateConfigImplBuilder withExportFilter(CmdbFilter exportFilter) {
            this.exportFilter = exportFilter;
            return this;
        }

        @JsonSetter("exportFilter")
        public EtlTemplateConfigImplBuilder withExportFilterAsString(String exportFilter) {
            this.exportFilter = parseFilter(exportFilter);
            return this;
        }

        public EtlTemplateConfigImplBuilder withImportFilter(CmdbFilter importFilter) {
            this.importFilter = importFilter;
            return this;
        }

        @JsonSetter("importFilter")
        public EtlTemplateConfigImplBuilder withImportFilterAsString(String importFilter) {
            this.importFilter = parseFilter(importFilter);
            return this;
        }

        public EtlTemplateConfigImplBuilder withFilter(CmdbFilter filter) {
            this.filter = filter;
            return this;
        }

        @JsonSetter("filter")
        public EtlTemplateConfigImplBuilder withFilterAsString(String filter) {
            this.filter = parseFilter(filter);
            return this;
        }

        public EtlTemplateConfigImplBuilder withReferenceFilter(CmdbFilter referenceFilter) {
            this.referenceFilter = referenceFilter;
            return this;
        }

        @JsonSetter("referenceFilter")
        public EtlTemplateConfigImplBuilder withReferenceFilterAsString(String referenceFilter) {
            this.referenceFilter = parseFilter(referenceFilter);
            return this;
        }

        @Override
        public EtlTemplateConfigImpl build() {
            return new EtlTemplateConfigImpl(this);
        }

    }
}
