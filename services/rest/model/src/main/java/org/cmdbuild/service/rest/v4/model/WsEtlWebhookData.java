/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.cmdbuild.etl.webhook.WebhookConfigImpl;
import org.cmdbuild.etl.webhook.WebhookMethod;
import org.cmdbuild.utils.json.CmJsonUtils;

import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;

/**
 * @author ldare
 */
public class WsEtlWebhookData {

    private final String code, description, event, target, method, url, language;
    private final JsonNode headers, body;
    private final Boolean active;

    public WsEtlWebhookData(
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("event") String event,
            @JsonProperty("target") String target,
            @JsonProperty("method") String method,
            @JsonProperty("url") String url,
            @JsonProperty("headers") JsonNode headers,
            @JsonProperty("body") JsonNode body,
            @JsonProperty("language") String language,
            @JsonProperty("active") Boolean active) { // TODO handle filter?
        this.code = code;
        this.description = description;
        this.event = event;
        this.target = target;
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.language = language;
        this.active = active;
    }

    public WebhookConfigImpl.WebhookConfigImplBuilder toWebhookTrigger() {
        return WebhookConfigImpl.builder()
                .withCode(code)
                .withDescription(description)
                .withEvents(event)
                .withTarget(target)
                .withMethod(parseEnum(method, WebhookMethod.class))
                .withUrl(url)
                .withHeaders(applyOrNull(headers, CmJsonUtils::toJson))
                .withBody(applyOrNull(body, CmJsonUtils::toJson))
                .withLanguage(language)
                .withActive(active);
    }

    public String getTarget() {
        return this.target;
    }
}
