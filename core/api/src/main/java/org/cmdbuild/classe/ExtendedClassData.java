/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import static com.google.common.base.Objects.equal;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.calendar.CalendarTriggerInfo;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.formstructure.FormStructure;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.widget.model.WidgetData;

public interface ExtendedClassData {

    List<FormTrigger> getFormTriggers();

    List<ContextMenuItem> getContextMenuItems();

    List<WidgetData> getWidgets();

    @Nullable
    FormStructure getFormStructure();

    List<CalendarTriggerInfo> getCalendarTriggers();

    default List<CalendarTriggerInfo> getCalendarTriggersForAttr(String attr) {
        return getCalendarTriggers().stream().filter(t -> equal(t.getOwnerAttr(), attr)).collect(toList());
    }

    default boolean hasForm() {
        return getFormStructure() != null;
    }

}
