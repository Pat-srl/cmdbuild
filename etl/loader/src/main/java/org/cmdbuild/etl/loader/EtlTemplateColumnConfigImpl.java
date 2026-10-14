/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Objects.equal;
import static java.lang.String.format;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.etl.loader.EtlTemplateColumnConfig.AttributeType.AT_DEFAULT;
import static org.cmdbuild.etl.loader.EtlTemplateColumnConfig.ColumnErrorAction.CEA_FAIL;
import static org.cmdbuild.etl.loader.EtlTemplateColumnConfig.ColumnOnMissingRefAction.CMA_ERROR;
import static org.cmdbuild.etl.loader.EtlTemplateColumnConfig.RequiredAttrMode.RAM_AUTO;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfigImpl.EtlTemplateColumnConfigImplBuilder;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_DEFAULT;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_IGNORE;
import static org.cmdbuild.etl.loader.EtlTemplateDateTimeMode.ETDT_EXTJS;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

@JsonDeserialize(builder = EtlTemplateColumnConfigImplBuilder.class)
@JsonInclude(Include.NON_NULL)
public class EtlTemplateColumnConfigImpl implements EtlTemplateColumnConfig {

    private final String attributeName, columnName, dateFormat, timeFormat, dateTimeFormat, decimalSeparator, thousandsSeparator, classpath, expr;
    private final EtlTemplateColumnMode mode;
    private final EtlTemplateDateTimeMode dateTimeMode;
    private final String defaultValue;
    private final ColumnErrorAction errorAction;
    private final ColumnOnMissingRefAction missingRefAction;
    private final RequiredAttrMode required;
    private final AttributeType attributeType;
    private final RelationDirection relationDirection;
    private final String referenceTemplate;

    private EtlTemplateColumnConfigImpl(EtlTemplateColumnConfigImplBuilder builder) {
        this.mode = firstNotNull(builder.mode, ETCM_DEFAULT);
        this.dateTimeMode = firstNotNull(builder.dateTimeMode, ETDT_EXTJS);
        this.errorAction = firstNotNull(builder.errorAction, CEA_FAIL);
        this.missingRefAction = firstNotNull(builder.missingRefAction, CMA_ERROR);
        this.required = firstNotNull(builder.required, RAM_AUTO);
        this.attributeType = firstNotNull(builder.attributeType, AT_DEFAULT);
        this.relationDirection = firstNotNull(builder.relationDirection, RD_DIRECT);
        this.columnName = trimToEmpty(builder.columnName);
        this.classpath = builder.classpath;
        this.expr = builder.expr;
        if (equal(mode, ETCM_IGNORE)) {
            this.attributeName = firstNotBlank(builder.attributeName, format("ignore_%s", randomId()));
            this.defaultValue = null;
        } else {
            this.attributeName = firstNotBlank(builder.attributeName, columnName);
            this.defaultValue = builder.defaultValue;
        }
        switch (mode) {
            case ETCM_DEFAULT -> {
                //TODO validation (?)
                this.dateFormat = builder.dateFormat;
                this.timeFormat = builder.timeFormat;
                this.dateTimeFormat = builder.dateTimeFormat;
                this.decimalSeparator = builder.decimalSeparator;
                this.thousandsSeparator = builder.thousandsSeparator;
            }
            default -> {
                this.dateFormat = null;
                this.timeFormat = null;
                this.dateTimeFormat = null;
                this.decimalSeparator = null;
                this.thousandsSeparator = null;
            }
        }
        this.referenceTemplate = builder.referenceTemplate;
    }

    @Override
    @Nullable
    public String getReferenceTemplate() {
        return referenceTemplate;
    }

    @Override
    @Nullable
    public String getClasspath() {
        return classpath;
    }

    @Override
    @Nullable
    public String getExpr() {
        return expr;
    }

    @Override
    public RequiredAttrMode getRequiredAttrMode() {
        return required;
    }

    @Override
    public ColumnErrorAction getOnErrorAction() {
        return errorAction;
    }

    @Override
    public ColumnOnMissingRefAction getOnMissingRefAction() {
        return missingRefAction;
    }

    @Override
    public AttributeType getAttributeType() {
        return attributeType;
    }

