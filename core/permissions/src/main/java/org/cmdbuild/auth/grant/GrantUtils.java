/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collection;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import org.cmdbuild.auth.grant.GrantMode;
import org.cmdbuild.auth.grant.GrantPrivilege;
import org.cmdbuild.auth.grant.GroupOfNoPrivileges;
import org.cmdbuild.auth.grant.GroupOfPrivileges;
import org.cmdbuild.auth.grant.GroupOfPrivilegesImpl;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_NONE;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_READ;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_WRITE;
import static org.cmdbuild.auth.grant.GrantMode.GM_READ;
import static org.cmdbuild.auth.grant.GrantMode.GM_WF_ADMIN;
import static org.cmdbuild.auth.grant.GrantMode.GM_WF_BASIC;
import static org.cmdbuild.auth.grant.GrantMode.GM_WF_DEFAULT;
import static org.cmdbuild.auth.grant.GrantMode.GM_WF_PLUS;
import static org.cmdbuild.auth.grant.GrantMode.GM_WRITE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_ALL;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_CLONE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_CREATE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_DELETE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_PRINT;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_READ;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_SEARCH;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_UPDATE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WF_BASIC;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WF_LIFECYCLE;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WF_READTOUCHED;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WRITE;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_SERVICE;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_UI;
import org.cmdbuild.auth.grant.GroupOfPrivilegesImpl.GroupOfPrivilegesImplBuilder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.isBoolean;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import org.cmdbuild.utils.privileges.PrivilegeProcessor;
import org.cmdbuild.utils.privileges.PrivilegeProcessorImpl;

public class GrantUtils {

    private final static PrivilegeProcessor<GrantPrivilege> GRANT_PRIVILEGE_PROCESSOR = PrivilegeProcessorImpl.<GrantPrivilege>builder()
            .withPrivilegeImplicationMap(map(GP_ALL, set(GP_WRITE, GP_WF_LIFECYCLE),
                    GP_WRITE, set(GP_WF_READTOUCHED, GP_READ, GP_CREATE, GP_UPDATE, GP_DELETE, GP_CLONE),
                    GP_READ, set(GP_WF_BASIC, GP_PRINT, GP_SEARCH),
                    GP_WF_BASIC, set(GP_PRINT, GP_SEARCH)
            )).build();

    private final static PrivilegeProcessor<GrantAttributePrivilege> GRANT_ATTRIBUTE_PRIVILEGE_PROCESSOR = PrivilegeProcessorImpl.<GrantAttributePrivilege>builder()
            .withNullPrivilegeValues(GAP_NONE)
            .withPrivilegeImplicationMap(map(GAP_WRITE, set(GAP_READ))).build();

    private final static PrivilegeProcessor<GrantAttributePrivilege> GRANT_DMS_PRIVILEGE_PROCESSOR = PrivilegeProcessorImpl.<GrantAttributePrivilege>builder()
            .withPrivilegeImplicationMap(map(GAP_WRITE, set(GAP_READ))).build();

    private final static Set<GrantAttributePrivilege> GRANT_ATTRIBUTE_WRITE_DEFAULT_EXPANDED = ImmutableSet.copyOf(expandPrivileges(GAP_WRITE));

    private final static Map<GrantMode, Set<GrantPrivilege>> MODE_TO_PRIVILEGES = unmodifiableMap(new EnumMap(map(GM_WRITE, expandPrivileges(GP_WRITE),
            GM_READ, expandPrivileges(GP_READ),
            GM_WF_ADMIN, expandPrivileges(GP_ALL),
            GM_WF_PLUS, expandPrivileges(GP_WF_READTOUCHED, GP_WF_BASIC, GP_READ),
            GM_WF_DEFAULT, expandPrivileges(GP_WF_READTOUCHED, GP_WF_BASIC),
            GM_WF_BASIC, expandPrivileges(GP_WF_BASIC)
    )));

    public static Set<GrantPrivilege> modeToPrivileges(GrantMode mode) {
        return MODE_TO_PRIVILEGES.getOrDefault(checkNotNull(mode), emptySet());
    }

