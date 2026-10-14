/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.MessagingException;
import org.cmdbuild.email.Email;

/**
 *
 * @author afelice
 */
public interface EmailSenderProvider extends EmailProvider {

    Email sendEmail(Email email) throws MessagingException;
}
