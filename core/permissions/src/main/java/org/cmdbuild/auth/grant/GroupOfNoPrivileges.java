/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_SERVICE;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_UI;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public enum GroupOfNoPrivileges implements GroupOfPrivileges {

    INSTANCE;

    @Override
    public Map<GrantPrivilegeScope, Set<GrantPrivilege>> getPrivileges() {
        return map(GPS_UI, emptySet(), GPS_SERVICE, emptySet());
    }

    @Override
    @Nullable
    public Map<String, Set<GrantAttributePrivilege>> getAttributePrivileges() {
        return emptyMap();
    }

    @Override
    @Nullable
    public Map<String, Set<GrantAttributePrivilege>> getDmsPrivileges() {
        return emptyMap();
    }

    @Override
    @Nullable
    public Map<String, Set<GrantAttributePrivilege>> getGisPrivileges() {
        return emptyMap();
    }

    @Override
    public String getSource() {
        return "dummy";
    }

    @Override
    public CmdbFilter getFilter() {
        return CmdbFilterImpl.noopFilter();
    }

    @Override
    public Map<String, Object> getCustomPrivileges() {
        return emptyMap();
    }

}
