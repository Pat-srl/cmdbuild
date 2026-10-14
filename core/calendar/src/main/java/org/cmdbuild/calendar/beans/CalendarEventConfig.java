/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.calendar.beans;

import org.cmdbuild.utils.json.JsonBean;

@JsonBean(CalendarEventConfigImpl.class)
public interface CalendarEventConfig {

    PostCardDeleteAction getOnCardDeleteAction();
    
    EventEditMode getEventEditMode();
}
