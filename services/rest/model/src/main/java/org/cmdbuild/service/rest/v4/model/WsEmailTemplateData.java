/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.email.beans.EmailTemplateImpl.EmailTemplateImplBuilder;
import org.cmdbuild.utils.lang.CmNullableUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;

/**
 *
 * @author ldare
 */
public class WsEmailTemplateData {

    private final Long delay, account, signature;
    private final String from, to, cc, bcc, subject, body, name, description, contentType, provider;
    private final Boolean keepSynchronization, promptSynchronization, active;
    private final Map<String, String> data;
    private final List<WsReportConfigData> reports;

    public WsEmailTemplateData(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("delay") Long delay,
            @JsonProperty("from") String from,
            @JsonProperty("to") String to,
            @JsonProperty("cc") String cc,
            @JsonProperty("bcc") String bcc,
            @JsonProperty("subject") String subject,
            @JsonProperty("contentType") String contentType,
            @JsonProperty("body") String body,
            @JsonProperty("account") Long account,
            @JsonProperty("signature") Long signature,
            @JsonProperty("keepSynchronization") Boolean keepSynchronization,
            @JsonProperty("promptSynchronization") Boolean promptSynchronization,
            @JsonProperty("provider") String provider,
            @JsonProperty("active") Boolean active,
            @JsonProperty("showOnClasses") String showOnClasses,
            @JsonProperty("data") Map<String, String> data,
            @JsonProperty("reports") List<WsReportConfigData> reports) {
        this.delay = delay;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.account = account;
        this.signature = signature;
        this.name = name;
        this.contentType = contentType;
        this.description = description;
        this.keepSynchronization = keepSynchronization;
        this.promptSynchronization = promptSynchronization;
        this.provider = provider;
        this.active = active;
        this.data = map(nullToEmpty(data)).with("showOnClasses", showOnClasses);//TODO change this to checknotnull
        this.reports = ImmutableList.copyOf(CmNullableUtils.firstNotNull(reports, Collections.emptyList()));
    }

    public EmailTemplateImplBuilder toEmailTemplate() {
        return EmailTemplateImpl.builder()
                .withDelay(delay)
                .withAccount(account)
                .withBcc(bcc)
                .withCc(cc)
                .withContent(body)
                .withDelay(delay)
                .withFrom(from)
                .withKeepSynchronization(keepSynchronization)
                .withPromptSynchronization(promptSynchronization)
                .withSubject(subject)
                .withTo(to)
                .withDescription(description)
                .withCode(name)
                .withContentType(contentType)
                .withMeta(data)
                .withSignature(signature)
                .withNotificationProvider(provider)
                .withActive(active)
                .withReports(reports.stream().map((r) -> r.buildReportConfig().build()).collect(Collectors.toList()));
    }
} // end WsEmailTemplateData class
