package org.cmdbuild.auth.user;

import java.util.Collection;
import org.cmdbuild.auth.grant.UserPrivileges;
import static org.cmdbuild.auth.utils.AuthUtils.checkAuthorized;

public interface OperationUserSupplier {

    OperationUser getUser();

    default UserPrivileges getPrivileges() {
        return getUser().getPrivilegeContext();
    }

    default String getUsername() {
        return getUser().getUsername();
    }

    default String getCurrentGroup() {
        return getUser().getDefaultGroupName();
    }

    default void checkPrivileges(PrivilegeChecker checker, String message, Object... args) {
        checkAuthorized(hasPrivileges(checker), message, args);
    }

    default boolean hasPrivileges(PrivilegeChecker checker) {
        return checker.hasPrivileges(getPrivileges());
    }

    default void checkPrivileges(PrivilegeCheckerWithUser checker, String message, Object... args) {
        checkAuthorized(hasPrivileges(checker), message, args);
    }

    default void checkPrivileges(PrivilegeChecker checker) {
        checkAuthorized(hasPrivileges(checker), "access denied: user does not have the required privileges for this operation");
    }

    default void checkPrivileges(PrivilegeCheckerWithUser checker) {
        checkAuthorized(hasPrivileges(checker), "access denied: user does not have the required privileges for this operation");
    }

    default boolean hasPrivileges(PrivilegeCheckerWithUser checker) {
        return checker.hasPrivileges(getUser(), getPrivileges());
    }

    default Collection<String> getActiveGroupNames() {
        return getUser().getActiveGroupNames();
    }

    default boolean hasMultitenant() {
        return getUser().hasMultitenant();
    }

    default boolean ignoreTenantPolicies() {
        return getUser().ignoreTenantPolicies();
    }

    interface PrivilegeChecker {

        boolean hasPrivileges(UserPrivileges privileges);
    }

    interface PrivilegeCheckerWithUser {

        boolean hasPrivileges(OperationUser user, UserPrivileges privileges);
    }
}
