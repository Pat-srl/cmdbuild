/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;

/**
 *
 * @author afelice
 */
public interface EmailMoverProvider {

    void moveToFolder(Message message, String targetFolderName) throws MessagingException;
}
