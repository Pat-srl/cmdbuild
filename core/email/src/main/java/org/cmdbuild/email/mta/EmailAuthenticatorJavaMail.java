/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.Authenticator;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.utils.MyAuthenticator;
import org.cmdbuild.utils.lang.CmMapUtils;

/**
 *
 * @author afelice
 */
public class EmailAuthenticatorJavaMail implements EmailAuthenticator {

    @Override
    public Authenticator buildAuthenticator(EmailAccount account, CmMapUtils.FluentMap<String, String> properties) {
        return new MyAuthenticator(account.getUsername(), account.getPassword());
    }
}
