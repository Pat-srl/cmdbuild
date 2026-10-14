/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;


import org.cmdbuild.auth.grant.GrantData;
import org.cmdbuild.auth.grant.GrantService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Objects.equal;
import static java.lang.String.format;
import static org.cmdbuild.auth.grant.GrantConstants.*;
import static org.cmdbuild.auth.grant.GrantData.*;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_PROCESS;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.*;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * @author ldare
 */
public class GrantSerializer {

    public static Object serializeGrant(GrantData grant, GrantService grantService) {
        return serializeGrant(grant, false, grantService);
    }

    public static Object serializeGrant(GrantData grant, boolean includeObjectDescription, GrantService grantService) {
        return map(
                "_id", grant.getId(),
                "role", grant.getRoleId(),
                "mode", serializeEnum(grant.getMode()),
                "objectType", serializeEnum(grant.getType()),
                "objectTypeName", grant.getObjectIdOrClassNameOrCode(),
                "filter", grant.getPrivilegeFilter(),
                "attributePrivileges", grant.getAttributePrivileges(),
                "dmsPrivileges", grant.getDmsPrivileges(),
                "gisPrivileges", grant.getGisPrivileges()
        ).accept((m) -> {
            switch (grant.getType()) {
                case POT_CLASS, POT_PROCESS -> {
                    m.put("_is_process", equal(grant.getType(), POT_PROCESS));
                    list(GDCP_CREATE, GDCP_UPDATE, GDCP_DELETE, GDCP_CLONE, GDCP_PRINT).forEach((p) -> {
                        m.put(format("_can_%s", p), toBooleanOrDefault(grant.getCustomPrivileges().get(p), true));
                    });
                    list(GDCP_FLOW_CLOSED_MODIFY_ATTACHMENT).forEach((p) -> {
                        m.skipNullValues().put(format("_can_%s", p), toBooleanOrNull(grant.getCustomPrivileges().get(p)));
                    });
                    m.put("_on_filter_mismatch", serializeEnum(parseEnumOrDefault(toStringOrNull(grant.getCustomPrivileges().get(GDCP_ON_FILTER_MISMATCH)), GM_NONE)));
                    list(GDCP_ATTACHMENT, GDCP_DETAIL, GDCP_EMAIL, GDCP_HISTORY, GDCP_NOTE, GDCP_RELATION, GDCP_SCHEDULE).forEach((p) -> {
                        m.put(format("_%s_access_read", p), toBooleanOrNull(grant.getCustomPrivileges().get(format("%s_read", p))));
                        m.put(format("_%s_access_write", p), toBooleanOrNull(grant.getCustomPrivileges().get(format("%s_write", p))));
                    });
                    list(GDCP_RELGRAPH).forEach(p -> {
                        m.put(format("_%s_access", p), toBooleanOrDefault(grant.getCustomPrivileges().get(p), true));//TODO: improve this and decide if it's an action or what (_can_# or _#_access)
                    });
                    grant.getCustomPrivileges().forEach((k, v) -> {
                        Matcher matcher = Pattern.compile("(widget|contextmenu)_(.+)").matcher(k);
                        if (matcher.matches()) {
                            m.put(format("_%s_%s_access", checkNotBlank(matcher.group(1)), checkNotBlank(matcher.group(2))), v);
                        }
                    });
                }
                case POT_VIEW -> list(GDCP_PRINT, GDCP_SEARCH).forEach((p) -> {
                    m.put(format("_can_%s", p), toBooleanOrDefault(grant.getCustomPrivileges().get(p), true));
                });
            }
            switch (grant.getType()) {
                case POT_CLASS -> list(GDCP_BULK_UPDATE, GDCP_BULK_DELETE, GDCP_SEARCH).forEach((p) -> {
                    m.put(format("_can_%s", p), toBooleanOrNull(grant.getCustomPrivileges().get(p)));
                });
                case POT_PROCESS -> list(GDCP_BULK_ABORT, GDCP_SEARCH).forEach((p) -> {
                    m.put(format("_can_%s", p), toBooleanOrNull(grant.getCustomPrivileges().get(p)));
                });
            }
            if (includeObjectDescription) {
                m.put("_object_description", grantService.getGrantObjectDescription(grant));
            }
        });
    }
}
