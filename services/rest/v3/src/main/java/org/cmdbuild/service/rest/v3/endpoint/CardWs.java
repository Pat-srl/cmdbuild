package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsCardData;

import org.cmdbuild.service.rest.v4.model.WsForDomainOptions;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;

@Path("classes/{" + CLASS_ID + "}/cards/")
@Tag(name = "Card", description = "Operations related to cards")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CardWs cardWs;

    public CardWs(org.cmdbuild.service.rest.v4.endpoint.CardWs cardWs) {
        this.cardWs = checkNotNull(cardWs);
    }

    @GET
    @Path("{" + CARD_ID + "}/")
    @Operation(
            summary = "Get a card",
            description = "Get a card",
            responses = { @ApiResponse(responseCode = "200", description = "Successful retrieval of card data") },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @QueryParam("includeModel") @DefaultValue(FALSE) Boolean includeModel,
            @QueryParam("includeWidgets") @DefaultValue(FALSE) Boolean includeWidgets,
            @QueryParam("includeStats") @DefaultValue(FALSE) Boolean includeStats,
            @QueryParam("infoOnly") @DefaultValue(FALSE) Boolean infoOnly) {
        return cardWs.readOne(classId, cardId, includeModel, includeWidgets, includeStats, infoOnly);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Query cards",
            description = "Query cards",
            parameters = {
                    @Parameter(name = CLASS_ID, description = "Name of the class to query"),
                    @Parameter(name = "functionValue", description = "Function value to use for query"),
                    @Parameter(name = "distinct", description = "Attribute to use for distinct query"),
                    @Parameter(name = "distinctIncludeNull", description = "Whether to include null values in distinct query"),
                    @Parameter(name = "count", description = "Attribute to use for count query")
            },
            requestBody = @RequestBody(description = "Query options"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of card data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(@PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
                           WsQueryOptions wsQueryOptions,
                           WsForDomainOptions wsForDomainOptions,
                           @QueryParam("functionValue") String selectFunctionValue,
                           @QueryParam("distinctIncludeNull") @DefaultValue(FALSE) Boolean distinctIncludeNull,
                           @QueryParam("distinct") String distinctAttribute,
                           @QueryParam("count") String countAttribute
    ) {
        return cardWs.readMany(classId, wsQueryOptions, wsForDomainOptions, selectFunctionValue, distinctIncludeNull, distinctAttribute, countAttribute);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new card",
            description = "Create a new card",
            parameters = {
                    @Parameter(name = CLASS_ID, description = "Name of the class to query"),
            },
            requestBody = @RequestBody(description = "Card data"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of card data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            WsCardData data
    ) {
        return cardWs.create(classId, data);
    }

    @PUT
    @Path("{" + CARD_ID + "}/")
    @Operation(
            summary = "Update a card",
            description = "Update a card",
            parameters = {
                    @Parameter(name = CLASS_ID, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, description = "ID of the card to query"),
            },
            requestBody = @RequestBody(description = "Card data"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of card data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            WsCardData data
    ) {
        return cardWs.update(classId, cardId, data);
    }

    @DELETE
    @Path("{" + CARD_ID + "}/")
    @Operation(
            summary = "Delete a card",
            description = "Delete a card",
            parameters = {
                    @Parameter(name = CLASS_ID, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, description = "ID of the card to query"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of card data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId
    ) {
        return cardWs.delete(classId, cardId);
    }

    @PUT
    @Path("")
    @Operation(
            summary = "Update multiple cards",
            description = "Update multiple cards",
            parameters = {
                    @Parameter(name = CLASS_ID, description = "Name of the class to query"),
            },
            requestBody = @RequestBody(description = "Card data"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of card data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object updateMany(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            WsCardData data,
            WsQueryOptions wsQueryOptions
    ) {
        return cardWs.updateMany(classId, data, wsQueryOptions);
    }

    @DELETE
    @Path("")
    @Operation(
            summary = "Delete multiple cards",
            description = "Delete multiple cards",
            parameters = {
                    @Parameter(name = CLASS_ID, description = "Name of the class to query"),
            },
            requestBody = @RequestBody(description = "Query options"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of card data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteMany(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to delete") String classId,
            WsQueryOptions wsQueryOptions
    ) {
        return cardWs.deleteMany(classId, wsQueryOptions);
    }
}
