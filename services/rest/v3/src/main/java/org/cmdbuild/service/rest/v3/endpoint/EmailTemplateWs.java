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
import org.cmdbuild.service.rest.v4.endpoint.EmailTemplateWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.EmailTemplateWs_Management;
import org.cmdbuild.service.rest.v4.model.WsEmailTemplateData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("email/templates/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "Email Templates", description = "Operations related to email templates")
public class EmailTemplateWs {

    private final EmailTemplateWs_Administration emailTemplateWs_adm;
    private final EmailTemplateWs_Management emailTemplateWs_mng;

    public EmailTemplateWs(EmailTemplateWs_Administration emailTemplateWs_adm, EmailTemplateWs_Management emailTemplateWs_mng) {
        this.emailTemplateWs_adm = checkNotNull(emailTemplateWs_adm);
        this.emailTemplateWs_mng = checkNotNull(emailTemplateWs_mng);
    }

    @GET
    @Path("by-class/{"+ CLASS_ID + "}")
    @Operation(
            summary = "Get email templates for a class",
            description = "Get email templates for a class. If the user has admin view permissions, all email templates for the class will be returned. Otherwise, only email templates for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'. If the user does not have permissions to view any email templates for the class, an empty list will be returned.",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "ID of the class to retrieve email templates for", required = true),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = SORT, in = ParameterIn.QUERY, description = "Sorting to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about email templates, such as the content of the template and the list of bindings" ),
                    @Parameter(name = "includeBindings", in = ParameterIn.QUERY, description = "Whether to include bindings in the response", schema = @Schema(type = "boolean"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email templates for the class"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view email templates for the class"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllForClass(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed,
            @QueryParam("includeBindings") @DefaultValue(FALSE) Boolean includeBindings
    ) {
        if (isAdminViewMode(viewMode)) {
            return emailTemplateWs_adm.readAllForClass(classId, filterStr, sort, limit, offset, detailed, includeBindings);
        }
        return emailTemplateWs_mng.readAllForClass(classId, filterStr, sort, limit, offset, detailed, includeBindings);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all email templates",
            description = "Get all email templates. If the user has admin view permissions, all email templates will be returned. Otherwise, only email templates for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'. If the user does not have permissions to view any email templates, an empty list will be returned.",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = SORT, in = ParameterIn.QUERY, description = "Sorting to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about email templates, such as the content of the template and the list of bindings" ),
                    @Parameter(name = "includeBindings", in = ParameterIn.QUERY, description = "Whether to include bindings in the response", schema = @Schema(type = "boolean"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email templates"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view email templates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed,
            @QueryParam("includeBindings") @DefaultValue(FALSE) Boolean includeBindings
    ) {
        if (isAdminViewMode(viewMode)) {
            return emailTemplateWs_adm.readAll(filterStr, sort, limit, offset, detailed, includeBindings);
        }
        return emailTemplateWs_mng.readAll(filterStr, sort, limit, offset, detailed, includeBindings);
    }

    @GET
    @Path("{templateId}/")
    @Operation(
            summary = "Get email template by id",
            description = "Get email template by id. If the user has admin view permissions, the email template will be returned if it exists, regardless of the user's management permissions for that email template. Otherwise, the email template will only be returned if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'. If the user does not have permissions to view the email template or the email template does not exist, a 403 Forbidden response will be returned.",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "templateId", in = ParameterIn.PATH, description = "ID of the email template to retrieve", required = true),
                    @Parameter(name = "includeBindings", in = ParameterIn.QUERY, description = "Whether to include bindings in the response", schema = @Schema(type = "boolean"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email template"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the email template or the email template does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("templateId") String id,
            @QueryParam("includeBindings") @DefaultValue(FALSE) Boolean includeBindings
    ) {
        return emailTemplateWs_mng.read(id, includeBindings);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new email template",
            description = "Create a new email template. The user must have admin permissions to create an email template. If the creation is successful, the created email template will be returned in the response.",
            requestBody = @RequestBody( description = "Data for the new email template", required = true, content = @Content(schema = @Schema(implementation = WsEmailTemplateData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of email template"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid email template data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create email templates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsEmailTemplateData data
    ) {
        return emailTemplateWs_adm.create(data);
    }

    @PUT
    @Path("{templateId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an email template",
            description = "Update an email template. The user must have admin permissions to update an email template. If the update is successful, the updated email template will be returned in the response.",
            parameters = {
                    @Parameter(name = "templateId", in = ParameterIn.PATH, description = "ID of the email template to update", required = true)
            },
            requestBody = @RequestBody(description = "Data to update the email template with", required = true, content = @Content(schema = @Schema(implementation = WsEmailTemplateData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of email template"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid email template data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update email templates or the email template does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("templateId") Long templateId,
            WsEmailTemplateData data
    ) {
        return emailTemplateWs_adm.update(templateId, data);
    }

    @DELETE
    @Path("{templateId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete an email template",
            description = "Delete an email template. The user must have admin permissions to delete an email template. If the deletion is successful, a success message will be returned in the response.",
            parameters = {
                    @Parameter(name = "templateId", in = ParameterIn.PATH, description = "ID of the email template to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of email template"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete email templates or the email template does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("templateId") Long templateId
    ) {
        return emailTemplateWs_adm.delete(templateId);
    }
}
