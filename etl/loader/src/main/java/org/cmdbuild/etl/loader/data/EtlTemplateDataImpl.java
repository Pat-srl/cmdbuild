/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.etl.loader.EtlTemplateConfig;

@CardMapping("_ImportExportTemplate")
public class EtlTemplateDataImpl implements EtlTemplateData {

    private final Long id;
    private final String code, description;
    private final EtlTemplateConfig config;
    private final boolean isActive;

    private EtlTemplateDataImpl(EtlTemplateDataImplBuilder builder) {
        this.id = builder.id;
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.isActive = firstNotNull(builder.isActive, true);
        this.config = checkNotNull(builder.config);
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getCode() {
        return code;
    }

    @CardAttr(ATTR_DESCRIPTION)
    @Override
    public String getDescription() {
        return description;
    }

    @CardAttr("Config")
    @JsonBean(EtlTemplateConfigImpl.class)
    @Override
    public EtlTemplateConfig getConfig() {
        return config;
    }

    @Override
    @CardAttr("Active")
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "ImportExportTemplateData{" + "id=" + id + ", code=" + code + '}';
    }

    public static EtlTemplateDataImplBuilder builder() {
        return new EtlTemplateDataImplBuilder();
    }

    public static EtlTemplateDataImplBuilder copyOf(EtlTemplateData source) {
        return new EtlTemplateDataImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withConfig(source.getConfig())
                .withActive(source.isActive());
    }

    public static class EtlTemplateDataImplBuilder implements Builder<EtlTemplateDataImpl, EtlTemplateDataImplBuilder> {

        private Long id;
        private String code;
        private String description;
        private EtlTemplateConfig config;
        private Boolean isActive;

        public EtlTemplateDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EtlTemplateDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public EtlTemplateDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EtlTemplateDataImplBuilder withConfig(EtlTemplateConfig config) {
            this.config = config;
            return this;
        }

        public EtlTemplateDataImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        @Override
        public EtlTemplateDataImpl build() {
            return new EtlTemplateDataImpl(this);
        }

    }
}
