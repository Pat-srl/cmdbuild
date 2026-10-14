/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.calendar;

import java.util.List;
import org.cmdbuild.calendar.beans.CalendarTrigger;

/**
 *
 * @author afelice
 */
public interface CalendarTriggersService {

    List<CalendarTrigger> getAllTriggers();

    List<CalendarTrigger> getTriggersByOwnerClassIncludeInherited(String ownerClass);

    List<CalendarTrigger> getTriggersByOwnerClassOwnerAttrIncludeInherited(String ownerClass, String ownerAttr);

    List<CalendarTrigger> getTriggersByOwnerClass(String ownerClass);

    List<CalendarTrigger> getTriggersByOwnerClassOwnerAttr(String ownerClass, String ownerAttr);

    CalendarTrigger createTrigger(CalendarTrigger trigger);

    CalendarTrigger updateTrigger(CalendarTrigger trigger);

    void deleteTrigger(long id);

    CalendarTrigger getTriggerById(long id);

    CalendarTrigger getTriggerByCode(String triggerCode);

}
