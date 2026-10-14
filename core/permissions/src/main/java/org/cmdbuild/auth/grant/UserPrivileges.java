package org.cmdbuild.auth.grant;

import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.auth.grant.GrantPrivilege;
import org.cmdbuild.auth.grant.PrivilegeSubject;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_READ;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WRITE;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_READ;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_WRITE;
import org.cmdbuild.auth.role.RolePrivilegeHolder;

public interface UserPrivileges extends RolePrivilegeHolder {

    boolean hasServicePrivilege(GrantPrivilege privilege, PrivilegeSubject privilegedObject);

    Map<String, UserPrivilegesForObject> getAllPrivileges();

    UserPrivilegesForObject getPrivilegesForObject(PrivilegeSubject object);

    default boolean hasReadAccess(PrivilegeSubject privilegedObject) {
        return hasPrivileges(RP_DATA_ALL_READ) || hasServicePrivilege(GP_READ, privilegedObject);
    }

    default boolean hasWriteAccess(PrivilegeSubject privilegedObject) {
        return hasPrivileges(RP_DATA_ALL_WRITE) || hasServicePrivilege(GP_WRITE, privilegedObject);
    }

    default boolean hasSourceGroups() {
        return !getSourceGroups().isEmpty();
    }

    default Set<String> getSourceGroups() {
        return emptySet();
    }

}
