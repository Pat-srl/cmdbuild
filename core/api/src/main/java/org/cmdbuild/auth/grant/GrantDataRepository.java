package org.cmdbuild.auth.grant;

import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.List;
import org.cmdbuild.auth.role.Role;

public interface GrantDataRepository {

    List<GrantData> getGrantsForTypeAndRole(PrivilegedObjectType type, long groupId);

    List<GrantData> getGrantsForRole(long roleId);

    List<GrantData> setGrantsForRole(long roleId, Collection<GrantData> grants);

    List<GrantData> updateGrantsForRole(long roleId, Collection<GrantData> grants);

    default List<GrantData> setGrantsForRole(Role role, GrantData... grants) {
        return setGrantsForRole(role.getId(), asList(grants));
    }

}
