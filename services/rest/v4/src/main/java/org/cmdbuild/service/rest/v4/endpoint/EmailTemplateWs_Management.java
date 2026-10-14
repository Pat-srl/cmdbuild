/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.service.rest.v4.command.EmailTemplateWsCommand;
import org.cmdbuild.services.serialization.EmailTemplateSerializationHelper;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author schursin
 */
@Path("email/templates/")
@Tag(name = "Email Templates")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class EmailTemplateWs_Management {

    private final EmailTemplateService emailTemplateService;
    private final EmailTemplateSerializationHelper emailTemplateSerializationHelper;
    private final EmailTemplateWsCommand command;

    public EmailTemplateWs_Management(EmailTemplateService emailTemplateService, EmailTemplateSerializationHelper emailTemplateSerializationHelper, EmailTemplateWsCommand command) {
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.command = checkNotNull(command);
        this.emailTemplateSerializationHelper = checkNotNull(emailTemplateSerializationHelper);
    }

    @GET
    @Path("by-class/{" + CLASS_ID + "}")
    @Operation(
            summary = "Get all email templates for a class",
            description = "Get all email templates for a class",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful retrieval of email templates"),
                    @ApiResponse( responseCode = "404", description = "The class was not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAllForClass(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @QueryParam(FILTER) @Parameter(description = "How to filter results", schema = @Schema(ref = "DefaultEmailFilter")) String filterStr,
            @QueryParam(SORT) @Parameter(description = "How to order results", schema = @Schema(ref = "DefaultSortExample")) String sort,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) Boolean detailed,
            @QueryParam("includeBindings") @DefaultValue(FALSE) @Parameter(description = "Include bindings in the response", schema = @Schema(type = "boolean")) Boolean includeBindings
    ) {
        checkNotBlank(classId);
        List<EmailTemplate> emailTemplateList = command.doReadAllForClass(emailTemplateService::getAllActive, filterStr, sort, classId);
        return response(paged(emailTemplateList.stream().map(c -> emailTemplateSerializationHelper.serializeTemplate(c, detailed, includeBindings)).collect(toList()), offset, limit));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all email templates",
            description = "Get all email templates",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful retrieval of email templates"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(FILTER) @Parameter(description = "How to filter results", schema = @Schema(ref = "DefaultEmailFilter")) String filterStr,
            @QueryParam(SORT) @Parameter(description = "How to order results", schema = @Schema(ref = "DefaultSortExample")) String sort,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) Boolean detailed,
            @QueryParam("includeBindings") @DefaultValue(FALSE) @Parameter(description = "Include bindings in the response", schema = @Schema(type = "boolean")) Boolean includeBindings
    ) {
        List<EmailTemplate> emailTemplateList = command.doReadAll(emailTemplateService::getAllActive, filterStr, sort);
        return response(paged(emailTemplateList.stream().map(c -> emailTemplateSerializationHelper.serializeTemplate(c, detailed, includeBindings)).collect(toList()), offset, limit));
    }

    @GET
    @Path("{templateId}/")
    @Operation(
            summary = "Get a specific email template",
            description = "Get a specific email template",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful retrieval of email template"),
                    @ApiResponse( responseCode = "404", description = "The email template was not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("templateId") @Parameter(description = "Id of the email template", schema = @Schema(type = "string")) String id,
            @QueryParam("includeBindings") @DefaultValue(FALSE) @Parameter(description = "Include bindings in the response", schema = @Schema(type = "boolean")) Boolean includeBindings
    ) {
        EmailTemplate emailTemplate = command.doRead(id);
        return response(emailTemplateSerializationHelper.serializeTemplate(emailTemplate, true, includeBindings));
    }
}
