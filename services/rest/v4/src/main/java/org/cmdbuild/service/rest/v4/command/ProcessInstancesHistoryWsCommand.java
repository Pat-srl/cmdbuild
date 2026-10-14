/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.base.Splitter;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.workflow.inner.FlowHistoryService;
import org.cmdbuild.workflow.model.Flow;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

/**
 * @author ldare
 */
@Component
public class ProcessInstancesHistoryWsCommand {

    private final FlowHistoryService flowHistoryService;

    public ProcessInstancesHistoryWsCommand(FlowHistoryService flowHistoryService) {
        this.flowHistoryService = checkNotNull(flowHistoryService);
    }

    public PagedElements<DatabaseRecord> doGetHistory(String classId, Long cardId, Integer limit, Integer offset, String filterStr, String types) {
        DaoQueryOptionsImpl query = DaoQueryOptionsImpl.builder()
                .withPaging(offset, limit)
                .withFilter(filterStr)
                .orderBy(ATTR_BEGINDATE, DESC)
                .build();
        List<CardHistoryService.HistoryElement> historyTypes = Splitter.on(",").splitToList(types).stream().map(e -> parseEnumOrNull(e, CardHistoryService.HistoryElement.class)).collect(toList());
        return flowHistoryService.getHistory(classId, cardId, query, historyTypes);
    }

    public Flow doGetHistoryRecord(String classId, Long id, Long recordId) {
        Flow record = flowHistoryService.getHistoryRecord(classId, recordId);
        checkArgument(equal(record.getCurrentId(), id));
        return record;
    }
}
