/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.data;

import java.util.List;
import org.cmdbuild.calendar.beans.CalendarSequence;

public interface CalendarSequenceRepository {

    CalendarSequence createSequence(CalendarSequence sequence);

    CalendarSequence updateSequence(CalendarSequence sequence);

    CalendarSequence getSequence(long id);

    void deleteSequence(long id);

    List<CalendarSequence> getSequencesByCard(long cardId);
}
