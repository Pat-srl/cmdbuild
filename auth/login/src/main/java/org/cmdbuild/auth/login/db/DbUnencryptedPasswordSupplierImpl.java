/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.db;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.user.UserRepository;
import static org.cmdbuild.auth.utils.CmPasswordUtils.decryptPasswordIfPossible;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.user.PasswordSupplier;

@Component
@Primary
public class DbUnencryptedPasswordSupplierImpl implements PasswordSupplier {

    private final UserRepository userRepository;

    public DbUnencryptedPasswordSupplierImpl(UserRepository userRepository) {
        this.userRepository = checkNotNull(userRepository);
    }

    @Override
    @Nullable
    public String getUnencryptedPasswordOrNull(LoginUserIdentity login) {
        return decryptPasswordIfPossible(userRepository.getActiveUserData(login).getPassword());
    }

    @Override
    public String getEncryptedPassword(LoginUserIdentity login) {
        return userRepository.getActiveUserData(login).getPassword();
    }

}
