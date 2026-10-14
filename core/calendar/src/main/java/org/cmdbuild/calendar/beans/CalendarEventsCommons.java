/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import java.time.ZoneId;
import java.util.List;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.email.beans.EmailTemplateInlineData;

public interface CalendarEventsCommons {

    String getDescription();

    String getContent();

    CalendarEventType getType();

    String getTimeZone();

    LookupValue getCategory();

    LookupValue getPriority();

    List<String> getParticipants();

    List<EmailTemplateInlineData> getNotifications();

    EventEditMode getEventEditMode();

    PostCardDeleteAction getOnCardDeleteAction();

    default ZoneId getZoneId() {
        return ZoneId.of(getTimeZone());
    }
}
