/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cmdbuild.auth.grant.GrantMode;
import org.cmdbuild.auth.grant.PrivilegedObjectType;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.auth.grant.GrantConstants.*;
import static org.cmdbuild.auth.grant.GrantData.*;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import static org.cmdbuild.utils.json.CmJsonUtils.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.*;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * @author ldare
 */
public class WsGrantData {

    private final String role;
    private final GrantMode mode;
    private final PrivilegedObjectType type;
    private final Object classNameOrObjectId;
    private final Map<String, String> attributePrivileges;
    private final Map<String, String> dmsPrivileges;
    private final Map<String, String> gisPrivileges;
    private final Map<String, Object> customPrivileges;
    private final String filter;

    @JsonCreator
    public WsGrantData(ObjectNode json) {
        Map<String, Object> map = fromJson(json, MAP_OF_OBJECTS);
        this.role = toStringOrNull(map.get("role"));
        this.mode = parseEnum((String) map.get("mode"), GrantMode.class);
        this.type = parseEnum((String) map.get("objectType"), PrivilegedObjectType.class);
        this.classNameOrObjectId = checkNotNull(map.get("objectTypeName"));
        this.filter = trimToNull((String) map.get("filter"));
        this.attributePrivileges = Optional.ofNullable(json.get("attributePrivileges")).map(p -> p.isObject() ? fromJson(p, MAP_OF_STRINGS) : null).orElse(null);
        this.dmsPrivileges = Optional.ofNullable(json.get("dmsPrivileges")).map(p -> p.isObject() ? fromJson(p, MAP_OF_STRINGS) : null).orElse(null);
        this.gisPrivileges = Optional.ofNullable(json.get("gisPrivileges")).map(p -> p.isObject() ? fromJson(p, MAP_OF_STRINGS) : null).orElse(null);
        this.customPrivileges = switch (this.type) {
            case POT_CLASS, POT_PROCESS -> {
                yield (Map) map().skipNullValues().accept(m -> {
                    list(GDCP_CREATE, GDCP_UPDATE, GDCP_DELETE, GDCP_CLONE, GDCP_PRINT, GDCP_SEARCH, GDCP_FLOW_CLOSED_MODIFY_ATTACHMENT, GDCP_BULK_UPDATE, GDCP_BULK_DELETE, GDCP_BULK_ABORT).forEach(c -> {
                        m.put(c, toBooleanOrNull(map.get(format("_can_%s", c))));
                    });
                    list(GDCP_ATTACHMENT, GDCP_DETAIL, GDCP_EMAIL, GDCP_HISTORY, GDCP_NOTE, GDCP_RELATION, GDCP_SCHEDULE).forEach(c -> {
                        m.put(format("%s_read", c), toBooleanOrNull(map.get(format("_%s_access_read", c))));
                        m.put(format("%s_write", c), toBooleanOrNull(map.get(format("_%s_access_write", c))));
                    });
                    m.put(GDCP_RELGRAPH, toBooleanOrNull(map.get(format("_%s_access", GDCP_RELGRAPH))));
                    m.put(GDCP_ON_FILTER_MISMATCH, serializeEnum(parseEnumOrDefault((String) map.get("_on_filter_mismatch"), GM_NONE)));
                    map.forEach((k, v) -> {
                        Matcher matcher = Pattern.compile("_(widget|contextmenu)_(.+)_access").matcher(k);
                        if (matcher.matches()) {
                            m.put(format("%s_%s", checkNotBlank(matcher.group(1)), checkNotBlank(matcher.group(2))), toBooleanOrNull(v));
                        }
                    });
                });
            }
            case POT_VIEW ->
                    (Map) map().skipNullValues().accept(m -> list(GDCP_PRINT, GDCP_SEARCH).forEach(c -> m.put(c, toBooleanOrNull(map.get(format("_can_%s", c))))));
            default -> emptyMap();
        };
    }

    public String getRole() {
        return role;
    }

    public GrantMode getMode() {
        return mode;
    }

    public PrivilegedObjectType getType() {
        return type;
    }

    public Object getClassNameOrObjectId() {
        return classNameOrObjectId;
    }

    public Map<String, String> getAttributePrivileges() {
        return attributePrivileges;
    }

    public Map<String, String> getDmsPrivileges() {
        return dmsPrivileges;
    }

    public Map<String, String> getGisPrivileges() {
        return gisPrivileges;
    }

    public Map<String, Object> getCustomPrivileges() {
        return customPrivileges;
    }

    public String getFilter() {
        return filter;
    }
}
