/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.client.rest.model.AttributeDataImpl.SimpleAttributeDataBuilder;
import org.cmdbuild.dao.entrytype.AttributeMetadata.FormulaType;
import org.cmdbuild.dao.entrytype.AttributeMetadata.ShowPassword;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import org.cmdbuild.dao.entrytype.TextContentSecurity;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@JsonDeserialize(builder = SimpleAttributeDataBuilder.class)
public class AttributeDataImpl implements AttributeData {

    private final String type, name, description, domainName, defaultValue, group, targetClass, targetType, editorType, filter, lookupType, ipType, unitOfMeasure, unitOfMeasureLocation, formulaCode;
    private final boolean showInGrid, unique, required, inherited, active, password;
    private final Boolean showThousandsSeparator, showLabel, labelRequired;
    private final Integer index, precision, scale, length, classOrder;
    private final Map<String, String> metadata;
    private final AttributePermissionMode mode;
    private final TextContentSecurity textContentSecurity;
    private final FormulaType formulaType;
    private final ShowPassword showPassword;

    private AttributeDataImpl(SimpleAttributeDataBuilder builder) {
        this.type = checkNotBlank(builder.type);
        this.name = checkNotBlank(builder.name);
        this.description = checkNotNull(builder.description);
        this.domainName = (builder.domainName);
        this.defaultValue = (builder.defaultValue);
        this.group = (builder.group);
        this.targetClass = (builder.targetClass);
        this.targetType = (builder.targetType);
        this.editorType = (builder.editorType);
        this.filter = (builder.filter);
        this.lookupType = (builder.lookupType);
        this.unitOfMeasure = (builder.unitOfMeasure);
        this.unitOfMeasureLocation = (builder.unitOfMeasureLocation);
        this.showInGrid = (builder.showInGrid);
        this.unique = (builder.unique);
        this.required = (builder.required);
        this.inherited = (builder.inherited);
        this.active = (builder.active);
        this.mode = (builder.mode);
        this.showThousandsSeparator = builder.showThousandsSeparator;
        this.showLabel = builder.showLabel;
        this.labelRequired = builder.labelRequired;
        this.index = (builder.index);
        this.precision = (builder.precision);
        this.scale = (builder.scale);
        this.length = (builder.length);
        this.classOrder = (builder.classOrder);
        this.ipType = (builder.ipType);
        this.metadata = builder.metadata == null ? emptyMap() : map(builder.metadata).immutable();
        this.textContentSecurity = builder.textContentSecurity;
        this.formulaCode = builder.formulaCode;
        this.formulaType = builder.formulaType;
        this.password = firstNotNull(builder.password, false);
        this.showPassword = builder.showPassword;
    }

    @Override
    public String getIpType() {
        return ipType;
    }

