package org.cmdbuild.auth.grant;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.Grant;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import org.cmdbuild.auth.grant.GrantPrivilege;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.auth.grant.PrivilegedObjectType;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class GrantImpl implements Grant {

    private final PrivilegedObjectType objectType;
    private final Set<GrantPrivilege> privileges;
    private final PrivilegeSubjectWithInfo object;
    private final String privilegeFilter;
    private final Map<String, GrantAttributePrivilege> attributePrivileges;
    private final Map<String, GrantAttributePrivilege> dmsPrivileges;
    private final Map<String, GrantAttributePrivilege> gisPrivileges;
    private final Map<String, Object> customPrivileges;

    private GrantImpl(PrivilegePairImplBuilder builder) {
        this.objectType = checkNotNull(builder.objectType);
        this.object = checkNotNull(builder.object);
        this.privilegeFilter = builder.privilegeFilter;
        this.privileges = ImmutableSet.copyOf(checkNotNull(builder.privileges));
        switch (objectType) {
            case POT_CLASS -> {
                this.attributePrivileges = ImmutableMap.copyOf(firstNotNull(builder.attributePrivileges, emptyMap()));
                this.dmsPrivileges = ImmutableMap.copyOf(firstNotNull(builder.dmsPrivileges, emptyMap()));
                this.gisPrivileges = ImmutableMap.copyOf(firstNotNull(builder.gisPrivileges, emptyMap()));
            }
            case POT_PROCESS -> {
                this.attributePrivileges = ImmutableMap.copyOf(firstNotNull(builder.attributePrivileges, emptyMap()));
                this.dmsPrivileges = ImmutableMap.copyOf(firstNotNull(builder.dmsPrivileges, emptyMap()));
                this.gisPrivileges = emptyMap();
            }
            default -> {
                this.attributePrivileges = emptyMap();
                this.dmsPrivileges = emptyMap();
                this.gisPrivileges = emptyMap();
            }
        }
        this.customPrivileges = ImmutableMap.copyOf(firstNotNull(builder.customPrivileges, emptyMap()));
    }

    @Override
    public PrivilegedObjectType getObjectType() {
        return objectType;
    }

    @Override
    public Set<GrantPrivilege> getPrivileges() {
        return privileges;
    }

    @Override
    public PrivilegeSubjectWithInfo getSubject() {
        return object;
    }

    @Override
    @Nullable
    public String getFilterOrNull() {
        return privilegeFilter;
    }

    @Override
    public Map<String, GrantAttributePrivilege> getAttributePrivileges() {
        return attributePrivileges;
    }

    @Override
    public Map<String, GrantAttributePrivilege> getDmsPrivileges() {
        return dmsPrivileges;
    }

    @Override
    public Map<String, GrantAttributePrivilege> getGisPrivileges() {
        return gisPrivileges;
    }

    @Override
    public Map<String, Object> getCustomPrivileges() {
        return customPrivileges;
    }

    public static PrivilegePairImplBuilder builder() {
        return new PrivilegePairImplBuilder();
    }

    public static PrivilegePairImplBuilder copyOf(Grant source) {
        return new PrivilegePairImplBuilder()
                .withObjectType(source.getObjectType())
                .withPrivileges(source.getPrivileges())
                .withObject(source.getSubject())
                .withPrivilegeFilter(source.getFilterOrNull())
                .withAttributePrivileges(source.getAttributePrivileges())
                .withDmsPrivileges(source.getDmsPrivileges())
                .withGisPrivileges(source.getGisPrivileges())
                .withCustomPrivileges(source.getCustomPrivileges());
    }

    public static class PrivilegePairImplBuilder implements Builder<GrantImpl, PrivilegePairImplBuilder> {

        private PrivilegedObjectType objectType;
        private Set<GrantPrivilege> privileges;
        private PrivilegeSubjectWithInfo object;
        private String privilegeFilter;
        private Map<String, GrantAttributePrivilege> attributePrivileges;
        private Map<String, GrantAttributePrivilege> dmsPrivileges;
        private Map<String, GrantAttributePrivilege> gisPrivileges;
        private Map<String, Object> customPrivileges;

        public PrivilegePairImplBuilder withObjectType(PrivilegedObjectType objectType) {
            this.objectType = objectType;
            return this;
        }

        public PrivilegePairImplBuilder withPrivileges(Set<GrantPrivilege> privileges) {
            this.privileges = privileges;
            return this;
        }

        public PrivilegePairImplBuilder withObject(PrivilegeSubjectWithInfo object) {
            this.object = object;
            return this;
        }

        public PrivilegePairImplBuilder withPrivilegeFilter(String privilegeFilter) {
            this.privilegeFilter = privilegeFilter;
            return this;
        }

        public PrivilegePairImplBuilder withAttributePrivileges(Map<String, GrantAttributePrivilege> attributePrivileges) {
            this.attributePrivileges = attributePrivileges;
            return this;
        }

        public PrivilegePairImplBuilder withDmsPrivileges(Map<String, GrantAttributePrivilege> dmsPrivileges) {
            this.dmsPrivileges = dmsPrivileges;
            return this;
        }

        public PrivilegePairImplBuilder withGisPrivileges(Map<String, GrantAttributePrivilege> gisPrivileges) {
            this.gisPrivileges = gisPrivileges;
            return this;
        }

        public PrivilegePairImplBuilder withCustomPrivileges(Map<String, Object> customPrivileges) {
            this.customPrivileges = customPrivileges;
            return this;
        }

        @Override
        public GrantImpl build() {
            return new GrantImpl(this);
        }

    }
}
