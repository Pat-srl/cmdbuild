/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import static com.google.common.base.Objects.equal;
import java.time.LocalDate;
import jakarta.annotation.Nullable;
import org.cmdbuild.calendar.CalendarTriggerInfo;
import org.cmdbuild.utils.date.Interval;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface CalendarTrigger extends CalendarEventsCommons, CalendarConfigCommons, CalendarTriggerInfo {

    static final String CAL_TRIGGER_ATTR_OWNERCLASS = "OwnerClass", CAL_TRIGGER_ATTR_OWNERATTR = "OwnerAttr";

    String getCode();
    
    @Nullable
    @Override
    String getTimeZone();

    Interval getDelay();

    @Nullable
    LocalDate getLastEvent();
    
    TriggerScope getScope();
    
    default boolean hasScope(TriggerScope scope){
        return equal(getScope(), scope);
    }

    @Override
    default EventEditMode getEventEditMode() {
        return getConfig().getEventEditMode();
    }

    @Override
    default PostCardDeleteAction getOnCardDeleteAction() {
        return getConfig().getOnCardDeleteAction();
    }

    default boolean hasTimeZone() {
        return isNotBlank(getTimeZone());
    }

    default boolean hasDelay() {
        return !getDelay().isZero();
    }

    enum TriggerScope {
        TS_INTERACTIVE_ONLY, TS_ALWAYS
    }

}
