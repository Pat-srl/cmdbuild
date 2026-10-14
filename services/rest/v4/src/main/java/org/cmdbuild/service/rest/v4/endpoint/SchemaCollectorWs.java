/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.api.SchemaCollectorApi;
import org.cmdbuild.api.SystemApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.command.SchemaCollectorWsCommand;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ALL_AUTHORITY;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ALL_AUTHORITY;

/**
 *
 * @author afelice
 */
@Path("administration/schema/collect")
@Tag(name = "Schema Collector", description = "Schema Collector")
@Consumes(TEXT_PLAIN)
@Produces(TEXT_PLAIN)
@RolesAllowed(ADMIN_ALL_AUTHORITY)
@Component
public class SchemaCollectorWs {

    /**
     * A <i>dummy implementation</i> (that throws
     * <code>UnsupportedOperationException</code> on all methods) if
     * schemaCollector module not available.
     */
    private final SchemaCollectorWsCommand command;

    public SchemaCollectorWs(SchemaCollectorWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("test")
    @Operation(
            summary = "Test Schema Collector",
            description = "Test Schema Collector",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of test data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object test(
            @QueryParam("msg") @Parameter(description = "Message to test") String msg
    ) {
        return command.doTest(msg);
    }

    @PUT
    @Path("collectSchema")
    @Operation(
            summary = "Collect Schema",
            description = "Collect Schema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of schema data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object collectSchema(@QueryParam("name") @Parameter(description = "System mnemonic name") String curSystemMnemonicName, @QueryParam("id") @Parameter(description = "System id") String curSystemId) {
        return command.doCollectSchema(curSystemMnemonicName, curSystemId);
    }

    @PUT
    @Path("compareSchema")
    @Operation(
            summary = "Compare Schema",
            description = "Compare Schema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of schema comparison data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object compareSchema(
            @QueryParam("other") @Parameter(description = "Other schema") String otherSchemaSerialization,
            @QueryParam("name") @Parameter(description = "System mnemonic name") String curSystemMnemonicName
    ) {
        return command.doCompareSchema(otherSchemaSerialization, curSystemMnemonicName);
    }

    @PUT
    @Path("compareSchemaBetween")
    @Operation(
            summary = "Compare Schema Between",
            description = "Compare Schema Between",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of schema comparison data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object compareSchemaBetween(
            @QueryParam("new") @Parameter(description = "New schema") String newSchemaSerialization,
            @QueryParam("a") @Parameter(description = "Old schema") String aSchemaSerialization
    ) {
        return command.doCompareSchemaBetween(newSchemaSerialization, aSchemaSerialization);
    }

    @PUT
    @Path("applySchemaDiff")
    @Operation(
            summary = "Apply Schema Diff",
            description = "Apply Schema Diff",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful application of schema diff data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object applySchemaDiff(
            @QueryParam("diff") @Parameter(description = "Schema diff") String diffSchemaSerialization
    ) {
        return command.doApplySchemaDiff(diffSchemaSerialization);
    }
}
