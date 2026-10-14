/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.modeldiff.diff.schema.GeneratedDiffSchema;

import java.io.IOException;

import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ALL_AUTHORITY;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@Path("schema/")
@RolesAllowed(ADMIN_ALL_AUTHORITY)
@Tag(name = "Schema", description = "Schema")
public class SchemaWs {

    private final org.cmdbuild.service.rest.v4.endpoint.SchemaWs schemaWs;

    public SchemaWs(org.cmdbuild.service.rest.v4.endpoint.SchemaWs schemaWs) {
        this.schemaWs = checkNotNull(schemaWs);
    }

    @GET
    @Path("load")
    @Operation(
            summary = "Load schema",
            description = "Load schema",
            parameters = {
                    @Parameter(name = "name", description = "Name of the schema to load"),
                    @Parameter(name = "mode", description = "Mode of the schema to load")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of schema data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object load(
            @QueryParam("name") @DefaultValue("schema") String name,
            @QueryParam("mode") @DefaultValue("json") String mode
    ) {
        return schemaWs.load(name, mode);
    }

    @POST
    @Path("diff")
    @Operation(
            summary = "Diff schema",
            description = "Diff schema",
            requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data")),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of diff data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object diff(
            @Multipart("file") DataHandler dataHandler
    ) throws IOException {
        return schemaWs.diff(dataHandler);
    }

    @POST
    @Path("merge")
    @Operation(
            summary = "Merge schema",
            description = "Merge schema",
            requestBody = @RequestBody(content = @Content(mediaType = "application/json")),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful merge of schema data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object merge(
            GeneratedDiffSchema diff
    ) {
        return schemaWs.merge(diff);
    }
}
