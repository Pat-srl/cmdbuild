/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;
import org.cmdbuild.offline.loader.OfflineLoaderService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

import org.cmdbuild.service.rest.v4.command.OfflineDataWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.cmdbuild.service.rest.common.serializationhelpers.OfflineSerializationHelper.serializeOffline;
import static org.cmdbuild.service.rest.common.serializationhelpers.OfflineSerializationHelper.serializeOfflineDiff;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@Path("offline/data")
@Tag(name = "Offline Data", description = "Offline Data")
@Produces(APPLICATION_JSON)
@Component
public class OfflineDataWs {

    private final OfflineDataWsCommand command;

    public OfflineDataWs(OfflineDataWsCommand command) {
        this.command = checkNotNull(command);
    }

    @POST
    @Path("/{offlineCode}/load")
    @Operation(
            summary = "Load offline data",
            description = "Load offline data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Offline data not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object load(
            @PathParam("offlineCode") @Parameter(description = "Code of the offline data to load") String offlineCode,
            @RequestBody(description = "Filters to apply when loading the offline data, as map of filter name to value", required = false) Map<String, String> filters
    ) {
        command.doLoad(offlineCode, filters);
        return response(serializeOffline(offlineCode));
    }

    @POST
    @Path("/{offlineCode}/diff/{tempId}")
    @Operation(
            summary = "Get diff for offline data",
            description = "Get diff for offline data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Offline data not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object diff(
            @PathParam("offlineCode") @Parameter(description = "Code of the offline data to diff") String offlineCode,
            @PathParam("tempId") @Parameter(description = "Id of the temporary file") String tempId
    ) {
        String diff = command.doDiff(offlineCode, tempId);
        return response(serializeOfflineDiff(offlineCode, diff, tempId));
    }

    @POST
    @Path("/{offlineCode}/merge/{tempId}")
    @Operation(
            summary = "Merge offline data from diff",
            description = "Merge offline data from diff",
            responses = {@ApiResponse(responseCode = "200", description = "Successful operation")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object merge(@PathParam("offlineCode") @Parameter(description = "Code of the offline data") String offlineCode, @PathParam("tempId") @Parameter(description = "Id of the temporary file") String tempId, @RequestBody(description = "Diff data, as returned by the diff operation, describing the changes to merge", required = true) GeneratedDiffData wsDiffData) {
        List<Map<String, Object>> data = command.doMerge(offlineCode, tempId, wsDiffData);
        return response(data);
    }

    @POST
    @Path("/{offlineCode}/notify")
    @Operation(
            requestBody = @RequestBody(description = "File with the offline data changes to notify", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "file", schema = @Schema(type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED))
                    })),
            summary = "Notify offline data changes",
            description = "Notify offline data changes",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Offline data not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object notify(
            @Multipart("file") DataHandler dataHandler,
            @PathParam("offlineCode") @Parameter(description = "Code of the offline data to notify") String offlineCode
    ) {
        command.doNotify(dataHandler, offlineCode);
        return response(serializeOffline(offlineCode));
    }
}
