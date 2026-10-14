/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import com.google.common.base.Predicates;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlTemplateWsCommand;
import org.cmdbuild.service.rest.v4.model.WsEtlTemplateData;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.serializationhelpers.EtlTemplateSerializationHelper.filterAndApplySerialization;
import static org.cmdbuild.service.rest.common.serializationhelpers.EtlTemplateSerializationHelper.serializeDetailedTemplate;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;

/**
 * @author ldare
 */
@Path("administration/etl/templates/")
@Tags({
        @Tag( name = "ETL Templates", description = "APIs to manage ETL templates." ),
        @Tag( name = "Administration" )
})
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class EtlTemplateWs_Administration {

    private final EtlTemplateWsCommand command;

    public EtlTemplateWs_Administration(EtlTemplateWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all ETL templates",
            description = "Get all ETL templates",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation") ,
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object readAll(
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        List<EtlTemplate> etlTemplateList = command.doReadAll(Predicates.alwaysTrue());
        return response(paged(filterAndApplySerialization(etlTemplateList, wsQueryOptions)));
    }

    @GET
    @Path("by-class/{" + CLASS_ID + "}")
    @Operation(
            summary = "Get all ETL templates for a class",
            description = "Get all ETL templates for a class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL templates") ,
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object readAllForClass(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query")  String classId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam("include_related_domains") @DefaultValue(FALSE) @Parameter(description = "Include related domains") boolean includeRelatedDomains
    ) {
        List<EtlTemplate> etlTemplateList = command.doReadAllForClass(classId, includeRelatedDomains, Predicates.alwaysTrue());

        return response(paged(filterAndApplySerialization(etlTemplateList, wsQueryOptions)));
    }

    @GET
    @Path("by-process/{" + CLASS_ID + "}")
    @Operation(
            summary = "Get all ETL templates for a process",
            description = "Get all ETL templates for a process",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL templates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object readAllForProcess(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query")  String classId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam("include_related_domains") @DefaultValue(FALSE) @Parameter(description = "Include related domains") boolean includeRelatedDomains
    ) {
        List<EtlTemplate> etlTemplateList = command.doReadAllForProcess(classId, includeRelatedDomains, Predicates.alwaysTrue());

        return response(paged(filterAndApplySerialization(etlTemplateList, wsQueryOptions)));
    }

    @GET
    @Path("by-view/{viewId}")
    @Operation(
            summary = "Get all ETL templates for a view",
            description = "Get all ETL templates for a view",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL templates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object readAllForView(
            @PathParam("viewId") @Parameter(description = "ID of the view to query") String viewId, @BeanParam WsQueryOptions wsQueryOptions
    ) {
        List<EtlTemplate> etlTemplateList = command.doReadAllForView(viewId, Predicates.alwaysTrue());

        return response(paged(filterAndApplySerialization(etlTemplateList, wsQueryOptions)));
    }

    @GET
    @Path("{templateId}/")
    @Operation(
            summary = "Get an ETL template by id or code",
            description = "Obtain details of a specific ETL template",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL template data") ,
                    @ApiResponse(responseCode = "404", description = "ETL template not found") ,
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object readOne(
            @PathParam("templateId") @Parameter(description = "ID or code of the ETL template to query") String idOrCode
    ) {
        EtlTemplate template = command.doReadOne(idOrCode);
        return response(serializeDetailedTemplate(template));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new ETL template",
            description = "Create a new ETL template",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of ETL template") ,
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Etl Template data", required = true, content = @Content(schema = @Schema(implementation = WsEtlTemplateData.class))) WsEtlTemplateData data
    ) {
        EtlTemplate etlTemplate = command.doCreate(data);
        return response(serializeDetailedTemplate(etlTemplate));
    }

    @PUT
    @Path("{templateId}/")
    @Operation(
            summary = "Update an existing ETL template",
            description = "Update an existing ETL template",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of ETL template"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("templateId") @Parameter(description = "ID of the ETL template to update") String templateId,
            @RequestBody(description = "Etl Template data", required = true, content = @Content(schema = @Schema(implementation = WsEtlTemplateData.class))) WsEtlTemplateData data
    ) {
        EtlTemplate etlTemplate = command.doUpdate(templateId, data);
        return response(serializeDetailedTemplate(etlTemplate));
    }

    @DELETE
    @Path("{templateId}/")
    @Operation(
            summary = "Delete an ETL template",
            description = "Delete a specific ETL template",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of ETL template"),
                    @ApiResponse(responseCode = "404", description = "The ETL template was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("templateId") @Parameter(description = "ID of the ETL template to delete") String templateName
    ) {
        command.doDelete(templateName);
        return success();
    }
}
