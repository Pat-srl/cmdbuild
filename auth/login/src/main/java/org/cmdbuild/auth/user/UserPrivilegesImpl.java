package org.cmdbuild.auth.user;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.transform;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.ImmutableSet.copyOf;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.transformValues;
import com.google.common.collect.Multimaps;
import static java.util.Arrays.asList;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang3.tuple.Triple;
import org.cmdbuild.auth.grant.Grant;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_ON_FILTER_MISMATCH;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import org.cmdbuild.auth.grant.GrantPrivilege;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_SERVICE;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_UI;
import org.cmdbuild.auth.grant.GrantUtils;
import static org.cmdbuild.auth.grant.GrantUtils.mergePrivilegeGroups;
import static org.cmdbuild.auth.grant.GrantUtils.modeToPrivileges;
import org.cmdbuild.auth.grant.GroupOfNoPrivileges;
import org.cmdbuild.auth.grant.GroupOfPrivileges;
import org.cmdbuild.auth.grant.GroupOfPrivilegesImpl;
import org.cmdbuild.auth.grant.NoUserPrivilegesForObject;
import org.cmdbuild.auth.grant.PrivilegeSubject;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RolePrivilege;
import static org.cmdbuild.auth.utils.UserPrivilegesUtils.getDefaultCustomPrivileges;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPrivilegesImpl implements UserPrivileges {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Set<String> sourceGroups;
    private final Map<String, UserPrivilegesForObject> privilegesMap;
    private final Set<RolePrivilege> rolePrivileges;

    private UserPrivilegesImpl(UserPrivilegesImplBuilder builder) {
        this.rolePrivileges = ImmutableSet.copyOf(checkNotNull(builder.rolePrivileges));
        this.privilegesMap = ImmutableMap.copyOf(checkNotNull(builder.privilegesMap));
        this.sourceGroups = copyOf(checkNotNull(builder.sourceGroups));

        if (logger.isDebugEnabled()) {
            logger.debug("build user privileges");
            privilegesMap.forEach((key, value) -> {
                logger.debug("privileges for {} = {}", key, value);
            });
        }
    }

    @Override
    public Map<String, UserPrivilegesForObject> getAllPrivileges() {
        return privilegesMap;
    }

    @Override
    public Set<RolePrivilege> getRolePrivileges() {
        return rolePrivileges;
    }

    @Override
    public Set<String> getSourceGroups() {
        return sourceGroups;
    }

    @Override
    public boolean hasServicePrivilege(GrantPrivilege requested, PrivilegeSubject privilegedObject) {
        return getServicePrivilegesFor(privilegedObject.getPrivilegeId()).contains(requested);
    }

    private Set<GrantPrivilege> getServicePrivilegesFor(String privilegeId) {
        UserPrivilegesForObject privileges = privilegesMap.get(privilegeId);
        return privileges == null ? emptySet() : privileges.getMaxPrivilegesForSomeRecords().getServicePrivileges();
    }

    @Override
    public UserPrivilegesForObject getPrivilegesForObject(PrivilegeSubject object) {
        UserPrivilegesForObject privileges = privilegesMap.get(object.getPrivilegeId());
        if (privileges == null) {
            Map<String, Object> custom = getDefaultCustomPrivileges(object, rolePrivileges);
            if (custom.isEmpty()) {
                return NoUserPrivilegesForObject.INSTANCE;
            } else {
                return new UserPrivilegesImplBuilder.UserPrivilegesForObjectWithoutFilterImpl(GroupOfPrivilegesImpl.copyOf(GroupOfNoPrivileges.INSTANCE).withCustomPrivileges(custom).build());
            }
        }
        return privileges;
    }

    public static UserPrivilegesImplBuilder builder() {
        return new UserPrivilegesImplBuilder();
    }

    public static class UserPrivilegesImplBuilder implements Builder<UserPrivilegesImpl, UserPrivilegesImplBuilder> {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final List<Triple<Role, Grant, GroupOfPrivileges>> groupOfPrivileges = list();
        private final Map<String, UserPrivilegesForObject> privilegesMap = map();
        private final Set<String> sourceGroups = set();
        private final Set<RolePrivilege> rolePrivileges = set();

        @Override
        public UserPrivilegesImpl build() {
            Multimaps.index(groupOfPrivileges, (p) -> p.getMiddle().getSubject().getPrivilegeId()).asMap().forEach((target, privilegesList) -> {
                UserPrivilegesForObject privileges = buildUserPrivilegesForObject(target, privilegesList);
                PrivilegeSubjectWithInfo subject = privilegesList.iterator().next().getMiddle().getSubject();
                privileges = addDefaultCustomPrivileges(privileges, getDefaultCustomPrivileges(subject, rolePrivileges));
                logger.debug("add privileges for object = {} : {}", target, privileges);
                privilegesMap.put(target, privileges);
            });

            return new UserPrivilegesImpl(this);
        }

        private UserPrivilegesForObject buildUserPrivilegesForObject(String target, Collection<Triple<Role, Grant, GroupOfPrivileges>> privilegesList) {
            logger.debug("aggregating privileges for object = {}", target);
            List<Triple<Role, Grant, GroupOfPrivileges>> privilegesWithoutFilter = privilegesList.stream().filter((t) -> !t.getRight().hasFilter()).collect(toList());
            List<Triple<Role, Grant, GroupOfPrivileges>> privilegesWithFilter = privilegesList.stream().filter((t) -> t.getRight().hasFilter()).collect(toList());

            String sumGroupId = buildSumGroupId(privilegesWithoutFilter);
            GroupOfPrivileges minPrivilegesForAllRecords = mergePrivilegeGroups(transform(privilegesWithoutFilter, Triple::getRight)).withSource(sumGroupId).build();

            if (privilegesWithFilter.isEmpty()) {
                return new UserPrivilegesForObjectWithoutFilterImpl(minPrivilegesForAllRecords);
            } else {
                GroupOfPrivileges maxPrivilegesForAllRecords = mergePrivilegeGroups(transform(privilegesList, Triple::getRight)).withSource(buildSumGroupId(privilegesList)).build();

                List<GroupOfPrivileges> mergedPrivilegesWithFilter = privilegesWithFilter.stream().map(Triple::getRight).map((thisGroupOfPrivileges)
                        -> mergePrivilegeGroups(thisGroupOfPrivileges, minPrivilegesForAllRecords)
                                .withFilter(thisGroupOfPrivileges.getFilter())
                                .withSource(sumGroupId + "+" + thisGroupOfPrivileges.getSource()).build()
                ).collect(toList());

                return new UserPrivilegesForObjectWithFilterImpl(minPrivilegesForAllRecords, maxPrivilegesForAllRecords, mergedPrivilegesWithFilter);
            }
        }

        private String buildSumGroupId(Collection<Triple<Role, Grant, GroupOfPrivileges>> list) {
            Set<String> thisSourceGroups = list.stream().map(Triple::getLeft).map(Role::getName).collect(toSet());
            if (thisSourceGroups.isEmpty()) {
                return "NONE";
            } else if (thisSourceGroups.size() == 1) {
                return getOnlyElement(thisSourceGroups);
            } else {
                return "SUM";
            }
        }

        public UserPrivilegesImplBuilder withGroups(Role... groups) {
            return this.withGroups(asList(groups));
        }

        public UserPrivilegesImplBuilder withGroups(Iterable<Role> groups) {
            for (Role group : groups) {
                addGroup(group);
            }
            return this;
        }

        public UserPrivilegesImplBuilder withRolePrivileges(Iterable<RolePrivilege> privileges) {
            Iterables.addAll(this.rolePrivileges, privileges);
            return this;
        }

        private void addGroup(Role role) {
            logger.debug("load user privileges for group = {}", role);
            checkNotBlank(role.getName());
            List<Grant> privileges = role.getAllPrivileges();
            addPrivileges(role, privileges);
            sourceGroups.add(role.getName());
            rolePrivileges.addAll(role.getRolePrivileges());
        }

        private final Set<String> grantPrivileges = EnumSet.allOf(GrantPrivilege.class).stream().map(g -> serializeEnum(g)).collect(toSet());

        private void addPrivileges(Role role, Collection<Grant> grants) {

            grants.forEach(grant -> {
                Set<GrantPrivilege> servicePrivileges = grant.getPrivileges(),
                        uiPrivileges = EnumSet.allOf(GrantPrivilege.class);

                Map<String, Object> allCustomPrivileges = grant.getCustomPrivileges(),
                        uiCustomPrivileges = map(allCustomPrivileges).withKeys(grantPrivileges),
                        otherCustomprivileges = map(allCustomPrivileges).withoutKeys(grantPrivileges);

                uiCustomPrivileges.forEach((k, v) -> {
                    if (toBoolean(v) == false) {
                        uiPrivileges.remove(parseEnum(k, GrantPrivilege.class));
                    }
                });

                addGroupOfPrivileges(role, grant, GroupOfPrivilegesImpl.builder()
                        .withPrivileges(GPS_SERVICE, servicePrivileges)
                        .withPrivileges(GPS_UI, uiPrivileges)
                        .withAttributePrivileges(map(transformValues(grant.getAttributePrivileges(), GrantUtils::expandPrivileges)))
                        .withDmsPrivileges(map(transformValues(grant.getDmsPrivileges(), GrantUtils::expandDmsPrivileges)))
                        .withGisPrivileges(map(transformValues(grant.getGisPrivileges(), GrantUtils::expandGisPrivileges)))
                        .withFilter(grant.getFilterOrNull())
                        .withSource("role:" + role.getName())
                        .withCustomPrivileges(otherCustomprivileges)
                        .build());
            });
        }

        private void addGroupOfPrivileges(Role role, Grant grant, GroupOfPrivileges privileges) {
            logger.debug("role {}, grant {}, add group of privileges = {}", role, grant.getName(), privileges.getPrivilegesDetailedInfos());
            groupOfPrivileges.add(Triple.of(role, grant, privileges));
            if (privileges.hasFilter() && !equal(privileges.getOnFilterMismatchPrivilege(), GM_NONE)) {
                groupOfPrivileges.add(Triple.of(role, grant, GroupOfPrivilegesImpl.builder()
                        .withSource(privileges.getSource() + "_nofilter")
                        .withPrivileges(GPS_SERVICE, modeToPrivileges(privileges.getOnFilterMismatchPrivilege()))
                        .withPrivileges(GPS_UI, privileges.getUiPrivileges())
                        .withCustomPrivileges(map(privileges.getCustomPrivileges()).withoutKey(GDCP_ON_FILTER_MISMATCH))
                        .withDmsPrivileges(privileges.getDmsPrivileges())
                        .withGisPrivileges(privileges.getGisPrivileges())
                        .withAttributePrivileges(privileges.getAttributePrivileges())
                        .build()
                ));
            }
        }

        private static UserPrivilegesForObject addDefaultCustomPrivileges(UserPrivilegesForObject source, Map<String, Object> defaults) {
            if (source instanceof UserPrivilegesForObjectWithoutFilterImpl) {
                return new UserPrivilegesForObjectWithoutFilterImpl(addDefaultCustomPrivileges(source.getMinPrivilegesForAllRecords(), defaults));
            } else {
                return new UserPrivilegesForObjectWithFilterImpl(addDefaultCustomPrivileges(source.getMinPrivilegesForAllRecords(), defaults), addDefaultCustomPrivileges(source.getMaxPrivilegesForSomeRecords(), defaults), list(source.getPrivilegeGroupsWithFilter()).map(p -> addDefaultCustomPrivileges(p, defaults)));
            }
        }

        private static GroupOfPrivileges addDefaultCustomPrivileges(GroupOfPrivileges source, Map<String, Object> defaults) {
            return GroupOfPrivilegesImpl.copyOf(source).withCustomPrivileges(map(defaults).skipNullValues().with(source.getCustomPrivileges())).build();
        }

        private static class UserPrivilegesForObjectWithoutFilterImpl implements UserPrivilegesForObject {

            private final GroupOfPrivileges privileges;

            public UserPrivilegesForObjectWithoutFilterImpl(GroupOfPrivileges privileges) {
                this.privileges = checkNotNull(privileges);
            }

            @Override
            public GroupOfPrivileges getMinPrivilegesForAllRecords() {
                return privileges;
            }

            @Override
            public GroupOfPrivileges getMaxPrivilegesForSomeRecords() {
                return privileges;
            }

            @Override
            public List<GroupOfPrivileges> getPrivilegeGroupsWithFilter() {
                return emptyList();
            }

            @Override
            public boolean hasPrivilegesWithFilter() {
                return false;
            }

        }

        private static class UserPrivilegesForObjectWithFilterImpl implements UserPrivilegesForObject {

            private final GroupOfPrivileges minPrivileges, maxPrivileges;
            private final List<GroupOfPrivileges> privilegesWithFilter;

            public UserPrivilegesForObjectWithFilterImpl(GroupOfPrivileges minPrivileges, GroupOfPrivileges maxPrivileges, List<GroupOfPrivileges> privilegesWithFilter) {
                this.minPrivileges = checkNotNull(minPrivileges);
                this.maxPrivileges = checkNotNull(maxPrivileges);
                this.privilegesWithFilter = ImmutableList.copyOf(privilegesWithFilter);
                checkArgument(!minPrivileges.hasFilter());
                checkArgument(!maxPrivileges.hasFilter());
                checkArgument(!privilegesWithFilter.isEmpty());
                privilegesWithFilter.forEach((p) -> checkArgument(p.hasFilter()));

            }

            @Override
            public GroupOfPrivileges getMinPrivilegesForAllRecords() {
                return minPrivileges;
            }

            @Override
            public GroupOfPrivileges getMaxPrivilegesForSomeRecords() {
                return maxPrivileges;
            }

            @Override
            public List<GroupOfPrivileges> getPrivilegeGroupsWithFilter() {
                return privilegesWithFilter;
            }

            @Override
            public boolean hasPrivilegesWithFilter() {
                return true;
            }

        }

    }
}
