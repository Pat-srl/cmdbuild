package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.endpoint.ClassPrintWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ClassPrintWs_Management;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;

@Path("{type:classes|processes}/")
@Tag(name = "Class print", description = "Operations related to printing of classes")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ClassPrintWs {

    private ClassPrintWs_Administration classPrintWs_adm;
    private ClassPrintWs_Management classPrintWs_mng;

    public ClassPrintWs(ClassPrintWs_Administration classPrintWs_adm, ClassPrintWs_Management classPrintWs_mng) {
        this.classPrintWs_adm = checkNotNull(classPrintWs_adm);
        this.classPrintWs_mng = checkNotNull(classPrintWs_mng);
    }
 
    @GET
    @Path("{" + CLASS_ID + "}/print/{file}")
    @Operation(
            summary = "Print a report for a class",
            description = "Print a report for a class",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = EXTENSION, in = ParameterIn.QUERY, description = "File extension", schema = @Schema(allowableValues = {"pdf", "xlsx"})),
                    @Parameter(name = "attributes", in = ParameterIn.QUERY, description = "Attributes to include in the report")
            },
            requestBody = @RequestBody(description = "Query options"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of report"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printClassReport(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            WsQueryOptions wsQueryOptions,
            @QueryParam(EXTENSION) String extension,
            @QueryParam("attributes") String attributes
    ) {
        return classPrintWs_mng.printClassReport(classId, wsQueryOptions, extension, attributes);
    } 

    @GET
    @Path("{" + CLASS_ID + "}/print_schema/{file}")
    @Operation(
            summary = "Print class schema report",
            description = "Print class schema report",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = "file", in = ParameterIn.PATH, description = "File name"),
                    @Parameter(name = EXTENSION, in = ParameterIn.QUERY, description = "File extension", schema = @Schema(allowableValues = {"pdf", "xlsx"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of report"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printClassSchemaReport(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam("file") String fileName,
            @QueryParam(EXTENSION) String extension
    ) {
        return classPrintWs_mng.printClassSchemaReport(classId, fileName, extension);
    }

    @GET
    @Path("print_schema/{file}")
    @Operation(
            summary = "Print system schema report",
            description = "Print system schema report",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query", required = false),
                    @Parameter(name = "file", in = ParameterIn.PATH, description = "File name"),
                    @Parameter(name = EXTENSION, in = ParameterIn.QUERY, description = "File extension", schema = @Schema(allowableValues = {"pdf", "xlsx"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of report"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public DataHandler printSchemaReport(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam("file") String fileName,
            @QueryParam(EXTENSION) String extension
    ) {
        return classPrintWs_adm.printSchemaReport(classId, fileName, extension);
    }
}
