/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.template.TemplateBindings;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface EmailTemplateService extends EmailSysTemplateRepository {

    List<EmailTemplate> getAll();

    @Nullable
    EmailTemplate getByIdOrNull(long id);

    @Nullable
    EmailTemplate getByNameOrNull(String name);

    EmailTemplate createEmailTemplate(EmailTemplate emailTemplate);

    EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate);

    void deleteEmailTemplate(long id);

    TemplateBindings fetchTemplateBindings(EmailTemplate emailTemplate);

    EmailTemplate getTemplate(EmailTemplateInlineConfig notification);

    default EmailTemplate getById(long id) {
        return checkNotNull(getByIdOrNull(id), "notification template not found for id =< %s >", id);
    }

    default EmailTemplate getByName(String code) {
        return checkNotNull(getByNameOrNull(code), "notification template not found for code =< %s >", code);
    }

    default EmailTemplate getByNameOrId(String idOrCode) {
        return checkNotNull(getByNameOrIdOrNull(idOrCode), "notification template not found for id or code =< %s >", idOrCode);
    }

    @Nullable
    default EmailTemplate getByNameOrIdOrNull(String idOrCode) {
        if (NumberUtils.isCreatable(idOrCode)) {
            return getByIdOrNull(toLong(idOrCode));
        } else {
            return getByNameOrNull(idOrCode);
        }
    }

    default List<EmailTemplate> getAllActive() {
        return list(getAll()).filter(EmailTemplate::isActive);
    }
}
