/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import java.util.List;
import org.cmdbuild.classe.access.CardHistoryService.HistoryElement;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.workflow.model.Flow;

public interface FlowHistoryService {

    PagedElements<DatabaseRecord> getHistory(String classId, long cardId, DaoQueryOptions queryOptions, List<HistoryElement> historyTypes);

    Flow getHistoryRecord(String classId, long recordId);
}
