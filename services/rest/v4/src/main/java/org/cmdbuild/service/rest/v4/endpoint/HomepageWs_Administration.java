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
import org.cmdbuild.homepage.HomepageData;
import org.cmdbuild.homepage.HomepageService;
import org.cmdbuild.service.rest.common.serializationhelpers.HomepageSerializationHelper;
import org.cmdbuild.service.rest.v4.command.HomepageWsCommand;
import org.cmdbuild.service.rest.v4.model.WsHomepageData;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.lowagie.text.ElementTags.OFFSET;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 *
 * @author mbuzzulini
 */
@Path("administration/homepages/")
@Tags({
        @Tag(name = "Homepage", description = "APIs to manage the homepage"),
        @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class HomepageWs_Administration {

    private final HomepageService homepageService;
    private final HomepageSerializationHelper helper;
    private final HomepageWsCommand command;

    public HomepageWs_Administration(ObjectTranslationService objectTranslationService, HomepageService homepageService, HomepageWsCommand command) {
        this.homepageService = checkNotNull(homepageService);
        this.helper = new HomepageSerializationHelper(objectTranslationService);
        this.command = command;
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all homepages",
            description = "Obtain a list of all homepages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of homepages retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object getAll(
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "If true includes full details in the response") Boolean detailed,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response") Integer limit,
            @QueryParam(OFFSET) @Parameter(description = "A long value to set an offset in the resultset") Integer offset
    ) {
        List<HomepageData> listHomepage = command.doGetAll(homepageService::getAll);
        return response(helper.applySerializationToListHomepageData(listHomepage, detailed, limit, offset));
    }

    @GET
    @Path("{id}/")
    @Operation(
            summary = "Get a specific homepage by ID",
            description = "Get a specific homepage by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Homepage retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Homepage not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object getById(
            @PathParam(ID) @Parameter(description = "Id or Codde of the homepage") String idOrCode
    ) {
        HomepageData homepage = command.doReadOne(idOrCode, homepageService::getByIdOrCode);
        return response(helper.serializeDetailedHomepageData(homepage));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new homepage",
            description = "Create a new homepage",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Homepage created successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Data for the new homepage", required = true) WsHomepageData data
    ) {
        HomepageData homepage = command.doCreate(data);
        return response(helper.serializeDetailedHomepageData(homepage));
    }

    @PUT
    @Path("{id}/")
    @Operation(
            summary = "Update an existing homepage",
            description = "Update an existing homepage",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Homepage updated successfully", content = @Content(schema = @Schema(implementation = WsHomepageData.class))),
                    @ApiResponse(responseCode = "404", description = "Homepage not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(ID) @Parameter(description = "Id of the homepage to update") Long id,
            @RequestBody(description = "Updated data for the homepage", required = true) WsHomepageData data
    ) {
        HomepageData homepage = command.doUpdate(id, data);
        return response(helper.serializeDetailedHomepageData(homepage));
    }

    @DELETE
    @Path("{id}/")
    @Operation(
            summary = "Delete a homepage",
            description = "Delete a homepage",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Homepage deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Homepage not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(ID) @Parameter(description = "Id of the homepage to delete") Long id
    ) {
        command.doDelete(id);
        return success();
    }
}
