/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.UrlEscapers;
import static java.lang.String.format;
import org.cmdbuild.client.rest.api.EmailApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.beans.EmailAccountImpl;
import org.cmdbuild.service.rest.v4.model.WsEmailData;
import static org.cmdbuild.utils.encode.CmPackUtils.pack;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public class EmailApiImpl extends AbstractServiceClientImpl implements EmailApi {

    public EmailApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public EmailAccount getEmailAccount(String idOrCode) {
        JsonNode data = get("email/accounts/" + UrlEscapers.urlPathSegmentEscaper().escape(checkNotBlank(idOrCode))).asJackson().get("data");
        return EmailAccountImpl.builder()
                .withId(data.get("_id").asLong())
                .withName(data.get("name").asText())
                .withAddress(data.get("address").asText())
                .withUsername(data.get("username").asText())
                .withPassword(data.get("password").asText())
                .withSmtpServer(data.get("smtp_server").asText())
                .withSmtpPort(toIntegerOrNull(data.get("smtp_port").asText()))
                .withSmtpSsl(toBooleanOrNull(data.get("smtp_ssl").asText()))
                .withSmtpStartTls(toBooleanOrNull(data.get("smtp_starttls").asText()))
                .withImapServer(data.get("imap_server").asText())
                .withImapPort(toIntegerOrNull(data.get("imap_port").asText()))
                .withImapSsl(toBooleanOrNull(data.get("imap_ssl").asText()))
                .withImapStartTls(toBooleanOrNull(data.get("imap_starttls").asText()))
                .withSentEmailFolder(data.get("imap_output_folder").asText())
                .build();
    }

    @Override
    public void loadEmail(String className, long cardId, String content) {
        post(format("classes/%s/cards/%s/emails/load", UrlEscapers.urlPathSegmentEscaper().escape(checkNotBlank(className)), checkNotNullAndGtZero(cardId)), map("data", checkNotBlank(content)));
    }

    @Override
    public void acquireEmail(byte[] content) {
        post("classes/_ANY/cards/_ANY/emails/acquire", map("data", pack(content)));
    }

    @Override
    public Email testEmailTemplate(String className, long cardId, String templateId) {
        Email email = fromJson(post(
                format("classes/%s/cards/%s/emails?template_only=true", UrlEscapers.urlPathSegmentEscaper().escape(checkNotBlank(className)), checkNotNullAndGtZero(cardId)), map("template", checkNotBlank(templateId))
        ).asJackson()
                .get("data"), WsEmailData.class)
                .toEmail()
                .build();
        return email;
    }

}
