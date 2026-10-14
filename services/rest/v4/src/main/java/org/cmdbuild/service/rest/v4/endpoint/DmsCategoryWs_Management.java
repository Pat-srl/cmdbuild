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
import jakarta.ws.rs.*;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.service.rest.common.serializationhelpers.LookupSerializationHelper;
import org.cmdbuild.service.rest.v4.command.DmsCategoryWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 * @author ldare
 */
@Path("dms/categories/")
@Tag( name = "DMS Categories", description = "DMS Categories")
@Produces(APPLICATION_JSON)
@Component
public class DmsCategoryWs_Management {

    private final LookupSerializationHelper lookupSerializationHelper;
    private final DmsCategoryWsCommand command;

    public DmsCategoryWs_Management(LookupSerializationHelper lookupSerializationHelper, DmsCategoryWsCommand command) {
        this.lookupSerializationHelper = checkNotNull(lookupSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @Operation(
            summary = "Get a DMS category",
            description = "Get a DMS category",
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of DMS category data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Id of the DMS category", schema = @Schema(type = "string")) String lookupTypeId
    ) {
        LookupType lookupType = command.doRead(lookupTypeId);
        return response(lookupSerializationHelper.serializeLookupType(lookupType));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all DMS categories",
            description = "Get all DMS categories",
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of DMS categories data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample"))  Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter for the DMS categories", schema = @Schema(ref = "DefaultFilterExample")) String filter
    ) {
        List<LookupType> lookupTypes = command.doReadAll(filter);
        return response(paged(lookupTypes, offset, limit).map(lookupSerializationHelper::serializeLookupType));
    }
}
