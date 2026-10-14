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
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.OfflineWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.OfflineWs_Management;
import org.cmdbuild.service.rest.v4.model.WsOfflineData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_OFFLINE_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;

/**
 *
 * @author ataboga
 */
@Path("offline/")
@Tag(name = "Offline", description = "Offline")
@Produces(APPLICATION_JSON)
public class OfflineWs {

    private final OfflineWs_Administration offlineWs_adm;
    private final OfflineWs_Management offlineWs_mng;

    public OfflineWs(OfflineWs_Administration offlineWs_adm, OfflineWs_Management offlineWs_mng) {
        this.offlineWs_adm = checkNotNull(offlineWs_adm);
        this.offlineWs_mng = checkNotNull(offlineWs_mng);
    }

    @GET
    @Path("")
    @Operation(
            summary = "List all offline",
            description = "List all offline",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about offline, such as the configuration of widgets contained in the offline")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of offline data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed
    ) {
        if (isAdminViewMode(viewMode)) {
            return offlineWs_adm.readAll(detailed);
        }
        return offlineWs_mng.readAll(detailed);
    }

    @GET
    @Path("/{offlineCode}")
    @Operation(
            summary = "Get offline by code",
            description = "Get offline by code",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "offlineCode", in = ParameterIn.PATH, description = "Code of the offline to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline with the specified code does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("offlineCode") String offlineCode
    ) {
        if (isAdminViewMode(viewMode)) {
            return offlineWs_adm.read(offlineCode);
        }
        return offlineWs_mng.read(offlineCode);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create offline",
            description = "Create offline",
            requestBody = @RequestBody(description = "Data for the offline to create", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of offline data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object create(
            WsOfflineData data
    ) {
        return offlineWs_adm.create(data);
    }

    @PUT
    @Path("/{offlineCode}")
    @Operation(
            summary = "Update offline",
            description = "Update offline",
            requestBody = @RequestBody(description = "Data for the offline to update", required = true),
            parameters = {@Parameter(name = "offlineCode", in = ParameterIn.PATH, description = "Code of the offline to update", required = true)},
            responses = {@ApiResponse(responseCode = "200", description = "Successful update of offline data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("offlineCode") String offlineCode,
            WsOfflineData data
    ) {
        return offlineWs_adm.update(offlineCode, data);
    }

    @DELETE
    @Path("/{offlineCode}")
    @Operation(
            summary = "Delete offline",
            description = "Delete offline",
            parameters = {@Parameter(name = "offlineCode", in = ParameterIn.PATH, description = "Code of the offline to delete", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline with the specified code does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_OFFLINE_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("offlineCode") String offlineCode
    ) {
        return offlineWs_adm.delete(offlineCode);
    }

    @POST
    @Path("/{offlineCode}/lock")
    @Operation(
            summary = "Lock offline",
            description = "Lock offline",
            parameters = {@Parameter(name = "offlineCode", in = ParameterIn.PATH, description = "Code of the offline to lock", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful lock of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline with the specified code does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object lock(@PathParam("offlineCode") String offlineCode
    ) {
        return offlineWs_mng.lock(offlineCode);
    }

    @DELETE
    @Path("/{offlineCode}/unlock")
    @Operation(
            summary = "Release lock offline",
            description = "Release lock offline",
            parameters = {@Parameter(name = "offlineCode", in = ParameterIn.PATH, description = "Code of the offline to release lock", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful release of lock of offline data"),
                    @ApiResponse(responseCode = "404", description = "Offline with the specified code does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object releaseLock(@PathParam("offlineCode") String offlineCode) {
        return offlineWs_mng.releaseLock(offlineCode);
    }
}
