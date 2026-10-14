/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.auth.role.GroupConfigImpl;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.role.RoleType;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.*;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author ldare
 */
public class WsRoleData {

    private final Long id;
    private final String name, description, email, startingClass;
    private final boolean isActive;
    private final Boolean bulkUpdate, bulkDelete, bulkAbort, fullTextSearch;
    private final RoleType type;
    private final Boolean processWidgetAlwaysEnabled;
    private final List<RolePrivilege> rolePrivileges;

    public WsRoleData(
            @JsonProperty("_id") Long id,
            @JsonProperty("type") String type,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("email") String email,
            @JsonProperty("startingClass") String startingClass,
            @JsonProperty("active") Boolean isActive,
            @JsonProperty("bulkUpdate") Boolean bulkUpdate,
            @JsonProperty("bulkDelete") Boolean bulkDelete,
            @JsonProperty("bulkAbort") Boolean bulkAbort,
            @JsonProperty("fullTextSearch") Boolean fullTextSearch,
            @JsonProperty("processWidgetAlwaysEnabled") Boolean processWidgetAlwaysEnabled,
            @JsonProperty("rolePrivileges") List<String> rolePrivileges) {
        this.id = id;
        this.name = checkNotBlank(name);
        this.description = description;
        this.email = email;
        this.startingClass = trimToNull(startingClass);
        this.isActive = firstNotNull(isActive, true);
        this.type = parseEnumOrDefault(type, RoleType.DEFAULT);
        this.processWidgetAlwaysEnabled = processWidgetAlwaysEnabled;
        this.bulkUpdate = bulkUpdate;
        this.bulkDelete = bulkDelete;
        this.bulkAbort = bulkAbort;
        this.fullTextSearch = fullTextSearch;
        this.rolePrivileges = list(nullToEmpty(rolePrivileges)).map(r -> parseEnum(r, RolePrivilege.class));
    }

    public RoleImpl.RoleImplBuilder toRole() {
        return RoleImpl.builder()
                .withId(id)
                .withActive(isActive)
                .withType(type)
                .withDescription(description)
                .withEmail(email)
                .withName(name)
                .withCustomPrivileges(mapOf(String.class, Boolean.class).accept(m -> rolePrivileges.forEach(r -> m.put(serializeEnum(r), true))))
                .withConfig(GroupConfigImpl.builder()
                        .withStartingClass(startingClass)
                        .withProcessWidgetAlwaysEnabled(processWidgetAlwaysEnabled)
                        .withBulkUpdate(bulkUpdate)
                        .withBulkDelete(bulkDelete)
                        .withBulkAbort(bulkAbort)
                        .withFullTextSearch(fullTextSearch)
                        .build());
    }
}
