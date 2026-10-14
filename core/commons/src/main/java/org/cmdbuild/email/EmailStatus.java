/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

public enum EmailStatus {
    ES_RECEIVED, ES_SENT, ES_OUTGOING, ES_ERROR, ES_DRAFT, ES_SKIPPED, ES_ACQUIRED;

    public static String serializeEmailStatus(EmailStatus status) {
        return serializeEnum(status);
    }

    @Nullable
    public static EmailStatus parseEmailStatus(@Nullable String status) {
        return parseEnumOrNull(status, EmailStatus.class);
    }
}
