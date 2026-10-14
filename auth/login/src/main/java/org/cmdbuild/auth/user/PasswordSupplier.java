package org.cmdbuild.auth.user;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.utils.lang.CmPreconditions;

public interface PasswordSupplier {

    @Nullable
    String getUnencryptedPasswordOrNull(LoginUserIdentity login);

    String getEncryptedPassword(LoginUserIdentity login);

    default String getUnencryptedPassword(LoginUserIdentity login) {
        return CmPreconditions.checkNotBlank(getUnencryptedPasswordOrNull(login), "unencrypted password is not available for login = %s", login);
    }

}
