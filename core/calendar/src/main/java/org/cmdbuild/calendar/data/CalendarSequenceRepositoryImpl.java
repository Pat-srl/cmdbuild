/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.data;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.cmdbuild.calendar.beans.CalendarSequence;
import static org.cmdbuild.calendar.beans.CalendarSequence.CALENDAR_ATTR_CARD;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CalendarSequenceRepositoryImpl implements CalendarSequenceRepository {

    private final DaoService dao;

    public CalendarSequenceRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public CalendarSequence createSequence(CalendarSequence sequence) {
        return dao.create(sequence);
    }

    @Override
    public CalendarSequence updateSequence(CalendarSequence sequence) {
        return dao.update(sequence);
    }

    @Override
    public CalendarSequence getSequence(long id) {
        return dao.getById(CalendarSequence.class, id);
    }

    @Override
    public void deleteSequence(long id) {
        dao.delete(CalendarSequence.class, id);
    }

    @Override
    public List<CalendarSequence> getSequencesByCard(long cardId) {
        return dao.selectAll().from(CalendarSequence.class).where(CALENDAR_ATTR_CARD, EQ, cardId).asList();
    }

}
