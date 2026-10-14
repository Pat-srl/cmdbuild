/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import java.util.List;
import java.util.Objects;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.auth.grant.*;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import static org.cmdbuild.auth.role.RoleType.ADMIN;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.v4.model.WsGrantData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import org.springframework.stereotype.Component;

/**
 * @author ldare
 */
@Component
public class GrantWsCommand {

    private final GrantDataRepository grantDataRepository;
    private final GrantService grantService;
    private final RoleRepository roleRepository;

    public GrantWsCommand(GrantDataRepository grantDataRepository, GrantService grantService, RoleRepository roleRepository) {
        this.grantDataRepository = checkNotNull(grantDataRepository);
        this.grantService = checkNotNull(grantService);
        this.roleRepository = checkNotNull(roleRepository);
    }

    public List<GrantData> doReadMany(String roleId, String filterStr, Boolean includeRecordsWithoutGrant) {
        Role role = roleRepository.getByNameOrId(roleId);
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        List<GrantData> grants;
        if (includeRecordsWithoutGrant) {
            grants = grantService.getGrantsForRoleIncludeRecordsWithoutGrant(role.getId());
        } else {
            grants = grantDataRepository.getGrantsForRole(role.getId());
        }
        if (!filter.isNoop()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            grants = AttributeFilterProcessor.<GrantData>builder()
                    .withKeyToValueFunction((key, grant) -> {
                        return switch (key) {
                            case "objectType" ->
                                serializeEnum(grant.getType());
                            default ->
                                throw new IllegalArgumentException("unsupported filter key = " + key);
                        };
                    }).withFilter(filter.getAttributeFilter())
                    .filter(grants);
        }
        return grants;
    }

    public List<GrantData> doReadOneByObject(String roleId, String objectTypeStr, String objectTypeName) {
        if (Objects.equals(roleId, "_ALL")) {
            return list(roleRepository.getAllGroups()).filter(not(equal(Role::getType, ADMIN))).map(g -> {
                Role role = roleRepository.getByNameOrId(g.getName());
                PrivilegedObjectType objectType = parseEnum(objectTypeStr, PrivilegedObjectType.class);
                return grantService.getGrantDataByRoleAndTypeAndName(role.getId(), objectType, objectTypeName);
            });
        } else {
            Role role = roleRepository.getByNameOrId(roleId);
            PrivilegedObjectType objectType = parseEnum(objectTypeStr, PrivilegedObjectType.class);
            return list(grantService.getGrantDataByRoleAndTypeAndName(role.getId(), objectType, objectTypeName));
        }
    }

    public List<GrantData> doUpdate(String roleId, List<WsGrantData> data) {
        if (Objects.equals(roleId, "_ALL")) {
            return updateMultiRoleGrants(data);
        } else {
            Role role = roleRepository.getByNameOrId(roleId);
            return updateRoleGrants(role, data);
        }
    }

    private List<GrantData> updateRoleGrants(Role role, List<WsGrantData> data) {
        return grantDataRepository.updateGrantsForRole(role.getId(), data.stream().map(d -> GrantDataImpl.builder()
                .withAttributePrivileges(d.getAttributePrivileges())
                .withDmsPrivileges(d.getDmsPrivileges())
                .withGisPrivileges(d.getGisPrivileges())
                .withCustomPrivileges(d.getCustomPrivileges())
                .withMode(d.getMode())
                .withObjectIdOrClassName(d.getClassNameOrObjectId())
                .withPrivilegeFilter(d.getFilter())
                .withType(d.getType())
                .withRoleId(role.getId())
                .build()).collect(toList()));
    }

    private List<GrantData> updateMultiRoleGrants(List<WsGrantData> data) {
        checkArgument(data.size() == data.stream().map(WsGrantData::getRole).distinct().toList().size(), "list of grants has duplicate of same group");
        List<GrantData> grants = list();
        list(data).forEach(d -> {
            grants.addAll(updateRoleGrants(roleRepository.getByNameOrId(checkNotBlank(d.getRole())), list(d)));
        });
        return grants;
    }
}
