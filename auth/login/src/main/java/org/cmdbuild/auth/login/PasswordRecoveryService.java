/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

public interface PasswordRecoveryService {

    public final static String PASSWORD_EXPIRATION_AND_RECOVERY_SERVICE_NAME = "passwordExpirationAndRecovery";

    void requirePasswordRecovery(String username);
}
