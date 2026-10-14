/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dashboard.inner;

import jakarta.annotation.Nullable;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.utils.lang.Builder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static org.cmdbuild.dao.constants.SystemAttributes.*;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_Dashboard")
public class DashboardDataImpl implements DashboardData {

    private final String code, description, config, iconFont;
    private final Long id;
    private final boolean isActive;

    private DashboardDataImpl(DashboardDataImplBuilder builder) {
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.config = checkNotBlank(builder.config);
        this.id = builder.id;
        this.isActive = firstNotNull(builder.active, true);
        this.iconFont = nullToEmpty(builder.iconFont);
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getCode() {
        return code;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @CardAttr
    @Override
    public String getConfig() {
        return config;
    }

    @Override
    @CardAttr
    public boolean isActive() {
        return isActive;
    }

    @Override
    @Nullable
    @CardAttr("IconFont")
    public String getIconFont() {
        return iconFont;
    }

    public static DashboardDataImplBuilder builder() {
        return new DashboardDataImplBuilder();
    }

    public static DashboardDataImplBuilder copyOf(DashboardData source) {
        return new DashboardDataImplBuilder()
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withConfig(source.getConfig())
                .withId(source.getId())
                .withActive(source.isActive())
                .withIconFont(source.getIconFont());
    }

    public static class DashboardDataImplBuilder implements Builder<DashboardDataImpl, DashboardDataImplBuilder> {

        private String code;
        private String description;
        private String config;
        private Long id;
        private Boolean active;
        private String iconFont;

        public DashboardDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public DashboardDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public DashboardDataImplBuilder withConfig(String config) {
            this.config = config;
            return this;
        }

        public DashboardDataImplBuilder withData(Object data) {
            this.config = toJson(checkNotNull(data));
            return this;
        }

        public DashboardDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DashboardDataImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public DashboardDataImplBuilder withIconFont(String iconFont) {
            this.iconFont = iconFont;
            return this;
        }

        @Override
        public DashboardDataImpl build() {
            return new DashboardDataImpl(this);
        }

    }
}
