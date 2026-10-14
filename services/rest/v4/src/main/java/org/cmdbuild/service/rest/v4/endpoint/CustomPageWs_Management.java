/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.service.rest.v4.command.CustomPageWsCommand;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.custompage.CustomPageService;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.serializationhelpers.CustomPageSerializationHelper.serializeCustomPage;
import static org.cmdbuild.service.rest.common.serializationhelpers.CustomPageSerializationHelper.serializeCustomPageList;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("custompages/")
@Tag( name = "Custom Pages")
@Produces(APPLICATION_JSON)
@Component
public class CustomPageWs_Management {

    private final CustomPageService customPageService;
    private final ObjectTranslationService objectTranslationService;
    private final CustomPageWsCommand command;

    public CustomPageWs_Management(CustomPageService customPageService, ObjectTranslationService objectTranslationService, CustomPageWsCommand command) {
        this.customPageService = checkNotNull(customPageService);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all custom pages for the current user and device",
            description = "Obtain a list of all custom pages available for the current user and device",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of custom pages", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = UiComponentInfo.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    public Object list() {
        List<UiComponentInfo> listUiComponent = command.doList(customPageService::getActiveForCurrentUserAndDevice);
        return response(serializeCustomPageList(listUiComponent, objectTranslationService));
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get a custom page for the current user",
            description = "Get a custom page by its identifier for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of custom page data", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = UiComponentInfo.class))),
                    @ApiResponse(responseCode = "404", description = "The custom page was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    public Object get(
            @PathParam(ID) @Parameter(description = "Id of custom page", schema = @Schema(type = "long")) Long id
    ) {
        UiComponentInfo customPage = command.doGet(id, customPageService::getForUser);
        return response(serializeCustomPage(customPage, objectTranslationService));
    }
}
