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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.service.rest.v4.command.LookupTypeWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType.serializeLookupTypeProps;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 * @author ldare
 */
@Path("lookup_types/")
@Tag( name = "Lookup types", description = "Lookup types")
@Component
public class LookupTypeWs_Management {

    private final LookupService lookupService;
    private final LookupTypeWsCommand command;

    public LookupTypeWs_Management(LookupService lookupLogic, LookupTypeWsCommand command) {
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
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of the lookup type", schema = @Schema(type = "string")) String lookupTypeId
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
    public Object readAll(
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response") Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0")) Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter to apply to the resultset", schema = @Schema(type = "string")) String filter
    ) {
        List<LookupType> lookupTypes = command.doReadAll(filter);
        return response(paged(lookupTypes, offset, limit).map(lt -> serializeLookupTypeProps(lookupService, lt)));
    }
}
