/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.etl.loader.data.EtlTemplateConfigImpl;
import org.cmdbuild.etl.loader.data.EtlTemplateConfigImpl.EtlTemplateConfigImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EtlTemplateImpl implements EtlTemplate {

    private final String code, description;
    private final EtlTemplateConfig config;
    private final boolean isActive;

    private EtlTemplateImpl(EtlTemplateImplBuilder builder) {
        this.code = checkNotBlank(builder.code, "template code is null");
        this.description = nullToEmpty(builder.description);
        this.isActive = firstNotNull(builder.active, true);
        this.config = builder.config.build();
    }

    @Override
    public EtlTemplateRecordError getOnRecordError() {
        return config.getOnRecordError();
    }

    @Override
    public EnableCreate getEnableCreate() {
        return config.getEnableCreate();
    }

    @Override
    public CmdbFilter getReferenceFilter() {
        return config.getReferenceFilter();
    }

    @Override
    public String getErrorTemplate() {
        return config.getErrorTemplate();
    }

    @Override
    public String getNotificationTemplate() {
        return config.getNotificationTemplate();
    }

    @Override
    public String getErrorAccount() {
        return config.getErrorAccount();
    }

    @Override
    public String getNotificationAccount() {
        return config.getNotificationAccount();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getTargetName() {
        return config.getTargetName();
    }

    @Override
    public EtlTemplateType getType() {
        return config.getType();
    }

    @Override
    public EtlFileFormat getFileFormat() {
        return config.getFileFormat();
    }

    @Override
    @Nullable
    public String getAttributeNameForUpdateAttrOnMissing() {
        return config.getAttributeNameForUpdateAttrOnMissing();
    }

    @Override
    @Nullable
    public String getAttributeValueForUpdateAttrOnMissing() {
        return config.getAttributeValueForUpdateAttrOnMissing();
    }

    @Override
    @Nullable
    public String getAttributeModeForUpdateAttrOnMissing() {
        return config.getAttributeModeForUpdateAttrOnMissing();
    }

    @Override
    @Nullable
    public String getScriptForUpdateAttrOnMissing() {
        return config.getScriptForUpdateAttrOnMissing();
    }

    @Override
    @Nullable
    public List<String> getAvailableAttrsForScriptOnMissing() {
        return config.getAvailableAttrsForScriptOnMissing();
    }

    @Override
    @Nullable
    public String getScriptOnCreated() {
        return config.getScriptOnCreated();
    }

    @Override
    @Nullable
    public String getScriptOnModified() {
        return config.getScriptOnModified();
    }

    @Override
    @Nullable
    public String getScriptOnUnmodified() {
        return config.getScriptOnUnmodified();
    }

    @Override
    public CmdbFilter getExportFilter() {
        return config.getExportFilter();
    }

    @Override
    public CmdbFilter getImportFilter() {
        return config.getImportFilter();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public EtlTemplateTarget getTargetType() {
        return config.getTargetType();
    }

    @Override
    public List<EtlTemplateColumnConfig> getColumns() {
        return config.getColumns();
    }

    @Override
    public EtlMergeMode getMergeMode() {
        return config.getMergeMode();
    }

    @Override
    @Nullable
    public String getCsvSeparator() {
        return config.getCsvSeparator();
    }

//    @Override
//    @Nullable
//    public String getImportKeyAttribute() {
//        return config.getImportKeyAttribute();
//    }
    @Override
    @Nullable
    public List<String> getImportKeyAttributes() {
        return config.getImportKeyAttributes();
    }

    @Override
    public boolean getUseHeader() {
        return config.getUseHeader();
    }

    @Override
    public boolean getIgnoreColumnOrder() {
        return config.getIgnoreColumnOrder();
    }

    @Override
    @Nullable
    public Integer getHeaderRow() {
        return config.getHeaderRow();
    }

    @Override
    @Nullable
    public Integer getDataRow() {
        return config.getDataRow();
    }

    @Override
    @Nullable
    public Integer getFirstCol() {
        return config.getFirstCol();
    }

    @Override
    @Nullable
    public String getSource() {
        return config.getSource();
    }

    @Override
    public String getClasspath() {
        return config.getClasspath();
    }

    @Override
    public String getCallback() {
        return config.getCallback();
    }

    @Override
    public CmdbFilter getFilter() {
        return config.getFilter();
    }

    @Override
    public String getCharset() {
        return config.getCharset();
    }

    @Override
    @Nullable
    public EtlTemplateDateTimeMode getDateTimeMode() {
        return config.getDateTimeMode();
    }

    @Override
    @Nullable
    public String getDateFormat() {
        return config.getDateFormat();
    }

    @Override
    @Nullable
    public String getTimeFormat() {
        return config.getTimeFormat();
    }

    @Override
    @Nullable
    public String getDateTimeFormat() {
        return config.getDateTimeFormat();
    }

    @Override
    @Nullable
    public String getDecimalSeparator() {
        return config.getDecimalSeparator();
    }

    @Override
    @Nullable
    public String getThousandsSeparator() {
        return config.getThousandsSeparator();
    }

    @Override
    public boolean getHandleMissingRecordsOnError() {
        return config.getHandleMissingRecordsOnError();
    }

    @Override
    public String toString() {
        return "ImportExportTemplateImpl{" + "code=" + code + ", target=" + getTargetName() + '}';
    }

    public static EtlTemplateImplBuilder builder() {
        return new EtlTemplateImplBuilder();
    }

    public static EtlTemplateImplBuilder copyOf(EtlTemplate source) {
        return copyOf((EtlTemplateConfig) source)
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withActive(source.isActive());
    }

    public static EtlTemplateImplBuilder copyOf(EtlTemplateConfig source) {
        return builder().withConfig(source);
    }

    public static class EtlTemplateImplBuilder implements Builder<EtlTemplateImpl, EtlTemplateImplBuilder> {

        private String code;
        private String description;
        private Boolean active;
        private EtlTemplateConfigImplBuilder config = EtlTemplateConfigImpl.builder();

        public EtlTemplateImplBuilder withConfig(EtlTemplateConfig config) {
            this.config = EtlTemplateConfigImpl.copyOf(config);
            return this;
        }

        public EtlTemplateImplBuilder withConfig(Consumer<EtlTemplateConfigImplBuilder> c) {
            this.config.accept(c);
            return this;
        }

        public EtlTemplateImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public EtlTemplateImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EtlTemplateImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public EtlTemplateImplBuilder withOnRecordError(EtlTemplateRecordError onRecordError) {
            config.withOnRecordError(onRecordError);
            return this;
        }

        public EtlTemplateImplBuilder withColumns(List<EtlTemplateColumnConfig> columns) {
            config.withColumns(columns);
            return this;
        }

        public EtlTemplateImplBuilder withFilter(String filter) {
            config.withFilterAsString(filter);
            return this;
        }

        public EtlTemplateImplBuilder withFilter(CmdbFilter filter) {
            config.withFilter(filter);
            return this;
        }

        public EtlTemplateImplBuilder withReferenceFilter(CmdbFilter filter) {
            config.withReferenceFilter(filter);
            return this;
        }

        public EtlTemplateImplBuilder withColumnNameFromAttributeName() {
            config.withColumnNameFromAttributeName();
            return this;
        }

        public EtlTemplateImplBuilder withHandleMissingRecordsOnError(Boolean handleMissingRecordsOnError) {
            config.withHandleMissingRecordsOnError(handleMissingRecordsOnError);
            return this;
        }

        public EtlTemplateImplBuilder withIgnoreColumnOrder(Boolean ignoreColumnOrder) {
            config.withIgnoreColumnOrder(ignoreColumnOrder);
            return this;
        }

        public EtlTemplateImplBuilder withHeaderRow(Integer headerRow) {
            config.withHeaderRow(headerRow);
            return this;
        }

        public EtlTemplateImplBuilder withDataRow(Integer dataRow) {
            config.withDataRow(dataRow);
            return this;
        }

        public EtlTemplateImplBuilder withFirstCol(Integer firstCol) {
            config.withFirstCol(firstCol);
            return this;
        }

        public EtlTemplateImplBuilder withCharset(String charset) {
            config.withCharset(charset);
            return this;
        }

        public EtlTemplateImplBuilder withTargetName(String targetName) {
            config.withTargetName(targetName);
            return this;
        }

        public EtlTemplateImplBuilder withTarget(EntryType entryType) {
            config.withTarget(entryType);
            return this;
        }

        public EtlTemplateImplBuilder withScriptForUpdateAttrOnMissing(String script) {
            config.withScriptForUpdateAttrOnMissing(script);
            return this;
        }

        public EtlTemplateImplBuilder withAvailableAttrsForScriptOnMissing(List<String> availableAttrsForScriptOnMissing) {
            config.withAvailableAttrsForScriptOnMissing(availableAttrsForScriptOnMissing);
            return this;
        }

        public EtlTemplateImplBuilder withAvailableAttrsForScriptOnMissing(String availableAttrsForScriptOnMissing) {
            config.withAvailableAttrsForScriptOnMissing(availableAttrsForScriptOnMissing);
            return this;
        }

        public EtlTemplateImplBuilder withScriptOnCreated(String script) {
            config.withScriptOnCreated(script);
            return this;
        }

        public EtlTemplateImplBuilder withScriptOnModified(String script) {
            config.withScriptOnModified(script);
            return this;
        }

        public EtlTemplateImplBuilder withScriptOnUnmodified(String script) {
            config.withScriptOnUnmodified(script);
            return this;
        }

        public EtlTemplateImplBuilder withAttributeNameForUpdateAttrOnMissing(String attributeNameForUpdateAttrOnMissing) {
            config.withAttributeNameForUpdateAttrOnMissing(attributeNameForUpdateAttrOnMissing);
            return this;
        }

        public EtlTemplateImplBuilder withAttributeValueForUpdateAttrOnMissing(String attributeValueForUpdateAttrOnMissing) {
            config.withAttributeValueForUpdateAttrOnMissing(attributeValueForUpdateAttrOnMissing);
            return this;
        }

        public EtlTemplateImplBuilder withAttributeModeForUpdateAttrOnMissing(String attributeModeForUpdateAttrOnMissing) {
            config.withAttributeValueForUpdateAttrOnMissing(attributeModeForUpdateAttrOnMissing);
            return this;
        }

        public EtlTemplateImplBuilder withErrorTemplate(String errorTemplate) {
            config.withErrorTemplate(errorTemplate);
            return this;
        }

        public EtlTemplateImplBuilder withNotificationTemplate(String errorTemplate) {
            config.withNotificationTemplate(errorTemplate);
            return this;
        }

        public EtlTemplateImplBuilder withErrorAccount(String errorAccount) {
            config.withErrorAccount(errorAccount);
            return this;
        }

        public EtlTemplateImplBuilder withNotificationAccount(String notificationAccount) {
            config.withNotificationAccount(notificationAccount);
            return this;
        }

        public EtlTemplateImplBuilder withExportFilter(String exportFilter) {
            config.withExportFilterAsString(exportFilter);
            return this;
        }

        public EtlTemplateImplBuilder withTargetType(EtlTemplateTarget targetType) {
            config.withTargetType(targetType);
            return this;
        }

        public EtlTemplateImplBuilder withType(EtlTemplateType type) {
            config.withType(type);
            return this;
        }

        public EtlTemplateImplBuilder withFileFormat(EtlFileFormat format) {
            config.withFileFormat(format);
            return this;
        }

        public EtlTemplateImplBuilder withColumns(EtlTemplateColumnConfig... columns) {
            return this.withColumns(ImmutableList.copyOf(columns));
        }

        public EtlTemplateImplBuilder withMergeMode(EtlMergeMode mergeMode) {
            config.withMergeMode(mergeMode);
            return this;
        }

        public EtlTemplateImplBuilder withEnableCreate(EnableCreate enableCreate) {
            config.withEnableCreate(enableCreate);
            return this;
        }

        public EtlTemplateImplBuilder withCsvSeparator(String csvSeparator) {
            config.withCsvSeparator(csvSeparator);
            return this;
        }

        public EtlTemplateImplBuilder withImportKeyAttributes(String importKeyAttribute) {
            config.withImportKeyAttributes(importKeyAttribute);
            return this;
        }

        public EtlTemplateImplBuilder withImportKeyAttributes(List<String> importKeyAttributes) {
            config.withImportKeyAttributes(importKeyAttributes);
            return this;
        }

        public EtlTemplateImplBuilder withImportKeyAttributes(String... importKeyAttributes) {
            config.withImportKeyAttributes(list(importKeyAttributes));
            return this;
        }

        public EtlTemplateImplBuilder withSource(String source) {
            config.withSource(source);
            return this;
        }

        public EtlTemplateImplBuilder withUseHeader(Boolean useHeader) {
            config.withUseHeader(useHeader);
            return this;
        }

        @Override
        public EtlTemplateImpl build() {
            return new EtlTemplateImpl(this);
        }

    }
}
