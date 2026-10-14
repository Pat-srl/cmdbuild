/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.Map;
import java.util.Set;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.transformValues;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonMap;
import java.util.EnumSet;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.intersectClassPermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.intersectClassPermissionsNoExpand;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.mergeClassPermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.removeClassPermissions;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_CORE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_UI;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class ClassPermissionsImpl implements ClassPermissions {

    private final static ClassPermissions ALL = new ClassPermissionsImplBuilder().withPermissions(map(PS_CORE, EnumSet.allOf(ClassPermission.class), PS_SERVICE, EnumSet.allOf(ClassPermission.class), PS_UI, EnumSet.allOf(ClassPermission.class))).build(),
            NONE = new ClassPermissionsImplBuilder().withPermissions(map(PS_CORE, emptySet(), PS_SERVICE, emptySet(), PS_UI, emptySet())).build();

    private final Map<PermissionScope, Set<ClassPermission>> permissions;
    private final Map<String, Object> otherPermissions;
    private final Map<String, Set<GrantAttributePrivilege>> dmsPermissions;
    private final Map<String, Set<GrantAttributePrivilege>> gisPermissions;

    private ClassPermissionsImpl(ClassPermissionsImplBuilder builder) {
        this.permissions = ImmutableMap.copyOf(transformValues(checkNotNull(builder.permissions), ImmutableSet::copyOf));
        this.otherPermissions = ImmutableMap.copyOf(builder.otherPermissions);
        this.dmsPermissions = ImmutableMap.copyOf(builder.dmsPermissions);
        this.gisPermissions = ImmutableMap.copyOf(builder.gisPermissions);
    }

    @Override
    public Map<PermissionScope, Set<ClassPermission>> getPermissionsMap() {
        return permissions;
    }

    @Override
    public Map<String, Object> getOtherPermissions() {
        return otherPermissions;
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getDmsPermissions() {
        return dmsPermissions;
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getGisPermissions() {
        return gisPermissions;
    }

    public static ClassPermissions all() {
        return ALL;
    }

    public static ClassPermissions none() {
        return NONE;
    }

    public static ClassPermissionsImplBuilder builder() {
        return new ClassPermissionsImplBuilder();
    }

    public static ClassPermissionsImplBuilder copyOf(ClassPermissions source) {
        return new ClassPermissionsImplBuilder()
                .withPermissions(source.getPermissionsMap())
                .withOtherPermissions(source.getOtherPermissions())
                .withDmsPermissions(source.getDmsPermissions())
                .withGisPermissions(source.getGisPermissions());
    }

    public static class ClassPermissionsImplBuilder implements Builder<ClassPermissionsImpl, ClassPermissionsImplBuilder> {

        private Map<PermissionScope, Set<ClassPermission>> permissions = ImmutableMap.of(PS_CORE, emptySet(), PS_SERVICE, emptySet(), PS_UI, emptySet());
        private final Map<String, Object> otherPermissions = map();
        private final Map<String, Set<GrantAttributePrivilege>> dmsPermissions = map();
        private final Map<String, Set<GrantAttributePrivilege>> gisPermissions = map();

        public ClassPermissionsImplBuilder withPermissions(Map<PermissionScope, Set<ClassPermission>> permissions) {
            this.permissions = permissions;
            return this;
        }

        @Override
        public ClassPermissionsImpl build() {
            return new ClassPermissionsImpl(this);
        }

        public ClassPermissionsImplBuilder addPermissions(ClassPermissions toAdd) {
            this.permissions = mergeClassPermissions(permissions, toAdd.getPermissionsMap());
            return this;
        }

        public ClassPermissionsImplBuilder addPermissions(PermissionScope scope, Set<ClassPermission> toAdd) {
            this.permissions = mergeClassPermissions(permissions, map(none().getPermissionsMap()).with(scope, toAdd));
            return this;
        }

        public ClassPermissionsImplBuilder removePermissions(PermissionScope scope, Set<ClassPermission> toRemove) {
            this.permissions = removeClassPermissions(permissions, singletonMap(scope, toRemove));
            return this;
        }

        public ClassPermissionsImplBuilder removePermissionsExactly(Set<ClassPermission> toRemove) {
            permissions = map(permissions).mapValues(p -> set(p).without(toRemove));
            return this;
        }

        public ClassPermissionsImplBuilder intersectPermissions(PermissionScope scope, Set<ClassPermission> toIntersect) {
            return this.intersectPermissions(singletonMap(scope, toIntersect));
        }

        public ClassPermissionsImplBuilder intersectPermissions(ClassPermissions toIntersect) {
            return this.intersectPermissions(toIntersect.getPermissionsMap());
        }

        public ClassPermissionsImplBuilder intersectPermissions(Map<PermissionScope, Set<ClassPermission>> toIntersect) {
            this.permissions = intersectClassPermissions(permissions, toIntersect);
            return this;
        }

        public ClassPermissionsImplBuilder intersectPermissionsNoExpand(PermissionScope scope, Set<ClassPermission> toIntersect) {
            return this.intersectPermissionsNoExpand(singletonMap(scope, toIntersect));
        }

        public ClassPermissionsImplBuilder intersectPermissionsNoExpand(Map<PermissionScope, Set<ClassPermission>> toIntersect) {
            this.permissions = intersectClassPermissionsNoExpand(permissions, toIntersect);
            return this;
        }

        public ClassPermissionsImplBuilder withOtherPermissions(Map<String, Object> customPermissions) {
            this.otherPermissions.clear();
            this.otherPermissions.putAll(customPermissions);
            return this;
        }

        public ClassPermissionsImplBuilder addOtherPermissions(Map<String, Object> customPermissions) {
            this.otherPermissions.putAll(customPermissions);
            return this;
        }

        public ClassPermissionsImplBuilder withOtherPermissions(String key, Object value) {
            this.otherPermissions.put(key, value);
            return this;
        }

        public ClassPermissionsImplBuilder withDmsPermissions(Map<String, Set<GrantAttributePrivilege>> dmsPermissions) {
            this.dmsPermissions.clear();
            this.dmsPermissions.putAll(dmsPermissions);
            return this;
        }

        public ClassPermissionsImplBuilder withGisPermissions(Map<String, Set<GrantAttributePrivilege>> gisPermissions) {
            this.gisPermissions.clear();
            this.gisPermissions.putAll(gisPermissions);
            return this;
        }

    }
}
