/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import static java.util.Collections.emptyList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.calendar.CalendarTriggerInfo;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.dao.entrytype.ClassDefinition;

public interface ExtendedClassDefinition extends ExtendedClassData {

    ClassDefinition getClassDefinition();

    List<Pair<String, Direction>> getDefaultClassOrdering();

    List<AttributeGroupInfo> getAttributeGroups();

    @Override
    default List<CalendarTriggerInfo> getCalendarTriggers() {
        return emptyList();
    }

    enum Direction {
        ASC, DESC
    }
}
