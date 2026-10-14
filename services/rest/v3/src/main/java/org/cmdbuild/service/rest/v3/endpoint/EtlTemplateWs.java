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
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.endpoint.EtlTemplateWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.EtlTemplateWs_Management;
import org.cmdbuild.service.rest.v4.model.WsEtlTemplateData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;


@Path("etl/templates/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "ETL Templates", description = "ETL Templates")
public class EtlTemplateWs {

    private final EtlTemplateWs_Administration etlTemplateWs_adm;
    private final EtlTemplateWs_Management etlTemplateWs_mng;

    public EtlTemplateWs(EtlTemplateWs_Administration etlTemplateWs_adm, EtlTemplateWs_Management etlTemplateWs_mng) {
        this.etlTemplateWs_adm = checkNotNull(etlTemplateWs_adm);
        this.etlTemplateWs_mng = checkNotNull(etlTemplateWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all ETL templates",
            description = "Get all ETL templates. If the user has admin view permissions, all templates will be returned. Otherwise, only templates for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = WsQueryOptions.class))),
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view ETL templates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            WsQueryOptions wsQueryOptions
    ) {
        if (isAdminViewMode(viewMode)) {
            return etlTemplateWs_adm.readAll(wsQueryOptions);
        }
        return etlTemplateWs_mng.readAll(wsQueryOptions);
    }

    @GET
    @Path("by-class/{"+ CLASS_ID + "}")
    @Operation(
            summary = "Get ETL templates for a class",
            description = "Get ETL templates for a class. If the user has admin view permissions, all templates will be returned. Otherwise, only templates for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = WsQueryOptions.class))),
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "ID of the class to retrieve ETL templates for", required = true),
                    @Parameter(name = "include_related_domains", in = ParameterIn.QUERY, description = "Whether to include templates related to domains related to the specified class" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view ETL templates for the specified class"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllForClass(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            WsQueryOptions wsQueryOptions,
            @QueryParam("include_related_domains") @DefaultValue(FALSE) boolean includeRelatedDomains
    ) {
        if (isAdminViewMode(viewMode)) {
            return etlTemplateWs_adm.readAllForClass(classId, wsQueryOptions, includeRelatedDomains);
        }
        return etlTemplateWs_mng.readAllForClass(classId, wsQueryOptions, includeRelatedDomains);
    }

    @GET
    @Path("by-process/{"+ CLASS_ID + "}")
    @Operation(
            summary = "Get ETL templates for a process",
            description = "Get ETL templates for a process. If the user has admin view permissions, all templates will be returned. Otherwise, only templates for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = WsQueryOptions.class))),
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "ID of the process to retrieve ETL templates for", required = true),
                    @Parameter(name = "include_related_domains", in = ParameterIn.QUERY, description = "Whether to include templates related to domains related to the specified process" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view ETL templates for the specified process"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllForProcess(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            WsQueryOptions wsQueryOptions,
            @QueryParam("include_related_domains") @DefaultValue(FALSE) boolean includeRelatedDomains
    ) {
        if (isAdminViewMode(viewMode)) {
            return etlTemplateWs_adm.readAllForProcess(classId, wsQueryOptions, includeRelatedDomains);
        }
        return etlTemplateWs_mng.readAllForProcess(classId, wsQueryOptions, includeRelatedDomains);
    }

    @GET
    @Path("by-view/{viewId}")
    @Operation(
            summary = "Get ETL templates for a view",
            description = "Get ETL templates for a view. If the user has admin view permissions, all templates will be returned. Otherwise, only templates for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = WsQueryOptions.class))),
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "viewId", in = ParameterIn.PATH, description = "ID of the view to retrieve ETL templates for", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view ETL templates for the specified view"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllForView(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("viewId") String viewId,
            WsQueryOptions wsQueryOptions
    ) {
        if (isAdminViewMode(viewMode)) {
            return etlTemplateWs_adm.readAllForView(viewId, wsQueryOptions);
        }
        return etlTemplateWs_mng.readAllForView(viewId, wsQueryOptions);
    }

    @GET
    @Path("{templateId}/")
    @Operation(
            summary = "Get ETL template by id or code",
            description = "Get ETL template by id or code. If the user has admin view permissions, the template will be returned if it exists, regardless of the user's management permissions for that template. Otherwise, the template will only be returned if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "templateId", in = ParameterIn.PATH, description = "Id or code of the ETL template to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the ETL template or the template does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readOne(@PathParam("templateId") String idOrCode) {
        return etlTemplateWs_mng.readOne(idOrCode);
    }

    @GET
    @Path("{templateId}/export|{templateId}/export/{fileName}")
    public DataHandler executeExportTemplate(
            @PathParam("templateId") String idOrCode,
            @QueryParam(FILTER) String filterStr
    ) {
        return etlTemplateWs_mng.executeExportTemplate(idOrCode, filterStr);
    }

    @POST
    @Path("{templateId}/import")
    @Consumes(MULTIPART_FORM_DATA)
    @Operation(
            summary = "Execute an import ETL template",
            description = "Execute an import ETL template by uploading a file. The file must be sent as multipart form data with the name '" + FILE + "'. If the 'detailed_report' query parameter is set to true, the response will contain a detailed report of the execution, including any errors encountered during the process. Otherwise, only a summary of the execution will be returned.",
            requestBody = @RequestBody( content = @Content(schema = @Schema(implementation = DataHandler.class))),
            parameters = {
                    @Parameter(name = "templateId", in = ParameterIn.PATH, description = "Id or code of the ETL template to import", required = true),
                    @Parameter(name = "detailed_report", in = ParameterIn.QUERY, description = "Whether to return a detailed report of the execution")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the file is not a valid CSV file"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to import the ETL template or the template does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object executeImportTemplate(
            @PathParam("templateId") String idOrCode,
            @Multipart(value = FILE, required = true) DataHandler dataHandler,
            @QueryParam("detailed_report") @DefaultValue(FALSE) Boolean detailedReport
    ) {
        return etlTemplateWs_mng.executeImportTemplate(idOrCode, dataHandler, detailedReport);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new ETL template",
            description = "Create a new ETL template. The template code must be unique and can only contain letters, numbers, underscores and hyphens",
            requestBody = @RequestBody(description = "Data for the new ETL template", required = true, content = @Content(schema = @Schema(implementation = WsEtlTemplateData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the template code is not unique or contains invalid characters, or the request body is invalid"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create ETL templates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsEtlTemplateData data
    ) {
        return etlTemplateWs_adm.create(data);
    }

    @PUT
    @Path("{templateId}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an ETL template",
            description = "Update an ETL template. The template code must be unique and can only contain letters, numbers, underscores and hyphens",
            parameters = {
                    @Parameter(name = "templateId", in = ParameterIn.PATH, description = "Id or code of the ETL template to update", required = true)
            },
            requestBody = @RequestBody(description = "Data for updating the ETL template", required = true, content = @Content(schema = @Schema(implementation = WsEtlTemplateData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the template code is not unique or contains invalid characters, or the request body is invalid"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update ETL templates or the template does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("templateId") String templateId,
            WsEtlTemplateData data
    ) {
        return etlTemplateWs_adm.update(templateId, data);
    }

    @DELETE
    @Path("{templateId}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete an ETL template",
            description = "Delete an ETL template",
            parameters = {
                    @Parameter(name = "templateId", in = ParameterIn.PATH, description = "Id or code of the ETL template to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete ETL templates or the template does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("templateId") String templateName
    ) {
        return etlTemplateWs_adm.delete(templateName);
    }

    @POST
    @Path("inline/export|inline/export/{fileName}")
    @Operation(
            summary = "Execute an export ETL template without saving it first",
            description = "Execute an export ETL template without saving it first. The template configuration must be sent as multipart form data with the name 'config'. The data to export must be sent as multipart form data with the name 'data'. The response will contain the exported data file. The file name can be specified in the path, otherwise it will default to 'exported_data'",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = WsEtlTemplateData.class))),
            parameters = {
                    @Parameter(name = "config", in = ParameterIn.QUERY, description = "Configuration for the ETL template", required = true),
                    @Parameter(name = "data", in = ParameterIn.QUERY, description = "Data to export", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler executeInlineExportTemplate(
            @Multipart(value = "data", required = true) String data,
            @Multipart(value = "config", required = true) WsEtlTemplateData config
    ) {
        return etlTemplateWs_mng.executeInlineExportTemplate(data, config);
    }

    @POST
    @Path("inline/import")
    @Consumes(MULTIPART_FORM_DATA)
    @Operation(
            summary = "Execute an import ETL template without saving it first",
            description = "Execute an import ETL template without saving it first. The template configuration must be sent as multipart form data with the name 'config'. The file to import must be sent as multipart form data with the name 'file'. The response will contain a report of the execution, including any errors encountered during the process.",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = WsEtlTemplateData.class))),
            parameters = {
                    @Parameter(name = "file", in = ParameterIn.QUERY, description = "File to import", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the file is not a valid CSV file or the template configuration is invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object executeInlineImportTemplate(
            @Multipart(value = FILE, required = true) DataHandler data,
            @Multipart(value = "config", required = true) WsEtlTemplateData config
    ) {
        return etlTemplateWs_mng.executeInlineImportTemplate(data, config);
    }
}
