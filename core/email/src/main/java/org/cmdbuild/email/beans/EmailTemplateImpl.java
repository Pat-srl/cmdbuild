/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.beans;

import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.isNull;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_EMAIL;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.report.ReportConfig;
import org.cmdbuild.report.ReportConfigImpl;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@CardMapping("_EmailTemplate")
public class EmailTemplateImpl implements EmailTemplate {

    private final Long id, account, delay, signature;
    private final String code, description, from, to, cc, bcc, subject, body, contentType, type;
    private final boolean keepSynchronization, promptSynchronization;
    private final Map<String, String> meta;
    private final List<ReportConfig> reports;
    private final boolean isActive;

    public final static String USED_FOR_REPORT_BINDING = "USED_FOR_REPORT_BINDING";

    private EmailTemplateImpl(EmailTemplateImplBuilder builder) {
        this.id = builder.id;
        this.type = firstNotBlank(builder.type, NOTIFICATION_PROVIDER_EMAIL);
        this.code = checkNotBlank(builder.name, "template name is null");
        this.delay = builder.delay;
        this.account = builder.account;
        this.description = builder.description;
        this.from = builder.from;
        this.signature = builder.signature;
        this.to = builder.to;
        this.cc = builder.cc;
        this.bcc = builder.bcc;
        this.subject = builder.subject;
        this.body = builder.body;
        this.isActive = firstNotNull(builder.isActive, true);
        this.contentType = checkNotBlank(builder.contentType, "content type is null");
        this.keepSynchronization = firstNotNull(builder.keepSynchronization, true);
        this.promptSynchronization = firstNotNull(builder.promptSynchronization, false);
        this.meta = map(firstNonNull(builder.meta, emptyMap())).immutable();
        this.reports = ImmutableList.copyOf(firstNotNull(builder.reports, emptyList()));
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr("ContentType")
    public String getContentType() {
        return contentType;
    }

    @Override
    @Nullable
    @CardAttr
    public Long getDelay() {
        return delay;
    }

    @Override
    @Nullable
    @CardAttr
    public Long getAccount() {
        return account;
    }

    @Override
    @Nullable
    @CardAttr(ATTR_CODE)
    public String getCode() {
        return code;
    }

    @Override
    @Nullable
    @CardAttr
    public String getDescription() {
        return description;
    }

    @Override
    @Nullable
    @CardAttr
    public String getFrom() {
        return from;
    }

    @Override
    @Nullable
    @CardAttr
    public String getTo() {
        return to;
    }

    @Override
    @Nullable
    @CardAttr("CC")
    public String getCc() {
        return cc;
    }

    @Override
    @Nullable
    @CardAttr("BCC")
    public String getBcc() {
        return bcc;
    }

    @Override
    @Nullable
    @CardAttr
    public String getSubject() {
        return subject;
    }

    @Override
    @Nullable
    @CardAttr("Body")
    public String getContent() {
        return body;
    }

    @Override
    @CardAttr("Data")
    @JsonBean
    public Map<String, String> getMeta() {
        return map(meta).accept(m -> {
            reports.forEach(r -> {
                m.put(r.getCode(), USED_FOR_REPORT_BINDING); // @todo to be removed in #7426
            });
        });
    }

    @Override
    @CardAttr
    public boolean getKeepSynchronization() {
        return keepSynchronization;
    }

    @Override
    @CardAttr
    public boolean getPromptSynchronization() {
        return promptSynchronization;
    }

    @Override
    @CardAttr("NotificationProvider")
    public String getNotificationProvider() {
        return type;
    }

    @Override
    public List<String> getReportCodes() {
        return list(reports).map(ReportConfig::getCode);
    }

    @Override
    @CardAttr("ReportConfigs")
    public List<ReportConfig> getReports() {
        return reports;
    }

    @Override
    public CmdbFilter getUploadAttachmentsFilter() {
        if (!meta.containsKey(UPLOAD_ATTACHMENTS_FILTERS)) {
            return CmdbFilterImpl.falseFilter(); // Nothing to return
        }

        String filterStr = meta.get(UPLOAD_ATTACHMENTS_FILTERS);
        if (StringUtils.isBlank(filterStr)) {
            return CmdbFilterImpl.falseFilter(); // Nothing to return
        }

        if (UPLOAD_ATTACHMENTS_ALL.equals(filterStr)) {
            filterStr = ""; // Means "all", filter nothing
        }
        return CmFilterUtils.parseFilter(filterStr);
    }

    @Override
    @CardAttr
    @Nullable
    public Long getSignature() {
        return signature;
    }

    @Override
    @CardAttr
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "EmailTemplate{" + "id=" + id + ", name=" + code + '}';
    }