    public static Set<GrantAttributePrivilege> expandPrivileges(GrantAttributePrivilege... privileges) {
        return GRANT_ATTRIBUTE_PRIVILEGE_PROCESSOR.expandPrivileges(privileges);
    }

    public static Set<GrantAttributePrivilege> expandDmsPrivileges(GrantAttributePrivilege... privileges) {
        return GRANT_DMS_PRIVILEGE_PROCESSOR.expandPrivileges(privileges);
    }

    public static Set<GrantAttributePrivilege> expandGisPrivileges(GrantAttributePrivilege... privileges) {
        return GRANT_ATTRIBUTE_PRIVILEGE_PROCESSOR.expandPrivileges(privileges);
    }

    public static Set<GrantPrivilege> expandPrivileges(GrantPrivilege... privileges) {
        return GRANT_PRIVILEGE_PROCESSOR.expandPrivileges(privileges);
    }

    public static Set<GrantPrivilege> expandPrivileges(Iterable<GrantPrivilege> privileges) {
        return GRANT_PRIVILEGE_PROCESSOR.expandPrivileges(privileges);
    }

    public static GroupOfPrivilegesImplBuilder mergePrivilegeGroups(GroupOfPrivileges... groupOfPrivileges) {
        return mergePrivilegeGroups(list(groupOfPrivileges));
    }

    public static GroupOfPrivilegesImplBuilder mergePrivilegeGroups(Collection<GroupOfPrivileges> groupOfPrivileges) {
        if (groupOfPrivileges.isEmpty()) {
            return GroupOfPrivilegesImpl.copyOf(GroupOfNoPrivileges.INSTANCE);
        } else if (groupOfPrivileges.size() == 1) {
            return GroupOfPrivilegesImpl.copyOf(getOnlyElement(groupOfPrivileges)).withFilter((String) null);
        } else {
            Set<GrantPrivilege> mergedPrivileges = groupOfPrivileges.stream().map(GroupOfPrivileges::getServicePrivileges).map(GRANT_PRIVILEGE_PROCESSOR::expandPrivileges).flatMap(Set::stream).collect(toSet());
            Set<GrantPrivilege> mergedUIPrivileges = groupOfPrivileges.stream().map(GroupOfPrivileges::getUiPrivileges).flatMap(Set::stream).collect(toSet());

            Set<String> grantAttrPrivKeys = groupOfPrivileges.stream().map(GroupOfPrivileges::getAttributePrivileges).map(CmMapUtils::nullToEmpty).map(Map::entrySet).flatMap(Set::stream).map(Entry::getKey).collect(toSet());
            Map<String, Set<GrantAttributePrivilege>> mergedAttrPrivileges = grantAttrPrivKeys.stream().collect(toMap(identity(), (attr) -> groupOfPrivileges.stream()
                    .map(GroupOfPrivileges::getAttributePrivileges).map(CmMapUtils::nullToEmpty).map((m) -> m.getOrDefault(attr, GRANT_ATTRIBUTE_WRITE_DEFAULT_EXPANDED)).flatMap(Set::stream).collect(toSet())));

            Map<String, Object> customPrivileges = mapOf(String.class, Object.class).accept(m -> { //TODO improve this
                list(groupOfPrivileges).flatMap(g -> g.getCustomPrivileges().keySet()).toSet().forEach(k -> {
                    List<String> values = list(groupOfPrivileges).map(g -> g.getCustomPrivileges().get(k)).filter(Objects::nonNull).map(CmStringUtils::toStringOrEmpty);
                    Set<String> set = set(values);
                    if (set.size() == 1 && values.size() == groupOfPrivileges.size()) {
                        m.put(k, getOnlyElement(set));
                    } else if (set.stream().allMatch(v -> isBoolean(toStringOrEmpty(v)))) {
                        m.put(k, set.contains(toStringNotBlank(true)));
                    }
                });
            });

            //TODO merge dms priv
            return GroupOfPrivilegesImpl.builder()
                    .withPrivileges(GPS_SERVICE, mergedPrivileges)
                    .withPrivileges(GPS_UI, mergedUIPrivileges)
                    .withCustomPrivileges(customPrivileges)
                    .withAttributePrivileges(mergedAttrPrivileges);
        }
    }
}
