/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.CalendarEventAttachmentsWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CalendarEventAttachmentsWs;
import org.cmdbuild.service.rest.v4.model.WsAttachmentData;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.temp.TempInfo;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.cmdbuild.service.rest.v4.endpoint.CalendarEventAttachmentsWs.EVENT_CLASS_NAME;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsAttachmentData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class CalendarEventAttachmentWsTest extends WsTestBase {

    private final CalendarEventAttachmentsWs instance;

    private final WsAttachmentData wsAttachmentData;
    private final List<String> idList;
    private final DocumentInfoAndDetail documentInfoAndDetail1;
    private final List<DocumentInfoAndDetail> documentInfoAndDetailList;
    private final DataHandler dataHandler1;
    private final DataHandler dataHandler2;
    private final DataHandler dataHandler3;
    private final TempInfo tempInfo1;
    private final TempInfo tempInfo2;
    private final TempInfo tempInfo3;
    private final LookupValue lookupValue;
    private final WsQueryOptions wsQueryOptions;

    public CalendarEventAttachmentWsTest() {

        idList = list(A_KNOWN_GENERIC_ID1, A_KNOWN_GENERIC_ID2, A_KNOWN_GENERIC_ID3);

        CardWsSerializationHelperv3 cardWsSerializationHelperv3 = mockBuildCardWsSerializationHelperv3();
        CalendarEventAttachmentsWsCommand helper = new CalendarEventAttachmentsWsCommand(dmsService, tempService);
        this.instance = new CalendarEventAttachmentsWs(helper, calendarService, cardWsSerializationHelperv3);

        wsAttachmentData = mockBuildWsAttachmentData("filename1");
        documentInfoAndDetail1 = mockBuildDocumentInfoAndDetail("fileName1");
        DocumentInfoAndDetail documentInfoAndDetail2 = mockBuildDocumentInfoAndDetail("fileName2");
        DocumentInfoAndDetail documentInfoAndDetail3 = mockBuildDocumentInfoAndDetail("fileName3");
        documentInfoAndDetailList = list(documentInfoAndDetail1, documentInfoAndDetail2, documentInfoAndDetail3);
        dataHandler1 = new DataHandler("obj1", "text/html");
        dataHandler2 = new DataHandler("obj2", "text/html");
        dataHandler3 = new DataHandler("obj3", "text/html");
        tempInfo1 = mockBuildTempInfo("temp1");
        tempInfo2 = mockBuildTempInfo("temp2");
        tempInfo3 = mockBuildTempInfo("temp3");
        lookupValue = mockBuildLookupValue(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LOOKUP_ID1);
        wsQueryOptions = TestHelper_WSData.mockBuildWsQueryOptions(true);
    }

    @Test
    public void testCreate_sourceClassIdNull_dataHandlerNull() throws IOException {
        System.out.println("create_sourceClassIdNull_dataHandlerNull");

        //arrange:
        when(tempService.getTempData(A_KNOWN_GENERIC_ID1)).thenReturn(dataHandler1);
        when(tempService.getTempData(A_KNOWN_GENERIC_ID2)).thenReturn(dataHandler2);
        when(tempService.getTempData(A_KNOWN_GENERIC_ID3)).thenReturn(dataHandler3);
        when(tempService.getTempInfo(A_KNOWN_GENERIC_ID1)).thenReturn(tempInfo1);
        when(tempService.getTempInfo(A_KNOWN_GENERIC_ID2)).thenReturn(tempInfo2);
        when(tempService.getTempInfo(A_KNOWN_GENERIC_ID3)).thenReturn(tempInfo3);
        when(dmsService.create(eq(EVENT_CLASS_NAME), eq(A_RANDOM_ID), any(DocumentData.class))).thenReturn(documentInfoAndDetail1);


        //act:
        Object resultObject = instance.create(A_RANDOM_ID, wsAttachmentData, null, null, A_KNOWN_CARD_ID1, A_KNOWN_ATTACHMENT_ID, idList);

        //assert:
        verify(tempService).getTempData(A_KNOWN_GENERIC_ID1);
        verify(tempService).getTempData(A_KNOWN_GENERIC_ID2);
        verify(tempService).getTempData(A_KNOWN_GENERIC_ID3);
        verify(tempService).getTempInfo(A_KNOWN_GENERIC_ID1);
        verify(tempService).getTempInfo(A_KNOWN_GENERIC_ID2);
        verify(tempService).getTempInfo(A_KNOWN_GENERIC_ID3);
        verify(dmsService, times(3)).create(eq(EVENT_CLASS_NAME), eq(A_RANDOM_ID), any(DocumentData.class));
        checkSuccess(resultObject);
    }

    @Test
    public void testCreate_sourceClassNotNull() throws IOException {
        System.out.println("create_sourceClassNotNull");

        //arrange:
        when(dmsService.getCardAttachmentById(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, A_KNOWN_ATTACHMENT_ID)).thenReturn(documentInfoAndDetail1);
        when(dmsService.getDocumentData(documentInfoAndDetail1.getDocumentId())).thenReturn(dataHandler1);
        when(dmsService.create(eq(EVENT_CLASS_NAME), eq(A_RANDOM_ID), any(DocumentData.class))).thenReturn(documentInfoAndDetail1);
        when(dmsService.getService()).thenReturn(dmsProviderService);
        when(dmsProviderService.isServiceOk()).thenReturn(true);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(lookupValue);


        //act:
        Object resultObject = instance.create(A_RANDOM_ID, wsAttachmentData, null, A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, A_KNOWN_ATTACHMENT_ID, idList);

        //assert:
        verify(dmsService).getCardAttachmentById(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, A_KNOWN_ATTACHMENT_ID);
        verify(dmsService).getDocumentData(documentInfoAndDetail1.getDocumentId());
        verify(dmsService).create(eq(EVENT_CLASS_NAME), eq(A_RANDOM_ID), any(DocumentData.class));
        verify(dmsService).getService();
        verify(dmsProviderService).isServiceOk();
        verify(dmsService).getCategoryLookupForAttachment(any(), any());
        checkResult("documentId", resultObject, "_id");

    }

    @Test
    public void testCreate_sourceClassNull() throws IOException {
        System.out.println("create_sourceClassNull");

        //arrange:
        when(dmsService.create(eq(EVENT_CLASS_NAME), eq(A_RANDOM_ID), any(DocumentData.class))).thenReturn(documentInfoAndDetail1);
        when(dmsService.getService()).thenReturn(dmsProviderService);
        when(dmsProviderService.isServiceOk()).thenReturn(true);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.create(A_RANDOM_ID, wsAttachmentData, dataHandler1, null, A_KNOWN_CARD_ID1, A_KNOWN_ATTACHMENT_ID, idList);

        //assert:
        verify(dmsService).create(eq(EVENT_CLASS_NAME), eq(A_RANDOM_ID), any(DocumentData.class));
        verify(dmsService).getService();
        verify(dmsProviderService).isServiceOk();
        verify(dmsService).getCategoryLookupForAttachment(any(), any());
        verify(dmsService).checkRegularFileAttachment(any(DocumentData.class), anyString());
        verify(dmsService).checkRegularFileSize(any(DocumentData.class), anyString());
        checkResult("documentId", resultObject, "_id");
    }

    @Test
    public void testReadMany() {
        System.out.println("readMany");

        //arrange:
        when(dmsService.getCardAttachments(EVENT_CLASS_NAME, A_KNOWN_LONG_ID1, wsQueryOptions.getQuery(), wsQueryOptions.isDetailed())).thenReturn(documentInfoAndDetailList);
        when(dmsService.getService()).thenReturn(dmsProviderService);
        when(dmsProviderService.isServiceOk()).thenReturn(true);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.readmany(wsQueryOptions, A_KNOWN_LONG_ID1);

        //assert:
        verify(dmsService).getCardAttachments(EVENT_CLASS_NAME, A_KNOWN_LONG_ID1, wsQueryOptions.getQuery(), wsQueryOptions.isDetailed());
        verify(dmsService, times(3)).getService();
        verify(dmsProviderService, times(3)).isServiceOk();
        verify(dmsService, times(3)).getCategoryLookupForAttachment(any(), any());
        checkResultList(list("fileName1", "fileName2", "fileName3"), resultObject, "name");
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        when(dmsService.getCardAttachmentById(EVENT_CLASS_NAME, A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID)).thenReturn(documentInfoAndDetail1);
        when(dmsService.getService()).thenReturn(dmsProviderService);
        when(dmsProviderService.isServiceOk()).thenReturn(true);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID);

        //assert:
        verify(dmsService).getCardAttachmentById(EVENT_CLASS_NAME, A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID);
        verify(dmsService).getService();
        verify(dmsProviderService).isServiceOk();
        verify(dmsService).getCategoryLookupForAttachment(any(), any());
        checkResult("fileName1", resultObject, "name");
    }

    @Test
    public void testDownload() {
        System.out.println("download");

        //arrange:
        when(dmsService.getDocumentData(A_KNOWN_ATTACHMENT_ID)).thenReturn(dataHandler1);

        //act:
        Object resultObject = instance.download(A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID);

        //assert:
        verify(dmsService).getDocumentData(A_KNOWN_ATTACHMENT_ID);
        checkDataHandler("text/html", resultObject);
    }

    @Test
    public void testPreview_presentPreview() {
        System.out.println("preview_presentPreview");

        //arrange:
        when(dmsService.preview(A_KNOWN_ATTACHMENT_ID)).thenReturn(Optional.ofNullable(dataHandler1));

        //act:
        Object resultObject = instance.preview(A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID);

        //assert:
        verify(dmsService).preview(A_KNOWN_ATTACHMENT_ID);
        checkSuccess(resultObject);
        checkDetailed(resultObject, "dataUrl");
    }

    @Test
    public void testPreview_nullPreview() {
        System.out.println("preview_nullPreview");

        //arrange:
        when(dmsService.preview(A_KNOWN_ATTACHMENT_ID)).thenReturn(Optional.empty());

        //act:
        Object resultObject = instance.preview(A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID);

        //assert:
        verify(dmsService).preview(A_KNOWN_ATTACHMENT_ID);
        checkSuccess(resultObject);
        checkNotDetailed(resultObject, "dataUrl");
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(dmsService.updateDocumentWithAttachmentId(eq(EVENT_CLASS_NAME), eq(A_KNOWN_LONG_ID1), eq(A_KNOWN_ATTACHMENT_ID), any(DocumentData.class))).thenReturn(documentInfoAndDetail1);
        when(dmsService.getService()).thenReturn(dmsProviderService);
        when(dmsProviderService.isServiceOk()).thenReturn(true);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(lookupValue);


        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID, null, null);

        //assert:
        verify(dmsService).updateDocumentWithAttachmentId(eq(EVENT_CLASS_NAME), eq(A_KNOWN_LONG_ID1), eq(A_KNOWN_ATTACHMENT_ID), any(DocumentData.class));
        verify(dmsService).getService();
        verify(dmsProviderService).isServiceOk();
        verify(dmsService).getCategoryLookupForAttachment(any(), any());
        checkResult("fileName1", resultObject, "name");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID);

        //assert:
        verify(dmsService).deleteByAttachmentId(EVENT_CLASS_NAME, A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID);
        checkSuccess(resultObject);
    }

    @Test
    public void testGetAttachmentHistory() {
        System.out.println("getAttachmentHistory");

        //arrange:
        when(dmsService.getCardAttachmentById(EVENT_CLASS_NAME, A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID)).thenReturn(documentInfoAndDetail1);
        when(dmsService.getCardAttachmentVersions(EVENT_CLASS_NAME, A_KNOWN_LONG_ID1, documentInfoAndDetail1.getFileName())).thenReturn(documentInfoAndDetailList);
        when(dmsService.getService()).thenReturn(dmsProviderService);
        when(dmsProviderService.isServiceOk()).thenReturn(true);
        when(dmsService.getCategoryLookupForAttachment(any(), any())).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.getAttachmentHistory(A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID);

        //assert:
        verify(dmsService).getCardAttachmentById(EVENT_CLASS_NAME, A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID);
        verify(dmsService).getCardAttachmentVersions(EVENT_CLASS_NAME, A_KNOWN_LONG_ID1, documentInfoAndDetail1.getFileName());
        verify(dmsService, times(3)).getService();
        verify(dmsProviderService, times(3)).isServiceOk();
        verify(dmsService, times(3)).getCategoryLookupForAttachment(any(), any());
        checkResultList(list("fileName1", "fileName2", "fileName3"), resultObject, "name");
    }

    @Test
    public void testDownloadPreviousVersions() {
        System.out.println("downloadPreviousVersion");

        //arrange:
        when(dmsService.download(A_KNOWN_ATTACHMENT_ID, A_KNOWN_GENERIC_ID1)).thenReturn(dataHandler1);

        //act:
        Object resultObject = instance.downloadPreviousVersion(A_KNOWN_LONG_ID1, A_KNOWN_ATTACHMENT_ID, A_KNOWN_GENERIC_ID1);

        //assert:
        verify(dmsService).download(A_KNOWN_ATTACHMENT_ID, A_KNOWN_GENERIC_ID1);
        checkDataHandler("text/html", resultObject);
    }
}