    @Override
    public RelationDirection getRelationDirection() {
        return relationDirection;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    @JsonIgnore
    public EtlTemplateColumnMode getMode() {
        return mode;
    }

    @JsonProperty("mode")
    public String getModeAsString() {
        return serializeEnum(mode);
    }

    @Override
    @Nullable
    public String getDefault() {
        return defaultValue;
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
    public String toString() {
        return "ImportExportColumnConfig{" + "attributeName=" + attributeName + ", columnName=" + columnName + '}';
    }

    public static EtlTemplateColumnConfigImplBuilder builder() {
        return new EtlTemplateColumnConfigImplBuilder();
    }

    public static EtlTemplateColumnConfigImpl build(String attrName) {
        return builder().withAttributeName(attrName).build();
    }

    public static EtlTemplateColumnConfigImpl build(String attrName, String columnName) {
        return builder().withAttributeName(attrName).withColumnName(columnName).build();
    }

    public static EtlTemplateColumnConfigImplBuilder copyOf(EtlTemplateColumnConfig source) {
        return new EtlTemplateColumnConfigImplBuilder()
                .withAttributeName(source.getAttributeName())
                .withColumnName(source.getColumnName())
                .withMode(source.getMode())
                .withDefault(source.getDefault())
                .withDateTimeMode(source.getDateTimeMode())
                .withDateFormat(source.getDateFormat())
                .withTimeFormat(source.getTimeFormat())
                .withDateTimeFormat(source.getDateTimeFormat())
                .withDecimalSeparator(source.getDecimalSeparator())
                .withThousandsSeparator(source.getThousandsSeparator())
                .withOnErrorAction(source.getOnErrorAction())
                .withOnMissingRefAction(source.getOnMissingRefAction())
                .withAttributeType(source.getAttributeType())
                .withRelationDirection(source.getRelationDirection())
                .withRequiredAttrMode(source.getRequiredAttrMode())
                .withClasspath(source.getClasspath())
                .withExpr(source.getExpr())
                .withReferenceTemplate(source.getReferenceTemplate());
    }

    public static class EtlTemplateColumnConfigImplBuilder implements Builder<EtlTemplateColumnConfigImpl, EtlTemplateColumnConfigImplBuilder> {

        private String attributeName, columnName, timeFormat, dateFormat, decimalSeparator, dateTimeFormat, thousandsSeparator, classpath, expr;
        private EtlTemplateColumnMode mode;
        private EtlTemplateDateTimeMode dateTimeMode;
        private String defaultValue;
        private ColumnErrorAction errorAction;
        private ColumnOnMissingRefAction missingRefAction;
        private RequiredAttrMode required;
        private AttributeType attributeType;
        private RelationDirection relationDirection;
        private String referenceTemplate;

        public EtlTemplateColumnConfigImplBuilder withReferenceTemplate(String referenceTemplate) {
            this.referenceTemplate = referenceTemplate;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withExpr(String expr) {
            this.expr = expr;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withClasspath(String classpath) {
            this.classpath = classpath;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withAttributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withAttributeType(AttributeType attributeType) {
            this.attributeType = attributeType;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withRelationDirection(RelationDirection relationDirection) {
            this.relationDirection = relationDirection;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withOnErrorAction(ColumnErrorAction errorAction) {
            this.errorAction = errorAction;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withOnMissingRefAction(ColumnOnMissingRefAction missingRefAction) {
            this.missingRefAction = missingRefAction;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withRequiredAttrMode(RequiredAttrMode required) {
            this.required = required;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withMode(EtlTemplateColumnMode mode) {
            this.mode = mode;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withMode(String mode) {
            this.mode = parseEnum(mode, EtlTemplateColumnMode.class);
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withDefault(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withDateTimeMode(EtlTemplateDateTimeMode dateTimeMode) {
            this.dateTimeMode = dateTimeMode;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withTimeFormat(String timeFormat) {
            this.timeFormat = timeFormat;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withDateTimeFormat(String dateTimeFormat) {
            this.dateTimeFormat = dateTimeFormat;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withDecimalSeparator(String decimalSeparator) {
            this.decimalSeparator = decimalSeparator;
            return this;
        }

        public EtlTemplateColumnConfigImplBuilder withThousandsSeparator(String thousandsSeparator) {
            this.thousandsSeparator = thousandsSeparator;
            return this;
        }

        @Override
        public EtlTemplateColumnConfigImpl build() {
            return new EtlTemplateColumnConfigImpl(this);
        }

    }
}
