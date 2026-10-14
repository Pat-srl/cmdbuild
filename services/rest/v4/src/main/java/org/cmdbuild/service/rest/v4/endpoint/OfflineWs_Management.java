/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.offline.Offline;
import org.cmdbuild.offline.OfflineService;
import org.cmdbuild.offline.loader.OfflineLoaderService;
import org.cmdbuild.service.rest.common.serializationhelpers.OfflineSerializationHelper;
import org.cmdbuild.service.rest.v4.command.OfflineWsCommand;
import org.cmdbuild.service.rest.v4.model.WsModelConfiguration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;

/**
 * @author ldare
 */
@Path("offline/")
@Tag(name = "Offline", description = "Offline")
@Produces(APPLICATION_JSON)
@Component
public class OfflineWs_Management {

    private final OfflineService offlineService;
    private final OfflineSerializationHelper offlineSerializationHelper;
    private final OfflineLoaderService offlineLoaderService;
    private final OfflineWsCommand command;

    public OfflineWs_Management(OfflineService offlineService, OfflineSerializationHelper offlineSerializationHelper, OfflineLoaderService offlineLoaderService, OfflineWsCommand command) {
        this.offlineService = checkNotNull(offlineService);
        this.offlineSerializationHelper = checkNotNull(offlineSerializationHelper);
        this.offlineLoaderService = checkNotNull(offlineLoaderService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "List all offline",
            description = "List all offline",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of offline data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(DETAILED) @Parameter(description = "Include detailed offline data") @DefaultValue(FALSE) Boolean detailed
    ) {
        List<Offline> offlineList = command.doReadAll(offlineService::getActiveForCurrentUser);
        return response(offlineList.stream().map(offlineSerializationHelper.serializeOffline(detailed)).collect(toList()));
    }

    @GET
    @Path("/{offlineCode}")
    @Operation(
            summary = "Get offline by code",
            description = "Get offline by code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("offlineCode") @Parameter(description = "Code of offline to return", required = true, example = "1") String offlineCode
    ) {
        Offline offline = command.doRead(offlineCode);
        Map<String, Object> serializeDetailedOffline = offlineSerializationHelper.serializeDetailedOffline(offline);
        WsModelConfiguration dataModel = new WsModelConfiguration(offlineLoaderService.getDataModel(offlineCode));
        dataModel.setMasterClass((String) serializeDetailedOffline.get("masterClass"));
        return response(dataModel);
    }

    @POST
    @Path("{offlineCode}/lock")
    @Operation(
            summary = "Lock offline",
            description = "Lock offline",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful lock of offline data"),
                    @ApiResponse(responseCode = "409", description = "Offline already locked"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object lock(
            @PathParam("offlineCode") @Parameter(description = "Code of offline to lock", required = true, example = "1") String offlineCode
    ) {
        command.checkOfflineAvailable();
        return offlineSerializationHelper.aquireLockOffline(offlineCode);
    }

    @DELETE
    @Path("{offlineCode}/unlock")
    @Operation(
            summary = "Release lock offline",
            description = "Release lock offline",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful release of lock of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline not locked"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object releaseLock(@PathParam("offlineCode") @Parameter(description = "Code of offline to unlock", required = true, example = "1") String offlineCode) {
        command.checkOfflineAvailable();
        return offlineSerializationHelper.releaseLockOffline(offlineCode);
    }


}
