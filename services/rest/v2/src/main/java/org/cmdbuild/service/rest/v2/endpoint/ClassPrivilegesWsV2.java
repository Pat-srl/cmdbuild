package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.auth.grant.GrantData;
import org.cmdbuild.auth.grant.GrantDataRepository;
import org.cmdbuild.auth.grant.GrantService;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import org.cmdbuild.auth.role.Role;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_WRITE;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("roles/{roleId}/classes_privileges/")
@Produces(APPLICATION_JSON)
public class ClassPrivilegesWsV2 {

    private final GrantDataRepository repository;
    private final RoleRepository roleRepository;
    private final GrantService grantService;
    private final OperationUserSupplier user;

    private final static Map<String, String> mappingGrantMode = ImmutableMap.of("read", "r", "write", "w");

    public ClassPrivilegesWsV2(GrantDataRepository repository, RoleRepository roleRepository, GrantService grantService, OperationUserSupplier user) {
        this.repository = checkNotNull(repository);
        this.roleRepository = checkNotNull(roleRepository);
        this.grantService = checkNotNull(grantService);
        this.user = checkNotNull(user);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("roleId") String roleId) {
        Role role = roleRepository.getByNameOrId(roleId);
        List<GrantData> grants = repository.getGrantsForTypeAndRole(POT_CLASS, role.getId());
        return map("success", true, "data", grants.stream().map(this::serializeGrant).collect(toList()), "meta", map("total", grants.size()));
    }

    @GET
    @Path("{classId}/")
    public Object readOne(@PathParam("roleId") String roleId, @PathParam("classId") String classId) {
        Role role = roleRepository.getByNameOrId(roleId);
        GrantData grant = grantService.getGrantDataByRoleAndTypeAndName(role.getId(), POT_CLASS, classId);
        return map("success", true, "data", serializeGrant(grant), "meta", map());
    }

    private Object serializeGrant(GrantData grant) {
        return map(
                "mode", user.hasPrivileges(p -> p.hasPrivileges(RP_DATA_ALL_WRITE)) ? "w" : mappingGrantMode.get(serializeEnum(grant.getMode())),
                "name", grant.getObjectIdOrClassNameOrCode(),
                "description", grantService.getGrantObjectDescription(grant),
                "_id", grant.getObjectIdOrClassNameOrCode()
        );
    }

}
