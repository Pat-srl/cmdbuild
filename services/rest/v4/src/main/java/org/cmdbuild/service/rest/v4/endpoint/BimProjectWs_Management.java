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
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.*;
import org.cmdbuild.bim.BimObject;
import org.cmdbuild.bim.BimProject;
import org.cmdbuild.bim.BimProjectExt;
import org.cmdbuild.service.rest.common.serializationhelpers.BimProjectSerializationHelper;
import org.cmdbuild.service.rest.v4.command.BimProjectWsCommand;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.serializationhelpers.BimProjectSerializationHelper.serializeProjectAndObject;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Path("bim/projects/")
@Tag( name = "BIM", description = "BIM Projects" )
@Produces(APPLICATION_JSON)
@Component
public class BimProjectWs_Management {

    private final BimProjectWsCommand command;

    public BimProjectWs_Management(BimProjectWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get projects and objects",
            description = "Returns sorted serialized projects and objects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = BimProjectExt.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} ) }
    )
    public Object getAll() {
        return response(command.doGetAll().stream().filter(BimProject::isActive).map(BimProjectSerializationHelper::serializeProjectAndObject));
    }

    /**
     * Gets BIM object by project and global ID
     */
    @GET
    @Path("{projectId}/values/{globalId}")
    @Operation(
            summary = "Gets BIM object by project and global ID",
            description = "Gets BIM object by project and global ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bim object retrieved successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = BimObject.class))),
                    @ApiResponse(responseCode = "404", description = "BIM object not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getValue(
            @PathParam("projectId") @Parameter(description = "Id of Project") Long projectId,
            @PathParam("globalId") @Parameter(description = "Global Id") String globalId,
            @QueryParam("if_exists") @Parameter(description = "If true, return only if exists") @DefaultValue(FALSE) Boolean ifExists
    ) {
        BimObject value = command.doGetValue(projectId, globalId);
        if (ifExists && value == null) {
            return response(map("exists", false));
        }
        checkNotNull(value, "bim value not found for gid =< %s >", globalId);
        return response(map(
                "_id", value.getId(),
                "ownerType", value.getOwnerClassId(),
                "ownerId", value.getOwnerCardId(),
                "projectId", value.getProjectId(),
                "globalId", value.getGlobalId()
        ).accept((m) -> {
            if (ifExists) {
                m.put("exists", true);
            }
        }));
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get project by id",
            description = "Returns serialized project by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = BimProjectExt.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getOne(
            @PathParam(ID) @Parameter(description = "Id of Project to query") Long id
    ) {
        BimProjectExt projectExt = command.doGetOne(id);
        if (projectExt.isActive()) {
            return response(serializeProjectAndObject(projectExt));
        }
        return success();
    }

    @GET
    @Path("{id}/file")
    @Operation(
            summary = "Download project file",
            description = "Download project file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/octet-stream")),
                    @ApiResponse(responseCode = "404", description = "The project file was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public DataHandler downloadIfcFile(
            @PathParam(ID) @Parameter(description = "Id of Project to query") Long id,
            @QueryParam("ifcFormat") @Parameter(description = "Ifc format to download") @Nullable String ifcFormat,
            @QueryParam("bimFormat") @Parameter(description = "Bim format to download") String bimFormat
    ) {
        return command.doDownloadIfcFile(id, ifcFormat, bimFormat);
    }
}
