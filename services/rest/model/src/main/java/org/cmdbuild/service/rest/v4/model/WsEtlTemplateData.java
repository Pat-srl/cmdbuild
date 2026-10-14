/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import org.cmdbuild.etl.loader.EtlFileFormat;
import org.cmdbuild.etl.loader.EtlTemplateType;
import org.cmdbuild.etl.loader.EtlMergeMode;
import org.cmdbuild.etl.loader.EtlTemplateConfig;
import org.cmdbuild.etl.loader.EtlTemplateTarget;
import org.cmdbuild.etl.loader.EtlTemplate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.etl.loader.*;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_NO_MERGE;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_ID;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_CLASS;
import static org.cmdbuild.etl.loader.EtlTemplateType.ETT_IMPORT_EXPORT;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

/**
 * @author ldare
 */
public class WsEtlTemplateData {

    private final String code, description, targetName, exportFilter, importFilter, mergeModeUpdateAttr, mergeModeUpdateValue, csvSeparator, source, charset, errorTemplate, errorAccount, notificationTemplate, notificationAccount;
    private final List<String> importKeyAttributes;
    private final Boolean active;
    private final EtlTemplateType type;
    private final EtlMergeMode mergeMode;
    private final EtlTemplateTarget targetType;
    private final EtlFileFormat fileFormat;
    private final List<WsEtlColumnData> columns;
    private final Boolean useHeader, ignoreColumnOrder, handleMissingRecordsOnError;
    private final EtlTemplateConfig.EnableCreate enableCreate;
    private final Integer headerRow, dataRow, firstCol;
    private final JsonNode filter;
    private final List<WsAttributeData> attributes;
    private final String dateFormat, timeFormat, decimalSeparator, dateTimeFormat, thousandsSeparator;

    public WsEtlTemplateData(
            @JsonProperty("errorTemplate") String errorTemplate,
            @JsonProperty("notificationTemplate") String notificationTemplate,
            @JsonProperty("errorAccount") String errorAccount,
            @JsonProperty("notificationAccount") String notificationAccount,
            @JsonProperty("fileFormat") String fileFormat,
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("targetName") String targetName,
            @JsonProperty("targetType") String targetType,
            @JsonProperty("source") String source,
            @JsonProperty("exportFilter") String exportFilter,
            @JsonProperty("importFilter") String importFilter,
            @JsonProperty("mergeMode") String mergeMode,
            @JsonProperty("mergeMode_when_missing_update_attr") String mergeModeUpdateAttr,
            @JsonProperty("mergeMode_when_missing_update_value") String mergeModeUpdateValue,
            @JsonProperty("active") Boolean active,
            @JsonProperty("enableCreate") String enableCreate,
            @JsonProperty("type") String type,
            @JsonProperty("useHeader") Boolean useHeader,
            @JsonProperty("ignoreColumnOrder") Boolean ignoreColumnOrder,
            @JsonProperty("headerRow") Integer headerRow,
            @JsonProperty("dataRow") Integer dataRow,
            @JsonProperty("firstCol") Integer firstCol,
            @JsonProperty("charset") String charset,
            @JsonProperty("csv_separator") String csvSeparator,
            @JsonProperty("importKeyAttributes") Object importKeyAttributes,
            @JsonProperty("filter") JsonNode filter,
            @JsonProperty("columns") List<WsEtlColumnData> columns,
            @JsonProperty("dateFormat") String dateFormat,
            @JsonProperty("timeFormat") String timeFormat,
            @JsonProperty("decimalSeparator") String decimalSeparator,
            @JsonProperty("dateTimeFormat") String dateTimeFormat,
            @JsonProperty("thousandsSeparator") String thousandsSeparator,
            @JsonProperty("handleMissingRecordsOnError") Boolean handleMissingRecordsOnError,
            @JsonProperty("attributes") List<WsAttributeData> attributes) {
        this.errorTemplate = errorTemplate;
        this.notificationTemplate = notificationTemplate;
        this.errorAccount = errorAccount;
        this.code = code;
        this.description = description;
        this.targetName = targetName;
        this.targetType = parseEnumOrNull(targetType, EtlTemplateTarget.class);
        this.exportFilter = exportFilter;
        this.importFilter = importFilter;
        this.mergeModeUpdateAttr = mergeModeUpdateAttr;
        this.mergeModeUpdateValue = mergeModeUpdateValue;
        this.source = source;
        this.active = active;
        this.type = parseEnumOrNull(type, EtlTemplateType.class);
        this.mergeMode = parseEnumOrNull(mergeMode, EtlMergeMode.class);
        this.columns = firstNotNull(columns, emptyList());
        this.fileFormat = parseEnumOrNull(fileFormat, EtlFileFormat.class);
        this.csvSeparator = csvSeparator;
        this.notificationAccount = notificationAccount;
        this.importKeyAttributes = convert(importKeyAttributes, List.class);
        this.useHeader = useHeader;
        this.headerRow = headerRow;
        this.dataRow = dataRow;
        this.firstCol = firstCol;
        this.ignoreColumnOrder = ignoreColumnOrder;
        this.filter = filter;
        this.charset = charset;
        this.attributes = ImmutableList.copyOf(firstNotNull(attributes, emptyList()));
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        this.decimalSeparator = decimalSeparator;
        this.thousandsSeparator = thousandsSeparator;
        this.dateTimeFormat = dateTimeFormat;
        this.handleMissingRecordsOnError = handleMissingRecordsOnError;
        this.enableCreate = parseEnumOrNull(enableCreate, EtlTemplateConfig.EnableCreate.class);
    }

