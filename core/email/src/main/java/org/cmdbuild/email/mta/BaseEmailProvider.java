/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.mta.EmailMtaServiceImpl.EMAIL_HEADER_X_MAILER;
import static org.cmdbuild.email.utils.EmailMtaUtils.fillMessage;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

/**
 *
 * @author afelice
 */
public abstract class BaseEmailProvider implements EmailProvider {

    protected final EmailAccount emailAccount;

    public BaseEmailProvider(EmailAccount emailAccount) {
        this.emailAccount = checkNotNull(emailAccount);
    }

    protected Message buildMessage(Email email, InstanceInfoService infoService, Session session) throws MessagingException {
        Message message = new MimeMessage(session);
        fillMessage(message, email);
        message.addHeader(EMAIL_HEADER_X_MAILER, format("CMDBuild v%s", infoService.getVersion()));
        message.addHeader("X-CMDBuild-Version", infoService.getVersion());
        message.addHeader("X-CMDBuild-Revision", infoService.getRevision());
        message.addHeader("X-CMDBuild-InstanceInfo", infoService.getInstanceInfo());
        if (email.hasId()) {
            message.addHeader("X-CMDBuild-EmailId", toStringNotBlank(email.getId()));
        }
        return message;
    }

}
