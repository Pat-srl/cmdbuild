package org.cmdbuild.auth.login;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.transform;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.grant.UserPrivileges;
import static org.cmdbuild.auth.login.AuthRequestInfo.AUTH_REQUEST_LOGIN_MODULE_PARAM;
import static org.cmdbuild.auth.login.GodUserUtils.getGodDummyGroup;
import static org.cmdbuild.auth.login.GodUserUtils.getGodLoginUser;
import static org.cmdbuild.auth.login.GodUserUtils.getGodPrivilegeContext;
import static org.cmdbuild.auth.login.GodUserUtils.isGodDummyGroup;
import static org.cmdbuild.auth.login.GodUserUtils.isGodUser;
import static org.cmdbuild.auth.login.LoginModuleConfiguration.DEFAULT_LOGIN_MODULE_TYPE;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.emptyResponse;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.login;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleInfo;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_TENANT;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.LoginUserImpl;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.auth.user.OperationUserImpl.builder;
import org.cmdbuild.auth.user.PasswordSupplier;
import org.cmdbuild.auth.user.PasswordUserScriptService;
import org.cmdbuild.auth.user.UserPrivilegesImpl;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.eventlog.EventLogService;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String EVENT_LOGIN_FAILED = "cm_auth_login_failed";

    private final RoleRepository groupRepository;
    private final MultitenantService multitenantService;
    private final List<PasswordAuthenticator> passwordAuthenticators;
    private final Map<String, LoginModuleClientRequestAuthenticator> loginModuleClientRequestAuthenticatorsByType;
    private final List<ClientRequestAuthenticator> clientRequestAuthenticators;
    private final UserRepository userRepository;
    private final AuthenticationConfiguration conf;
    private final PasswordSupplier passwordService;
    private final Provider<EventLogService> eventLogService; //TODO fix this
    private final SessionDataSupplier sessionDataService;
    private final PasswordUserScriptService userScriptService;

    public AuthenticationServiceImpl(List<ClientRequestAuthenticator> clientRequestAuthenticators, SessionDataSupplier sessionDataService, Provider<EventLogService> eventLogService, PasswordSupplier passwordService, RoleRepository groupRepository, MultitenantService multitenantService, List<PasswordAuthenticator> passwordAuthenticators, List<LoginModuleClientRequestAuthenticator> loginModuleClientRequestAuthenticators, UserRepository userRepository, AuthenticationConfiguration conf, PasswordUserScriptService userScriptService) {
        this.groupRepository = checkNotNull(groupRepository);
        this.multitenantService = checkNotNull(multitenantService);
        this.passwordAuthenticators = checkNotEmpty(ImmutableList.copyOf(passwordAuthenticators));
        this.loginModuleClientRequestAuthenticatorsByType = map(loginModuleClientRequestAuthenticators, LoginModuleClientRequestAuthenticator::getType, identity()).immutable();
        this.userRepository = checkNotNull(userRepository);
        this.conf = checkNotNull(conf);
        this.passwordService = checkNotNull(passwordService);
        this.eventLogService = checkNotNull(eventLogService);
        this.sessionDataService = checkNotNull(sessionDataService);
        this.clientRequestAuthenticators = checkNotEmpty(ImmutableList.copyOf(clientRequestAuthenticators));
        this.userScriptService = checkNotNull(userScriptService);
    }

    @Override
    public String getEncryptedPassword(LoginUserIdentity login) {
        return passwordService.getEncryptedPassword(login);
    }

    @Override
    public LoginUser checkPasswordAndGetUser(LoginUserIdentity login, String password) {
        boolean validRecoveryToken = false;
        if (isNotNullAndGtZero(conf.getMaxLoginAttempts())) {
            long failedLogins = eventLogService.get()
                    .getEvents(EVENT_LOGIN_FAILED, now().minusSeconds(checkNotNullAndGtZero(conf.getMaxLoginAttemptsWindowSeconds())))
                    .stream().filter(e -> equal(e.getData().get("login"), login.getValue())).count();
            if (failedLogins >= conf.getMaxLoginAttempts()) {
                storeLoginFailedEvent(login);
                logger.debug("too many failed login attempts for user = {} (failed {} times): access denied", login, failedLogins);
                throw new UserVisibleAuthenticationException("CM: too many failed login attempts! account locked, retry later");
            }
        }
        for (PasswordAuthenticator passwordAuthenticator : getActivePasswordAuthenticators()) {
            try {
                logger.debug("try to validate password for user = {} with authenticator = {}", login, passwordAuthenticator);
                LoginUserIdentity identityLogin;
                if (conf.isDefaultLoginModuleEnabled()) {
                    LoginModuleConfiguration loginModule = conf.getLoginModuleByCode(DEFAULT_LOGIN_MODULE_TYPE);
                    if (isBlank(loginModule.getLoginHandlerScript())) {
                        identityLogin = login;
                    } else {
                        identityLogin = userScriptService.getLoginFromScript(loginModule, LoggerFactory.getLogger(format("%s.HANDLER", getClass().getName())), map("username", login.getValue(), "password", password));
                    }
                } else {
                    identityLogin = login;
                }
                PasswordCheckResult result = passwordAuthenticator.checkPassword(identityLogin, password);
                switch (result.getStatus()) {
                    case PCR_HAS_VALID_PASSWORD -> {
                        logger.debug("successfully authenticated user = {} with authenticator = {}", result.getLogin(), passwordAuthenticator);
                        return getUserForLogin(result.getLogin());
                    }
                    case PCR_HAS_VALID_RECOVERY_TOKEN ->
                        validRecoveryToken = true;
                }
            } catch (Exception ex) {
                logger.error(marker(), "authentication error for user = {}", login, ex);
            }
        }
        if (validRecoveryToken) {
            logger.debug("authenticated user = {} with recovery token user (password reset will be required)", login);
            throw new PasswordResetRequiredAuthenticationException("unable to validate credentials: password reset is required for user = %s", login);
        } else {
            storeLoginFailedEvent(login);
            throw new AuthenticationException("invalid login credentials or authentication error for user = %s", login);
        }
    }

    @Override
    public RequestAuthenticatorResponse<LoginUser> validateCredentialsAndCreateAuthResponse(AuthRequestInfo request) {
        for (ClientRequestAuthenticator clientRequestAuthenticator : list(clientRequestAuthenticators).filter(ClientRequestAuthenticator::isEnabled)) {//TODO improve this (??)
            logger.debug("processing auth request with client request authenticator = {}", clientRequestAuthenticator);
            RequestAuthenticatorResponse<LoginUserIdentity> response = clientRequestAuthenticator.authenticate(request);
            if (response != null) {
                logger.debug("received auth response = {}", response);
                if (response.hasLogin()) {
                    return login(getUserForLogin(response.getLogin()))
                            .withCustomAttributes(response.getCustomAttributes())
                            .withRedirect(response.getRedirectUrl());
                } else {
                    return ((RequestAuthenticatorResponseImpl) response);
                }
            }
        }
        for (LoginModuleConfiguration loginModuleConfiguration : list(conf.getLoginModules()).filter(LoginModuleConfiguration::isEnabled)) {
            logger.debug("processing auth response (if any) with login module = {} type = {}", loginModuleConfiguration.getCode(), loginModuleConfiguration.getType());
            RequestAuthenticatorResponse<LoginUserIdentity> response = getClientRequestAuthenticatorForLoginModule(loginModuleConfiguration).handleAuthResponse(request, loginModuleConfiguration);
            if (response != null) {//TODO improve this, duplicate code
                logger.debug("received auth response = {}", response);
                if (response.hasLogin()) {
                    return login(getUserForLogin(response.getLogin()))
                            .withCustomAttributes(response.getCustomAttributes())
                            .withRedirect(response.getRedirectUrl())
                            .withCustomAttribute(SESSION_LOGIN_MODULE_CODE, loginModuleConfiguration.getCode());
                } else {
                    return ((RequestAuthenticatorResponseImpl) response)
                            .withCustomAttribute(SESSION_LOGIN_MODULE_CODE, loginModuleConfiguration.getCode());
                }
            }
        }
        String loginModule = request.getParameter(AUTH_REQUEST_LOGIN_MODULE_PARAM);
        if (isBlank(loginModule) && conf.isAutoSsoRedirectEnabled() && !conf.isDefaultLoginModuleEnabledAndVisible() && conf.getNonDefaultNonHiddenActiveLoginModules().size() == 1) {
            loginModule = getOnlyElement(conf.getNonDefaultNonHiddenActiveLoginModules()).getCode();
        }
        if (isNotBlank(loginModule)) { //TODO improve this, duplicate code
            LoginModuleConfiguration loginModuleConfiguration = conf.getLoginModuleByCode(loginModule);
            logger.debug("processing auth request with login module = {} type = {}", loginModuleConfiguration.getCode(), loginModuleConfiguration.getType());
            checkArgument(loginModuleConfiguration.isEnabled());
            RequestAuthenticatorResponse<LoginUserIdentity> response = getClientRequestAuthenticatorForLoginModule(loginModuleConfiguration).handleAuthRequest(request, loginModuleConfiguration);
            if (response != null) {//TODO improve this, duplicate code
                logger.debug("received auth response = {}", response);
                if (response.hasLogin()) {
                    return login(getUserForLogin(response.getLogin()))
                            .withCustomAttributes(response.getCustomAttributes())
                            .withRedirect(response.getRedirectUrl())
                            .withCustomAttribute(SESSION_LOGIN_MODULE_CODE, loginModuleConfiguration.getCode());
                } else {
                    return ((RequestAuthenticatorResponseImpl) response)
                            .withCustomAttribute(SESSION_LOGIN_MODULE_CODE, loginModuleConfiguration.getCode());
                }
            }
        }
        return emptyResponse();
    }

    @Override
    public RequestAuthenticatorResponse<Void> invalidateCredentialsAndCreateLogoutResponse(@Nullable Object request) {
        String moduleCode = sessionDataService.getCurrentSessionData(SESSION_LOGIN_MODULE_CODE);
        if (isNotBlank(moduleCode)) {
            LoginModuleConfiguration loginModuleConfiguration = conf.getLoginModuleByCode(moduleCode);
            RequestAuthenticatorResponse<Void> response = getClientRequestAuthenticatorForLoginModule(loginModuleConfiguration).logout(request, loginModuleConfiguration);
            if (response != null) {
                return response;
            }
        }
        return emptyResponse();
    }

    @Nullable
    @Override
    public LoginUser getUserOrNull(LoginUserIdentity identity) {
        if (isGodUser(identity)) {
            return getGodLoginUser();
        } else {
            return userRepository.getActiveValidUserOrNull(identity);
        }
    }

    @Override
    @Nullable
    public String getUnencryptedPasswordOrNull(LoginUserIdentity login) {
        return passwordService.getUnencryptedPasswordOrNull(login);
    }

    @Override
    @Nullable
    public LoginUser getUserByIdOrNull(Long userId) {
        return userRepository.getUserByIdOrNull(userId);
    }

    @Override
    public Collection<Role> getAllGroups() {
        return groupRepository.getAllGroups();
    }

    @Override
    public Role fetchGroupWithId(Long groupId) {
        return groupRepository.getByIdOrNull(groupId);
    }

    @Override
    public Role getGroupWithNameOrNull(String groupName) {
        return groupRepository.getGroupWithNameOrNull(groupName);
    }

    @Override
    public OperationUser validateCredentialsAndCreateOperationUser(LoginData loginData) {
        logger.debug("try to login user = {} with group = {} and full info = {}", loginData.getLoginString(), loginData.getLoginGroupName(), loginData);
        LoginUser loginUser;
        LoginUserIdentity identity = LoginUserIdentity.build(loginData.getLoginString());
        if (loginData.isPasswordRequired()) {
            loginUser = checkPasswordAndGetUser(identity, loginData.getPassword());
        } else {
            loginUser = getUserForLogin(identity);
        }

        if (!loginData.isServiceUsersAllowed() && loginUser.isService()) {
            logger.warn("login not allowed for user = {}: user is service and service user login is not allowed", loginUser.getUsername());
            throw new AuthenticationException("login failed");
        }

        if (loginData.forceUserGroup() && isNotBlank(loginData.getLoginGroupName()) && !loginUser.hasGroup(loginData.getLoginGroupName())) {
            logger.debug("force group = {} for user = {}", loginData.getLoginGroupName(), loginUser.getUsername());
            loginUser = LoginUserImpl.copyOf(loginUser).addGroup(getGroupInfoForGroup(loginData.getLoginGroupName())).build();
        }

        return buildOperationUser(loginData, loginUser);
    }

    @Override
    public OperationUser updateOperationUser(LoginData loginData, OperationUser operationUser) {
        return buildOperationUser(loginData, operationUser.getLoginUser());
    }

    @Override
    public RoleInfo getGroupInfoForGroup(String groupName) {
        return groupRepository.getGroupWithName(groupName);
    }

    @Override
    public Collection<String> getGroupNamesForUserWithId(Long userId) {
        LoginUser user = getUserByIdOrNull(userId);
        return user == null ? emptyList() : user.getGroupNames();
    }

    @Override
    public Collection<String> getGroupNamesForUserWithUsername(String loginString) {
        LoginUser user = getUserByUsernameOrNull(loginString);
        return user == null ? emptyList() : user.getGroupNames();
    }

    @Override
    public LoginUser getUserWithId(Long userId) {
        return getUserByIdOrNull(userId);
    }

    @Override
    public Role getGroupWithId(Long groupId) {
        return fetchGroupWithId(groupId);
    }

    @Override
    public Role getGroupWithName(String groupName) {
        return checkNotNull(getGroupWithNameOrNull(groupName), "group not found for name = %s", groupName);
    }

    @Override
    public OperationUser buildOperationUser(LoginData loginData, LoginUser loginUser) {
        String groupName = loginData.getLoginGroupName();
        UserPrivileges privilegeCtx;
        Role selectedGroup;
        if (isGodUser(loginUser)) {
            Role godRole = getGodDummyGroup();
            List<RoleInfo> groups = list(godRole);
            if (isNotBlank(groupName) && !isGodDummyGroup(groupName)) {
                selectedGroup = groupRepository.getGroupWithName(groupName);
                groups.add(selectedGroup);
            } else {
                selectedGroup = godRole;
            }
            privilegeCtx = getGodPrivilegeContext();
            loginUser = LoginUserImpl.copyOf(loginUser)
                    .withGroups(groups)
                    .withAvailableTenantContext(multitenantService.getAdminAvailableTenantContext()).build();
        } else {
            if (isNotBlank(groupName)) {
                checkArgument(loginUser.getGroupNames().contains(groupName), "user has not group = %s", groupName);
                selectedGroup = groupRepository.getGroupWithName(groupName);
            } else {
                Role guessedGroup = guessPreferredGroup(loginUser);
                if (guessedGroup == null) {
                    logger.debug("created not-valid session (user = {} does not have a default group and belongs to multiple groups)", loginUser.getUsername());
                    return builder().withAuthenticatedUser(loginUser).withTargetDevice(loginData.getTargetDevice()).withUserTenantContext(multitenantService.buildUserTenantContext(loginUser, loginData)).build();
                } else {
                    selectedGroup = guessedGroup;
                }
            }
            if (loginUser.hasMultigroupEnabled() && loginUser.getGroupNames().size() > 1) {
                privilegeCtx = buildPrivilegeContext(list(transform(loginUser.getGroupNames(), groupRepository::getGroupWithName)));
            } else {
                privilegeCtx = buildPrivilegeContext(selectedGroup);
            }
            if (privilegeCtx.hasPrivileges(RP_DATA_ALL_TENANT) || loginUser.getAvailableTenantContext().ignoreTenantPolicies()) {
                loginUser = LoginUserImpl.copyOf(loginUser).withAvailableTenantContext(multitenantService.getAdminAvailableTenantContext()).build();
            } else {
                loginUser = LoginUserImpl.copyOf(loginUser).withAvailableTenantContext(getUserByUsername(loginUser.getUsername()).getAvailableTenantContext()).build();
            }
        }
        UserTenantContext userTenantContext = multitenantService.buildUserTenantContext(loginUser, loginData);
        return builder()
                .withAuthenticatedUser(loginUser)
                .withTargetDevice(loginData.getTargetDevice())
                .withPrivilegeContext(privilegeCtx)
                .withDefaultGroup(selectedGroup)
                .withUserTenantContext(userTenantContext)
                .withSessionType(loginData.getSessionType())
                .build();
    }

    private void storeLoginFailedEvent(LoginUserIdentity login) {
        LoginUser user = getUserOrNull(login);
        if (user != null) {
            eventLogService.get().store(EVENT_LOGIN_FAILED, user.getId(), map("username", user.getUsername(), "login", login.getValue()));
        } else {
            eventLogService.get().store(EVENT_LOGIN_FAILED, map("login", login.getValue()));
        }
    }

    private LoginModuleClientRequestAuthenticator getClientRequestAuthenticatorForLoginModule(LoginModuleConfiguration loginModuleConfiguration) {
        return checkNotNull(loginModuleClientRequestAuthenticatorsByType.get(loginModuleConfiguration.getType()), "client request handler not found for module type =< %s >", loginModuleConfiguration.getType());
    }

    private List<PasswordAuthenticator> getActivePasswordAuthenticators() {
        return list(passwordAuthenticators).filter(PasswordAuthenticator::isEnabled);
    }

    private UserPrivileges buildPrivilegeContext(Role... groups) {
        return UserPrivilegesImpl.builder().withGroups(groups).build();
    }

    private UserPrivileges buildPrivilegeContext(Iterable<Role> groups) {
        return UserPrivilegesImpl.builder().withGroups(groups).build();
    }

    /**
     * Gets the default group (if any) or the only one. If no default group has
     * been found and more than one group is present, {@code null} is returned.
     */
    @Nullable
    private Role guessPreferredGroup(LoginUser user) {
        if (user.hasDefaultGroup()) {
            return groupRepository.getGroupWithName(user.getDefaultGroupName());
        } else if (user.getGroupNames().size() == 1) {
            return groupRepository.getGroupWithName(getOnlyElement(user.getGroupNames()));
        } else {
            return null;
        }
    }

}
