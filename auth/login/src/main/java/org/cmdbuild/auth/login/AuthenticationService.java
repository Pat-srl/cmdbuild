package org.cmdbuild.auth.login;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.PasswordSupplier;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

public interface AuthenticationService extends PasswordSupplier {

    final String SESSION_LOGIN_MODULE_CODE = "org.cmdbuild.auth.login.SESSION_LOGIN_MODULE_CODE";

    LoginUser checkPasswordAndGetUser(LoginUserIdentity login, String password);

    RequestAuthenticatorResponse<LoginUser> validateCredentialsAndCreateAuthResponse(AuthRequestInfo request);

    RequestAuthenticatorResponse<Void> invalidateCredentialsAndCreateLogoutResponse(@Nullable Object request);

    LoginUser getUserByIdOrNull(Long userId);

    @Nullable
    LoginUser getUserOrNull(LoginUserIdentity identity);

    Collection<Role> getAllGroups();

    Role fetchGroupWithId(Long groupId);

    @Nullable
    Role getGroupWithNameOrNull(String groupName);

    OperationUser validateCredentialsAndCreateOperationUser(LoginData loginData);

    OperationUser updateOperationUser(LoginData loginData, OperationUser operationUser);

    RoleInfo getGroupInfoForGroup(String groupName);

    Collection<String> getGroupNamesForUserWithId(Long userId);

    Collection<String> getGroupNamesForUserWithUsername(String loginString);

    LoginUser getUserWithId(Long userId);

    Role getGroupWithId(Long groupId);

    Role getGroupWithName(String groupName);

    OperationUser buildOperationUser(LoginData loginData, LoginUser loginUser);

    default LoginUser getUserForLogin(LoginUserIdentity identity) {
        try {
            return checkNotNull(getUserOrNull(identity), "user not found for identity = %s", identity);
        } catch (Exception ex) {
            throw runtime(ex, "CMO 101: user not enabled");
        }
    }

    @Nullable
    default LoginUser getUserByUsernameOrNull(String username) {
        return getUserOrNull(LoginUserIdentity.builder().withValue(username).build());
    }

    default LoginUser getUserByUsername(String username) {
        return checkNotNull(getUserByUsernameOrNull(username), "user not found for username = %s", username);
    }

}
