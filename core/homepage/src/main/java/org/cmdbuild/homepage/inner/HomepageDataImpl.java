/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.homepage.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.homepage.HomepageData;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_Homepage")
public class HomepageDataImpl implements HomepageData {

    private final String code, description, config, iconFont, groupName;
    private final Long id;
    private final boolean isActive;

    public HomepageDataImpl(HomepageDataImplBuilder builder) {
        this.code = checkNotBlank(builder.groupName);
        this.description = nullToEmpty(builder.groupName);
        this.config = checkNotBlank(builder.config);
        this.id = builder.id;
        this.isActive = firstNotNull(builder.active, true);
        this.iconFont = nullToEmpty(builder.iconFont);
        this.groupName = checkNotBlank(builder.groupName);
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
    @CardAttr("GroupName")
    public String getGroupName() {
        return groupName;
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

    public static HomepageDataImplBuilder builder() {
        return new HomepageDataImplBuilder();
    }

    public static HomepageDataImplBuilder copyOf(HomepageData source) {
        checkNotNull(source, "source cannot be null");
        return new HomepageDataImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withConfig(source.getConfig())
                .withActive(source.isActive())
                .withIconFont(source.getIconFont())
                .withGroupName(source.getGroupName());
    }

    public static class HomepageDataImplBuilder implements Builder<HomepageDataImpl, HomepageDataImplBuilder> {

        private Long id;
        private String code, description, config, iconFont, groupName;
        private Boolean active;

        public HomepageDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public HomepageDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public HomepageDataImplBuilder withConfig(String config) {
            this.config = config;
            return this;
        }

        public HomepageDataImplBuilder withData(Object data) {
            this.config = toJson(checkNotNull(data));
            return this;
        }

        public HomepageDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public HomepageDataImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public HomepageDataImplBuilder withIconFont(String iconFont) {
            this.iconFont = iconFont;
            return this;
        }

        public HomepageDataImplBuilder withGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        @Override
        public HomepageDataImpl build() {
            return new HomepageDataImpl(this);
        }
    }
}