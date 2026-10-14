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
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.endpoint.EtlGateWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.EtlGateWs_Management;
import org.cmdbuild.service.rest.v4.model.WsImportExportGateData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;

@Path("etl/gates/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "ETL Gates", description = "ETL Gates")
public class EtlGateWs {

    private final EtlGateWs_Administration etlGateWs_adm;
    private final EtlGateWs_Management etlGateWs_mng;

    public EtlGateWs(EtlGateWs_Administration etlGateWs_adm, EtlGateWs_Management etlGateWs_mng) {
        this.etlGateWs_adm = checkNotNull(etlGateWs_adm);
        this.etlGateWs_mng = checkNotNull(etlGateWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all ETL gates",
            description = "Get all ETL gates. If the user has admin view permissions, all gates will be returned. Otherwise, only gates for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = WsQueryOptions.class))),
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "include_etl_templates", in = ParameterIn.QUERY, description = "Whether to include ETL templates in the response. An ETL template is a special type of ETL gate that is used as a template for creating new gates. It is not associated with any class and is not executable. This parameter can be used to include or exclude ETL templates from the response")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view ETL gates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            WsQueryOptions wsQueryOptions,
            @QueryParam("include_etl_templates") @DefaultValue(FALSE) boolean includeEtlTemplates
    ) {
        return etlGateWs_mng.readAll(wsQueryOptions, includeEtlTemplates);
    }

    @GET
    @Path("by-class/{"+ CLASS_ID + "}")
    @Operation(
            summary = "Get all ETL gates for a class",
            description = "Get all ETL gates for a class. If the user has admin view permissions, all gates will be returned. Otherwise, only gates for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = WsQueryOptions.class))),
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "ID of the class for which to retrieve ETL gates"),
                    @Parameter(name = "include_etl_templates", in = ParameterIn.QUERY, description = "Whether to include ETL templates in the response. An ETL template is a special type of ETL gate that is used as a template for creating new gates. It is not associated with any class and is not executable. This parameter can be used to include or exclude ETL templates from the response")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view ETL gates for the specified class"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllForClass(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            WsQueryOptions wsQueryOptions,
            @QueryParam("include_etl_templates") @DefaultValue(FALSE) boolean includeEtlTemplates
    ) {
        if (isAdminViewMode(viewMode)) {
            return etlGateWs_adm.readAllForClass(classId, wsQueryOptions, includeEtlTemplates);
        }
        return etlGateWs_mng.readAllForClass(classId, wsQueryOptions, includeEtlTemplates);
    }

    @GET
    @Path("{code}/")
    @Operation(
            summary = "Get ETL gate by code",
            description = "Get ETL gate by code",
            parameters = {
                    @Parameter(name = "code", in = ParameterIn.PATH, description = "Code of the ETL gate to retrieve"),
                    @Parameter(name = "include_etl_templates", in = ParameterIn.QUERY, description = "Whether to include ETL templates in the response. An ETL template is a special type of ETL gate that is used as a template for creating new gates. It is not associated with any class and is not executable. This parameter can be used to include or exclude ETL templates from the response")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the ETL gate or the gate does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("code") String code,
            @QueryParam("include_etl_templates") @DefaultValue(FALSE) boolean includeEtlTemplates
    ) {
        return etlGateWs_mng.read(code, includeEtlTemplates);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new ETL gate",
            description = "Create a new ETL gate",
            requestBody = @RequestBody(description = "ETL gate data to create", required = true, content = @Content(schema = @Schema(implementation = WsImportExportGateData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of ETL gate"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid ETL gate data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create ETL gates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )

    public Object create(
            WsImportExportGateData data
    ) {
        return etlGateWs_adm.create(data);
    }

    @PUT
    @Path("{code}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an existing ETL gate",
            description = "Update an existing ETL gate. The ETL gate to update is identified by the code provided in the path parameter. The request body should contain the updated data for the ETL gate",
            parameters = {
                    @Parameter(name = "code", in = ParameterIn.PATH, description = "Code of the ETL gate to update")
            },
            requestBody = @RequestBody(description = "Updated ETL gate data", required = true, content = @Content(schema = @Schema(implementation = WsImportExportGateData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of ETL gate"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid ETL gate data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update ETL gates or the specified ETL gate does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("code") String code,
            WsImportExportGateData data
    ) {
        return etlGateWs_adm.update(code, data);
    }

    @DELETE
    @Path("{code}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete an ETL gate",
            description = "Delete an ETL gate. The ETL gate to delete is identified by the code provided in the path parameter",
            parameters = {
                    @Parameter(name = "code", in = ParameterIn.PATH, description = "Code of the ETL gate to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of ETL gate"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete ETL gates or the specified ETL gate does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("code") String code
    ) {
        return etlGateWs_adm.delete(code);
    }
}
