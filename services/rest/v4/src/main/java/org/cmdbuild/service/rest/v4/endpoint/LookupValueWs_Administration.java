/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.cmdbuild.service.rest.v4.command.LookupValueWsCommand;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOOKUPS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOOKUPS_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.LookupValueSerializationHelper.serializeLookupValue;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("administration/lookup_types/{" + LOOKUP_TYPE_ID + "}/values/")
@Tags({
        @Tag( name = "Lookup Values", description = "APIs to manage Lookup Values." ),
        @Tag( name = "Administration" )
})
@Produces(APPLICATION_JSON)
@Component
public class LookupValueWs_Administration {

    private final LookupValueWsCommand command;
    private final LookupService lookupService;
    private final ObjectTranslationService objectTranslationService;

    public LookupValueWs_Administration(LookupService lookupService, ObjectTranslationService objectTranslationService, LookupValueWsCommand command) {
        this.lookupService = checkNotNull(lookupService);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{" + LOOKUP_VALUE_ID + "}/")
    @Operation(
            summary = "Get a lookup value",
            description = "Get a lookup value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup value"),
                    @ApiResponse(responseCode = "404", description = "The lookup value was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_VIEW_AUTHORITY)
    public Object read(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of lookup type", schema = @Schema(type = "string")) String lookupTypeId,
            @PathParam(LOOKUP_VALUE_ID) @Parameter(description = "Id of lookup value", schema = @Schema(type = "long")) Long lookupValueId
    ) {
        LookupValue lookup = command.doRead(lookupValueId);
        return response(serializeLookupValue(lookup, objectTranslationService, lookupService));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all lookup values",
            description = "Get all lookup values",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup values"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_VIEW_AUTHORITY)
    public Object readAll(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of lookup type", schema = @Schema(type = "string")) String lookupTypeId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")  Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter query to apply to the resultset", schema = @Schema(type = "string", example = "code:test")) String filterStr,
            @QueryParam("forClass") @Parameter(description = "Filter by class", schema = @Schema(type = "string", example = "org.cmdbuild.model.Process")) String forClass,
            @QueryParam("forAttr") @Parameter(description = "Filter by attribute", schema = @Schema(type = "string", example = "code")) String forAttr
    ) {

        PagedElements<LookupValue> lookups = command.doReadAll(lookupTypeId, limit, offset, filterStr, forClass, forAttr, true);
        return response(lookups.stream().map(l -> serializeLookupValue(l, objectTranslationService, lookupService)).collect(toList()), lookups.totalSize());
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create a lookup value",
            description = "Create a lookup value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of lookup value"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object create(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of lookup type", schema = @Schema(type = "string")) String lookupTypeId,
            @RequestBody(description = "Lookup Value data", required = true) WsLookupValue wsLookupValue
    ) {
        LookupValue lookup = command.doCreate(lookupTypeId, wsLookupValue);
        return response(serializeLookupValue(lookup, objectTranslationService, lookupService));
    }

    @PUT
    @Path("{lookupValueId}")
    @Operation(
            summary = "Update a lookup value",
            description = "Update a lookup value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of lookup value"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of lookup type", schema = @Schema(type = "string")) String lookupTypeId,
            @PathParam("lookupValueId") @Parameter(description = "Id of lookup value", schema = @Schema(type = "long")) Long lookupId,
            @RequestBody(description = "Lookup Value data", required = true) WsLookupValue wsLookupValue
    ) {
        LookupValue lookup = command.doUpdate(lookupTypeId, lookupId, wsLookupValue);
        return response(serializeLookupValue(lookup, objectTranslationService, lookupService));
    }

    @DELETE
    @Path("{lookupValueId}")
    @Operation(
            summary = "Delete a lookup value",
            description = "Delete a lookup value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of lookup value"),
                    @ApiResponse(responseCode = "404", description = "The lookup value was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of lookup type", schema = @Schema(type = "string")) String lookupTypeId,
            @PathParam("lookupValueId") @Parameter(description = "Id of lookup value", schema = @Schema(type = "long")) Long lookupId
    ) {
        command.doDelete(lookupTypeId, lookupId);
        return success();
    }

    @POST
    @Path("order")
    @Operation(
            summary = "Reorder lookup values",
            description = "Reorder lookup values",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful reordering of lookup values"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object reorder(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of lookup type", schema = @Schema(type = "string")) String lookupTypeId,
            @RequestBody(description = "Ids of the lookup values, in the desired order", required = true) List<Long> lookupValueIds
    ) {
        PagedElements<LookupValue> lookups = command.doReorder(lookupTypeId, lookupValueIds);
        return response(lookups.stream().map(l -> serializeLookupValue(l, objectTranslationService, lookupService)).collect(toList()), lookups.totalSize());
    }
}
