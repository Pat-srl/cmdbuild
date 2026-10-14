/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;

import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@Path("offline/data")
@Tag(name = "Offline Data", description = "Offline Data")
@Produces(APPLICATION_JSON)
public class OfflineDataWs {

    private final org.cmdbuild.service.rest.v4.endpoint.OfflineDataWs offlineDataWs;

    public OfflineDataWs(org.cmdbuild.service.rest.v4.endpoint.OfflineDataWs offlineDataWs) {
        this.offlineDataWs = checkNotNull(offlineDataWs);
    }

    @POST
    @Path("/{offlineCode}/load")
    @Operation(
            summary = "Load offline data",
            description = "Load offline data",
            parameters = {
                    @Parameter(name = "offlinecode", in = ParameterIn.PATH, description = "Code of the offline data to load", required = true)
            },
            requestBody = @RequestBody(description = "Filters to apply when loading offline data", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object load(
            @PathParam("offlineCode") String offlineCode,
            @RequestBody Map<String, String> filters
    ) {
        return offlineDataWs.load(offlineCode, filters);
    }

    @POST
    @Path("/{offlineCode}/diff/{tempId}")
    @Operation(
            summary = "Get diff for offline data",
            description = "Get diff for offline data",
            parameters = {
                    @Parameter(name = "offlinecode", in = ParameterIn.PATH, description = "Code of the offline data to diff", required = true),
                    @Parameter(name = "tempid", in = ParameterIn.PATH, description = "Temporary id of the offline data to diff", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object diff(
            @PathParam("offlineCode") String offlineCode,
            @PathParam("tempId") String tempId
    ) {
        return offlineDataWs.diff(offlineCode, tempId);
    }

    @POST
    @Path("/{offlineCode}/merge/{tempId}")
    @Operation(
            summary = "Merge offline data from diff",
            description = "Merge offline data from diff",
            parameters = {
                    @Parameter(name = "offlinecode", in = ParameterIn.PATH, description = "Code of the offline data to merge", required = true),
                    @Parameter(name = "tempid", in = ParameterIn.PATH, description = "Temporary id of the offline data to merge", required = true)
            },
            requestBody = @RequestBody(description = "Diff data to merge", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object merge(
            @PathParam("offlineCode") String offlineCode,
            @PathParam("tempId") String tempId,
            GeneratedDiffData wsDiffData
    ) {
        return offlineDataWs.merge(offlineCode, tempId, wsDiffData);
    }

    @POST
    @Path("/{offlineCode}/notify")
    @Operation(
            summary = "Notify offline data changes",
            description = "Notify offline data changes",
            parameters = {@Parameter(name = "offlinecode", in = ParameterIn.PATH, description = "Code of the offline data to notify", required = true)},
            requestBody = @RequestBody(description = "Data to notify", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object notify(
            @Multipart("file") DataHandler dataHandler,
            @PathParam("offlineCode") String offlineCode
    ) {
        return offlineDataWs.notify(dataHandler, offlineCode);
    }
}
