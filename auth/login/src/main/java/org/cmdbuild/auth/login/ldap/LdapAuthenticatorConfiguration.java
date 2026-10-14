/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.ldap;

public interface LdapAuthenticatorConfiguration {

    boolean isLdapEnabled();

    String getLdapServerUrl();

    String getLdapServerAddress();

    int getLdapServerPort();

    String getLdapBaseDN();

    String getLdapSearchFilter();

    String getLdapBindAttribute();

    String getLdapAuthenticationMethod();

    String getLdapPrincipal();

    String getLdapPrincipalCredentials();

    boolean enableLdapFollowReferrals();

    boolean enableLdapSsl();

    boolean enableStartTls();

}
