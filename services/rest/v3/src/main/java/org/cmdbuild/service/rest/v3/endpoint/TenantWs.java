package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.TenantWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.TenantWs_Management;
import org.cmdbuild.services.rest.v4.config.WsTenantConfig;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SYSCONFIG_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;

@Path("tenants/")
@Tag(name = "Tenant", description = "Operations related to tenants")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ACCESS_AUTHORITY)
public class TenantWs {

    private final TenantWs_Administration tenantWs_adm;
    private final TenantWs_Management tenantWs_mng;

    public TenantWs(TenantWs_Administration tenantWs_adm, TenantWs_Management tenantWs_mng) {
        this.tenantWs_adm = checkNotNull(tenantWs_adm);
        this.tenantWs_mng = checkNotNull(tenantWs_mng);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all tenants",
            description = "Get all tenants",
            parameters = {
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of tenants to return", required = false, schema = @Schema(type = "integer", format = "int32")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination", required = false, schema = @Schema(type = "integer", format = "int32"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of tenant data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAll(
            @Nullable @QueryParam(LIMIT) Integer limit,
            @Nullable @QueryParam(START) Integer offset
    ) {
        return tenantWs_mng.getAll(limit, offset);
    }

    @POST
    @Path("configure")
    @Operation(
            summary = "Configure multitenant mode",
            description = "Configure multitenant mode",
            requestBody = @RequestBody(description = "Multitenant configuration data", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Multitenant mode configured successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object configureMultitenant(
            WsTenantConfig configData
    ) {
        return tenantWs_adm.configureMultitenant(configData);
    }
}
