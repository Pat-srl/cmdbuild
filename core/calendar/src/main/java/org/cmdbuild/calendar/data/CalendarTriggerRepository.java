/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.data;

import java.util.List;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import org.cmdbuild.dao.entrytype.Attribute;

public interface CalendarTriggerRepository {

    List<CalendarTrigger> getAllTriggers();

    CalendarTrigger createTrigger(CalendarTrigger trigger);

    CalendarTrigger updateTrigger(CalendarTrigger trigger);

    CalendarTrigger getTriggerById(long id);

    CalendarTrigger getTriggerByCode(String triggerCode);

    List<CalendarTrigger> getTriggersByOwnerClass(String ownerClass);

    List<CalendarTrigger> getTriggersByOwnerClassOwnerAttr(String ownerClass, String ownerAttr);

    void deleteTrigger(long id);

    default List<CalendarTrigger> getTriggersForAttribute(Attribute attr) {
        return getTriggersByOwnerClassOwnerAttr(attr.getOwner().getName(), attr.getName());
    }
}
