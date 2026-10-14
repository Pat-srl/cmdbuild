/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.services.serialization;

import com.google.common.base.Joiner;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.report.ReportConfigImpl;
import org.cmdbuild.template.TemplateBindings;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@Component
public class EmailTemplateSerializationHelper {

    private final EmailTemplateService templateService;
    private final ObjectTranslationService translationService;

    public EmailTemplateSerializationHelper(EmailTemplateService templateService, ObjectTranslationService translationService) {
        this.templateService = checkNotNull(templateService);
        this.translationService = checkNotNull(translationService);
    }

    public void serializeEmailTemplateBindings(EmailTemplate template, boolean includeBindings, BiConsumer<String, Object> adder) {
        if (includeBindings) {
            TemplateBindings bindings = templateService.fetchTemplateBindings(template);
            adder.accept("_bindings", map(
                    "client", bindings.getClientBindings(),
                    "server", bindings.getServerBindings()
            ));
        }
    }

    public void serializeEmailTemplateTranslation(EmailTemplate template, BiConsumer<String, Object> adder) {
        adder.accept("_description_translation", translationService.translateEmailTemplateDescription(template.getCode(), template.getDescription()));
    }

    public CmMapUtils.FluentMap<String, Object> serializeTemplate(EmailTemplate t, boolean detailed, boolean includeBindings) {
        return (detailed ? serializeDetailedTemplate(t) : serializeBasicTemplate(t)).accept(m -> {
            serializeEmailTemplateBindings(t, includeBindings, m::put);
            serializeEmailTemplateTranslation(t, m::put);
        });
    }

    public CmMapUtils.FluentMap<String, Object> serializeBasicTemplate(EmailTemplate t) {
        return map(
                "_id", firstNotBlank(t.getId(), t.getCode()),
                "name", t.getCode(),
                "description", t.getDescription(),
                "provider", t.getNotificationProvider(),
                "_can_write", isNotNullAndGtZero(t.getId())//TODO improve this, templates from file
        );
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedTemplate(EmailTemplate t) {
        return serializeBasicTemplate(t).with(
                "from", t.getFrom(),
                "to", t.getTo(),
                "cc", t.getCc(),
                "bcc", t.getBcc(),
                "subject", t.getSubject(),
                "body", t.getContent(),
                "contentType", t.getContentType(),
                "account", t.getAccount(),
                "signature", t.getSignature(),
                "keepSynchronization", t.getKeepSynchronization(),
                "promptSynchronization", t.getPromptSynchronization(),
                "delay", t.getDelay(),
                "data", t.getMeta(),
                "active", t.isActive(),
                "showOnClasses", Joiner.on(",").join(t.getShowOnClasses()),
                "reports", list(t.getReports()).map(ReportConfigImpl::toConfig)
        );
    }
}
