/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.header;

public interface HeaderAuthenticatorConfiguration {

    final String HEADER_AUTHENTICATOR_TYPE = "header";

    boolean isHeaderEnabled();

    String getHeaderAttributeName();

}
