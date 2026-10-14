/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.offline;

import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;

@CardMapping("_Offline")
public class OfflineDataImpl implements OfflineData {

    private final Long id;
    private final String code, description, metadata;
    private final boolean enabled;

    private OfflineDataImpl(OfflineDataImplBuilder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.description = builder.description;
        this.metadata = builder.metadata;
        this.enabled = builder.enabled;
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

    @Override
    @CardAttr("Metadata")
    public String getMetadata() {
        return metadata;
    }

    @Override
    @CardAttr("Enabled")
    public Boolean isEnabled() {
        return enabled;
    }

    public static OfflineDataImplBuilder builder() {
        return new OfflineDataImplBuilder();
    }

    public static OfflineDataImplBuilder copyOf(OfflineData source) {
        return new OfflineDataImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withMetadata(source.getMetadata())
                .withEnabled(source.isEnabled());
    }

    public static class OfflineDataImplBuilder implements Builder<OfflineDataImpl, OfflineDataImplBuilder> {

        private Long id;
        private String code, description, metadata;
        private boolean enabled;

        public OfflineDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public OfflineDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public OfflineDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public OfflineDataImplBuilder withMetadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public OfflineDataImplBuilder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        @Override
        public OfflineDataImpl build() {
            return new OfflineDataImpl(this);
        }

    }
}
