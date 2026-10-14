/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

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
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlGateWsCommand;
import org.cmdbuild.service.rest.v4.model.WsImportExportGateData;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.dao.utils.CmFilterProcessingUtils.mapFilter;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CODE;
import static org.cmdbuild.service.rest.v4.command.WsUtils.filterSerializations;
import static org.cmdbuild.service.rest.v4.serializationhelpers.EtlGateSerializationHelper.serializeDetailedGate;
import static org.cmdbuild.service.rest.v4.serializationhelpers.EtlGateSerializationHelper.serializeGate;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * @author ldare
 */
@Path("administration/etl/gates/")
@Tags({
        @Tag( name = "ETL Gates", description = "APIs to manage ETL gates." ),
        @Tag( name = "Administration" )
})
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class EtlGateWs_Administration {

    private final EtlTemplateService etlTemplateService;
    private final EtlGateWsCommand command;

    public EtlGateWs_Administration(EtlTemplateService etlTemplateService, EtlGateWsCommand command) {
        this.etlTemplateService = checkNotNull(etlTemplateService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all ETL gates",
            description = "Get all ETL gates",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL gates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object readAll(
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam("include_etl_templates") @Parameter(description = "Include etl templates") @DefaultValue(FALSE) boolean includeEtlTemplates
    ) {
        List<EtlGate> etlGateList = command.doReadAll();
        return response(paged(list(etlGateList).map(e -> serializeGate(e, wsQueryOptions.isDetailed(), includeEtlTemplates, etlTemplateService)).withOnly(mapFilter(wsQueryOptions.getQuery().getFilter())), wsQueryOptions.getQuery()));
    }

    @GET
    @Path("by-class/{" + CLASS_ID + "}")
    @Operation(
            summary = "Get all ETL gates for a given class",
            description = "Get all ETL gates for a given class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL gates"),
                    @ApiResponse(responseCode = "404", description = "The class was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object readAllForClass(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query")  String classId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam("include_etl_templates") @Parameter(description = "Include etl templates") @DefaultValue(FALSE) boolean includeEtlTemplates
    ) {
        FluentList<EtlGate> templist = command.doReadAllForClass(classId);

        List<FluentMap<String, Object>> listEtlGate = templist.map(e -> serializeGate(e, wsQueryOptions.isDetailed(), includeEtlTemplates, etlTemplateService));

        listEtlGate = filterSerializations(listEtlGate, wsQueryOptions);
        return response(paged(listEtlGate, wsQueryOptions.getQuery().getOffset(), wsQueryOptions.getQuery().getLimit()));
    }

    @GET
    @Path("{code}/")
    @Operation(
            summary = "Get an ETL gate by code",
            description = "Obtain details of a specific ETL gate",
            parameters = {
                    @Parameter(name = CODE, description = "Code of the ETL gate", schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL gate data"),
                    @ApiResponse(responseCode = "404", description = "The ETL gate was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object read(
            @PathParam(CODE) @Parameter(description = "Code of the ETL gate") String code,
            @QueryParam("include_etl_templates") @Parameter(description = "Include etl templates") @DefaultValue(FALSE) boolean includeEtlTemplates
    ) {
        EtlGate gate = command.doRead(code);
        return response(serializeGate(gate, true, includeEtlTemplates, etlTemplateService));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new ETL gate",
            description = "Create a new ETL gate",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of ETL gate"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Import Export Gate data", required = true, content = @Content(schema = @Schema(implementation = WsImportExportGateData.class))) WsImportExportGateData data
    ) {
        EtlGate etlGate = command.doCreate(data);
        return response(serializeDetailedGate(etlGate));
    }

    @PUT
    @Path("{code}/")
    @Operation(
            summary = "Update an existing ETL gate",
            description = "Update an existing ETL gate",
            parameters = {
                    @Parameter(name = CODE, description = "Code of the ETL gate", schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of ETL gate"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(CODE) @Parameter(description = "Code of the ETL gate") String code,
            @RequestBody(description = "Import Export Gate data", required = true, content = @Content(schema = @Schema(implementation = WsImportExportGateData.class))) WsImportExportGateData data
    ) {
        EtlGate etlGate = command.doUpdate(code, data);
        return response(serializeDetailedGate(etlGate));
    }

    @DELETE
    @Path("{code}/")
    @Operation(
            summary = "Delete an ETL gate",
            description = "Delete a specific ETL gate",
            parameters = {
                    @Parameter(name = CODE, description = "Code of the ETL gate", schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of ETL gate"),
                    @ApiResponse(responseCode = "404", description = "The ETL gate was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("code") @Parameter(description = "Code of the ETL gate") String code
    ) {
        command.doDelete(code);
        return success();
    }
}
