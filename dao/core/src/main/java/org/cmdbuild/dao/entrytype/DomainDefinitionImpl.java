/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.beans.DomainMetadataImpl.DomainMetadataImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class DomainDefinitionImpl implements DomainDefinition {

    private final Long oid;
    private final String name, sourceClass, targetClass;
    private final DomainMetadata metadata;

    private DomainDefinitionImpl(DomainDefinitionImplBuilder builder) {
        this.oid = builder.oid;
        this.sourceClass = checkNotBlank(builder.sourceClassName, "source class is null");
        this.targetClass = checkNotBlank(builder.targetClassName, "target class is null");
        this.name = firstNotBlank(builder.name, format("%s%s", sourceClass, targetClass));//TODO check size limit
        this.metadata = checkNotNull(builder.metadata, "domain metadata is null");
    }

    @Nullable
    @Override
    public Long getOid() {
        return oid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSourceClassName() {
        return sourceClass;
    }

    @Override
    public String getTargetClassName() {
        return targetClass;
    }

    @Override
    public DomainMetadata getMetadata() {
        return metadata;
    }

    public static DomainDefinitionImplBuilder builder() {
        return new DomainDefinitionImplBuilder();
    }

    public static DomainDefinitionImplBuilder copyOf(DomainDefinition source) {
        return new DomainDefinitionImplBuilder()
                .withOid(source.getOid())
                .withName(source.getName())
                .withSourceClass(source.getSourceClassName())
                .withTargetClass(source.getTargetClassName())
                .withMetadata(source.getMetadata());
    }

    public static class DomainDefinitionImplBuilder implements Builder<DomainDefinitionImpl, DomainDefinitionImplBuilder> {

        private Long oid;
        private String name;
        private String sourceClassName;
        private String targetClassName;
        private DomainMetadata metadata = new DomainMetadataImpl();

        public DomainDefinitionImplBuilder withOid(Long oid) {
            this.oid = oid;
            return this;
        }

        public DomainDefinitionImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DomainDefinitionImplBuilder withSourceClass(Classe sourceClass) {
            this.sourceClassName = sourceClass.getName();
            return this;
        }

        public DomainDefinitionImplBuilder withTargetClass(Classe targetClass) {
            this.targetClassName = targetClass.getName();
            return this;
        }

        public DomainDefinitionImplBuilder withSourceClass(String sourceClassName) {
            this.sourceClassName = sourceClassName;
            return this;
        }

        public DomainDefinitionImplBuilder withTargetClass(String targetClassName) {
            this.targetClassName = targetClassName;
            return this;
        }

        /**
         * <b>Warning</b>: will replace all contained metadata.
         * 
         * @param metadata
         * @return 
         */
        public DomainDefinitionImplBuilder withMetadata(DomainMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Will add given metadata
         * 
         * @param metadata
         * @return 
         */
        public DomainDefinitionImplBuilder withMetadata(Consumer<DomainMetadataImplBuilder> metadata) {
            this.metadata = DomainMetadataImpl.copyOf(this.metadata).accept(metadata).build();
            return this;
        }

        public DomainDefinitionImplBuilder withCascadeAction(CascadeAction direct, CascadeAction inverse) {
            return this.withMetadata(m -> m.withCascadeAction(direct, inverse));
        }

        public DomainDefinitionImplBuilder withCardinality(DomainCardinality cardinality) {
            this.metadata = DomainMetadataImpl.copyOf(metadata).withCardinality(cardinality).build();
            return this;
        }

        @Override
        public DomainDefinitionImpl build() {
            return new DomainDefinitionImpl(this);
        }

    }
}
