/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;

public interface EmailApi {

    EmailAccount getEmailAccount(String idOrCode);

    void loadEmail(String className, long cardId, String content);

    void acquireEmail(byte[] content);

    Email testEmailTemplate(String className, long cardId, String templateId);

}
