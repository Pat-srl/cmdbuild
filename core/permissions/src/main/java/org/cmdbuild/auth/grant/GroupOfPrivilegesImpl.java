/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import com.google.common.collect.ImmutableSet;
import static java.util.Collections.emptyMap;
import java.util.EnumSet;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class GroupOfPrivilegesImpl implements GroupOfPrivileges {

    private final Map<GrantPrivilegeScope, Set<GrantPrivilege>> privileges;
    private final Map<String, Set<GrantAttributePrivilege>> attributePrivileges;
    private final Map<String, Set<GrantAttributePrivilege>> dmsPrivileges;
    private final Map<String, Set<GrantAttributePrivilege>> gisPrivileges;
    private final Map<String, Object> customPrivileges;
    private final String source;
    private final CmdbFilter filter;

    private GroupOfPrivilegesImpl(GroupOfPrivilegesImplBuilder builder) {
        this.source = checkNotBlank(builder.source);
        this.privileges = builder.privileges.entrySet().stream().map(e -> Pair.of(e.getKey(), ImmutableSet.copyOf(e.getValue()))).collect(toImmutableMap(Pair::getKey, Pair::getValue));
        checkArgument(EnumSet.allOf(GrantPrivilegeScope.class).equals(this.privileges.keySet()));
        this.attributePrivileges = ImmutableMap.copyOf(firstNotNull(builder.attributePrivileges, emptyMap()));
        this.dmsPrivileges = ImmutableMap.copyOf(firstNotNull(builder.dmsPrivileges, emptyMap()));
        this.gisPrivileges = ImmutableMap.copyOf(firstNotNull(builder.gisPrivileges, emptyMap()));
        this.filter = isBlank(builder.filter) ? CmdbFilterImpl.noopFilter() : parseFilter(builder.filter);
        this.customPrivileges = ImmutableMap.copyOf(firstNotNull(builder.customPrivileges, emptyMap()));
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Map<GrantPrivilegeScope, Set<GrantPrivilege>> getPrivileges() {
        return privileges;
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getAttributePrivileges() {
        return attributePrivileges;
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getDmsPrivileges() {
        return dmsPrivileges;
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getGisPrivileges() {
        return gisPrivileges;
    }

    @Override
    public Map<String, Object> getCustomPrivileges() {
        return customPrivileges;
    }

    @Override
    public CmdbFilter getFilter() {
        return filter;
    }

    public static GroupOfPrivilegesImplBuilder builder() {
        return new GroupOfPrivilegesImplBuilder();
    }

    public static GroupOfPrivilegesImplBuilder copyOf(GroupOfPrivileges source) {
        return new GroupOfPrivilegesImplBuilder()
                .withPrivileges(source.getPrivileges())
                .withAttributePrivileges(source.getAttributePrivileges())
                .withDmsPrivileges(source.getDmsPrivileges())
                .withGisPrivileges(source.getGisPrivileges())
                .withCustomPrivileges(source.getCustomPrivileges())
                .withSource(source.getSource())
                .withFilter(source.getFilter());
    }

    public static class GroupOfPrivilegesImplBuilder implements Builder<GroupOfPrivilegesImpl, GroupOfPrivilegesImplBuilder> {

        private String source;
        private Map<GrantPrivilegeScope, Set<GrantPrivilege>> privileges = map();
        private Map<String, Set<GrantAttributePrivilege>> attributePrivileges;
        private Map<String, Set<GrantAttributePrivilege>> dmsPrivileges;
        private Map<String, Set<GrantAttributePrivilege>> gisPrivileges;
        private String filter;
        private Map<String, Object> customPrivileges;

        public GroupOfPrivilegesImplBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withPrivileges(GrantPrivilegeScope scope, Set<GrantPrivilege> privileges) {
            this.privileges.put(scope, privileges);
            return this;
        }

        public GroupOfPrivilegesImplBuilder withPrivileges(Map<GrantPrivilegeScope, Set<GrantPrivilege>> privileges) {
            this.privileges.putAll(privileges);
            return this;
        }

        public GroupOfPrivilegesImplBuilder withAttributePrivileges(Map<String, Set<GrantAttributePrivilege>> attributePrivileges) {
            this.attributePrivileges = attributePrivileges;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withDmsPrivileges(Map<String, Set<GrantAttributePrivilege>> dmsPrivileges) {
            this.dmsPrivileges = dmsPrivileges;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withGisPrivileges(Map<String, Set<GrantAttributePrivilege>> gisPrivileges) {
            this.gisPrivileges = gisPrivileges;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withCustomPrivileges(Map<String, Object> customPrivileges) {
            this.customPrivileges = customPrivileges;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withFilter(@Nullable String filter) {
            this.filter = filter;
            return this;
        }

        public GroupOfPrivilegesImplBuilder withFilter(@Nullable CmdbFilter filter) {
            this.filter = filter == null ? null : CmFilterUtils.serializeFilter(filter);
            return this;
        }

        @Override
        public GroupOfPrivilegesImpl build() {
            return new GroupOfPrivilegesImpl(this);
        }

    }
}
