/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.corecomponents.CoreComponent;
import org.cmdbuild.corecomponents.CoreComponentService;
import org.cmdbuild.corecomponents.CoreComponentType;
import org.cmdbuild.service.rest.v4.command.CoreComponentWsCommand;
import org.cmdbuild.service.rest.v4.model.WsCoreComponentData;
import org.cmdbuild.services.serialization.CoreComponentSerializationHelper;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CORECOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.CoreComponentSerializationHelper.applySerializationToCoreComponent;
import static org.cmdbuild.services.serialization.CoreComponentSerializationHelper.serializeDetails;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("administration/components/core/{"+TYPE+"}/")
@Tags({
        @Tag(name = "Core Components", description = "APIs to manage core components."),
        @Tag(name = "Administration")
})
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CoreComponentWs_Administration {

    private final CoreComponentService coreComponentService;
    private final CoreComponentWsCommand command;

    public CoreComponentWs_Administration(CoreComponentService coreComponentService, CoreComponentWsCommand command) {
        this.coreComponentService = checkNotNull(coreComponentService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{" + CODE + "}")
    @Operation(
            summary = "Get a core component by code",
            description = "Obtain details of a specific core component",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "type of the core component")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of core component data"),
                    @ApiResponse(responseCode = "404", description = "Core component not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_CORECOMPONENTS_VIEW_AUTHORITY)
    public Object get(
            @PathParam(CODE) @Parameter(description = "Code of the core component") String code
    ) {
        CoreComponent coreComponent = command.doGet(code, coreComponentService::getComponent);
        return response(applySerializationToCoreComponent(coreComponent));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get core components",
            description = "Obtain the list oc core components",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of core component data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_CORECOMPONENTS_VIEW_AUTHORITY)
    public Object listByType(
            @PathParam(TYPE) @Parameter(description = "Type of Core Component") CoreComponentType type,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response") Boolean detailed
    ) {
        List<CoreComponent> listCoreComponent = command.doListByType(type);
        return response(list(listCoreComponent).map(detailed ? CoreComponentSerializationHelper::serializeDetails : CoreComponentSerializationHelper::serializeInfo));
    }

    @DELETE
    @Path("{"+CODE+"}")
    @Operation(
            summary = "Delete a core component",
            description = "Delete a specific core component",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "type of the core component")
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of core component"),
                    @ApiResponse(responseCode = "404", description = "Core component not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(CODE) @Parameter(description = "Code of the core component") String code
    ) {
        command.doDelete(code);
        return success();
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a core component",
            description = "Create a new core component",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of core component"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY)
    public Object create(
            @PathParam(TYPE) @Parameter(description = "Type of Core Component") CoreComponentType type,
            @RequestBody(description = "Core component data", required = true) WsCoreComponentData data
    ) {
        CoreComponent coreComponent = command.doCreate(type, data);
        return response(serializeDetails(coreComponent));
    }

    @PUT
    @Path("{"+CODE+"}")
    @Operation(
            summary = "Updates core components and returns serialized details",
            description = "Updates core components and returns serialized details",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful update of core component"),
                    @ApiResponse(responseCode = "404", description = "Core component not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(CODE) @Parameter(description = "Code of the core component") String code,
            @RequestBody(description = "Updated core component data", required = true) WsCoreComponentData data
    ) {
        CoreComponent coreComponent = command.doUpdate(code, data);
        return response(serializeDetails(coreComponent));
    }
}
