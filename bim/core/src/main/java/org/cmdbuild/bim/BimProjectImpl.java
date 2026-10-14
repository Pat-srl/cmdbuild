/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import java.time.ZonedDateTime;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@CardMapping("_BimProject")
public class BimProjectImpl implements BimProject {

    private final Long id, parentId;
    private final String poid, name, description, importMapping, ifcFormat;
    private final boolean isActive;
    private final ZonedDateTime lastCheckin;
    private final byte[] xktFile;

    private BimProjectImpl(BimProjectImplBuilder builder) {
        this.id = builder.id;
        this.parentId = builder.parentId;
        this.poid = isNullOrLtEqZero(id) ? nullToEmpty(builder.poid) : checkNotBlank(builder.poid, "bim project poid is null");
        this.name = checkNotBlank(builder.name, "bim project name is null");
        this.description = firstNotBlank(builder.description, this.name);
        this.importMapping = builder.importMapping;
        this.isActive = firstNotNull(builder.isActive, true);
        this.lastCheckin = builder.lastCheckin;
        this.ifcFormat = builder.ifcFormat;
        this.xktFile = builder.xktFile;
    }

    @Override
    @CardAttr(ATTR_ID)
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr("ParentId")
    @Nullable
    public Long getParentId() {
        return parentId;
    }

    @Override
    @CardAttr("ProjectId")
    public String getProjectId() {
        return poid;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr
    @Nullable
    public String getImportMapping() {
        return importMapping;
    }

    @Override
    @CardAttr
    public boolean isActive() {
        return isActive;
    }

    @Override
    @CardAttr
    @Nullable
    public ZonedDateTime getLastCheckin() {
        return lastCheckin;
    }

    @Override
    @Nullable
//    @CardAttr TODO
    public String getIfcFormat() {
        return ifcFormat;
    }

    @Override
    @Nullable
    @CardAttr("XktFile")
    public byte[] getXktFile() {
        return xktFile;
    }

    @Override
    public String toString() {
        return "BimProject{" + "id=" + id + ", poid=" + poid + ", name=" + name + ", description=" + description + ", isActive=" + isActive + '}';
    }

    public static BimProjectImplBuilder builder() {
        return new BimProjectImplBuilder();
    }

    public static BimProjectImplBuilder copyOf(BimProject source) {
        return new BimProjectImplBuilder()
                .withProjectId(source.getProjectId())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withImportMapping(source.getImportMapping())
                .withId(source.getId())
                .withParentId(source.getParentId())
                .withActive(source.isActive())
                .withLastCheckin(source.getLastCheckin())
                .withIfcFormat(source.getIfcFormat())
                .withXktFile(source.getXktFile());
    }

    public static class BimProjectImplBuilder implements Builder<BimProjectImpl, BimProjectImplBuilder> {

        private String poid;
        private String name;
        private String description;
        private String importMapping, ifcFormat;
        private Boolean isActive;
        private ZonedDateTime lastCheckin;
        private Long id, parentId;
        private byte[] xktFile;

        public BimProjectImplBuilder withProjectId(String projectId) {
            this.poid = projectId;
            return this;
        }

        public BimProjectImplBuilder withXktFile(byte[] xktFile) {
            this.xktFile = xktFile;
            return this;
        }

        public BimProjectImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public BimProjectImplBuilder withParentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public BimProjectImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public BimProjectImplBuilder withIfcFormat(String ifcFormat) {
            this.ifcFormat = ifcFormat;
            return this;
        }

        public BimProjectImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public BimProjectImplBuilder withImportMapping(String importMapping) {
            this.importMapping = importMapping;
            return this;
        }

        public BimProjectImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public BimProjectImplBuilder withLastCheckin(ZonedDateTime lastCheckin) {
            this.lastCheckin = lastCheckin;
            return this;
        }

        @Override
        public BimProjectImpl build() {
            return new BimProjectImpl(this);
        }

    }
}
