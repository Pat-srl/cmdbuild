/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.v4.command.CustomPageWsCommand;
import org.cmdbuild.service.rest.v4.model.WsCustomPageData;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.custompage.CustomPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.serializationhelpers.CustomPageSerializationHelper.serializeCustomPage;
import static org.cmdbuild.service.rest.common.serializationhelpers.CustomPageSerializationHelper.serializeCustomPageList;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("administration/custompages/")
@Tags({
        @Tag( name = "Custom Pages", description = "APIs to manage custom pages." ),
        @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class CustomPageWs_Administration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CustomPageService customPageService;
    private final ObjectTranslationService objectTranslationService;
    private final CustomPageWsCommand command;

    public CustomPageWs_Administration(CustomPageService customPageService, ObjectTranslationService objectTranslationService, CustomPageWsCommand command) {
        this.customPageService = checkNotNull(customPageService);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "List all custom pages for current user",
            description = "Obtain a list of all custom pages for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = UiComponentInfo.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object list() {
        logger.debug("list all custom pages for current user");
        List<UiComponentInfo> listUiComponent = command.doList(customPageService::getAll);
        return response(serializeCustomPageList(listUiComponent, objectTranslationService));
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get a specific custom page",
            description = "Obtain details of a specific custom page",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = UiComponentInfo.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required"),
                    @ApiResponse(responseCode = "404", description = "Custom page not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object get(
            @PathParam(ID) @Parameter(description = "Id of Custom page to get") Long id
    )  {
        UiComponentInfo customPage = command.doGet(id, customPageService::get);
        return response(serializeCustomPage(customPage, objectTranslationService));
    }

    @DELETE
    @Path("{id}")
    @Operation(
            summary = "Delete a custom page",
            description = "Delete a custom page",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required"),
                    @ApiResponse(responseCode = "404", description = "Custom page not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object delete (
            @PathParam(ID) @Parameter(description = "Id of Custom page to delete") Long id
    ) {
        command.doDelete(id);
        return success();
    }

    @DELETE
    @Path("{id}/{targetDevice}")
    @Operation(
            summary = "Delete a custom page for a specific target device",
            description = "Delete a custom page for a specific target device",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required"),
                    @ApiResponse(responseCode = "404", description = "Custom page or target device not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object deleteForTargetDevice(
            @PathParam("id") @Parameter(description = "Id of Custom page to delete") Long id,
            @PathParam("targetDevice") @Parameter(description = "Target device for which to delete the custom page") TargetDevice targetDevice
    ) {
        UiComponentInfo component = command.doDeleteForTargetDevice(id, targetDevice);
        return response(serializeCustomPage(component, objectTranslationService));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            requestBody = @RequestBody(description = "Custom page definition (optional 'data' part, JSON) and the files composing the custom page (all other parts)", required = false,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsCustomPageData.class)),
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
                    })),
            summary = "Create a custom page",
            description = "Create a new custom page",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = UiComponentInfo.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(
            @Multipart("-data") List<DataHandler> files,
            @Multipart(value = "data", required = false) WsCustomPageData data,
            @QueryParam("merge") @Parameter(description = "Merge with existing custom page") @DefaultValue(FALSE) Boolean merge
    ) {
        UiComponentInfo customPage = command.doCreate(files, data, merge);
        return response(serializeCustomPage(customPage, objectTranslationService));
    }

    @PUT
    @Path("{id}")
    @Operation(
            requestBody = @RequestBody(description = "Custom page definition (optional 'data' part, JSON) and the files composing the custom page (all other parts)", required = false,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsCustomPageData.class)),
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
                    })),
            summary = "Update a custom page",
            description = "Update an existing custom page",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = UiComponentInfo.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(ID) @Parameter(description = "Id of Custom page to update") Long id,
            @Multipart("-data") List<DataHandler> files,
            @Multipart(value = "data|DEFAULT", required = false) WsCustomPageData data
    ) {
        UiComponentInfo contextMenuComponent = command.doUpdate(id, files, data);
        return response(serializeCustomPage(contextMenuComponent, objectTranslationService));
    }

    @GET
    @Path("{id}/{targetDevice}/{file}|{id}/{targetDevice}")
    @Operation(
            summary = "Download a custom page for a specific target device",
            description = "Download a custom page for a specific target device",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Custom page or file not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public DataHandler download(
            @PathParam(ID) @Parameter(description = "Id of Custom page to download") Long id,
            @PathParam("targetDevice") @Parameter(description = "Target device for which to download the custom page") TargetDevice targetDevice
    ) {
        return command.doDownload(id, targetDevice);
    }
}
