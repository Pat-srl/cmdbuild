package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.model.WsCardData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("views/{viewId}/cards/")
@Tag( name = "Cards", description = "Cards management")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ViewCardWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ViewCardWs viewCardWs;

    public ViewCardWs(org.cmdbuild.service.rest.v4.endpoint.ViewCardWs viewCardWs) {
        this.viewCardWs = checkNotNull(viewCardWs);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a card",
            description = "Creates a card",
            parameters = { @Parameter(name = "data", description = "Card data", required = true)},
            requestBody = @RequestBody(description = "Card data", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam("viewId") String viewId,
            WsCardData data
    ) {
        return viewCardWs.create(viewId, data);
    }

    @GET
    @Path("{" + CARD_ID + "}/")
    @Operation(
            summary = "Get a card",
            description = "Get a card by id",
            parameters = {
                    @Parameter(name = "viewId", description = "View id", required = true),
                    @Parameter(name = CARD_ID, description = "Card id", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam("viewId") String viewId,
            @PathParam(CARD_ID) String cardId
    ) {
        return viewCardWs.readOne(viewId, cardId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get multiple cards",
            description = "Get multiple cards with filtering, sorting, and pagination",
            parameters = {
                    @Parameter(name = "viewId", description = "View id", required = true),
                    @Parameter(name = FILTER, description = "Filter to apply to the query"),
                    @Parameter(name = SORT, description = "How to order results"),
                    @Parameter(name = LIMIT, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = POSITION_OF, description = "Position of the card in the list of cards"),
                    @Parameter(name = POSITION_OF_GOTOPAGE, description = "Whether to go to the page specified in the positionOf parameter", schema = @Schema(type = "boolean", defaultValue = TRUE))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam("viewId") String viewId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(POSITION_OF) Long positionOf,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage
    ) {
        return viewCardWs.readMany(viewId, filterStr, sort, limit, offset, positionOf, goToPage);
    }

    @PUT
    @Path("{" + CARD_ID + "}/")
    @Operation(
            summary = "Update a card",
            description = "Updates a card by id",
            parameters = {
                    @Parameter(name = "viewId", description = "View id", required = true),
                    @Parameter(name = CARD_ID, description = "Card id", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("viewId") String viewId,
            @PathParam(CARD_ID) Long cardId, WsCardData data
    ) {
        return viewCardWs.update(viewId, cardId, data);
    }

    @DELETE
    @Path("{" + CARD_ID + "}/")
    @Operation(
            summary = "Delete a card",
            description = "Deletes a card by id",
            parameters = {
                    @Parameter(name = "viewId", description = "View id", required = true),
                    @Parameter(name = CARD_ID, description = "Card id", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("viewId") String viewId,
            @PathParam(CARD_ID) Long cardId
    ) {
        return viewCardWs.delete(viewId, cardId);
    }

    @GET
    @Path("/cards/{" + CARD_ID + "}/print/{file}")
    @Operation(
            summary = "Print a card",
            description = "Print a card",
            parameters = {
                    @Parameter(name = "viewId", description = "View id", required = true),
                    @Parameter(name = CARD_ID, description = "Card id", required = true),
                    @Parameter(name = EXTENSION, description = "File extension", required = true)
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful generation of card report"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler print(
            @PathParam("viewId") String viewId,
            @PathParam(CARD_ID) String cardId,
            @QueryParam(EXTENSION) String extension
    ) {
        return viewCardWs.print(viewId, cardId, extension);
    }
}
