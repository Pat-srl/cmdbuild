/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.CardHistoryPrintWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CardHistoryPrintWs;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import static org.cmdbuild.classe.access.CardHistoryService.HistoryElement.HE_CARDS;
import static org.cmdbuild.classe.access.CardHistoryService.HistoryElement.HE_REFERENCES;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkDataHandler;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class CardHistoryPrintWsTest extends WsTestBase {

    private final CardHistoryPrintWs instance;

    private final WsQueryOptions wsQueryOptions;
    private final DataHandler dataHandler;
    private final Card card;
    private final Classe classe;

    public CardHistoryPrintWsTest() {

        CardHistoryPrintWsCommand command = new CardHistoryPrintWsCommand(userCardService, cardHistoryService, sysReportService);
        this.instance = new CardHistoryPrintWs(command);

        this.wsQueryOptions = TestHelper_WSData.mockBuildWsQueryOptions(DETAILED);
        this.dataHandler = mockBuildDataHandler(A_TEST_DATAHANDLER_TYPE);
        this.classe = mockBuildClasse(A_KNOWN_CLASS_NAME1);
        this.card = mockBuildCard(A_KNOWN_CARD_ID1, classe, map("attr1", 1, "attr2", 2, "attr3", 3));
    }

    @Test
    public void testPrintHistoryReport() {
        System.out.println("printHistoryReport");

        //arrange:
        String attributes = "[\"attr1\",\"attr2\",\"attr3\"]";
        when(userCardService.getUserCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1)).thenReturn(card);
        when(cardHistoryService.fetchHistoryTypes("types")).thenReturn(list(HE_CARDS, HE_REFERENCES));
        when(sysReportService.executeUserClassHistoryReport(any(Classe.class), anyLong(), eq(ReportFormat.CSV), any(DaoQueryOptions.class), anyList())).thenReturn(dataHandler);

        //act:
        DataHandler resultObject = instance.printHistoryReport(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, wsQueryOptions, attributes, "types");

        //assert:
        verify(userCardService).getUserCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1);
        verify(cardHistoryService).fetchHistoryTypes("types");
        checkDataHandler("text/plain", resultObject);
    }


}
