package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.math.NumberUtils;
import static org.cmdbuild.auth.login.LoginType.LT_EMAIL;
import static org.cmdbuild.auth.login.LoginType.LT_USERNAME;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.dao.utils.CmFilterUtils.noopFilter;
import static org.cmdbuild.dao.utils.CmSorterUtils.noopSorter;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface UserRepository {

    final String BLANK_PASSWORD = "___BLANK_PASSWORD___";

    @Nullable
    LoginUser getActiveValidUserOrNull(LoginUserIdentity login);

    @Nullable
    LoginUser getUserByIdOrNull(Long userId);

    PagedElements<UserData> getAllWithRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit);

    PagedElements<UserData> getAllWithoutRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit);

    UserData getUserDataById(long id);

    @Nullable
    UserData getUserDataByUsernameOrNull(String username);

    @Nullable
    List<UserData> getUserDataByRoleId(long roleId);

    UserData create(UserData user);

    UserData update(UserData user);

    @Nullable
    UserData getUserDataOrNull(LoginUserIdentity login);

    @Nullable
    UserData getActiveUserDataOrNull(LoginUserIdentity login);

    boolean currentUserCanModify(UserData user);

    boolean currentUserCanModify(UserData user, Collection<Role> roles, Collection<Long> tenants);

    boolean currentUserCanAddUsersToRole(Role role);

    default UserData getUserData(LoginUserIdentity login) {
        return checkNotNull(getUserDataOrNull(login), "user not found for login = %s", login);
    }

    default UserData getActiveUserData(LoginUserIdentity login) {
        return checkNotNull(getActiveUserDataOrNull(login), "active user not found for login = %s", login);
    }

    default LoginUser getActiveUser(LoginUserIdentity identity) {
        return checkNotNull(getActiveValidUserOrNull(identity), "active user not found for identity = %s", identity);
    }

    @Nullable
    default LoginUser getActiveUserByEmailOrNull(String email) {
        return getActiveValidUserOrNull(LoginUserIdentity.builder().withType(LT_EMAIL).withValue(email).build());
    }

    @Nullable
    default LoginUser getActiveUserByUsernameOrNull(String username) {
        return getActiveValidUserOrNull(LoginUserIdentity.builder().withType(LT_USERNAME).withValue(username).build());
    }

    default LoginUser getActiveUserByUsername(String username) {
        return checkNotNull(getActiveUserByUsernameOrNull(username), "active user not found for username = %s", username);
    }

    default LoginUser getUserById(long userId) {
        return checkNotNull(getUserByIdOrNull(userId), "user not found for id = %s", userId);
    }

    default LoginUser getUserByIdOrUsername(String value) {
        if (NumberUtils.isCreatable(value)) {
            return getUserById(toLong(value));
        } else {
            return getActiveUserByUsername(value);
        }
    }

    default List<UserData> getAllWithRole(long roleId) {
        return getAllWithRole(roleId, noopFilter(), noopSorter(), null, null).elements();
    }
}
