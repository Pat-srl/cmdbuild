/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


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
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.ClassPrintWsCommand;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Path("administration/{" + TYPE + ":classes|processes}/")
@Tag(name = "Class print", description = "Operations related to printing of classes")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ClassPrintWs_Administration {

    private final ClassPrintWsCommand command;

    public ClassPrintWs_Administration(ClassPrintWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{" + CLASS_ID + "}/print/{file}")
    @Operation(
            summary = "Print a report for a class",
            description = "Print a report for a class",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of report"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public DataHandler printClassReport(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam(EXTENSION) @Parameter(description = "Extension of the report to generate") String extension,
            @QueryParam(ATTRIBUTES) @Parameter(description = "List of attributes to include in the report") String attributes
    ) {
        return command.doPrintClassReport(classId, wsQueryOptions, extension, attributes);
    }

    @GET
    @Path("{" + CLASS_ID + "}/print_schema/{" + FILE + "}")
    @Operation(
            summary = "Print class schema report",
            description = "Print class schema report",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of report"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public DataHandler printClassSchemaReport(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(FILE) @Parameter(description = "Name of the file to generate") String fileName,
            @QueryParam(EXTENSION) @Parameter(description = "Extension of the report to generate") String extension
    ) {
        return command.doPrintClassSchemaReport(classId, fileName, extension);
    }

    @GET
    @Path("print_schema/{" + FILE + "}")
    @Operation(
            summary = "Print system schema report",
            description = "Print system schema report",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
            },
            responses = {@ApiResponse(responseCode = "200", description = "Successful generation of report")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public DataHandler printSchemaReport(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(FILE) @Parameter(description = "Name of the file to generate") String fileName,
            @QueryParam(EXTENSION) @Parameter(description = "Extension of the report to generate") String extension
    ) {
        return command.doPrintSchemaReport(classId, fileName, extension);

    }
}
