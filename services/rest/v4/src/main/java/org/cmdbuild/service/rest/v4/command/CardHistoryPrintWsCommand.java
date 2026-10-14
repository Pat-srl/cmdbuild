/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_BASIC;
import static org.cmdbuild.service.rest.v4.command.ClassPrintWsCommand.buildQueryOptions;

/**
 * @author ldare
 */
@Component
public class CardHistoryPrintWsCommand {

    private final UserCardService userCardService;
    private final CardHistoryService cardHistoryService;
    private final SysReportService sysReportService;

    public CardHistoryPrintWsCommand(UserCardService userCardService, CardHistoryService cardHistoryService, SysReportService sysReportService) {
        this.userCardService = checkNotNull(userCardService);
        this.cardHistoryService = checkNotNull(cardHistoryService);
        this.sysReportService = checkNotNull(sysReportService);
    }

    public DataHandler doPrintHistoryReport(String classId, Long cardId, WsQueryOptions wsQueryOptions, String attributes, String types) {
        Card card = userCardService.getUserCard(classId, cardId);
        DaoQueryOptions queryOptions = buildQueryOptions(card.getType(), wsQueryOptions, attributes);
        List<CardHistoryService.HistoryElement> historyTypes = cardHistoryService.fetchHistoryTypes(types);
        checkArgument(card.getType().hasServicePermission(CP_READ) || (card.getType().isProcess() && card.getType().hasServicePermission(CP_WF_BASIC)), "user not authorized to access card %s.%s", classId, cardId);
        return sysReportService.executeUserClassHistoryReport(card.getType(), card.getId(), ReportFormat.CSV, queryOptions, historyTypes);
    }
}
