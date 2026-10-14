/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;

/**
 * @author ldare
 */
@Component
public class CardHistoryWsCommand {

    private final CardHistoryService cardHistoryService;

    public CardHistoryWsCommand(CardHistoryService cardHistoryService) {
        this.cardHistoryService = cardHistoryService;
    }

    public PagedElements<DatabaseRecord> doGetHistory(String classId, Long cardId, WsQueryOptions wsQueryOptions, String types) {
        DaoQueryOptions queryOptions = buildHistoryDaoQueryOptions(wsQueryOptions);
        List<CardHistoryService.HistoryElement> historyTypes = cardHistoryService.fetchHistoryTypes(types);
        return cardHistoryService.getHistoryElements(classId, cardId, queryOptions, historyTypes);
    }

    public List<Card> doGetHistoryOnlyChanges(String classId, Long cardId, WsQueryOptions wsQueryOptions, String types) {
        DaoQueryOptions queryOptions = buildHistoryDaoQueryOptions(wsQueryOptions).withoutPaging();
        List<CardHistoryService.HistoryElement> historyTypes = cardHistoryService.fetchHistoryTypes(types);
        return cardHistoryService.getHistoryElementsOnlyChanges(classId, cardId, queryOptions, historyTypes);
    }

    public Card doGetHistoryRecord(String classId, Long id, Long recordId) {
        Card record = cardHistoryService.getHistoryRecord(classId, recordId);
        checkArgument(equal(record.getCurrentId(), id));
        return record;
    }

    /**
     *
     * @param wsQueryOptions (optionally) with pagination information
     * @return
     */
    private DaoQueryOptions buildHistoryDaoQueryOptions(WsQueryOptions wsQueryOptions) {
        DaoQueryOptionsImpl query = DaoQueryOptionsImpl.copyOf(wsQueryOptions.getQuery())
                .withPaging(wsQueryOptions.getOffset(), wsQueryOptions.getLimit())
                .withFilter(wsQueryOptions.getQuery().getFilter())
                .accept(q -> {
                    if (wsQueryOptions.getQuery().getSorter().isNoop()) {
                        q.withSorter(CmdbSorterImpl.sorter(ATTR_BEGINDATE, DESC));
                    }
                })
                .build();
        return query;
    }


}
