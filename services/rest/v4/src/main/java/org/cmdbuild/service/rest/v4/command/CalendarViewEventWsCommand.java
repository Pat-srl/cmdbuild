/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.cleanup.ViewType.VT_CALENDAR;
import static org.cmdbuild.dao.utils.CmFilterUtils.merge;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper.CAL_ATTR_MAPPING;

/**
 * @author ldare
 */
@Component
public class CalendarViewEventWsCommand {

    private final CalendarService calendarService;
    private final ViewService viewService;

    public CalendarViewEventWsCommand(CalendarService calendarService, ViewService viewService) {
        this.calendarService = checkNotNull(calendarService);
        this.viewService = checkNotNull(viewService);
    }

    public CalendarEvent doReadOne(Long eventId) {
        return calendarService.getEventById(eventId);
    }

    public PagedElements<CalendarEvent> doReadMany(String viewId, Long limit, Long offset, String filterStr) {
        View view = viewService.getSharedForCurrentUserByNameOrId(viewId);
        checkArgument(view.isOfType(VT_CALENDAR));
        return calendarService.getUserEvents(DaoQueryOptionsImpl.builder().withFilter(merge(parseFilter(filterStr), parseFilter(view.getFilter()))).withPaging(offset, limit).build().mapAttrNames(CAL_ATTR_MAPPING));
    }
}
