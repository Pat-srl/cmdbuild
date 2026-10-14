/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.utils;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_ATTACHMENT;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_DETAIL;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_EMAIL;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_HISTORY;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_NOTE;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_RELATION;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_RELGRAPH;
import static org.cmdbuild.auth.grant.GrantConstants.GDCP_SCHEDULE;
import org.cmdbuild.auth.grant.PrivilegeSubject;
import org.cmdbuild.auth.role.RolePrivilege;
import static org.cmdbuild.auth.utils.PrivilegeSubjectUtils.getEntryType;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_CLASS;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserPrivilegesUtils {

    public static Map<String, Object> getDefaultCustomPrivileges(PrivilegeSubject subject, Set<RolePrivilege> rolePrivileges) {
        return switch (getEntryType(subject)) {
            case ET_CLASS -> {
                boolean isProcess = ((Classe) subject).isProcess();
                yield mapOf(String.class, Object.class).accept(m -> {
                    list(GDCP_ATTACHMENT, GDCP_DETAIL, GDCP_EMAIL, GDCP_HISTORY, GDCP_NOTE, GDCP_RELATION).accept(l -> {
                        if (!isProcess) {
                            l.add(GDCP_SCHEDULE);
                        }
                    }).forEach(p -> {
                        m.put(format("%s_read", p), rolePrivileges.contains(parseEnum(format("%s_tab_%s_access_read", isProcess ? "flow" : "card", p), RolePrivilege.class)));
                        m.put(format("%s_write", p), rolePrivileges.contains(parseEnum(format("%s_tab_%s_access_write", isProcess ? "flow" : "card", p), RolePrivilege.class)));
                    });
                    list(GDCP_RELGRAPH).forEach(p -> {
                        m.put(p, rolePrivileges.contains(parseEnum(format("%s_access", p), RolePrivilege.class)));
                    });
                });
            }
            default ->
                emptyMap();
        };
    }

    public static Collection<? extends GrantedAuthority> getAutoritiesFromPrivileges(Set<RolePrivilege> rolePrivileges) {
        return rolePrivileges.stream()
                .map(rp -> rp.name().replaceFirst("^RP_(.+)$", "ROLE_$1"))
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(toImmutableList());
    }
}
