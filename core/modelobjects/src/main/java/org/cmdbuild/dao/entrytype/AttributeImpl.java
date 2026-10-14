package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.AttributeMetadataImpl.AttributeMetadataImplBuilder;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.CLASSORDER;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.GROUP;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class AttributeImpl implements Attribute {

    private final EntryType owner;
    private final CardAttributeType type;
    private final String name;
    private final AttributeMetadata meta;
    private final AttributePermissions permissions;

    private AttributeImpl(AttributeImplBuilder builder) {
        this.owner = checkNotNull(builder.owner, "attribute owner is null");
        this.type = checkNotNull(builder.type, "attribute type is null");
        this.name = checkNotBlank(builder.name, "attribute name is null");
        this.meta = AttributeMetadataImpl.copyOf(checkNotNull(builder.meta)).withType(type).build();
        this.permissions = firstNotNull(builder.permissions, AttributePermissionsImpl.builder().withPermissions(meta.getPermissionMap()).build());
    }

    @Override
    public EntryType getOwner() {
        return owner;
    }

    @Override
    public CardAttributeType getType() {
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
    public String toString() {
        return "Attribute{" + "owner=" + owner.getName() + ", name=" + name + '}';
    }

    @Override
    public Map<PermissionScope, Set<AttributePermission>> getPermissionMap() {
        return permissions.getPermissionMap();
    }

    public static AttributeImplBuilder builder() {
        return new AttributeImplBuilder();
    }

    public static AttributeImplBuilder copyOf(AttributeWithoutOwner source) {
        return new AttributeImplBuilder()
                .withMeta(source.getMetadata())
                .withType(source.getType())
                .withName(source.getName())
                .withPermissions(source);
    }

    public static AttributeImplBuilder copyOf(Attribute source) {
        return copyOf((AttributeWithoutOwner) source)
                .withOwner(source.getOwner());
    }

    public static class AttributeImplBuilder implements Builder<AttributeImpl, AttributeImplBuilder> {

        private EntryType owner;
        private CardAttributeType type;
        private String name;
        private AttributeMetadata meta = new AttributeMetadataImpl();
        private AttributePermissions permissions;

        public AttributeImplBuilder withOwner(EntryType owner) {
            this.owner = owner;
            return this;
        }

        public AttributeImplBuilder withType(CardAttributeType type) {
            this.type = type;
            return this;
        }

        public AttributeImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AttributeImplBuilder withDescription(String description) {
            return this.withMeta(AttributeMetadataImpl.copyOf(meta).withDescription(description).build());
        }

        public AttributeImplBuilder withMode(AttributePermissionMode attributePermissionMode) {
            return this.withMeta(AttributeMetadataImpl.copyOf(meta).withMode(attributePermissionMode).build());
        }

        public AttributeImplBuilder withMeta(AttributeMetadata meta) {
            this.meta = meta;
            return this;
        }

        public AttributeImplBuilder withMeta(Consumer<AttributeMetadataImplBuilder> consumer) {
            return this.withMeta(AttributeMetadataImpl.copyOf(meta).accept(consumer).build());
        }

        public AttributeImplBuilder withPermissions(AttributePermissions permissions) {
            this.permissions = permissions;
            return this;
        }

        public AttributeImplBuilder withIndex(Integer index) {
            this.meta = AttributeMetadataImpl.copyOf(meta).withIndex(index).build();
            return this;
        }

        public AttributeImplBuilder withClassOrderInMeta(int classorder) {
            return AttributeImplBuilder.this.withMeta(new AttributeMetadataImpl(map(meta.getAll()).with(CLASSORDER, String.valueOf(classorder))));
        }

        public AttributeImplBuilder withMeta(String... items) {
            return this.withMeta(AttributeMetadataImpl.copyOf(meta).withMetadata(map(items)).build());
        }

        public AttributeImplBuilder withGroup(String group) {
            return this.withMeta(GROUP, group);
        }

        @Override
        public AttributeImpl build() {
            return new AttributeImpl(this);
        }

    }
}
