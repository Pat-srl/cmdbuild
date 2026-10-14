/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.Authenticator;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.utils.lang.CmMapUtils;

/**
 *
 * @author afelice
 */
public interface EmailAuthenticator {

    default Authenticator buildAuthenticator(EmailAccount account, CmMapUtils.FluentMap<String, String> properties) {
        throw new IllegalArgumentException("invalid authentication type");
    }

}
