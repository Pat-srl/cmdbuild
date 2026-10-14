/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.client.rest.api.CalendarApi;
import org.cmdbuild.service.rest.v4.model.WsEventData;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

public class CalendarApiImpl extends AbstractServiceClientImpl implements CalendarApi {

    public CalendarApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public CalendarEvent createEvent(CalendarEvent event) {
        JsonNode data = post("calendar/events", map(
                "category", event.getCategory().getCode(),
                "priority", event.getPriority().getCode(),
                "card", event.getCard(),
                "sequence", event.getSequence(),
                "content", event.getContent(),
                "description", event.getDescription(),
                "timeZone", event.getTimeZone(),
                "eventEditMode", serializeEnum(event.getEventEditMode()),
                "notifications", event.getNotifications(),
                "participants", event.getParticipants(),
                "onCardDeleteAction", serializeEnum(event.getOnCardDeleteAction()),
                "type", serializeEnum(event.getType()),
                "begin", toIsoDateTime(event.getBegin()),
                "end", toIsoDateTime(event.getEnd()),
                "owner", event.getOwner(),
                "status", serializeEnum(event.getStatus()),
                "source", serializeEnum(event.getSource()),
                "notes", event.getNotes()
        )).asJackson().get("data");
        return fromJson(data, WsEventData.class).buildEvent().withId(data.get("_id").asLong()).build();
    }

    @Override
    public CalendarEvent getEvent(long id) {
        return fromJson(get("calendar/events/" + id).asJackson().get("data"), WsEventData.class).buildEvent().withId(id).build();
    }

}
