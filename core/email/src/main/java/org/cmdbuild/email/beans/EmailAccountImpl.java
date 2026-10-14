/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CardMapping("_EmailAccount")
public class EmailAccountImpl implements EmailAccount {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Long id;
    private final Integer smtpPort, imapPort;
    private final String name, username, password, address, outputFolder, smtpServer, imapServer;
    private final boolean smtpSsl, smtpStartTls, imapSsl, imapStartTls;
    private final Map<String, Object> config;
    private final boolean isActive;

    private EmailAccountImpl(EmailAccountImplBuilder builder) {
        this.id = builder.id;
        this.smtpPort = builder.smtpPort;
        this.imapPort = builder.imapPort;
        this.name = builder.name;
        this.username = builder.username;
        this.password = Cm3EasyCryptoUtils.decryptValue(builder.password);
        this.address = builder.address;
        this.outputFolder = builder.outputFolder;
        this.smtpServer = builder.smtpServer;
        this.imapServer = builder.imapServer;
        this.smtpSsl = firstNotNull(builder.smtpSsl, false);
        this.smtpStartTls = firstNotNull(builder.smtpStartTls, false);
        this.imapSsl = firstNotNull(builder.imapSsl, false);
        this.imapStartTls = firstNotNull(builder.imapStartTls, false);
        this.config = map(checkNotNull(builder.config)).immutable();
        this.isActive = firstNotNull(builder.isActive, true);

        if (isImapConfigured() && (imapSsl && imapStartTls)) {
            LOGGER.warn(marker(), "you have enabled both imaps and imap starttls for account {} : this is usually an error, at most one of these should be enabled", this);
        }
        if (isSmtpConfigured() && (smtpSsl && smtpStartTls)) {
            LOGGER.warn(marker(), "you have enabled both smtps and smtp starttls for account {} : this is usually an error, at most one of these should be enabled", this);
        }
    }

    @CardAttr(ATTR_ID)
    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @CardAttr
    @Nullable
    @Override
    public Integer getSmtpPort() {
        return smtpPort;
    }

    @CardAttr
    @Nullable
    @Override
    public Integer getImapPort() {
        return imapPort;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getName() {
        return name;
    }

    @CardAttr
    @Nullable
    @Override
    public String getUsername() {
        return username;
    }

    @Nullable
    @Override
    public String getPassword() {
        return password;
    }

    @CardAttr("Password")
    @Nullable
    public String getPasswordForDb() {
        return Cm3EasyCryptoUtils.encryptValueIfNotEncrypted(password);
    }

    @CardAttr
    @Nullable
    @Override
    public String getAddress() {
        return address;
    }

    @CardAttr("OutputFolder")
    @Nullable
    @Override
    public String getSentEmailFolder() {
        return outputFolder;
    }

    @CardAttr
    @Nullable
    @Override
    public String getSmtpServer() {
        return smtpServer;
    }

    @CardAttr
    @Nullable
    @Override
    public String getImapServer() {
        return imapServer;
    }

    @CardAttr
    @Nullable
    @Override
    public boolean getSmtpSsl() {
        return smtpSsl;
    }

    @CardAttr
    @Nullable
    @Override
    public boolean getSmtpStartTls() {
        return smtpStartTls;
    }

    @CardAttr
    @Nullable
    @Override
    public boolean getImapSsl() {
        return imapSsl;
    }

    @CardAttr
    @Nullable
    @Override
    public boolean getImapStartTls() {
        return imapStartTls;
    }

    @CardAttr
    @JsonBean
    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    @Override
    @CardAttr
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "EmailAccount{" + "id=" + id + ", name=" + name + ", address=" + address + '}';
    }

    public static EmailAccountImplBuilder builder() {
        return new EmailAccountImplBuilder();
    }

    public static EmailAccountImplBuilder copyOf(EmailAccount source) {
        return new EmailAccountImplBuilder()
                .withId(source.getId())
                .withSmtpPort(source.getSmtpPort())
                .withImapPort(source.getImapPort())
                .withName(source.getName())
                .withUsername(source.getUsername())
                .withPassword(source.getPassword())
                .withAddress(source.getAddress())
                .withSentEmailFolder(source.getSentEmailFolder())
                .withSmtpServer(source.getSmtpServer())
                .withImapServer(source.getImapServer())
                .withSmtpSsl(source.getSmtpSsl())
                .withSmtpStartTls(source.getSmtpStartTls())
                .withImapSsl(source.getImapSsl())
                .withImapStartTls(source.getImapStartTls())
                .withConfig(source.getConfig())
                .withActive(source.isActive());
    }

    public static class EmailAccountImplBuilder implements Builder<EmailAccountImpl, EmailAccountImplBuilder> {

        private Long id;
        private Integer smtpPort;
        private Integer imapPort;
        private String name;
        private String username;
        private String password;
        private String address;
        private String outputFolder;
        private String smtpServer;
        private String imapServer;
        private Boolean smtpSsl;
        private Boolean smtpStartTls;
        private Boolean imapSsl;
        private Boolean imapStartTls;
        private Boolean isActive;
        private Map<String, Object> config = map();

        public EmailAccountImplBuilder withConfig(Map<String, ?> config) {
            this.config.putAll(firstNotNull(config, emptyMap()));
            return this;
        }

        public EmailAccountImplBuilder withConfig(String key, Object value) {
            this.config.put(key, value);
            return this;
        }

        public EmailAccountImplBuilder withAuthenticationType(@Nullable String value) {
            return this.withConfig(AUTHENTICATION_TYPE, toStringOrNull(value));
        }

        public EmailAccountImplBuilder withMaxEmailAttachmentsSizeMegs(@Nullable Integer value) {
            return this.withConfig(MAX_EMAIL_ATTACHMENTS_SIZE, toStringOrNull(value));
        }

        public EmailAccountImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EmailAccountImplBuilder withSmtpPort(Integer smtpPort) {
            this.smtpPort = smtpPort;
            return this;
        }

        public EmailAccountImplBuilder withImapPort(Integer imapPort) {
            this.imapPort = imapPort;
            return this;
        }

        public EmailAccountImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public EmailAccountImplBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        @CardAttr("Password")
        public EmailAccountImplBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public EmailAccountImplBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public EmailAccountImplBuilder withSentEmailFolder(String outputFolder) {
            this.outputFolder = outputFolder;
            return this;
        }

        public EmailAccountImplBuilder withSmtpServer(String smtpServer) {
            this.smtpServer = smtpServer;
            return this;
        }

        public EmailAccountImplBuilder withImapServer(String imapServer) {
            this.imapServer = imapServer;
            return this;
        }

        public EmailAccountImplBuilder withSmtpSsl(Boolean smtpSsl) {
            this.smtpSsl = smtpSsl;
            return this;
        }

        public EmailAccountImplBuilder withSmtpStartTls(Boolean smtpStartTls) {
            this.smtpStartTls = smtpStartTls;
            return this;
        }

        public EmailAccountImplBuilder withImapSsl(Boolean imapSsl) {
            this.imapSsl = imapSsl;
            return this;
        }

        public EmailAccountImplBuilder withImapStartTls(Boolean imapStartTls) {
            this.imapStartTls = imapStartTls;
            return this;
        }

        public EmailAccountImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        @Override
        public EmailAccountImpl build() {
            return new EmailAccountImpl(this);
        }

    }
}
