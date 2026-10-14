/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

public interface LoginModule<T extends LoginModuleConfiguration> {

    LoginModuleType getType();

    String getCode();

    T getConfiguration();
}
