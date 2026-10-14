package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;

import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.graph.ClasseHierarchy;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class DomainImpl extends EntryTypeImpl implements Domain {

    private final ClassPermissions permissions;
    private final DomainMetadata metadata;
    private final Classe class1, class2;
    private final List<Classe> sourceClasses, targetClasses;

    private DomainImpl(DomainImplBuilder builder) {
        super(builder.name, builder.id, builder.attributes, emptyList());
        this.metadata = checkNotNull(builder.metadata);
        this.class1 = checkNotNull(builder.class1);
        this.class2 = checkNotNull(builder.class2);
        this.permissions = firstNotNull(builder.permissions, ClassPermissionsImpl.builder().withPermissions(metadata.getPermissions()).build());
        this.sourceClasses = checkNotNull(builder.sourceClasses).stream().filter(c -> !metadata.getDisabledSourceDescendants().contains(c.getName())).collect(toImmutableList());
        this.targetClasses = checkNotNull(builder.targetClasses).stream().filter(c -> !metadata.getDisabledTargetDescendants().contains(c.getName())).collect(toImmutableList());
    }

    @Override
    public DomainMetadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "DomainImpl{" + "name=" + getName() + '}';
    }

    @Override
    public Classe getSourceClass() {
        return class1;
    }

    @Override
    public Classe getTargetClass() {
        return class2;
    }

    @Override
    public List<Classe> getSourceClasses() {
        return sourceClasses;
    }

    @Override
    public List<Classe> getTargetClasses() {
        return targetClasses;
    }

    @Override
    public Map<PermissionScope, Set<ClassPermission>> getPermissionsMap() {
        return permissions.getPermissionsMap();
    }

    @Override
    public Map<String, Object> getOtherPermissions() {
        return permissions.getOtherPermissions();
    }

    public static DomainImplBuilder builder() {
        return new DomainImplBuilder();
    }

    public static DomainImplBuilder copyOf(Domain domain) {
        return builder()
                .withAllAttributes(domain.getAllAttributes())
                .withClass1(domain.getSourceClass(), domain.getSourceClasses())
                .withClass2(domain.getTargetClass(), domain.getTargetClasses())
                .withId(domain.getId())
                .withMetadata(domain.getMetadata())
                .withName(domain.getName())
                .withPermissions(domain);
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getDmsPermissions() {
        return emptyMap();
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getGisPermissions() {
        return emptyMap();
    }

    public static class DomainImplBuilder implements Builder<DomainImpl, DomainImplBuilder> {

        private final List<AttributeWithoutOwner> attributes = list();
        private ClassPermissions permissions;
        private String name;
        private Long id;
        private DomainMetadata metadata = new DomainMetadataImpl();
        private Classe class1, class2;
        private Collection<Classe> sourceClasses, targetClasses;

        public DomainImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DomainImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        /**
         * <b>Warning</b>: will replace all contained metadata.
         * 
         * @param metadata
         * @return 
         */
        public DomainImplBuilder withMetadata(DomainMetadata metadata) {
            this.metadata = metadata;
            return this;
        }
        
        /**
         * Will add given metadata
         * 
         * @param metadata
         * @return 
         */
        public DomainImplBuilder withMetadata(Consumer<DomainMetadataImpl.DomainMetadataImplBuilder> metadata) {
            this.metadata = DomainMetadataImpl.copyOf(this.metadata).accept(metadata).build();
            return this;
        }        

        public DomainImplBuilder withAllAttributes(Collection<? extends AttributeWithoutOwner> attributes) {
            this.attributes.addAll(attributes);
            return this;
        }

        public DomainImplBuilder withAttribute(AttributeWithoutOwner attribute) {
            this.attributes.add(attribute);
            return this;
        }

        public DomainImplBuilder withClass1(Classe dbClass) {
            this.class1 = dbClass;
            this.sourceClasses = singletonList(dbClass);
            return this;
        }

        public DomainImplBuilder withClass2(Classe dbClass) {
            this.class2 = dbClass;
            this.targetClasses = singletonList(dbClass);
            return this;
        }

        public DomainImplBuilder withClass1(ClasseHierarchy dbClass) {
            return this.withClass1(dbClass.getClasse(), dbClass.getDescendantsAndSelf());
        }

        public DomainImplBuilder withClass2(ClasseHierarchy dbClass) {
            return this.withClass2(dbClass.getClasse(), dbClass.getDescendantsAndSelf());
        }

        public DomainImplBuilder withClass1(Classe classe, Collection<Classe> classes) {
            this.class1 = classe;
            this.sourceClasses = classes;
            return this;
        }

        public DomainImplBuilder withClass2(Classe classe, Collection<Classe> classes) {
            this.class2 = classe;
            this.targetClasses = classes;
            return this;
        }

        public DomainImplBuilder withPermissions(ClassPermissions permissions) {
            this.permissions = permissions;
            return this;
        }

        @Override
        public DomainImpl build() {
            return new DomainImpl(this);
        }

    }

}
