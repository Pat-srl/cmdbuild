/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.v4.command.DmsCategoryValueWsCommand;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.LookupValueSerializationHelper.serializeLookupValue;
import static org.cmdbuild.services.serialization.LookupValueSerializationHelper.serializePagedLookupValues;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("dms/categories/{" + LOOKUP_TYPE_ID + "}/values/")
@Tag(name = "DMS Categories Values")
@Produces(APPLICATION_JSON)
@Component
public class DmsCategoryValueWs_Management {

    private final LookupService lookupService;
    private final ObjectTranslationService objectTranslationService;
    private final DmsCategoryValueWsCommand command;

    public DmsCategoryValueWs_Management(ObjectTranslationService objectTranslationService, LookupService lookupService, DmsCategoryValueWsCommand command) {
        this.lookupService = checkNotNull(lookupService);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = Preconditions.checkNotNull(command);
    }

    @GET
    @Path("{" + LOOKUP_VALUE_ID + "}/")
    @Operation(
            summary = "Get a specific DMS Category Value",
            description = "Get a specific DMS Category Value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS Category Value"),
                    @ApiResponse(responseCode = "404", description = "The DMS Category Value was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {}) }
    )
    public Object read(
            @PathParam(LOOKUP_VALUE_ID) @Parameter(description = "Lookup value ID", schema = @Schema(type = "integer")) Long lookupValueId
    ) {
        LookupValue lookupValue = command.doRead(lookupValueId);
        return response(serializeLookupValue(lookupValue, objectTranslationService, lookupService));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all DMS Category Values based on lookup type ID",
            description = "Obtain a list of all DMS Category Values based on lookup type ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS Category Values"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {}) }
    )
    public Object readAll(
            @PathParam(LOOKUP_TYPE_ID) @Parameter(description = "Lookup type ID", schema = @Schema(type = "string")) String lookupTypeId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")  Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter to limit the attributes returned in the response") String filterStr
    ) {
        if (equal(lookupTypeId, "_ALL")) {

            return response(paged(lookupService.getAllTypes(filterStr).stream().filter(LookupType::isDmsCategorySpeciality).map(LookupType::getName).sorted()
                    .flatMap(t -> lookupService.getActiveLookup(t, null, null, parseFilter(filterStr)).stream())
                    .map(l -> serializeLookupValue(l, objectTranslationService, lookupService)).collect(toImmutableList()), offset, limit));
        } else {
            CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
            PagedElements<LookupValue> pagedLookupValues = lookupService.getActiveLookup(decodeIfHex(lookupTypeId), offset, limit, filter);
            return response(serializePagedLookupValues(pagedLookupValues, objectTranslationService, lookupService), pagedLookupValues.totalSize());
        }
    }
}
