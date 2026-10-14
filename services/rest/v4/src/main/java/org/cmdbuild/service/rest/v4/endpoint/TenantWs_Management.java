/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.auth.multitenant.api.TenantInfo;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.service.rest.v4.command.TenantWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Path("tenants/")
@Tag(name = "Tenant", description = "Operations related to tenants")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ACCESS_AUTHORITY)
@Component
public class TenantWs_Management {

    private final TenantWsCommand command;

    public TenantWs_Management(TenantWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all tenants",
            description = "Get all tenants",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of tenant data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAll(
            @Nullable @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @Nullable @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        List<TenantInfo> list = command.doGetAll();

        long total;
        if (isPaged(offset, limit)) {
            PagedElements<TenantInfo> paged = PagedElements.paged(list, offset, limit);
            total = paged.totalSize();
            list = paged.elements();
        } else {
            total = list.size();
        }
        return response(list.stream().map((t) -> map("_id", t.getId(), "description", t.getDescription())).collect(toList()), total);
    }
}