/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import org.apache.commons.lang3.EnumUtils;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_ON_FILTER_MISMATCH;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_SERVICE;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_UI;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface GroupOfPrivileges {

    String getSource();

    Map<GrantPrivilegeScope, Set<GrantPrivilege>> getPrivileges();

    Map<String, Set<GrantAttributePrivilege>> getAttributePrivileges();

    Map<String, Set<GrantAttributePrivilege>> getDmsPrivileges();

    Map<String, Set<GrantAttributePrivilege>> getGisPrivileges();

    Map<String, Object> getCustomPrivileges();

    CmdbFilter getFilter();

    default boolean hasFilter() {
        return !getFilter().isNoop();
    }

    default Set<GrantPrivilege> getServicePrivileges() {
        return checkNotNull(getPrivileges().get(GPS_SERVICE));
    }

    default Set<GrantPrivilege> getUiPrivileges() {
        return checkNotNull(getPrivileges().get(GPS_UI));
    }

    default GrantMode getOnFilterMismatchPrivilege() {
        return parseEnumOrDefault(toStringOrNull(getCustomPrivileges().get(GDCP_ON_FILTER_MISMATCH)), GM_NONE);
    }

    default String getPrivilegesDetailedInfos() {
        StringBuilder infos = new StringBuilder();
        EnumUtils.getEnumList(GrantPrivilegeScope.class).forEach(s -> {
            infos.append(format("\n\t%s:\n", serializeEnum(s)));
            Set<GrantPrivilege> privileges = getPrivileges().get(s);
            privileges.forEach(g -> {
                infos.append(format("\t\t%s\n", serializeEnum(g)));
            });
        });
        if (!getAttributePrivileges().isEmpty()) {
            infos.append(format("\n\tattribute privileges:\n%s\n", mapToLoggableString(map(getAttributePrivileges()).mapValues(v -> v.stream().map(CmConvertUtils::serializeEnum).collect(joining(","))))));
        }
        if (!getCustomPrivileges().isEmpty()) {
            infos.append(format("\n\tcustom privileges:\n%s\n", mapToLoggableString(getCustomPrivileges())));
        }
        return infos.toString();
    }

    default boolean isEmpty() {
        return getCustomPrivileges().isEmpty() && getPrivileges().values().stream().allMatch(Set::isEmpty) && getAttributePrivileges().isEmpty();
    }

}
