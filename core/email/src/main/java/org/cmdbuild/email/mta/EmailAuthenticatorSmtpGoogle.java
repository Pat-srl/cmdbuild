/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import org.cmdbuild.utils.lang.CmMapUtils;

/**
 *
 * @author afelice
 */
public class EmailAuthenticatorSmtpGoogle extends EmailAuthenticatorGoogle implements EmailAuthenticator {

    @Override
    protected void enableXOAUTH2(CmMapUtils.FluentMap<String, String> properties) {
        properties.put("mail.smtp.auth.mechanisms", "XOAUTH2");
    }
}
