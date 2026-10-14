/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.data;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.function.Consumer;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_READ;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.calendar.beans.CalendarEvent;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_ATTR_BEGIN;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_ATTR_SEQUENCE;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import org.cmdbuild.common.utils.PositionOf;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.utils.PositionOfUtils.buildPositionOf;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.participant.ParticipantUtils.buildParticipants;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CalendarEventRepositoryImpl implements CalendarEventRepository {

    private final OperationUserSupplier userSupplier;
    private final DaoService dao;
    private final MultitenantConfiguration multitenantConfig;

    public CalendarEventRepositoryImpl(OperationUserSupplier userSupplier, DaoService dao, MultitenantConfiguration multitenantConfig) {
        this.userSupplier = checkNotNull(userSupplier);
        this.dao = checkNotNull(dao);
        this.multitenantConfig = checkNotNull(multitenantConfig);
    }

    @Override
    public CalendarEvent createEvent(CalendarEvent event) {
        return dao.create(event);
    }

    @Override
    public CalendarEvent updateEvent(CalendarEvent event) {
        return dao.update(event);
    }

    @Override
    public CalendarEvent getEventById(long id) {
        return dao.getById(CalendarEvent.class, id);
    }

    @Override
    public void deleteEvent(long id) {
        dao.delete(CalendarEvent.class, id);
    }

    @Override
    public List<CalendarEvent> getAllEvents() {
        return dao.selectAll().from(CalendarEvent.class).asList();
    }

    @Override
    public List<CalendarEvent> getEventsForSequence(long sequenceId) {
        return dao.selectAll().from(CalendarEvent.class).where(EVENT_ATTR_SEQUENCE, EQ, sequenceId).orderBy(EVENT_ATTR_BEGIN).asList();
    }

    @Override
    public PagedElements<CalendarEvent> getUserEvents(DaoQueryOptions queryOptions) {

        CmdbFilter filter = queryOptions.getFilter();
        CmdbSorter sorter = queryOptions.getSorter();
        long offset = queryOptions.getOffset();

        PositionOf positionOf = null;
        if (queryOptions.hasPositionOf()) {
            Long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, queryOptions.getPositionOf()).then()
                    .from(CalendarEvent.class)
                    .orderBy(sorter)
                    .where(filter)
                    .accept(addCalendarEventUserFilter())
                    .accept(addCalendarEventUserTenant())
                    .build().getRowNumberOrNull();
            positionOf = buildPositionOf(rowNumber, queryOptions);
            offset = positionOf.getActualOffset();
        }

        List<CalendarEvent> events = dao.selectAll()
                .from(CalendarEvent.class)
                .orderBy(sorter)
                .where(filter)
                .accept(addCalendarEventUserFilter())
                .accept(addCalendarEventUserTenant())
                .paginate(offset, queryOptions.getLimit())
                .asList();

        long total;
        if (isPaged(offset, queryOptions.getLimit())) {
            total = dao.selectCount()
                    .from(CalendarEvent.class)
                    .where(filter)
                    .accept(addCalendarEventUserFilter())
                    .accept(addCalendarEventUserTenant())
                    .getCount();
        } else {
            total = events.size();
        }
        return new PagedElements<>(events, total, positionOf);
    }

    @Override
    public CalendarEvent getUserEvent(long id) {
        return dao.selectAll()
                .from(CalendarEvent.class)
                .where("Id", EQ, id)
                .accept(addCalendarEventUserFilter())
                .accept(addCalendarEventUserTenant())
                .getOne();
    }

    private Consumer<QueryBuilder> addCalendarEventUserFilter() {
        OperationUser user = userSupplier.getUser();
        return addCalendarEventUserFilter(user);
    }

    private Consumer<QueryBuilder> addCalendarEventUserTenant() {
        OperationUser user = userSupplier.getUser();
        boolean multitenantEnabled = multitenantConfig.isMultitenantEnabled();
        return addCalendarEventUserTenant(user, multitenantEnabled);
    }

    public static Consumer<QueryBuilder> addCalendarEventUserFilter(OperationUser user) {
        return (q) -> {
            if (user.hasPrivileges(RP_DATA_ALL_READ)) {
                //no filter
            } else {
                q.whereExpr("\"Owner\" = ? OR \"Participants\" && ? OR ( \"Owner\" IS NULL AND cardinality(\"Participants\") = 0 )", user.getUsername(), buildParticipants().addUsers(user.getId()).addRoles(user.getActiveGroupIds()).toParticipants());
            }
        };
    }

    private static Consumer<QueryBuilder> addCalendarEventUserTenant(OperationUser user, boolean multitenantEnabled) {
        return (q) -> {
            if (multitenantEnabled && user.hasMultitenant()) {
                q.whereExpr("\"Card\" IS NULL OR \"Card\" = 0 OR _cm3_card_exists_with_value(_cm3_utils_name_to_regclass(?), ?, \"Card\")", BASE_CLASS_NAME, ATTR_ID);
            }
        };
    }

}
