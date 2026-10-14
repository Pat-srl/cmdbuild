/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.cas;

import org.cmdbuild.auth.login.LoginModuleConfiguration;

public interface CasAuthenticatorConfiguration extends LoginModuleConfiguration {

    final String CAS_LOGIN_MODULE_TYPE = "cas";

    String getCasServerUrl();

    String getCasLoginPage();

    String getCasTicketParam();

    String getCasServiceParam();

}
