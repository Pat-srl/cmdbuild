/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.service.rest.v4.command.CardPrintWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CardPrintWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkDataHandler;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class CardPrintWsTest extends WsTestBase {

    private final CardPrintWs instance;

    private final Card card;
    private final DataHandler dataHandler;

    public CardPrintWsTest() {
        CardPrintWsCommand command = new CardPrintWsCommand(userCardService, sysReportService);
        this.instance = new CardPrintWs(command);

        Classe classe = mockBuildClasse(A_KNOWN_CLASS_NAME1);
        this.card = mockBuildCard(A_KNOWN_LONG_ID1, classe, map(A_KNOWN_ATTR_NAME1, 1, A_KNOWN_ATTR_NAME2, 2, A_KNOWN_ATTR_NAME3, 3));
        this.dataHandler = mockBuildDataHandler(A_TEST_DATAHANDLER_TYPE);
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        when(userCardService.getUserCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1)).thenReturn(card);
        when(sysReportService.executeCardReport(any(Card.class), any(ReportFormat.class))).thenReturn(dataHandler);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, "csv");

        //assert:
        verify(userCardService).getUserCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1);
        verify(sysReportService).executeCardReport(any(Card.class), any(ReportFormat.class));
        checkDataHandler("text/plain", resultObject);
    }
}
