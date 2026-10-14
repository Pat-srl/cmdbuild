/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.dao.entrytype.AttributeMetadata.FormulaType;
import org.cmdbuild.dao.entrytype.AttributeMetadata.ShowPassword;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_DEFAULT;
import static org.cmdbuild.dao.entrytype.AttributePermissionMode.APM_WRITE;
import org.cmdbuild.dao.entrytype.TextContentSecurity;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class AttributeRequestDataImpl implements AttributeRequestData {

    private final String type, name, description, formulaCode, domainName, defaultValue, group, targetClass, targetType, editorType, filter, lookupType, ipType, direction, masterDetailDescription, unitOfMeasure, unitOfMeasureLocation, dmsCategory;
    private final boolean showInGrid, unique, required, active, isMasterDetail, isTrimEnabled;
    private final Boolean showThousandsSeparator, showLabel, labelRequired, password;
    private final Integer index, precision, scale, length, classOrder;
    private final Map<String, String> metadata;
    private final AttributePermissionMode mode;
    private final TextContentSecurity textContentSecurity;
    private final FormulaType formulaType;
    private final ShowPassword showPassword;

    private AttributeRequestDataImpl(AttributeRequestDataImplBuilder builder) {
        this.type = checkNotBlank(builder.type);
        this.name = checkNotBlank(builder.name);
        this.description = nullToEmpty(builder.description);
        this.domainName = (builder.domainName);
        this.direction = (builder.direction);
        this.defaultValue = (builder.defaultValue);
        this.group = (builder.group);
        this.targetClass = (builder.targetClass);
        this.targetType = (builder.targetType);
        this.editorType = (builder.editorType);
        this.filter = (builder.filter);
        this.lookupType = (builder.lookupType);
        this.unitOfMeasure = (builder.unitOfMeasure);
        this.unitOfMeasureLocation = (builder.unitOfMeasureLocation);
        this.showInGrid = firstNotNull(builder.showInGrid, true);
        this.unique = firstNotNull(builder.unique, false);
        this.required = firstNotNull(builder.required, false);
        this.active = firstNotNull(builder.active, true);
        this.mode = firstNotNull(builder.mode, APM_WRITE);
        this.index = (builder.index);
        this.precision = (builder.precision);
        this.scale = (builder.scale);
        this.length = (builder.length);
        this.classOrder = (builder.classOrder);
        this.ipType = (builder.ipType);
        this.textContentSecurity = (builder.textContentSecurity);
        this.metadata = builder.metadata == null ? emptyMap() : map(builder.metadata).immutable();
        this.isMasterDetail = firstNotNull(builder.isMasterDetail, false);
        this.showThousandsSeparator = builder.showThousandsSeparator;
        this.showLabel = builder.showLabel;
        this.labelRequired = builder.labelRequired;
        this.masterDetailDescription = (builder.masterDetailDescription);
        this.formulaType = builder.formulaType;
        this.formulaCode = builder.formulaCode;
        this.password = firstNotNull(builder.password, false);
        this.showPassword = builder.showPassword;
        this.dmsCategory = builder.dmsCategory;
        this.isTrimEnabled = firstNotNull(builder.isTrimEnabled, false);
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
    public String getDmsCategory() {
        return dmsCategory;
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
    public String getDirection() {
        return direction;
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
    public String getMasterDetailDescription() {
        return masterDetailDescription;
    }

    @Override
    public boolean isMasterDetail() {
        return isMasterDetail;
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
    public Boolean getPassword() {
        return password;
    }

    @Override
    public ShowPassword getShowPassword() {
        return showPassword;
    }

    @Override
    public Boolean getTrimEnabled() {
        return isTrimEnabled;
    }

    @Override
    public String toString() {
        return "SimpleAttributeData{" + "type=" + type + ", name=" + name + '}';
    }

    public static AttributeRequestDataImplBuilder builder() {
        return new AttributeRequestDataImplBuilder();
    }

    public static AttributeRequestDataImplBuilder copyOf(AttributeRequestData source) {
        return new AttributeRequestDataImplBuilder()
                .withType(source.getType())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withDomainName(source.getDomainName())
                .withDirection(source.getDirection())
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
                .withActive(source.getActive())
                .withShowThousandsSeparator(source.getShowThousandsSeparator())
                .withShowLabel(source.getShowLabel())
                .withLabelRequired(source.getLabelRequired())
                .withMode(source.getMode())
                .withIndex(source.getIndex())
                .withPrecision(source.getPrecision())
                .withScale(source.getScale())
                .withLength(source.getLength())
                .withMetadata(source.getMetadata())
                .withMasterDetail(source.isMasterDetail())
                .withMasterDetailDescription(source.getMasterDetailDescription())
                .withTextContentSecurity(source.getTextContentSecurity())
                .withFormulaCode(source.getFormulaCode())
                .withFormulaType(source.getFormulaType())
                .withPassword(source.getPassword())
                .withShowPassword(source.getShowPassword())
                .withDmsCategory(source.getDmsCategory())
                .withTrimEnabled(source.getTrimEnabled());
    }

    public static class AttributeRequestDataImplBuilder implements Builder<AttributeRequestDataImpl, AttributeRequestDataImplBuilder> {

        private String type, masterDetailDescription;
        private String name;
        private String description;
        private String domainName, direction;
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
        private String dmsCategory;
        private Boolean showInGrid;
        private Boolean unique;
        private Boolean required, isMasterDetail;
        private Boolean active;
        private Boolean showThousandsSeparator;
        private Boolean showLabel;
        private Boolean labelRequired;
        private AttributePermissionMode mode;
        private Integer index;
        private Integer precision;
        private Integer scale;
        private Integer length;
        private Integer classOrder;
        private Map<String, String> metadata;
        private TextContentSecurity textContentSecurity;
        private FormulaType formulaType;
        private String formulaCode;
        private Boolean password;
        private ShowPassword showPassword;
        private Boolean isTrimEnabled;

        public AttributeRequestDataImplBuilder withDefaults() {
            showInGrid = true;
            unique = false;
            required = false;
            active = true;
            isMasterDetail = false;
            mode = APM_DEFAULT;
            return this;
        }

        public AttributeRequestDataImplBuilder withPassword(Boolean password) {
            this.password = password;
            return this;
        }

        public AttributeRequestDataImplBuilder withShowPassword(ShowPassword showPassword) {
            this.showPassword = showPassword;
            return this;
        }

        public AttributeRequestDataImplBuilder withTextContentSecurity(TextContentSecurity textContentSecurity) {
            this.textContentSecurity = textContentSecurity;
            return this;
        }

        public AttributeRequestDataImplBuilder withFormulaType(FormulaType formulaType) {
            this.formulaType = formulaType;
            return this;
        }

        public AttributeRequestDataImplBuilder withFormulaCode(String formulaCode) {
            this.formulaCode = formulaCode;
            return this;
        }

        public AttributeRequestDataImplBuilder withTextContentSecurity(String textContentSecurity) {
            this.textContentSecurity = parseEnum(textContentSecurity, TextContentSecurity.class);
            return this;
        }

        public AttributeRequestDataImplBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public AttributeRequestDataImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AttributeRequestDataImplBuilder withDmsCategory(String dmsCategory) {
            this.dmsCategory = dmsCategory;
            return this;
        }

        public AttributeRequestDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public AttributeRequestDataImplBuilder withDomainName(String domainName) {
            this.domainName = domainName;
            return this;
        }

        public AttributeRequestDataImplBuilder withDirection(String direction) {
            this.direction = direction;
            return this;
        }

        public AttributeRequestDataImplBuilder withDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public AttributeRequestDataImplBuilder withGroup(String group) {
            this.group = group;
            return this;
        }

        public AttributeRequestDataImplBuilder withTargetClass(String targetClass) {
            this.targetClass = targetClass;
            return this;
        }

        public AttributeRequestDataImplBuilder withTargetType(String targetType) {
            this.targetType = targetType;
            return this;
        }

        public AttributeRequestDataImplBuilder withEditorType(String editorType) {
            this.editorType = editorType;
            return this;
        }

        public AttributeRequestDataImplBuilder withFilter(String filter) {
            this.filter = filter;
            return this;
        }

        public AttributeRequestDataImplBuilder withLookupType(String lookupType) {
            this.lookupType = lookupType;
            return this;
        }

        public AttributeRequestDataImplBuilder withUnitOfMeasure(String unitOfMeasure) {
            this.unitOfMeasure = unitOfMeasure;
            return this;
        }

        public AttributeRequestDataImplBuilder withUnitOfMeasureLocation(String unitOfMeasureLocation) {
            this.unitOfMeasureLocation = unitOfMeasureLocation;
            return this;
        }

        public AttributeRequestDataImplBuilder withShowInGrid(Boolean showInList) {
            this.showInGrid = showInList;
            return this;
        }

        public AttributeRequestDataImplBuilder withUnique(Boolean unique) {
            this.unique = unique;
            return this;
        }

        public AttributeRequestDataImplBuilder withRequired(Boolean required) {
            this.required = required;
            return this;
        }

        public AttributeRequestDataImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public AttributeRequestDataImplBuilder withShowThousandsSeparator(Boolean showThousandsSeparator) {
            this.showThousandsSeparator = showThousandsSeparator;
            return this;
        }

        public AttributeRequestDataImplBuilder withShowLabel(Boolean showLabel) {
            this.showLabel = showLabel;
            return this;
        }

        public AttributeRequestDataImplBuilder withLabelRequired(Boolean labelRequired) {
            this.labelRequired = labelRequired;
            return this;
        }

        public AttributeRequestDataImplBuilder withMode(String mode) {
            return this.withMode(AttributePermissionMode.valueOf(mode));
        }

        public AttributeRequestDataImplBuilder withMode(AttributePermissionMode mode) {
            this.mode = mode;
            return this;
        }

        public AttributeRequestDataImplBuilder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public AttributeRequestDataImplBuilder withPrecision(Integer precision) {
            this.precision = precision;
            return this;
        }

        public AttributeRequestDataImplBuilder withScale(Integer scale) {
            this.scale = scale;
            return this;
        }

        public AttributeRequestDataImplBuilder withLength(Integer length) {
            this.length = length;
            return this;
        }

        public AttributeRequestDataImplBuilder withClassOrder(Integer classOrder) {
            this.classOrder = classOrder;
            return this;
        }

        public AttributeRequestDataImplBuilder withIpType(String ipType) {
            this.ipType = ipType;
            return this;
        }

        public AttributeRequestDataImplBuilder withMasterDetailDescription(String masterDetailDescription) {
            this.masterDetailDescription = masterDetailDescription;
            return this;
        }

        public AttributeRequestDataImplBuilder withMasterDetail(Boolean isMasterDetail) {
            this.isMasterDetail = isMasterDetail;
            return this;
        }

        public AttributeRequestDataImplBuilder withTrimEnabled(Boolean isTrimEnabled) {
            this.isTrimEnabled = isTrimEnabled;
            return this;
        }

        public AttributeRequestDataImplBuilder withMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        @Override
        public AttributeRequestDataImpl build() {
            return new AttributeRequestDataImpl(this);
        }

    }
}
