/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.beans;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.utils.json.JsonBean;

import jakarta.annotation.Nullable;
import org.cmdbuild.email.beans.EmailTemplateInlineDataImpl.EmailTemplateJsonModelBuilder;
import org.cmdbuild.report.ReportConfig;
import org.cmdbuild.report.ReportConfigImpl;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenListOfMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

@JsonBean
@JsonDeserialize(builder = EmailTemplateJsonModelBuilder.class)
public class EmailTemplateInlineDataImpl implements EmailTemplateInlineData {

    private final String template, content, id;
    private final List<ReportConfig> report;
    private final Long delay;

    private EmailTemplateInlineDataImpl(EmailTemplateJsonModelBuilder builder) {
        this.id = firstNotBlank(builder.id, randomId());
        this.template = checkNotBlank(builder.template);
        this.content = builder.content;
        this.delay = builder.delay;
        this.report = ImmutableList.copyOf(firstNotNull(builder.report, emptyList()));
    }

    @Override
    @JsonIgnore
    public List<ReportConfig> getReportList() {
        return report;
    }

    @JsonAnyGetter
    public Map<String, String> getReportConfigs() {
        return flattenMaps(map("report", list(report).map(ReportConfigImpl::toConfig)));
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Nullable
    @Override
    public String getContent() {
        return content;
    }

    @Nullable
    @Override
    public Long getDelay() {
        return delay;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "EmailTemplateInlineDataImpl{" + "template=" + template + ", id=" + id + '}';
    }

    public static EmailTemplateJsonModelBuilder builder() {
        return new EmailTemplateJsonModelBuilder();
    }

    public static EmailTemplateInlineDataImpl build(String template) {
        return builder().withTemplate(template).build();
    }

    public static EmailTemplateJsonModelBuilder copyOf(EmailTemplateInlineData source) {
        return new EmailTemplateJsonModelBuilder()
                .withId(source.getId())
                .withTemplate(source.getTemplate())
                .withContent(source.getContent())
                .withDelay(source.getDelay())
                .withReportList(source.getReportList());
    }

    public static class EmailTemplateJsonModelBuilder implements Builder<EmailTemplateInlineDataImpl, EmailTemplateJsonModelBuilder> {

        private String template, id;
        private List<ReportConfig> report;
        private String content;
        private Long delay;
        @JsonAnySetter
        private final Map<String, Object> values = map();

        public EmailTemplateJsonModelBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public EmailTemplateJsonModelBuilder withTemplate(String template) {
            this.template = template;
            return this;
        }

        public EmailTemplateJsonModelBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public EmailTemplateJsonModelBuilder withReportList(List<ReportConfig> report) {
            this.report = report;
            return this;
        }

        public EmailTemplateJsonModelBuilder withDelay(Long delay) {
            this.delay = delay;
            return this;
        }

        @Override
        public EmailTemplateInlineDataImpl build() {
            if (report == null) {
                report = list(unflattenListOfMaps(values, "report")).map(m -> ReportConfigImpl.fromConfig((Map) m));
            }
            return new EmailTemplateInlineDataImpl(this);
        }

    }
}
