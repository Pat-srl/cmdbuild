/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import org.cmdbuild.calendar.beans.CalendarEvent;

public interface CalendarApi {

    CalendarEvent createEvent(CalendarEvent event);

    CalendarEvent getEvent(long id);

}
