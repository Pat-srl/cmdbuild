/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;

public interface EmailSysTemplateRepository {

    @Nullable
    EmailTemplate getSystemTemplateOrNull(String sysTemplateId);

    default EmailTemplate getSystemTemplate(String sysTemplateId) {
        return checkNotNull(getSystemTemplateOrNull(sysTemplateId), "system template not found for id =< %s >", sysTemplateId);
    }

    default boolean isSysTemplate(String sysTemplateId) {
        return getSystemTemplateOrNull(sysTemplateId) != null;
    }
}
