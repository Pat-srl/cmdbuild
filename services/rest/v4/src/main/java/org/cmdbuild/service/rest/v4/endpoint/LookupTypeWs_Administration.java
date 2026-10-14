/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType;
import org.cmdbuild.service.rest.v4.command.LookupTypeWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOOKUPS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOOKUPS_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType.serializeLookupTypeProps;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 * @author ldare
 */
@Path("administration/lookup_types/")
@Tag( name = "Lookup types", description = "Lookup types")
@Component
public class LookupTypeWs_Administration {

    private final LookupService lookupService;
    private final LookupTypeWsCommand command;

    public LookupTypeWs_Administration(LookupService lookupLogic, LookupTypeWsCommand command) {
        this.lookupService = checkNotNull(lookupLogic);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @Operation(
            summary = "Get lookup type by id",
            description = "Get lookup type by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup type data"),
                    @ApiResponse(responseCode = "404", description = "Lookup type not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested lookup type"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_VIEW_AUTHORITY)
    public Object read(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of lookup type to query") String lookupTypeId
    ) {
        LookupType lookupType = command.doRead(lookupTypeId);
        return response(serializeLookupTypeProps(lookupService, lookupType));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all lookup types",
            description = "Get all lookup types",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup types data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_VIEW_AUTHORITY)
    public Object readAll(
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter to apply to the query") String filter
    ) {
        List<LookupType> lookupTypes = command.doReadAll(filter);
        return response(paged(lookupTypes, offset, limit).map(lt -> serializeLookupTypeProps(lookupService, lt)));
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create lookup type",
            description = "Create lookup type",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of lookup type data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object createLookupType(
            @RequestBody(description = "Lookup Type data", required = true) WsLookupType wsLookupType
    ) {
        LookupType lookupType = command.doCreateLookupType(wsLookupType);
        return response(serializeLookupTypeProps(lookupService, lookupType));
    }

    @DELETE
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @Operation(
            summary = "Delete lookup type by id",
            description = "Delete lookup type by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of lookup type"),
                    @ApiResponse(responseCode = "404", description = "Lookup type not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to delete the requested lookup type"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object deleteLookupType(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of lookup type to delete") String lookupTypeId
    ) {
        command.doDeleteLookupType(lookupTypeId);
        return success();
    }
}
