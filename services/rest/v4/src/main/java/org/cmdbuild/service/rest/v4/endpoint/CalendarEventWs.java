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
import jakarta.ws.rs.*;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.HistorySerializationHelper;
import org.cmdbuild.service.rest.v4.command.CalendarEventWsCommand;
import org.cmdbuild.service.rest.v4.model.WsEventData;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_CARD;
import static org.cmdbuild.email.Email.EMAIL_ATTR_CARD;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import static org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper.CAL_ATTR_MAPPING;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.utils.PositionOfUtils.handlePositionOfAndGetMeta;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;

@Path("calendar/events/")
@Tag(name = "Calendar Events", description = "Calendar Events")
@Produces(APPLICATION_JSON)
@Component
public class CalendarEventWs {

    private final CalendarWsSerializationHelper calendarWsSerializationHelper;
    private final HistorySerializationHelper historySerializationHelper;
    private final CardMapperService cardMapperService;
    private final DaoService daoService;
    private final CalendarEventWsCommand command;

    public CalendarEventWs(CalendarWsSerializationHelper calendarWsSerializationHelper,
                           HistorySerializationHelper historySerializationHelper,
                           CardMapperService cardMapperService,
                           DaoService daoService,
                           CalendarEventWsCommand command) {
        this.calendarWsSerializationHelper = checkNotNull(calendarWsSerializationHelper);
        this.historySerializationHelper = checkNotNull(historySerializationHelper);
        this.cardMapperService = checkNotNull(cardMapperService);
        this.daoService = checkNotNull(daoService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{eventId}")
    @Operation(
            summary = "Get the data of a calendar event",
            description = "Obtain the details of a specific calendar event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar event data"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar event"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readOne(
            @PathParam(EVENT_ID) @Parameter(  description = "Id of the event to attach the file to" , schema = @Schema(type = "long")) Long eventId,
            @QueryParam("includeStats") @DefaultValue(FALSE) @Parameter(description = "Include attachment and email stats", schema = @Schema(type = "boolean")) Boolean includeStats
    ) {
        CalendarEvent event = command.doReadOne(eventId);
        return response(calendarWsSerializationHelper.serializeDetailedEvent(event).accept(m -> {
            if (includeStats) {
                m.put("_attachment_count", daoService.selectCount().from(DMS_MODEL_PARENT_CLASS).where(DOCUMENT_ATTR_CARD, EQ, event.getId()).getCount(), //TODO: duplicate code, improve this
                        "_email_count", daoService.selectCount().from(EMAIL_CLASS_NAME).where(EMAIL_ATTR_CARD, EQ, event.getId()).getCount());
            }
        }));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get a list of calendar events",
            description = "Retrieve a list of calendar events with optional filtering, sorting, and pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar events list"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar events"),
                    @ApiResponse(responseCode = "404", description = "No calendar events found matching the query parameters"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readMany(
            @QueryParam(FILTER) @Parameter(description = "How to filter results", schema = @Schema(ref = "DefaultCalendarEventsFilterExample")) String filterStr,
            @QueryParam(SORT) @Parameter(description = "How to order results", schema = @Schema(ref = "DefaultSortExample")) String sort,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) Boolean detailed,
            @QueryParam(POSITION_OF) @Parameter(description = "Position of the requested element in the resultset", schema = @Schema(type = "long")) Long positionOf,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) @Parameter(description = "Go to the page of the requested element", schema = @Schema(type = "boolean")) Boolean goToPage
    ) {//TODO improve this, auto processing of params in dao query options
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder().withFilter(filterStr).withSorter(sort).withPositionOf(positionOf, goToPage).withPaging(offset, limit).build().mapAttrNames(CAL_ATTR_MAPPING);
        PagedElements<CalendarEvent> events = command.doReadMany(queryOptions);
        return response(events.map(detailed ? calendarWsSerializationHelper::serializeDetailedEvent : calendarWsSerializationHelper::serializeEvent), handlePositionOfAndGetMeta(queryOptions, events));
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create a new calendar event",
            description =  "Create a new calendar event with the provided data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of calendar event") ,
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to create a new calendar event"),
                    @ApiResponse(responseCode = "409", description = "Calendar event with the same ID already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object createUserEvent(
            @RequestBody(description = "Calendar event data", required = true) WsEventData data
    ) {
        CalendarEvent event = command.doCreateUserEvent(data);
        return response(calendarWsSerializationHelper.serializeDetailedEvent(event));
    }

    @PUT
    @Path("{eventId}")
    @Operation(
            summary =  "Update an existing calendar event",
            description =  "Update the details of an existing calendar event identified by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of calendar event") ,
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to update the requested calendar event"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object update(
            @PathParam(EVENT_ID) @Parameter(description = "Id of event", schema = @Schema(type = "long")) Long eventId,
            @RequestBody(description = "Calendar event data", required = true) WsEventData data
    ) {
        CalendarEvent event = command.doUpdate(eventId, data);
        return response(calendarWsSerializationHelper.serializeDetailedEvent(event));
    }

    @DELETE
    @Path("{eventId}/")
    @Operation(
            summary =  "Delete a calendar event",
            description =  "Delete an existing calendar event identified by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of calendar event"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to delete the requested calendar event"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object delete(
            @PathParam(EVENT_ID) @Parameter(description = "Id of event", schema = @Schema(type = "long")) Long eventId
    ) {
        command.doDelete(eventId);
        return success();
    }

    @GET
    @Path("{eventId}/history")
    @Operation(
            summary =  "Get the history of a calendar event",
            description =  "Retrieve the history of changes made to a specific calendar event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar event history"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar event history"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getHistory(
            @PathParam(EVENT_ID) @Parameter(description = "Id of event", schema = @Schema(type = "long")) Long eventId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam(TYPES) @DefaultValue(CARDS) @Parameter(description = "Types") String types
    ) {
        PagedElements<DatabaseRecord> history = command.doGetHistory(eventId, wsQueryOptions, types);
        return response(history.stream().map(historySerializationHelper::serializeBasicHistory), history.totalSize());
    }

    @GET
    @Path("{eventId}/history/{recordId}")
    @Operation(
            summary =  "Get a specific history record of a calendar event",
            description =  "Retrieve a specific history record of changes made to a calendar event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of specific calendar event history record"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar event history record"),
                    @ApiResponse(responseCode = "404", description = "Calendar event or history record not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getHistoryRecord(
            @PathParam(EVENT_ID) @Parameter( description = "Id of the event" , schema = @Schema(type = "long")) Long eventId,
            @PathParam(RECORD_ID) @Parameter( description = "Id of the record", schema = @Schema(type = "long")) Long recordId
    ) {
        Card record = command.doGetHistoryRecord(eventId, recordId);
        return response(calendarWsSerializationHelper.serializeDetailedEvent(cardMapperService.cardToObject(record)).with(
                "_endDate", toIsoDateTime(record.getEndDate()),//TODO duplicate code from history ws, improve this
                "_status", record.getCardStatus().name())
                .accept(m -> {//TODO move this into calendar helper, add previuos?
                    String matcher = "^_(.+)(_changed)";
                    record.getRawValues().forEach(a -> {
                        if (a.getKey().matches(matcher)) {
                            String attr = a.getKey().replaceAll(matcher, "$1");
                            String value = a.getKey().replaceAll(matcher, "$2");
                            switch (attr) {
                                case "EventDate" ->
                                    m.put(format("_date%s", value), a.getValue());
                                case "Description" ->
                                    m.put(format("_description%s", value), a.getValue());
                                case "Content" ->
                                    m.put(format("_content%s", value), a.getValue());
                                case "Category" ->
                                    m.put(format("_category%s", value), a.getValue());
                                case "Priority" ->
                                    m.put(format("_priority%s", value), a.getValue());
                                case "EventStatus" ->
                                    m.put(format("_status%s", value), a.getValue());
                                case "EventType" ->
                                    m.put(format("_type%s", value), a.getValue());
                                case "Notes" ->
                                    m.put(format("_notes%s", value), a.getValue());
                            }
                        }
                    });
                }));
    }

    @GET
    @Path("/print/{file}")
    @Operation(
            summary =  "Generate a report of calendar events",
            description =  "Generate and download a report of calendar events in the specified format",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of calendar event report"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to generate the requested calendar event report"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printCalendarEventReport(
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam(EXTENSION) @Parameter(description = "File extension", schema = @Schema(type = "string")) String extension,
            @QueryParam(ATTRIBUTES) @Parameter(description = "Attributes to include in the report", schema = @Schema(type = "string")) String attributes
    ) {
        return command.doPrintCalendarEventReport(wsQueryOptions, extension, attributes);
    }

}
