/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.utils;

import org.cmdbuild.auth.grant.PrivilegeSubject;
import static org.cmdbuild.auth.grant.PrivilegeSubject.PS_CLASS;
import static org.cmdbuild.auth.grant.PrivilegeSubject.PS_DOMAIN;
import static org.cmdbuild.auth.grant.PrivilegeSubject.PS_STOREDFUNCTION;
import static org.cmdbuild.auth.grant.PrivilegeSubject.PS_VIEW;
import org.cmdbuild.dao.entrytype.EntryTypeType;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_CLASS;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_DOMAIN;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_FUNCTION;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_OTHER;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_VIEW;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class PrivilegeSubjectUtils {

    public static EntryTypeType getEntryType(PrivilegeSubject subject) {
        return getEntryType(subject.getPrivilegeId());
    }

    public static EntryTypeType getEntryType(String privilegeSubjectId) {
        String key = checkNotBlank(privilegeSubjectId).replaceFirst(":.*$", "").toLowerCase();
        return switch (key) {
            case PS_CLASS ->
                ET_CLASS;
            case PS_DOMAIN ->
                ET_DOMAIN;
            case PS_VIEW ->
                ET_VIEW;
            case PS_STOREDFUNCTION ->
                ET_FUNCTION;
            default ->
                ET_OTHER;
        };
    }
}
