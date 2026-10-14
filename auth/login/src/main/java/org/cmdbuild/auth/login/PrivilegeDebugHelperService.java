package org.cmdbuild.auth.login;

import jakarta.annotation.Nullable;

public interface PrivilegeDebugHelperService {

    String dumpDebugInfoForGroup(String groupName, @Nullable String filter);

    default String dumpDebugInfoForGroup(String groupName) {
        return dumpDebugInfoForGroup(groupName, null);
    }

}
