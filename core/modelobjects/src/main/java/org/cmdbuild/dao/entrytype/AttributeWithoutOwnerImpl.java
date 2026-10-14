/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import static org.cmdbuild.dao.beans.AttributeMetadataImpl.emptyAttributeMetadata;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class AttributeWithoutOwnerImpl implements AttributeWithoutOwner {

    private final CardAttributeType<?> type;
    private final String name;
    private final AttributeMetadata meta;
    private final AttributePermissions permissions;

    private AttributeWithoutOwnerImpl(AttributeWithoutOwnerImplBuilder builder) {
        this.type = checkNotNull(builder.type);
        this.name = checkNotBlank(builder.name);
        this.meta = AttributeMetadataImpl.copyOf(firstNotNull(builder.meta, emptyAttributeMetadata())).withType(type).build();
        this.permissions = AttributePermissionsImpl.builder().withPermissions(Optional.ofNullable(builder.permissions).map(AttributePermissions::getPermissionMap).orElse(meta.getPermissionMap())).build();
    }

    @Override
    public CardAttributeType<?> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AttributeMetadata getMetadata() {
        return meta;
    }

    @Override
    public Map<PermissionScope, Set<AttributePermission>> getPermissionMap() {
        return permissions.getPermissionMap();
    }

    @Override
    public String toString() {
        return "AttributeWithoutOwnerImpl{" + "name=" + name + '}';
    }

    public static AttributeWithoutOwnerImplBuilder builder() {
        return new AttributeWithoutOwnerImplBuilder();
    }

    public static AttributeWithoutOwnerImplBuilder copyOf(AttributeWithoutOwner source) {
        return new AttributeWithoutOwnerImplBuilder()
                .withType(source.getType())
                .withName(source.getName())
                .withPermissions(source)
                .withMeta(source.getMetadata());
    }

    public static class AttributeWithoutOwnerImplBuilder implements Builder<AttributeWithoutOwnerImpl, AttributeWithoutOwnerImplBuilder> {

        private CardAttributeType<?> type;
        private String name;
        private AttributeMetadata meta;
        private AttributePermissions permissions;

        public AttributeWithoutOwnerImplBuilder withType(CardAttributeType<?> type) {
            this.type = type;
            return this;
        }

        public AttributeWithoutOwnerImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AttributeWithoutOwnerImplBuilder withMeta(AttributeMetadata meta) {
            this.meta = meta;
            return this;
        }

        public AttributeWithoutOwnerImplBuilder withPermissions(AttributePermissions permissions) {
            this.permissions = permissions;
            return this;
        }

        @Override
        public AttributeWithoutOwnerImpl build() {
            return new AttributeWithoutOwnerImpl(this);
        }

    }
}
