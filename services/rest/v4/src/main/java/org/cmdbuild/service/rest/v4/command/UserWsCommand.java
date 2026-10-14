/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.auth.login.PasswordService;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserFilteredRepository;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsPasswordRecoveryData;
import org.cmdbuild.service.rest.v4.model.WsRoleOrTenantData;
import org.cmdbuild.service.rest.v4.model.WsUserData;
import org.cmdbuild.service.rest.v4.model.WsUserPswData;
import org.cmdbuild.userconfig.UserConfigService;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.auth.user.UserRepository.BLANK_PASSWORD;
import static org.cmdbuild.userconfig.UserConfigConst.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * @author ldare
 */
@Component
public class UserWsCommand {

    private final UserFilteredRepository userFilteredRepository;
    private final RoleRepository roleRepository;
    private final UserConfigService userConfigService;
    private final MultitenantService multitenantService;
    private final PasswordService passwordService;
    private final SessionService sessionService;

    public UserWsCommand(UserFilteredRepository userFilteredRepository, RoleRepository roleRepository, UserConfigService userConfigService, MultitenantService multitenantService, PasswordService passwordService, SessionService sessionService) {
        this.userFilteredRepository = userFilteredRepository;
        this.roleRepository = roleRepository;
        this.userConfigService = userConfigService;
        this.multitenantService = multitenantService;
        this.passwordService = passwordService;
        this.sessionService = sessionService;
    }

    public PagedElements<UserData> doReadMany(DaoQueryOptions queryOptions) {
        return userFilteredRepository.getMany(queryOptions);
    }

    public UserData doReadOne(Long id) {
        return userFilteredRepository.getUserDataById(id);
    }

    public UserData doCreate(WsUserData data) {
        checkCanModify(data);
        UserData user = data.toUserData().accept(u -> {
            if (Boolean.TRUE.equals(data.isChangePasswordRequired())) {
                u.withRecoveryToken(passwordService.encryptPassword(data.getPassword()));
            } else {
                u.withPassword(passwordService.encryptPassword(data.getPassword()));
            }
        }).build();
        user = userFilteredRepository.create(user);
        updatePrefs(user.getUsername(), data);
        updateRoles(user, data);
        updateTenants(user, data);
        return userFilteredRepository.getUserDataById(user.getId());
    }

    public UserData doUpdate(Long id, WsUserData data) {
        checkArgument(userFilteredRepository.currentUserCanModify(userFilteredRepository.getUserDataById(id)), "CM: current user is not allowed to create/modify user = %s", id);
        checkCanModify(data);
        UserData user = data.toUserData().withId(id).accept(u -> {
            if (data.isChangePasswordRequired()) {
                u.withPassword(BLANK_PASSWORD).withRecoveryToken(isNotBlank(data.getPassword()) ? passwordService.encryptPassword(data.getPassword()) : userFilteredRepository.getUserDataById(id).getRecoveryTokenOrPassword());
            } else {
                u.withRecoveryToken(null).withPassword(isNotBlank(data.getPassword()) ? passwordService.encryptPassword(data.getPassword()) : userFilteredRepository.getUserDataById(id).getPasswordOrRecoveryToken());
            }
        }).build();
        user = userFilteredRepository.update(user);
        updatePrefs(user.getUsername(), data);
        updateRoles(user, data);
        updateTenants(user, data);
        return userFilteredRepository.getUserDataById(id);
    }

    public void doChangePasswordForCurrentUser(WsUserPswData data) {
        Session session = sessionService.getCurrentSession();
        passwordService.verifyAndUpdatePasswordForUser(session.getOperationUser().getUsername(), data.getOldpassword(), data.getPassword());
    }

    public void doChangePassword(String username, WsUserPswData data) {
        passwordService.verifyAndUpdatePasswordForUser(username, data.getOldpassword(), data.getPassword());
    }

    public void doRequirePasswordRecovery(String username, WsPasswordRecoveryData data) {
        LoginUser user = userFilteredRepository.getActiveUserByUsernameOrNull(username);
        checkArgument(user != null && equalsIgnoreCase(user.getEmail(), data.getEmail()), "CM: user not found with the username and email you provided");//TODO check error reporting/security
        passwordService.requirePasswordRecovery(username);
    }

    public void checkCanModify(WsUserData data) {
        checkArgument(userFilteredRepository.currentUserCanModify(data.toUserData().build(), list(data.getUserGroups()).map(WsRoleOrTenantData::getId).map(roleRepository::getById), list(data.getUserTenants()).map(WsRoleOrTenantData::getId)), "CM: current user is not allowed to create/modify user with this access privileges");
    }

    public void updatePrefs(String username, WsUserData data) {
        userConfigService.setByUsernameDeleteIfNull(username, USER_CONFIG_INITIAL_PAGE, trimToNull(data.getInitialPage()));
        userConfigService.setByUsernameDeleteIfNull(username, USER_CONFIG_LANGUAGE, data.getLang());
        userConfigService.setByUsernameDeleteIfNull(username, USER_CONFIG_MULTIGROUP, toStringOrNull(data.getMultiGroup()));
        userConfigService.setByUsernameDeleteIfNull(username, USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES, serializeEnum(data.getMultiTenantActivationPrivileges()));
    }

    public void updateRoles(UserData user, WsUserData data) {
        roleRepository.setUserGroups(user.getId(), list(data.getUserGroups()).map(WsRoleOrTenantData::getId), data.getDefaultRole());
    }

    public void updateTenants(UserData user, WsUserData data) {
        if (multitenantService.isEnabled() && multitenantService.isUserTenantUpdateEnabled()) {
            multitenantService.setUserTenants(user.getId(), list(data.getUserTenants()).map(WsRoleOrTenantData::getId));
        }
    }
}
