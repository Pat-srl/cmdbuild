/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.classe.access.CardHistoryService.HistoryElement;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.workflow.dao.CardToFlowCardWrapperService;
import org.cmdbuild.workflow.model.Flow;
import org.springframework.stereotype.Component;

@Component
public class FlowHistoryServiceImpl implements FlowHistoryService {

    private final CardHistoryService historyService;
    private final CardToFlowCardWrapperService wrapperService;

    public FlowHistoryServiceImpl(CardHistoryService historyService, CardToFlowCardWrapperService wrapperService) {
        this.historyService = checkNotNull(historyService);
        this.wrapperService = checkNotNull(wrapperService);
    }

    @Override
    public PagedElements<DatabaseRecord> getHistory(String classId, long cardId, DaoQueryOptions queryOptions, List<HistoryElement> historyTypes) {
        return historyService.getHistoryElements(classId, cardId, queryOptions, historyTypes).map(e -> {
            e.getType().isClasse();
            if (e.getType().isClasse()) {
                return wrapperService.cardToFlowCard(e);
            } else {
                return e;
            }
        });
    }

    @Override
    public Flow getHistoryRecord(String classId, long recordId) {
        return wrapperService.cardToFlowCard(historyService.getHistoryRecord(classId, recordId));
    }

}
