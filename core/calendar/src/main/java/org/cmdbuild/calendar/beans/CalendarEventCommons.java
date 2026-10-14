/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import jakarta.annotation.Nullable;

public interface CalendarEventCommons extends CalendarEventsCommons {

    @Nullable
    String getOwner();

    @Nullable
    Long getCard();

    CalendarEventSource getSource();

}
