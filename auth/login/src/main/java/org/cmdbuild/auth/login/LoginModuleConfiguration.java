/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

public interface LoginModuleConfiguration {

    final String DEFAULT_LOGIN_MODULE_TYPE = "default";

    String getCode();

    String getDescription();

    String getIcon();

    boolean isEnabled();

    boolean isHidden();

    String getType();

    String getLoginHandlerScript();

}