    public Classe toInlineModel() {
        return ClasseImpl.builder().withName("Model").withAttributes(list(attributes).map(WsAttributeData::toAttrDefinition)).build();
    }

    public EtlTemplate toInlineTemplate() {
        return EtlTemplateImpl.builder()
                .withCode("Template")
                .withConfig(b -> b
                        .withColumns(list(attributes).map(WsAttributeData::toAttrDefinition).map(a -> EtlTemplateColumnConfigImpl.builder().withAttributeName(a.getName()).withColumnName(a.getName()).accept(c -> {
                            switch (a.getType().getName()) {
                                case REFERENCE, FOREIGNKEY, LOOKUP -> c.withMode(ETCM_ID);
                            }
                        }).build()))
                        .withType(ETT_IMPORT_EXPORT)
                        .withTargetType(ET_CLASS)
                        .withTargetName("Model")
                        .withFileFormat(fileFormat)
                        .withCsvSeparator(csvSeparator)
                        .withCharset(charset)
                        .withMergeMode(EM_NO_MERGE)
                        .withIgnoreColumnOrder(firstNotNull(ignoreColumnOrder, true))
                        .withUseHeader(firstNotNull(useHeader, true)))
                .build();
    }

    public EtlTemplateImpl.EtlTemplateImplBuilder toImportExportTemplate() {
        return EtlTemplateImpl.builder()
                .withCode(code)
                .withDescription(description)
                .withActive(active).withConfig(c -> c
                        .withAttributeNameForUpdateAttrOnMissing(mergeModeUpdateAttr)
                        .withAttributeValueForUpdateAttrOnMissing(mergeModeUpdateValue)
                        .withErrorAccount(errorAccount)
                        .withErrorTemplate(errorTemplate)
                        .withNotificationTemplate(notificationTemplate)
                        .withNotificationAccount(notificationAccount)
                        .withMergeMode(mergeMode)
                        .withTargetName(targetName)
                        .withTargetType(targetType)
                        .withType(type)
                        .withFileFormat(fileFormat)
                        .withCsvSeparator(csvSeparator)
                        .withImportKeyAttributes(importKeyAttributes)
                        .withUseHeader(useHeader)
                        .withIgnoreColumnOrder(ignoreColumnOrder)
                        .withHeaderRow(headerRow)
                        .withDataRow(dataRow)
                        .withFirstCol(firstCol)
                        .withCharset(charset)
                        .withSource(source)
                        .withFilterAsString(filter == null ? null : toJson(filter))
                        .withExportFilterAsString(exportFilter)
                        .withImportFilterAsString(importFilter)
                        .withTimeFormat(timeFormat)
                        .withDateFormat(dateFormat)
                        .withDecimalSeparator(decimalSeparator)
                        .withDateTimeFormat(dateTimeFormat)
                        .withThousandsSeparator(thousandsSeparator)
                        .withHandleMissingRecordsOnError(handleMissingRecordsOnError)
                        .withEnableCreate(enableCreate)
                        .withColumns(columns.stream().map(WsEtlColumnData::toColumnConfig).collect(toImmutableList())));
    }
}
