package org.cmdbuild.dao.entrytype;

import jakarta.annotation.Nullable;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.function.Consumer;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl.ClassMetadataImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ClassDefinitionImpl implements ClassDefinition {

    private final Long oid;
    private final String name, parentName;
    private final ClassMetadata metadata;

    private ClassDefinitionImpl(ClassDefinitionImplBuilder builder) {
        this.oid = builder.oid;
        this.name = checkNotBlank(builder.name);
        this.parentName = builder.parent;
        this.metadata = checkNotNull(builder.metadata);
    }

    @Override
    @Nullable
    public Long getOid() {
        return oid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @Nullable
    public String getParentOrNull() {
        return parentName;
    }

    @Override
    public ClassMetadata getMetadata() {
        return metadata;
    }

    public static ClassDefinitionImplBuilder builder() {
        return new ClassDefinitionImplBuilder();
    }

    public static ClassDefinitionImpl build(String name) {
        return builder().withName(name).build();
    }

    public static ClassDefinitionImpl build(String name, ClassType type) {
        return builder().withName(name).withMetadata((m) -> m.withClassType(type)).build();
    }

    public static ClassDefinitionImplBuilder copyOf(Classe classe) {
        return builder()
                .withOid(classe.getOid())
                .withParent(classe.getParentOrNull())
                .withName(classe.getName())
                .withMetadata(classe.getMetadata());
    }

    public static ClassDefinitionImplBuilder copyOf(ClassDefinition source) {
        return new ClassDefinitionImplBuilder()
                .withOid(source.getOid())
                .withName(source.getName())
                .withParent(source.getParentOrNull())
                .withMetadata(source.getMetadata());
    }

    @Override
    public String toString() {
        return "ClassDefinitionImpl{" + "oid=" + oid + ", name=" + name + '}';
    }

    public static class ClassDefinitionImplBuilder implements Builder<ClassDefinitionImpl, ClassDefinitionImplBuilder> {

        private Long oid;
        private String name;
        private String parent;
        private ClassMetadata metadata = new ClassMetadataImpl();

        public ClassDefinitionImplBuilder withOid(Long oid) {
            this.oid = oid;
            return this;
        }

        public ClassDefinitionImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ClassDefinitionImplBuilder withParent(Classe parent) {
            return this.withParent(parent == null ? null : parent.getName());
        }

        public ClassDefinitionImplBuilder withParent(String parent) {
            this.parent = parent;
            return this;
        }

        public ClassDefinitionImplBuilder withMetadata(ClassMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public ClassDefinitionImplBuilder withMetadata(Consumer<ClassMetadataImplBuilder> metadata) {
            this.metadata = (this.metadata == null ? ClassMetadataImpl.builder() : ClassMetadataImpl.copyOf(this.metadata)).accept(metadata).build();
            return this;
        }

        public ClassDefinitionImplBuilder withSuperclass(boolean isSuperclass) {
            return withMetadata(ClassMetadataImpl.copyOf(metadata).withSuperclass(isSuperclass).build());
        }

        @Override
        public ClassDefinitionImpl build() {
            return new ClassDefinitionImpl(this);
        }

    }
}
