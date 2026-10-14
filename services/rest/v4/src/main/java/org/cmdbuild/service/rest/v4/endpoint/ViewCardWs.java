package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.*;
import org.cmdbuild.cardfilter.CardFilterService;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.model.WsCardData;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.dao.utils.CmSorterUtils.parseSorter;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.utils.PositionOfUtils.handlePositionOfAndGetMeta;
import static org.cmdbuild.view.ViewService.JOIN_VIEW_ATTR_JOIN_ID;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.ViewCardWsCommand;
import org.cmdbuild.service.rest.v4.model.WsCardData;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.dao.utils.CmSorterUtils.parseSorter;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.utils.PositionOfUtils.handlePositionOfAndGetMeta;

@Path("views/{viewId}/cards/")
@Tag( name = "Cards", description = "Cards management")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ViewCardWs {

    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;
    private final ViewCardWsCommand command;

    public ViewCardWs(CardWsSerializationHelperv3 cardWsSerializationHelperv3, ViewCardWsCommand command) {
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
        this.command = checkNotNull(command);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a card",
            description = "Creates a card",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "409", description = "Card already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam("viewId") @Parameter(description = "Id of the view to create the card in") String viewId,
            @RequestBody(description = "Card data", required = true) WsCardData data
    ) {
        Card card = command.doCreate(viewId, data);
        return response(cardWsSerializationHelperv3.serializeCardWithValue(card));
    }

    @GET
    @Path("{" + CARD_ID + "}/")
    @Operation(
            summary = "Get a card",
            description = "Get a card by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam("viewId") @Parameter(description = "Id of the view to get the card from") String viewId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to get") String cardId
    ) {
        return response(cardWsSerializationHelperv3.serializeCardWithValue(command.doReadOne(viewId, cardId)));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get multiple cards",
            description = "Get multiple cards with filtering, sorting, and pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam("viewId") @Parameter(description = "Id of the view to get the cards from") String viewId,
            @QueryParam(FILTER) @Parameter(description = "Filter to apply to the cards") String filterStr,
            @QueryParam(SORT) @Parameter(description = "Sorting to apply to the cards") String sort,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(POSITION_OF) @Parameter(description = "Position of the requested element in the resultset", schema = @Schema(type = "long")) Long positionOf,
            @QueryParam(POSITION_OF_GOTOPAGE) @Parameter(description = "Go to the page of the requested element", schema = @Schema(type = "boolean")) @DefaultValue(TRUE) Boolean goToPage
    ) {
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withFilter(parseFilter(command.doGetFilterOrNull(filterStr)))//TODO map filter attribute names;
                .withSorter(parseSorter(sort))
                .withPaging(offset, limit)
                .withPositionOf(positionOf, goToPage)
                .build();
        PagedElements<Card> cards = command.doReadMany(viewId, queryOptions);
        return response(cards.stream().map(cardWsSerializationHelperv3::serializeCardWithValue).collect(toList()), cards.totalSize(), handlePositionOfAndGetMeta(queryOptions, cards));
    }

    @PUT
    @Path("{" + CARD_ID + "}/")
    @Operation(
            summary = "Update a card",
            description = "Updates a card by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("viewId") @Parameter(description = "Id of the view to update the card in") String viewId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to update") Long cardId,
            @RequestBody(description = "Card data (attribute values to update)", required = true) WsCardData data
    ) {
        Card card = command.doUpdate(viewId, cardId, data);
        return response(cardWsSerializationHelperv3.serializeCardWithValue(card));
    }

    @DELETE
    @Path("{" + CARD_ID + "}/")
    @Operation(
            summary = "Delete a card",
            description = "Deletes a card by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("viewId") @Parameter(description = "Id of the view to delete the card from") String viewId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to delete") Long cardId
    ) {
        command.doDelete(viewId, cardId);
        return success();
    }

    @GET
    @Path("/cards/{" + CARD_ID + "}/print/{file}")
    @Operation(
            summary = "Print a card",
            description = "Print a card",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful generation of card report"),
                    @ApiResponse( responseCode = "404", description = "Card not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler print(
            @PathParam("viewId") @Parameter(description = "Id of the view to print the card from") String viewId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to print") String cardId,
            @QueryParam(EXTENSION) @Parameter(description = "Report extension to generate") String extension
    ) {
        return command.doPrint(viewId, cardId, extension);
    }
}
