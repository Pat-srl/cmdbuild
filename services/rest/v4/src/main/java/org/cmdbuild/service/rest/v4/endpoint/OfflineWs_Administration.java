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
import org.cmdbuild.offline.Offline;
import org.cmdbuild.offline.OfflineService;
import org.cmdbuild.service.rest.common.serializationhelpers.OfflineSerializationHelper;
import org.cmdbuild.service.rest.v4.command.OfflineWsCommand;
import org.cmdbuild.service.rest.v4.model.WsOfflineData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_OFFLINE_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_OFFLINE_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;

/**
 * @author ldare
 */
@Path("administration/offline/")
@Tag(name = "Offline", description = "Offline")
@Produces(APPLICATION_JSON)
@Component
public class OfflineWs_Administration {

    private final OfflineService offlineService;
    private final OfflineSerializationHelper offlineSerializationHelper;
    private final OfflineWsCommand command;

    public OfflineWs_Administration(OfflineService offlineService, OfflineSerializationHelper offlineSerializationHelper, OfflineWsCommand command) {
        this.offlineService = checkNotNull(offlineService);
        this.offlineSerializationHelper = checkNotNull(offlineSerializationHelper);
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
    @RolesAllowed(ADMIN_OFFLINE_VIEW_AUTHORITY)
    public Object readAll(
            @QueryParam(DETAILED) @Parameter(description = "Detailed") @DefaultValue(FALSE) Boolean detailed
    ) {
        List<Offline> offlineList = command.doReadAll(offlineService::getAll);
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
    @RolesAllowed(ADMIN_OFFLINE_VIEW_AUTHORITY)
    public Object read(
            @PathParam("offlineCode") @Parameter(description = "Code of offline to return", required = true, example = "1") String offlineCode
    ) {
        Offline offline = command.doRead(offlineCode);
        Map<String, Object> serializeDetailedOffline = offlineSerializationHelper.serializeDetailedOffline(offline);
        return response(serializeDetailedOffline);
    }

    @POST
    @Path("/{offlineCode}/lock")
    @Operation(
            summary = "Lock offline",
            description = "Lock offline",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful lock of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested offline"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object lock(
            @PathParam("offlineCode") @Parameter(description = "Code of offline to lock", required = true, example = "1") String offlineCode
    ) {
        command.checkOfflineAvailable();
        return offlineSerializationHelper.aquireLockOffline(offlineCode);
    }

    @DELETE
    @Path("/{offlineCode}/unlock")
    @Operation(
            summary = "Release lock offline",
            description = "Release lock offline",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful release of lock of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested offline"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object releaseLock(
            @PathParam("offlineCode") @Parameter(description = "Code of offline to release lock", required = true, example = "1") String offlineCode
    ) {
        command.checkOfflineAvailable();
        return offlineSerializationHelper.releaseLockOffline(offlineCode);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create offline",
            description = "Create offline",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of offline data"),
                    @ApiResponse(responseCode = "409", description = "Offline already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object create(@RequestBody(description = "Offline data", required = true) WsOfflineData data) {
        Offline offline = command.doCreate(data);
        return response(offlineSerializationHelper.serializeDetailedOffline(offline));
    }

    @PUT
    @Path("/{offlineCode}")
    @Operation(
            summary = "Update offline",
            description = "Update offline",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("offlineCode") @Parameter(description = "Code of offline to update", required = true, example = "1") String offlineCode,
            @RequestBody(description = "Offline data", required = true) WsOfflineData data
    ) {
        Offline offline = command.doUpdate(offlineCode, data);
        return response(offlineSerializationHelper.serializeDetailedOffline(offline));
    }

    @DELETE
    @Path("/{offlineCode}")
    @Operation(
            summary = "Delete offline",
            description = "Delete offline",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("offlineCode") @Parameter(description = "Code of offline to delete", required = true, example = "1") String offlineCode
    ) {
        command.doDelete(offlineCode);
        return success();
    }
}