    @Override
    public Integer getClassOrder() {
        return classOrder;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getTargetClass() {
        return targetClass;
    }

    @Override
    public String getTargetType() {
        return targetType;
    }

    @Override
    public String getEditorType() {
        return editorType;
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public String getLookupType() {
        return lookupType;
    }

    @Override
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    @Override
    public String getUnitOfMeasureLocation() {
        return unitOfMeasureLocation;
    }

    @Override
    public boolean getShowInGrid() {
        return showInGrid;
    }

    @Override
    public boolean getUnique() {
        return unique;
    }

    @Override
    public boolean getRequired() {
        return required;
    }

    @Override
    public boolean getInherited() {
        return inherited;
    }

    @Override
    public boolean getActive() {
        return active;
    }

    @Override
    @Nullable
    public Boolean getShowThousandsSeparator() {
        return showThousandsSeparator;
    }

    @Override
    @Nullable
    public Boolean getShowLabel() {
        return showLabel;
    }

    @Override
    @Nullable
    public Boolean getLabelRequired() {
        return labelRequired;
    }

    @Override
    public Boolean getPassword() {
        return password;
    }

    @Override
    public AttributePermissionMode getMode() {
        return mode;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public Integer getPrecision() {
        return precision;
    }

    @Override
    public Integer getScale() {
        return scale;
    }

    @Override
    public Integer getLength() {
        return length;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public TextContentSecurity getTextContentSecurity() {
        return textContentSecurity;
    }

    @Override
    public FormulaType getFormulaType() {
        return formulaType;
    }

    @Override
    public String getFormulaCode() {
        return formulaCode;
    }

    @Override
    public ShowPassword getShowPassword() {
        return showPassword;
    }

    @Override
    public String toString() {
        return "SimpleAttributeData{" + "type=" + type + ", name=" + name + '}';
    }

    public static SimpleAttributeDataBuilder builder() {
        return new SimpleAttributeDataBuilder();
    }

    public static SimpleAttributeDataBuilder copyOf(AttributeDataImpl source) {
        return new SimpleAttributeDataBuilder()
                .withType(source.getType())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withDomainName(source.getDomainName())
                .withDefaultValue(source.getDefaultValue())
                .withGroup(source.getGroup())
                .withTargetClass(source.getTargetClass())
                .withTargetType(source.getTargetType())
                .withEditorType(source.getEditorType())
                .withFilter(source.getFilter())
                .withLookupType(source.getLookupType())
                .withUnitOfMeasure(source.getUnitOfMeasure())
                .withUnitOfMeasureLocation(source.getUnitOfMeasureLocation())
                .withShowInGrid(source.getShowInGrid())
                .withUnique(source.getUnique())
                .withRequired(source.getRequired())
                .withInherited(source.getInherited())
                .withActive(source.getActive())
                .withShowThousandsSeparator(source.getShowThousandsSeparator())
                .withShowLabel(source.getShowLabel())
                .withLabelRequired(source.getLabelRequired())
                .withMode(source.getMode())
                .withIndex(source.getIndex())
                .withPrecision(source.getPrecision())
                .withScale(source.getScale())
                .withMaxLength(source.getLength())
                .withMetadata(source.getMetadata())
                .withFormulaCode(source.getFormulaCode())
                .withFormulaType(source.getFormulaType())
                .withTextContentSecurity(source.getTextContentSecurity())
                .withPassword(source.getPassword())
                .withShowPassword(source.getShowPassword());
    }

    public static class SimpleAttributeDataBuilder implements Builder<AttributeDataImpl, SimpleAttributeDataBuilder> {

        private String type;
        private String name;
        private String description;
        private String domainName;
        private String defaultValue;
        private String group;
        private String targetClass;
        private String targetType;
        private String editorType;
        private String filter;
        private String lookupType;
        private String ipType;
        private String unitOfMeasure;
        private String unitOfMeasureLocation;
        private String formulaCode;
        private FormulaType formulaType;
        private Boolean showInGrid;
        private Boolean unique;
        private Boolean required;
        private Boolean inherited;
        private Boolean active;
        private Boolean showThousandsSeparator;
        private Boolean showLabel;
        private Boolean labelRequired;
        private Boolean password;
        private AttributePermissionMode mode;
        private Integer index;
        private Integer precision;
        private Integer scale;
        private Integer length;
        private Integer classOrder;
        private Map<String, String> metadata;
        private TextContentSecurity textContentSecurity;
        private ShowPassword showPassword;

        public SimpleAttributeDataBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public SimpleAttributeDataBuilder withShowPassword(ShowPassword showPassword) {
            this.showPassword = showPassword;
            return this;
        }

        public SimpleAttributeDataBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SimpleAttributeDataBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SimpleAttributeDataBuilder withDomainName(String domainName) {
            this.domainName = domainName;
            return this;
        }

        public SimpleAttributeDataBuilder withDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public SimpleAttributeDataBuilder withGroup(String group) {
            this.group = group;
            return this;
        }

        public SimpleAttributeDataBuilder withTargetClass(String targetClass) {
            this.targetClass = targetClass;
            return this;
        }

        public SimpleAttributeDataBuilder withTargetType(String targetType) {
            this.targetType = targetType;
            return this;
        }

        public SimpleAttributeDataBuilder withEditorType(String editorType) {
            this.editorType = editorType;
            return this;
        }

        public SimpleAttributeDataBuilder withFilter(String filter) {
            this.filter = filter;
            return this;
        }

        public SimpleAttributeDataBuilder withLookupType(String lookupType) {
            this.lookupType = lookupType;
            return this;
        }

        public SimpleAttributeDataBuilder withUnitOfMeasure(String unitOfMeasure) {
            this.unitOfMeasure = unitOfMeasure;
            return this;
        }

        public SimpleAttributeDataBuilder withUnitOfMeasureLocation(String unitOfMeasureLocation) {
            this.unitOfMeasureLocation = unitOfMeasureLocation;
            return this;
        }

        public SimpleAttributeDataBuilder withShowInGrid(Boolean showInList) {
            this.showInGrid = showInList;
            return this;
        }

        public SimpleAttributeDataBuilder withUnique(Boolean unique) {
            this.unique = unique;
            return this;
        }

        @JsonProperty("mandatory")
        public SimpleAttributeDataBuilder withRequired(Boolean required) {
            this.required = required;
            return this;
        }

        public SimpleAttributeDataBuilder withInherited(Boolean inherited) {
            this.inherited = inherited;
            return this;
        }

        public SimpleAttributeDataBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public SimpleAttributeDataBuilder withPassword(Boolean password) {
            this.password = password;
            return this;
        }

        public SimpleAttributeDataBuilder withShowThousandsSeparator(Boolean showThousandsSeparator) {
            this.showThousandsSeparator = showThousandsSeparator;
            return this;
        }

        public SimpleAttributeDataBuilder withShowLabel(Boolean showLabel) {
            this.showLabel = showLabel;
            return this;
        }

        public SimpleAttributeDataBuilder withLabelRequired(Boolean labelRequired) {
            this.labelRequired = labelRequired;
            return this;
        }

        public SimpleAttributeDataBuilder withMode(AttributePermissionMode mode) {
            this.mode = mode;
            return this;
        }

        public SimpleAttributeDataBuilder withMode(String mode) {
            this.mode = parseEnum(mode, AttributePermissionMode.class);
            return this;
        }

        public SimpleAttributeDataBuilder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public SimpleAttributeDataBuilder withPrecision(Integer precision) {
            this.precision = precision;
            return this;
        }

        public SimpleAttributeDataBuilder withScale(Integer scale) {
            this.scale = scale;
            return this;
        }

        public SimpleAttributeDataBuilder withMaxLength(Integer length) {
            this.length = length;
            return this;
        }

        public SimpleAttributeDataBuilder withClassOrder(Integer classOrder) {
            this.classOrder = classOrder;
            return this;
        }

        public SimpleAttributeDataBuilder withIpType(String ipType) {
            this.ipType = ipType;
            return this;
        }

        public SimpleAttributeDataBuilder withMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public SimpleAttributeDataBuilder withTextContentSecurity(TextContentSecurity textContentSecurity) {
            this.textContentSecurity = textContentSecurity;
            return this;
        }

        public SimpleAttributeDataBuilder withTextContentSecurity(String textContentSecurity) {
            this.textContentSecurity = parseEnum(textContentSecurity, TextContentSecurity.class);
            return this;
        }

        public SimpleAttributeDataBuilder withFormulaType(String formulaType) {
            this.formulaType = parseEnum(formulaType, FormulaType.class);
            return this;
        }

        public SimpleAttributeDataBuilder withFormulaType(FormulaType formulaType) {
            this.formulaType = formulaType;
            return this;
        }

        public SimpleAttributeDataBuilder withFormulaCode(String formulaCode) {
            this.formulaCode = formulaCode;
            return this;
        }

        @Override
        public AttributeDataImpl build() {
            return new AttributeDataImpl(this);
        }

    }
}
