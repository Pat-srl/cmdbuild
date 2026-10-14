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
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.service.rest.common.serializationhelpers.LookupSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType;
import org.cmdbuild.service.rest.v4.command.DmsCategoryWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 * @author ldare
 */
@Path("administration/dms/categories/")
@Tag( name = "DMS Categories", description = "DMS Categories")
@Produces(APPLICATION_JSON)
@Component
public class DmsCategoryWs_Administration {

    private final LookupSerializationHelper lookupSerializationHelper;
    private final DmsCategoryWsCommand command;

    public DmsCategoryWs_Administration(LookupSerializationHelper lookupSerializationHelper, DmsCategoryWsCommand command) {
        this.lookupSerializationHelper = checkNotNull(lookupSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @Operation(
            summary = "Get a DMS category",
            description = "Get a DMS category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS category data"),
                    @ApiResponse(responseCode = "404", description = "DMS category not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_DMS_VIEW_AUTHORITY)
    public Object read(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Lookup type ID") String lookupTypeId
    ) {
        LookupType lookupType = command.doRead(lookupTypeId);
        return response(lookupSerializationHelper.serializeLookupType(lookupType));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all DMS categories",
            description = "Get all DMS categories",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS categories data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_DMS_VIEW_AUTHORITY)
    public Object readAll(
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample"))  Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter to apply to the query") String filter
    ) {
        List<LookupType> lookupTypes = command.doReadAll(filter);
        return response(paged(lookupTypes, offset, limit).map(lookupSerializationHelper::serializeLookupType));
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create a new DMS category",
            description = "Create a new DMS category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of DMS category data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object createLookupType(@RequestBody(description = "Lookup Type data", required = true) WsLookupType wsLookupType) {
        LookupType lookupType = command.doCreateLookupType(wsLookupType);
        return response(lookupSerializationHelper.serializeLookupType(lookupType));
    }

    @DELETE
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @Operation(
            summary = "Delete a DMS category",
            description = "Delete a DMS category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of DMS category"),
                    @ApiResponse(responseCode = "404", description = "DMS category not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object deleteLookupType(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Lookup type ID") String lookupTypeId
    ) {
        command.doDeleteLookupType(lookupTypeId);
        return success();
    }
}
