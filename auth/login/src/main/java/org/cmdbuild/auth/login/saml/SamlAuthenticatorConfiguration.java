/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.saml;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.LoginModuleConfiguration;

public interface SamlAuthenticatorConfiguration extends LoginModuleConfiguration {

    final String SAML_LOGIN_MODULE_TYPE = "saml";

    String getSamlServiceProviderEntityId();

    String getSamlIdpEntityId();

    String getSamlIdpLoginUrl();

    @Nullable
    String getSamlIdpLogoutUrl();

    @Nullable
    String getCmdbuildBaseUrlForSaml();

    @Nullable
    String getSamlServiceProviderKey();

    @Nullable
    String getSamlServiceProviderCertificate();

    @Nullable
    String getSamlIdpCertificate();

    @Nullable
    String getSamlSignatureAlgorithm();

    boolean getSamlRequireSignedMessages();

    boolean getSamlRequireSignedAssertions();

    boolean isSamlValidationStrict();

    boolean isSamlXmlValidation();

    boolean isSamlLogoutEnabled();
}
