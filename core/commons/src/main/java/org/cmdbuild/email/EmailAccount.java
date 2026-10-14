package org.cmdbuild.email;

import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface EmailAccount {

    final String MAX_EMAIL_ATTACHMENTS_SIZE = "cm_max_email_attachments_size", AUTHENTICATION_TYPE = "cm_auth_type", AUTHENTICATION_TYPE_DEFAULT = "default", AUTHENTICATION_TYPE_GOOGLE_OAUTH2 = "google_oauth2", AUTHENTICATION_TYPE_MS_OAUTH2 = "ms_oauth";

    @Nullable
    Long getId();

    String getName();

    @Nullable
    String getUsername();

    @Nullable
    String getPassword();

    String getAddress();

    @Nullable
    String getSmtpServer();

    @Nullable
    Integer getSmtpPort();

    boolean getSmtpSsl();

    boolean getSmtpStartTls();

    @Nullable
    String getSentEmailFolder();

    @Nullable
    String getImapServer();

    @Nullable
    Integer getImapPort();

    Map<String, Object> getConfig();

    boolean getImapSsl();

    boolean getImapStartTls();

    boolean isActive();

    default boolean isSmtpConfigured() {
        return isNotBlank(getSmtpServer());
    }

    default boolean isImapConfigured() {
        return isNotBlank(getImapServer());
    }

    default boolean isAuthenticationEnabled() {
        return isNotBlank(getUsername());
    }

    default boolean isModernAuthentication() {
        return !getAuthenticationType().equals(AUTHENTICATION_TYPE_DEFAULT);
    }

    default boolean hasSentEmailFolder() {
        return isImapConfigured() && isNotBlank(getSentEmailFolder());
    }

    @Nullable
    default String getAuthenticationType() {
        return firstNotBlank(toStringOrNull(getConfig().get(AUTHENTICATION_TYPE)), AUTHENTICATION_TYPE_DEFAULT);
    }

    @Nullable
    default Integer getMaxEmailAttachmentsSizeMegs() {
        return toIntegerOrNull(getConfig().get(MAX_EMAIL_ATTACHMENTS_SIZE));
    }

}