    public static EmailTemplateImplBuilder builder() {
        return new EmailTemplateImplBuilder();
    }

    public static EmailTemplateImplBuilder copyOf(EmailTemplate source) {
        return new EmailTemplateImplBuilder()
                .withId(source.getId())
                .withDelay(source.getDelay())
                .withAccount(source.getAccount())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withFrom(source.getFrom())
                .withTo(source.getTo())
                .withCc(source.getCc())
                .withBcc(source.getBcc())
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withMeta(source.getMeta())
                .withContentType(source.getContentType())
                .withKeepSynchronization(source.getKeepSynchronization())
                .withPromptSynchronization(source.getPromptSynchronization())
                .withNotificationProvider(source.getNotificationProvider())
                .withReports(source.getReports())
                .withSignature(source.getSignature())
                .withActive(source.isActive());
    }

    public static class EmailTemplateImplBuilder implements Builder<EmailTemplateImpl, EmailTemplateImplBuilder> {

        private Long id;
        private Long delay;
        private Long account, signature;
        private String name;
        private String description;
        private String from;
        private String to;
        private String cc;
        private String bcc;
        private String subject;
        private String body, contentType, type;
        private Boolean keepSynchronization;
        private Boolean promptSynchronization;
        private Map<String, String> meta;
        private List<ReportConfig> reports;
        private Boolean isActive;

        public EmailTemplateImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EmailTemplateImplBuilder withNotificationProvider(String type) {
            this.type = type;
            return this;
        }

        public EmailTemplateImplBuilder withReportCodes(List<String> reports) {
            this.reports = list(firstNotNull(reports, emptyList())).map(r -> ReportConfigImpl.builder().withCode(r).build());
            return this;
        }

        public EmailTemplateImplBuilder withReports(List<ReportConfig> reports) {
            if (reports.isEmpty()) {
                this.reports = emptyList();
            } else {
                this.reports = list(reports).map(
                        r -> ReportConfigImpl.copyOf(r).build()
                );
            }
            return this;
        }

        public EmailTemplateImplBuilder withDelay(Long delay) {
            this.delay = delay;
            return this;
        }

        public EmailTemplateImplBuilder withAccount(Long account) {
            this.account = account;
            return this;
        }

        public EmailTemplateImplBuilder withSignature(Long signature) {
            this.signature = signature;
            return this;
        }

        public EmailTemplateImplBuilder withCode(String name) {
            this.name = name;
            return this;
        }

        public EmailTemplateImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public EmailTemplateImplBuilder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public EmailTemplateImplBuilder withTextPlainContentType() {
            return this.withContentType("text/plain");
        }

        public EmailTemplateImplBuilder withTextHtmlContentType() {
            return this.withContentType("text/html");
        }

        public EmailTemplateImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EmailTemplateImplBuilder withFrom(String from) {
            this.from = from;
            return this;
        }

        public EmailTemplateImplBuilder withTo(String to) {
            this.to = to;
            return this;
        }

        public EmailTemplateImplBuilder withCc(String cc) {
            this.cc = cc;
            return this;
        }

        public EmailTemplateImplBuilder withBcc(String bcc) {
            this.bcc = bcc;
            return this;
        }

        public EmailTemplateImplBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public EmailTemplateImplBuilder withContent(String body) {
            this.body = body;
            return this;
        }

        public EmailTemplateImplBuilder withMeta(Map<String, String> data) {
            this.meta = data;
            return this;
        }

        /**
         *
         * @param language stored in {@link #meta} with key
         * {@link EmailTemplate#LANG_EXPR_ATTR}
         * @return
         */
        public EmailTemplateImplBuilder withLanguage(String language) {
            if (isNull(this.meta)) {
                this.meta = map();
            }

            this.meta.put(LANG_EXPR_ATTR, language);

            return this;
        }

        public EmailTemplateImplBuilder withKeepSynchronization(Boolean keepSynchronization) {
            this.keepSynchronization = keepSynchronization;
            return this;
        }

        public EmailTemplateImplBuilder withPromptSynchronization(Boolean promptSynchronization) {
            this.promptSynchronization = promptSynchronization;
            return this;
        }

        @Override
        public EmailTemplateImpl build() {
            return new EmailTemplateImpl(this);
        }

    }
}
