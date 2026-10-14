/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.email.beans.EmailAccountImpl;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author schursin
 */
public class WsEmailAccountData {

    private final String name, username, password, address, smtpServer, imapOutputFolder, imapServer, authType;

    public String getPassword() {
        return password;
    }

    private final Integer smtpPort, imapPort, maxAttachmentSizeForEmail;
    private final Boolean smtpSsl, smtpStarttls, imapSsl, imapStarttls, active;

    public WsEmailAccountData(
            @JsonProperty("name") String name,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("address") String address,
            @JsonProperty("smtp_server") String smtpServer,
            @JsonProperty("imap_output_folder") String imapOutputFolder,
            @JsonProperty("imap_server") String imapServer,
            @JsonProperty("smtp_port") Integer smtpPort,
            @JsonProperty("imap_port") Integer imapPort,
            @JsonProperty("maxAttachmentSizeForEmail") Integer maxAttachmentSizeForEmail,
            @JsonProperty("smtp_ssl") Boolean smtpSsl,
            @JsonProperty("smtp_starttls") Boolean smtpStarttls,
            @JsonProperty("imap_ssl") Boolean imapSsl,
            @JsonProperty("imap_starttls") Boolean imapStarttls,
            @JsonProperty("auth_type") String authType,
            @JsonProperty("active") Boolean active) {
        this.name = checkNotBlank(name);
        this.username = username;
        this.password = password;
        this.address = address;
        this.smtpServer = smtpServer;
        this.imapOutputFolder = imapOutputFolder;
        this.imapServer = imapServer;
        this.smtpPort = smtpPort;
        this.imapPort = imapPort;
        this.smtpSsl = smtpSsl;
        this.smtpStarttls = smtpStarttls;
        this.imapSsl = imapSsl;
        this.imapStarttls = imapStarttls;
        this.authType = authType;
        this.active = active;
        this.maxAttachmentSizeForEmail = maxAttachmentSizeForEmail;
    }

    public EmailAccountImpl.EmailAccountImplBuilder toEmailAccount() {
        return EmailAccountImpl.builder()
                .withName(name)
                .withAddress(address)
                .withImapPort(imapPort)
                .withImapServer(imapServer)
                .withImapSsl(imapSsl)
                .withImapStartTls(imapStarttls)
                .withSentEmailFolder(imapOutputFolder)
                .withSmtpPort(smtpPort)
                .withSmtpServer(smtpServer)
                .withSmtpSsl(smtpSsl)
                .withSmtpStartTls(smtpStarttls)
                .withUsername(username)
                .withPassword(password)
                .withMaxEmailAttachmentsSizeMegs(maxAttachmentSizeForEmail)
                .withAuthenticationType(authType)
                .withActive(active);
    }

} // end WsEmailAccountData class
