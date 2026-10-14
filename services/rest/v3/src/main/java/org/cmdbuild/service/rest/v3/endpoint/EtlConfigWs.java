package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsConfigMeta;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;

@Path("etl/configs/")
@Tag(name = "ETL Configuration", description = "ETL Configuration")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
public class EtlConfigWs {

    private final org.cmdbuild.service.rest.v4.endpoint.EtlConfigWs etlConfigWs;

    public EtlConfigWs(org.cmdbuild.service.rest.v4.endpoint.EtlConfigWs etlConfigWs) {
        this.etlConfigWs = checkNotNull(etlConfigWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all ETL configuration files",
            description = "Get all ETL configuration files",
            requestBody = @RequestBody(description = "Query options"),
            parameters = { @Parameter(name = "includeMeta", in = ParameterIn.QUERY, description = "Whether to include metadata in the response")},
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of ETL configuration files data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            WsQueryOptions wsQueryOptions,
            @QueryParam("includeMeta") @DefaultValue(FALSE) Boolean includeMeta
    ) {
        return etlConfigWs.readAll(wsQueryOptions, includeMeta);
    }

    @GET
    @Path("{code}/")
    @Operation(
            summary = "Get ETL configuration file by code",
            description = "Get ETL configuration file by code",
            parameters = {
                    @Parameter(name = "code", in = ParameterIn.PATH, description = "Configuration code"),
                    @Parameter(name = "includeMeta", in = ParameterIn.QUERY, description = "Whether to include metadata in the response"),
                    @Parameter(name = "if_exists", in = ParameterIn.QUERY, description = "Whether to return 404 if the file does not exist")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL configuration file data"),
                    @ApiResponse(responseCode = "404", description = "ETL configuration file not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested ETL configuration file"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("code") String code,
            @QueryParam("includeMeta") @DefaultValue(FALSE) Boolean includeMeta,
            @QueryParam("if_exists") @DefaultValue(FALSE) Boolean checkIfExists
    ) {
        return etlConfigWs.read(code, includeMeta, checkIfExists);
    }

    @GET
    @Path("{code}/items")
    @Operation(
            summary = "Get ETL configuration items by configuration code",
            description = "Get ETL configuration items by configuration code",
            requestBody = @RequestBody(description = "Query options"),
            parameters = { @Parameter(name = "code", in = ParameterIn.PATH, description = "Configuration code")},
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of ETL configuration items data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readItems(
            WsQueryOptions wsQueryOptions,
            @PathParam("code") String code
    ) {
        return etlConfigWs.readItems(wsQueryOptions, code);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create ETL configuration file",
            description = "Create ETL configuration file",
            requestBody = @RequestBody(description = "ETL configuration file metadata", required = true, content = @Content(schema = @Schema(implementation = WsConfigMeta.class))),
            parameters = { @Parameter(name = "overwriteIfExists", in = ParameterIn.QUERY, description = "Whether to overwrite an existing file with the same code")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of ETL configuration file data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid"),
                    @ApiResponse(responseCode = "409", description = "ETL configuration file with the same code already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object create(@Multipart(value = FILE, required = false) DataHandler dataHandler, @Nullable WsConfigMeta meta, @QueryParam("overwriteIfExists") @DefaultValue(FALSE) Boolean overwriteIfExists) {
        return etlConfigWs.create(dataHandler, meta, overwriteIfExists);
    }

    @PUT
    @Path("{code}/")
    @Operation(
            summary = "Update ETL configuration file",
            description = "Update ETL configuration file",
            parameters = { @Parameter(name = "code", in = ParameterIn.PATH, description = "Configuration code")},
            requestBody = @RequestBody(description = "ETL configuration file metadata", required = true, content = @Content(schema = @Schema(implementation = WsConfigMeta.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of ETL configuration file data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid"),
                    @ApiResponse(responseCode = "404", description = "ETL configuration file not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested ETL configuration file"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object update(@PathParam("code") String code, @Multipart(value = FILE, required = false) DataHandler dataHandler, @Nullable WsConfigMeta meta) {
        return etlConfigWs.update(code, dataHandler, meta);
    }

    @DELETE
    @Path("{code}/")
    @Operation(
            summary = "Delete ETL configuration file",
            description = "Delete ETL configuration file",
            parameters = { @Parameter(name = "code", in = ParameterIn.PATH, description = "Configuration code")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of ETL configuration file data"),
                    @ApiResponse(responseCode = "404", description = "ETL configuration file not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested ETL configuration file"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("code") String code
    ) {
        return etlConfigWs.delete(code);
    }
}
