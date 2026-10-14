/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;

import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.utils.crypto.PasswordBulletsUtils.stringToBullets;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class EmailAccountSerializationHelper {

    public static FluentMap<String, Object> serializeBasicAccount(EmailAccount a) {
        return map(
                "_id", a.getId(),
                "name", a.getName()
        );
    }

    public static FluentMap<String, Object> serializePublicAccount(EmailAccount a) {
        return serializeBasicAccount(a).with(
                "maxAttachmentSizeForEmail", a.getMaxEmailAttachmentsSizeMegs()
        );
    }

    public static FluentMap<String, Object> serializeDetailedAccount(EmailAccount a, EmailAccountService service) {
        return serializePublicAccount(a).with(
                "default", equal(a.getName(), service.getDefaultCodeOrNull()),
                "username", a.getUsername(),
                "password", stringToBullets(a.getPassword()),
                "address", a.getAddress(),
                "smtp_server", a.getSmtpServer(),
                "smtp_port", a.getSmtpPort(),
                "smtp_ssl", a.getSmtpSsl(),
                "smtp_starttls", a.getSmtpStartTls(),
                "imap_output_folder", a.getSentEmailFolder(),
                "imap_server", a.getImapServer(),
                "imap_port", a.getImapPort(),
                "imap_ssl", a.getImapSsl(),
                "imap_starttls", a.getImapStartTls(),
                "auth_type", a.getAuthenticationType(),
                "active", a.isActive()
        );
    }
}
