/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.cmdbuild.email.beans.EmailTemplateInlineDataImpl;
import org.cmdbuild.report.ReportConfigImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenListOfMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class WsNotificationData {

    private final String id;
    private final String template;
    private final String content;
    private final Long delay;
    @JsonAnySetter
    private final Map<String, Object> values = map();

    public WsNotificationData(
            @JsonProperty("_id") String id,
            @JsonProperty("template") String template,
            @JsonProperty("content") String content,
            @JsonProperty("delay") Long delay) {
        this.id = id;
        this.template = template;
        this.content = content;
        this.delay = delay;
    }

    public EmailTemplateInlineDataImpl.EmailTemplateJsonModelBuilder toTemplate() {
        return EmailTemplateInlineDataImpl.builder().withId(id).withContent(content).withDelay(delay).withReportList(list(unflattenListOfMaps(values, "reports")).map(ReportConfigImpl::fromConfig)).withTemplate(template);
    }

}
