/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ALL_AUTHORITY;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author afelice
 */
@Path("schema/collect")
@Tag(name = "Schema Collector", description = "Schema Collector")
@Consumes(TEXT_PLAIN)
@Produces(TEXT_PLAIN)
@RolesAllowed(ADMIN_ALL_AUTHORITY)
public class SchemaCollectorWs {

    private final org.cmdbuild.service.rest.v4.endpoint.SchemaCollectorWs schemaCollectorWs;

    public SchemaCollectorWs(org.cmdbuild.service.rest.v4.endpoint.SchemaCollectorWs schemaCollectorWs) {
        this.schemaCollectorWs = checkNotNull(schemaCollectorWs);
    }

    @GET
    @Path("test")
    @Operation(
            summary = "Test Schema Collector",
            description = "Test Schema Collector",
            parameters = {@Parameter(name = "msg", description = "Message to test the schema collector", required = true, in = ParameterIn.QUERY)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of test data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object test(
            @QueryParam("msg") String msg
    ) {
        return schemaCollectorWs.test(msg);
    }

    @PUT
    @Path("collectSchema")
    @Operation(
            summary = "Collect Schema",
            description = "Collect Schema",
            parameters = {
                    @Parameter(name = "name", description = "Name of the system to collect schema for", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "id", description = "ID of the system to collect schema for", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of schema data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object collectSchema(
            @QueryParam("name") String curSystemMnemonicName,
            @QueryParam("id") String curSystemId
    ) {
        return schemaCollectorWs.collectSchema(curSystemMnemonicName, curSystemId);
    }

    @PUT
    @Path("compareSchema")
    @Operation(
            summary = "Compare Schema",
            description = "Compare Schema",
            parameters = {
                    @Parameter(name = "other", description = "Schema to compare with", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "name", description = "Name of the system to compare schema for", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of schema comparison data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object compareSchema(
            @QueryParam("other") String otherSchemaSerialization,
            @QueryParam("name") String curSystemMnemonicName
    ) {
        return schemaCollectorWs.compareSchema(otherSchemaSerialization, curSystemMnemonicName);
    }

    @PUT
    @Path("compareSchemaBetween")
    @Operation(
            summary = "Compare Schema Between",
            description = "Compare Schema Between",
            parameters = {
                    @Parameter(name = "new", description = "Schema to compare with", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "a", description = "Schema to compare with", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of schema comparison data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object compareSchemaBetween(
            @QueryParam("new") String newSchemaSerialization,
            @QueryParam("a") String aSchemaSerialization
    ) {
        return schemaCollectorWs.compareSchemaBetween(newSchemaSerialization, aSchemaSerialization);
    }

    @PUT
    @Path("applySchemaDiff")
    @Operation(
            summary = "Apply Schema Diff",
            description = "Apply Schema Diff",
            parameters = {
                    @Parameter(name = "diff", description = "Schema diff to apply", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful application of schema diff data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object applySchemaDiff(
            @QueryParam("diff") String diffSchemaSerialization
    ) {
        return schemaCollectorWs.applySchemaDiff(diffSchemaSerialization);
    }
}
