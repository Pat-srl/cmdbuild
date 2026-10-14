/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
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
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.cmdbuild.service.rest.v4.command.DmsCategoryValueWsCommand;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.LookupValueSerializationHelper.serializeLookupValue;
import static org.cmdbuild.services.serialization.LookupValueSerializationHelper.serializePagedLookupValues;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("administration/dms/categories/{" + LOOKUP_TYPE_ID + "}/values/")
@Tags({
        @Tag(name = "DMS Categories Values", description = "APIs to manage DMS Categories Values."),
        @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class DmsCategoryValueWs_Administration {

    private final DmsCategoryValueWsCommand command;
    private final LookupService lookupService;
    private final ObjectTranslationService objectTranslationService;

    public DmsCategoryValueWs_Administration(LookupService lookupService, ObjectTranslationService objectTranslationService, DmsCategoryValueWsCommand command) {
        this.lookupService = checkNotNull(lookupService);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{" + LOOKUP_VALUE_ID + "}/")
    @Operation(
            summary = "Get a DMS Category Value",
            description = "Get a DMS Category Value by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS Category Value"),
                    @ApiResponse(responseCode = "404", description = "The DMS Category Value was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_DMS_VIEW_AUTHORITY)
    public Object read(
            @PathParam(LOOKUP_VALUE_ID) @Parameter(description = "Id of the DMS Category Value", required = true) Long lookupValueId
    ) {
        LookupValue lookupValue = command.doRead(lookupValueId);
        return response(serializeLookupValue(lookupValue, objectTranslationService, lookupService));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all DMS Categories Values",
            description = "Get all DMS Categories Values",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS Categories Values"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_DMS_VIEW_AUTHORITY)
    public Object readAll(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of the DMS Category (lookup type), or _ALL to list all DMS Categories", required = true) String lookupTypeId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter to limit the attributes returned in the response") String filterStr
    ) {
        if (equal(lookupTypeId, "_ALL")) {
            List<LookupType> listLookupType = lookupService.getAllTypes(filterStr);
            return response(paged(listLookupType.stream().filter(LookupType::isDmsCategorySpeciality).map(LookupType::getName).sorted()
                    .flatMap(t -> lookupService.getAllLookup(t, null, null, parseFilter(filterStr)).stream())
                    .map(l -> serializeLookupValue(l, objectTranslationService, lookupService)).collect(toImmutableList()), offset, limit));
        } else {
            CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
            PagedElements<LookupValue> pagedLookupValues = lookupService.getAllLookup(decodeIfHex(lookupTypeId), offset, limit, filter);
            return response(serializePagedLookupValues(pagedLookupValues, objectTranslationService, lookupService), pagedLookupValues.totalSize());
        }
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create a DMS Category Value",
            description = "Create a DMS Category Value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of DMS Category Value")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object create(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of the DMS Category Value", required = true) String lookupTypeId,
            @RequestBody(description = "Lookup Value data", required = true) WsLookupValue wsLookupValue
    ) {
        LookupValue lookupValue = command.doCreate(lookupTypeId, wsLookupValue);
        return response(serializeLookupValue(lookupValue, objectTranslationService, lookupService));
    }

    @PUT
    @Path("{lookupValueId}")
    @Operation(
            summary = "Update a DMS Category Value",
            description = "Update a DMS Category Value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of DMS Category Value")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of the DMS Category Value", required = true) String lookupTypeId,
            @PathParam(LOOKUP_VALUE_ID) @Parameter(description = "Id of the DMS Category Value", required = true) Long lookupId,
            @RequestBody(description = "Lookup Value data", required = true) WsLookupValue wsLookupValue
    ) {
        LookupValue lookup = command.doUpdate(lookupTypeId, lookupId, wsLookupValue);
        return response(serializeLookupValue(lookup, objectTranslationService, lookupService));
    }

    @DELETE
    @Path("{lookupValueId}")
    @Operation(
            summary = "Delete a DMS Category Value",
            description = "Delete a DMS Category Value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of DMS Category Value"),
                    @ApiResponse(responseCode = "404", description = "The DMS Category Value was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of the DMS Category Value", required = true) String lookupTypeId,
            @PathParam(LOOKUP_VALUE_ID) @Parameter(description = "Id of the DMS Category Value", required = true) Long lookupId
    ) {
        command.doDelete(lookupTypeId, lookupId);
        return success();
    }

    @POST
    @Path("order")
    @Operation(
            summary = "Reorder DMS Category Values",
            description = "Reorder DMS Category Values",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful reordering of DMS Category Values"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object reorder(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of the DMS Category Value", required = true) String lookupTypeId,
            @RequestBody(description = "Ids of the DMS category values, in the desired order", required = true) List<Long> lookupValueIds
    ) {
        PagedElements<LookupValue> lookups = command.doReorder(lookupTypeId, lookupValueIds);
        return response(serializePagedLookupValues(lookups, objectTranslationService, lookupService), lookups.totalSize());
    }
}
