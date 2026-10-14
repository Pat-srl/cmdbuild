/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.auth.grant.GrantMode;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.role.RoleInfoImpl;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.user.LoginUserInfo;
import org.cmdbuild.client.rest.api.UserApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public class UserApiImpl extends AbstractServiceClientImpl implements UserApi {

    public UserApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public LoginUserInfo createUser(String username, @Nullable String password, String... groups) {
        List roles = list(groups).stream().map(r -> {
            long roleId = get(format("administration/roles/%s", encodeUrlPath(checkNotBlank(r)))).asJackson().get("data").get("_id").asLong();
            return map("_id", roleId);
        }).collect(toList());
        JsonNode data = post("administration/users", map("username", checkNotBlank(username), "password", password, "userGroups", roles)).asJackson().get("data");
        return new LoginUserInfoImpl(data.get("_id").asLong(), data.get("username").asText());
    }

    @Override
    public RoleInfo createRole(String rolename, RolePrivilege... rolePrivileges) {
        JsonNode data = post("administration/roles", map("name", checkNotBlank(rolename), "rolePrivileges", list(rolePrivileges).map(r -> serializeEnum(r)))).asJackson().get("data");
        return new RoleInfoImpl(data.get("_id").asLong(), data.get("name").asText(), data.get("description").asText());
    }

    @Override
    public UserApi setUserPreferences(Map<String, String> preferences) {
        post("sessions/current/preferences", map(preferences));
        return this;
    }

    @Override
    public UserApi setRolePrivilegesOnClass(String rolename, String classId, GrantMode mode, CmdbFilter filter) {
        post(format("administration/roles/%s/grants/_ANY", encodeUrlPath(checkNotBlank(rolename))), list(map(
                "mode", serializeEnum(mode),
                "objectType", "class",
                "objectTypeName", checkNotBlank(classId),
                "filter", serializeFilter(filter))));
        return this;
    }

    @Override
    public UserApi setRolePrivilegesOnProcess(String rolename, String processId, GrantMode mode) {
        post(format("administration/roles/%s/grants/_ANY", encodeUrlPath(checkNotBlank(rolename))), list(map(
                "mode", serializeEnum(mode),
                "objectType", "process",
                "objectTypeName", checkNotBlank(processId)
        )));
        return this;
    }

    @Override
    public UserApi setRolePrivilegesOnView(String rolename, String viewId, GrantMode mode) {
        post(format("administration/roles/%s/grants/_ANY", encodeUrlPath(checkNotBlank(rolename))), list(map(
                "mode", serializeEnum(mode),
                "objectType", "view",
                "objectTypeName", checkNotBlank(viewId)
        )));
        return this;
    }

    private static class LoginUserInfoImpl implements LoginUserInfo {

        private final long id;
        private final String username;

        public LoginUserInfoImpl(long id, String username) {
            this.id = checkNotNullAndGtZero(id);
            this.username = checkNotBlank(username);
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getUsername() {
            return username;
        }

    }
}
