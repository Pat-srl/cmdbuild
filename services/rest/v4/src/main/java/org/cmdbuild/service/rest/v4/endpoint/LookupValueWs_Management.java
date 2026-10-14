/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.v4.command.LookupValueWsCommand;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.LookupValueSerializationHelper.serializeLookupValue;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("lookup_types/{" + LOOKUP_TYPE_ID + "}/values/")
@Tag(name = "Lookup Values")
@Produces(APPLICATION_JSON)
@Component
public class LookupValueWs_Management {

    private final LookupValueWsCommand command;
    private final LookupService lookupService;
    private final ObjectTranslationService objectTranslationService;

    public LookupValueWs_Management(LookupService lookupService, ObjectTranslationService objectTranslationService, LookupValueWsCommand command) {
        this.lookupService = checkNotNull(lookupService);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{" + LOOKUP_VALUE_ID + "}/")
    @Operation(
            summary = "Get a specific lookup value",
            description = "Obtain a specific lookup value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup value"),
                    @ApiResponse(responseCode = "404", description = "The lookup value was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of the lookup type to retrieve", schema = @Schema(type = "string")) String lookupTypeId,
            @PathParam(LOOKUP_VALUE_ID) @Parameter(description = "Id of the lookup value to retrieve", schema = @Schema(type = "long")) Long lookupValueId
    ) {
        LookupValue lookup = command.doRead(lookupValueId);
        return response(serializeLookupValue(lookup, objectTranslationService, lookupService));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all lookup values",
            description = "Obtain a list of all lookup values",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup values"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of the lookup type to retrieve", schema = @Schema(type = "string")) String lookupTypeId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")  Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter query to apply to the resultset", schema = @Schema(type = "string", example = "code:test")) String filterStr,
            @QueryParam("forClass") @Parameter(description = "Class to filter the lookup values by", schema = @Schema(type = "string")) String forClass,
            @QueryParam("forAttr") @Parameter(description = "Attribute to filter the lookup values by", schema = @Schema(type = "string")) String forAttr
    ) {

        PagedElements<LookupValue> lookups = command.doReadAll(lookupTypeId, limit, offset, filterStr, forClass, forAttr, false);
        return response(lookups.stream().map(l -> serializeLookupValue(l, objectTranslationService, lookupService)).collect(toList()), lookups.totalSize());
    }
}
