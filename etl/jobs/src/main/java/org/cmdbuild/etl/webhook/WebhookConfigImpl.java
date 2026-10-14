/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.etl.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.lang.String.format;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.dao.utils.CmFilterUtils.noopFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.etl.config.WaterwayItem;
import org.cmdbuild.etl.webhook.WebhookConfigImpl.WebhookConfigImplBuilder;
import static org.cmdbuild.etl.webhook.WebhookMethod.WHM_GET;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 *
 * @author ataboga
 */
@JsonDeserialize(builder = WebhookConfigImplBuilder.class)
public class WebhookConfigImpl implements WebhookConfig {

    private final String code, description, target, url, headers, body, language;
    private final WebhookMethod method;
    private final Boolean active;
    private final CmdbFilter filter;
    private final Set<String> events;

    private WebhookConfigImpl(WebhookConfigImplBuilder builder) {
        this.code = checkNotNull(builder.code);
        this.description = builder.description;
        this.target = builder.target;
        this.method = firstNotNull(builder.method, WHM_GET);
        this.url = checkNotNull(builder.url);
        this.headers = builder.headers;
        this.body = builder.body;
        this.events = checkNotEmpty(builder.events);
        this.language = toStringOrNull(builder.language);
        this.active = firstNotNull(builder.active, true);
        this.filter = firstNotNull(builder.filter, buildTargetFilter(this.target), noopFilter());
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    @Nullable
    public String getDescription() {
        return description;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    @Nullable
    public WebhookMethod getMethod() {
        return method;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getHeaders() {
        return headers;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public Set<String> getEvents() {
        return events;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    @Nullable
    public Boolean isActive() {
        return active;
    }

    @Override
    @Nullable
    public CmdbFilter getFilter() {
        return filter;
    }

    public static WebhookConfigImplBuilder builder() {
        return new WebhookConfigImplBuilder();
    }

    public static WebhookConfigImplBuilder copyOf(WebhookConfig source) {
        return new WebhookConfigImplBuilder()
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withTarget(source.getTarget())
                .withMethod(source.getMethod())
                .withUrl(source.getUrl())
                .withHeaders(source.getHeaders())
                .withBody(source.getBody())
                .withEvents(source.getEvents())
                .withLanguage(source.getLanguage())
                .withFilter(source.getFilter())
                .withActive(source.isActive());
    }

    public static WebhookConfigImplBuilder copyOf(WaterwayItem source) {
        return new WebhookConfigImplBuilder()
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withTarget(source.getConfig("target"))
                .withMethod(parseEnumOrDefault(source.getConfig("method"), WHM_GET))
                .withUrl(checkNotBlank(source.getConfig("url")))
                .withHeaders(source.getConfig("headers"))
                .withBody(source.getConfig("body"))
                .withEvents(checkNotEmpty(list(toListOfStrings(source.getConfig("event"))).collect(toImmutableSet())))
                .withLanguage(source.getConfig("lang"))
                .withFilter(source.getConfig("filter"))
                .withActive(source.isEnabled());
    }

    public static class WebhookConfigImplBuilder {

        private String code, description, target, url, headers, body, language;
        private WebhookMethod method;
        private Boolean active;
        private CmdbFilter filter;
        private Set<String> events;

        public WebhookConfigImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public WebhookConfigImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public WebhookConfigImplBuilder withTarget(String target) {
            this.target = target;
            return this;
        }

        public WebhookConfigImplBuilder withMethod(WebhookMethod method) {
            this.method = method;
            return this;
        }

        public WebhookConfigImplBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public WebhookConfigImplBuilder withHeaders(String headers) {
            this.headers = headers;
            return this;
        }

        public WebhookConfigImplBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        @JsonProperty("lang")
        public WebhookConfigImplBuilder withLanguage(String language) {
            this.language = language;
            return this;
        }

        public WebhookConfigImplBuilder withEvents(String events) {
            this.events = list(toListOfStrings(events)).collect(toImmutableSet());
            return this;
        }

        public WebhookConfigImplBuilder withEvents(Set<String> events) {
            this.events = events;
            return this;
        }

        public WebhookConfigImplBuilder withFilter(String filter) {
            this.filter = isBlank(filter) ? null : parseFilter(filter);
            return this;
        }

        public WebhookConfigImplBuilder withFilter(CmdbFilter filter) {
            this.filter = filter;
            return this;
        }

        public WebhookConfigImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public WebhookConfigImpl build() {
            return new WebhookConfigImpl(this);
        }

    }

    private CmdbFilter buildTargetFilter(String target) {
        if (isNotBlank(target)) {
            return parseFilter(format("{\"attribute\":{\"simple\":{\"attribute\":\"IdClass\",\"operator\":\"equal\",\"value\":\"%s\"}}}", target));
        } else {
            return null;
        }
    }

}
