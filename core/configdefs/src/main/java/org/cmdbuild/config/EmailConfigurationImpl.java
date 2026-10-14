package org.cmdbuild.config;

import jakarta.annotation.Nullable;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.entrytype.TextContentSecurity;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.email")
public final class EmailConfigurationImpl implements EmailQueueConfiguration, EmailConfiguration {

    @ConfigValue(key = "queue.enabled", description = "", defaultValue = FALSE, category = CC_ENV)
    private boolean enabled;

    @ConfigValue(key = "queue.time", description = "", defaultValue = "0")
    private int queueTime;

    @ConfigValue(key = "queue.maxErrors", description = "max failed tryes, after which a mail status is set to ERROR", defaultValue = "5")
    private int maxErrors;

    @ConfigValue(key = "queue.retry.delay.min", description = "delay for first retry, in case of email send error (in seconds)", defaultValue = "600")
    private int minRetryDelaySeconds;

    @ConfigValue(key = "queue.retry.delay.max", description = "max delay for next tries, in case of email send error (in seconds)", defaultValue = "86400")
    private int maxRetryDelaySeconds;

    @ConfigValue(key = "queue.retry.delay.increment", description = "proportional delay increment for subsequent retryes ( delay(n) = delay(n-1)*increment )", defaultValue = "3.0")
    private double retryDelayIncrement;

    @ConfigValue(key = "accountDefault", description = "default email account code")
    private String defaultEmailAccount;

    @ConfigValue(key = "signatureDefault", description = "default email signature")
    private String defaultEmailSignature;

    @ConfigValue(key = "job.continueOnError", description = "ignore errors when running email jobs (does not fail if a part of email processing throws an error). Set to false to enable fail-fast operation.", defaultValue = TRUE)
    private boolean emailJobSafe;

    @ConfigValue(key = "smtp.timeout", description = "smtp connection timeout, seconds; set to null to disable timeout", defaultValue = "30")
    private Integer smtpTimeoutSeconds;

    @ConfigValue(key = "imap.timeout", description = "imap connection timeout, seconds; set to null to disable timeout", defaultValue = "300")
    private Integer imapTimeoutSeconds;

    @ConfigValue(key = "defaultTextContentSecurity", description = "default text content security (for outgoing email content, etc), one of `html_all`, `html_safe`", defaultValue = "html_all")
    private TextContentSecurity defaultTextContentSecurity;

    @ConfigValue(key = "allowDuplicateAttachmentName", description = "allow duplicate attachment name; if true, will rename duplicate attachments when attaching to card/email/process", defaultValue = TRUE)
    private boolean allowDuplicateAttachmentName;

    @ConfigValue(key = "maxAttachmentSizeForEmail", description = "max size for all attachment, for a single email ( MB )")
    private Integer maxAttachmentSizeForEmailMegs;

    @ConfigValue(key = "attachment.addExtensionIfMissing", description = "add extension, if missing, from content type to attachment if possible", defaultValue = FALSE)
    private boolean addAttachmentExtensionIfMissing;

    @Override
    @Nullable
    public Integer getMaxAttachmentSizeForEmailMegs() {
        return maxAttachmentSizeForEmailMegs;
    }

    @Override
    public boolean allowDuplicateAttachmentName() {
        return allowDuplicateAttachmentName;
    }

    @Override
    public String getDefaultEmailSignature() {
        return defaultEmailSignature;
    }

    @Override
    public TextContentSecurity getDefaultTextContentSecurity() {
        return defaultTextContentSecurity;
    }

    @Override
    public boolean isQueueEnabled() {
        return enabled;
    }

    @Override
    @Nullable
    public Integer getSmtpTimeoutSeconds() {
        return smtpTimeoutSeconds;
    }

    @Override
    @Nullable
    public Integer getImapTimeoutSeconds() {
        return imapTimeoutSeconds;
    }

    @Override
    public long getQueueTime() {
        return queueTime;
    }

    @Override
    public int getMaxErrors() {
        return maxErrors;
    }

    @Override
    public int getMinRetryDelaySeconds() {
        return minRetryDelaySeconds;
    }

    @Override
    public int getMaxRetryDelaySeconds() {
        return maxRetryDelaySeconds;
    }

    @Override
    public double getRetryDelayIncrement() {
        return retryDelayIncrement;
    }

    @Override
    @Nullable
    public String getDefaultEmailAccountCode() {
        return defaultEmailAccount;
    }

    @Override
    public boolean emailJobContinueOnError() {
        return emailJobSafe;
    }

    @Override
    public boolean addAttachmentExtensionIfMissing() {
        return addAttachmentExtensionIfMissing;
    }
}
