/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.service.rest.v4.endpoint.CardWs;
import org.cmdbuild.service.rest.v4.model.WsCardData;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;

/**
 * @author ldare
 */
@Component
public class ViewCardWsCommand {

    private final ViewService viewService;
    private final CardFilterService cardFilterService;
    private final SysReportService sysReportService;

    public ViewCardWsCommand(ViewService viewService, CardFilterService cardFilterService, SysReportService sysReportService) {
        this.viewService = checkNotNull(viewService);
        this.cardFilterService = checkNotNull(cardFilterService);
        this.sysReportService = checkNotNull(sysReportService);
    }

    public Card doCreate(String viewId, WsCardData data) {
        return viewService.createUserCard(viewId, data.getValues());
    }

    public Card doReadOne(String viewId, String cardId) {
        return viewService.getCardForCurrentUser(viewId, cardId);
    }

    public PagedElements<Card> doReadMany(String viewId, DaoQueryOptions queryOptions) {
        return viewService.getCards(viewService.getForCurrentUserByNameOrId(viewId), queryOptions);
    }

    public Card doUpdate(String viewId, Long cardId, WsCardData data) {
        return viewService.updateUserCard(viewId, cardId, data.getValues());
    }

    public void doDelete(String viewId, Long cardId) {
        viewService.deleteUserCard(viewId, cardId);
    }

    public DataHandler doPrint(String viewId, String cardId, String extension) {
        View view = viewService.getForCurrentUserByNameOrId(viewId);
        Card card = viewService.getCardById(view, cardId);
        return sysReportService.executeCardReport(card, reportExtFromString(extension));
    }

    @Nullable//TODO duplicate code
    public String doGetFilterOrNull(@Nullable String filter) {
        return CardWs.getFilterOrNull(filter, (id) -> cardFilterService.getById(id).getConfiguration());
    }
}
