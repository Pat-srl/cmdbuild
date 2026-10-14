/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.serializationhelpers;

import org.cmdbuild.config.EmailConfiguration;
import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;

public class EmailSignatureSerializationHelper {

    public static FluentMap<String, Object> serializeSignature(EmailSignature s, boolean detailed, ObjectTranslationService translationService, EmailConfiguration config) {
        return mapOf(String.class, Object.class).with(
                "_id", s.getId(),
                "code", s.getCode(),
                "description", s.getDescription(),
                "_description_translation", translationService.translateEmailSignatureDescription(s.getCode(), s.getDescription()),
                "active", s.isActive(),
                "_default", equal(s.getCode(), config.getDefaultEmailSignature())).accept(m -> {
            if (detailed) {
                m.put(
                        "content_html", s.getContentHtml(),
                        "_content_html_translation", translationService.translateEmailSignatureContenthtml(s.getCode(), s.getContentHtml()));
            }
        });
    }
}
