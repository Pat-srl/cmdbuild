package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.commons.lang3.builder.Builder;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl.ClassMetadataImplBuilder;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_BASIC;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_READTOUCHED;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toList;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class ClasseImpl extends EntryTypeImpl implements Classe {

    private final ClassPermissions classPermissions;
    private final ClassMetadata meta;
    private final List<String> ancestors;

    private ClasseImpl(ClasseBuilder builder) {
        super(builder.name, builder.id, builder.attributes, builder.attributeGroups);
        this.meta = checkNotNull(builder.metadata, "classe meta cannot be null");
        this.classPermissions = ClassPermissionsImpl.copyOf(firstNotNull(builder.permissions, ClassPermissionsImpl.builder().withPermissions(meta.getPermissions()).build())).accept(p -> {
            if (!meta.isProcess()) {
                p.removePermissionsExactly(EnumSet.of(CP_WF_READTOUCHED, CP_WF_BASIC));
            }
        }).build();
        this.ancestors = ImmutableList.copyOf(firstNotNull(builder.ancestors, emptyList()));
    }

    @Override
    public void accept(CMEntryTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ClassMetadata getMetadata() {
        return meta;
    }

    @Override
    public Map<PermissionScope, Set<ClassPermission>> getPermissionsMap() {
        return classPermissions.getPermissionsMap();
    }

    @Override
    public Map<String, Object> getOtherPermissions() {//TODO improve this (delegate)
        return classPermissions.getOtherPermissions();
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getDmsPermissions() {//TODO improve this (delegate)
        return classPermissions.getDmsPermissions();
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getGisPermissions() {//TODO improve this (delegate)
        return classPermissions.getGisPermissions();
    }

    @Override
    public List<String> getAncestors() {
        return ancestors;
    }

    @Override
    public String toString() {
        return "ClasseImpl{" + "name=" + getName() + '}';
    }

    public static ClasseBuilder builder() {
        return new ClasseBuilder();
    }

    public static ClasseBuilder copyOf(Classe classe) {
        return new ClasseBuilder()
                .withId(classe.getOid())
                .withName(classe.getName())
                .withMetadata(classe.getMetadata())
                .withPermissions(classe)
                .withAncestors(classe.getAncestors())
                .withAttributes(classe.getAllAttributes())
                .withAttributeGroups(classe.getAttributeGroups());
    }

    public static class ClasseBuilder implements Builder<ClasseImpl> {

        private List<AttributeWithoutOwner> attributes = list();
        private String name;
        private Long id;
        private ClassMetadata metadata = new ClassMetadataImpl();
        private ClassPermissions permissions;
        private List<String> ancestors;
        private List<AttributeGroupData> attributeGroups = list();

        public ClasseBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ClasseBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ClasseBuilder withMetadata(ClassMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public ClasseBuilder withMetadata(Consumer<ClassMetadataImplBuilder> b) {
            this.metadata = ClassMetadataImpl.copyOf(firstNotNull(metadata, new ClassMetadataImpl(emptyMap()))).accept(b).build();
            return this;
        }

        public ClasseBuilder withPermissions(ClassPermissions permissions) {
            this.permissions = permissions;
            return this;
        }

        public ClasseBuilder withAncestors(List<String> ancestors) {
            this.ancestors = ancestors;
            return this;
        }

        public ClasseBuilder withAttributes(Iterable<? extends AttributeWithoutOwner> attributes) {
            this.attributes = (List) toList(attributes);
            return this;
        }

        public ClasseBuilder withAttributeGroups(Iterable<? extends AttributeGroupData> attributeGroups) {
            this.attributeGroups = (List) toList(attributeGroups);
            return this;
        }

        @Override
        public ClasseImpl build() {
            return new ClasseImpl(this);
        }

    }
}
