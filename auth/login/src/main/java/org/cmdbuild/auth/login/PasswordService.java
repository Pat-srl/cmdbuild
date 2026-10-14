/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import jakarta.annotation.Nullable;

public interface PasswordService extends PasswordRecoveryService, PasswordEncodingService {

    void verifyNewPasswordForPasswordUpdate(@Nullable String username, String newPassword);

    PasswordCheckStatus checkPasswordForUser(LoginUserIdentity login, String password);

    void verifyAndUpdatePasswordForUser(String username, String oldpassword, String password);
}
