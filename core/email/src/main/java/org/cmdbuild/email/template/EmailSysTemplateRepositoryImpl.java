/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import static com.google.common.collect.Maps.uniqueIndex;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class EmailSysTemplateRepositoryImpl implements EmailSysTemplateRepository {

    private final Map<String, EmailTemplate> sysTemplates;

    public EmailSysTemplateRepositoryImpl() {
        sysTemplates = uniqueIndex(list(
                EmailTemplateImpl.builder().withContentType("application/octet-stream").withCode("cm_send_to_current_user").withTo("[#ftl]${cmdb.currentUser.email}").build()
        ), EmailTemplate::getCode);
    }

    @Override
    @Nullable
    public EmailTemplate getSystemTemplateOrNull(String sysTemplateId) {
        return sysTemplates.get(sysTemplateId);
    }

}
