/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.ws.rs.core.MultivaluedHashMap;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.CalendarEventEmailWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CalendarEventEmailWs;
import org.cmdbuild.service.rest.v4.model.WsEmailData;
import org.cmdbuild.service.rest.v4.serializationhelpers.EmailSerializationHelper;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.temp.TempInfo;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_TABLE;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsEmailData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class CalendarEventEmailWsTest extends WsTestBase {

    private final String EMAIL_CLASS_NAME = "Email";

    private final CalendarEventEmailWs instance;
    private final EmailSerializationHelper emailSerializationHelper;
    private final CalendarEventEmailWsCommand command;

    private final List<String> tempIdList;
    private final Classe classe;
    private final Card card;
    private final Email email1;
    private final Email email2;
    private final Email email3;
    private final List<Email> emailList;
    private final EmailTemplate emailTemplate;
    private final DataHandler dataHandler1;
    private final DataHandler dataHandler2;
    private final TempInfo tempInfo;
    private final Attachment attachment1;
    private final Attachment attachment2;
    private final MultivaluedHashMap<String, String> headers;
    private final WsEmailData wsEmailData;
    private final List<DocumentInfoAndDetail> documentInfoAndDetailList;
    private final WsQueryOptions wsQueryOptions_detailed;
    private final WsQueryOptions wsQueryOptions_notDetailed;

    public CalendarEventEmailWsTest() {
        emailSerializationHelper = new EmailSerializationHelper(dmsService, emailTemplateService);
        command = new CalendarEventEmailWsCommand(dmsService, tempService, calendarService, emailService, operationUserSupplier, daoService, emailTemplateService, emailAccountService);
        instance = new CalendarEventEmailWs(emailSerializationHelper, command);
        tempIdList = list(A_KNOWN_GENERIC_ID1, A_KNOWN_GENERIC_ID2, A_KNOWN_GENERIC_ID3);
        classe = mockBuildClasse(A_KNOWN_CLASS_ID);
        Map<String, Object> attributes = map(
                "attr1", "val1",
                "attr2", 1L
        );
        card = mockBuildCard(A_KNOWN_CARD_ID1, classe, attributes);
        email1 = mockBuildEmail(A_KNOWN_LONG_ID1);
        email2 = mockBuildEmail(A_KNOWN_LONG_ID2);
        email3 = mockBuildEmail(A_KNOWN_LONG_ID3);
        emailList = list(email1, email2, email3);
        emailTemplate = mockBuildEmailTemplate("template");
        headers = new MultivaluedHashMap<String, String>() {{
            add("Content-Type", "application/json");
            add("Content-Disposition", "form-data; name=\"" + "email" + "\"");
        }};
        dataHandler1 = new DataHandler(new StubDataSource(
                """
                        {
                            "subject": "Test Email",
                            "from": "me@example.com",
                            "to": "you@example.com"
                        }
                        """,
                "application/json",
                "email"));
        dataHandler2 = new DataHandler(new StubDataSource(
                """
                        {
                            "subject": "Test Email",
                            "from": "me@example.com",
                            "to": "you@example.com",
                            "expr": "test expr",
                            "template": "1"
                        }
                        """,
                "application/json",
                "email"));
        tempInfo = mockBuildTempInfo("fileName");
        attachment1 = new Attachment("email", dataHandler1, headers);
        attachment2 = new Attachment("email", dataHandler2, headers);
        wsEmailData = mockBuildWsEmailData();
        documentInfoAndDetailList = list(
                mockBuildDocumentInfoAndDetail(A_KNOWN_NAME1),
                mockBuildDocumentInfoAndDetail(A_KNOWN_NAME2),
                mockBuildDocumentInfoAndDetail(A_KNOWN_NAME3)
        );
        wsQueryOptions_detailed = TestHelper_WSData.mockBuildWsQueryOptions(true);
        wsQueryOptions_notDetailed = TestHelper_WSData.mockBuildWsQueryOptions(false);
    }

    @Test
    public void testCreate1() {
        System.out.println("create1");

        //arrange:
        when(daoService.getCard(EVENT_TABLE, A_KNOWN_LONG_ID1)).thenReturn(card);
        when(operationUserSupplier.getUsername()).thenReturn("A_NICE_USERNAME");
        when(emailTemplateService.getByNameOrId(anyString())).thenReturn(emailTemplate);
        when(tempService.getTempInfo(anyString())).thenReturn(tempInfo);
        when(tempService.getTempData(anyString())).thenReturn(dataHandler1);
        when(emailService.applySysTemplate(any(Email.class), anyString())).thenReturn(email1);

        //act:
        Object resultObject = instance.create(A_KNOWN_LONG_ID1, true, true, tempIdList, list(attachment1));

        //assert:
        verify(daoService).getCard(EVENT_TABLE, A_KNOWN_LONG_ID1);
        verify(operationUserSupplier).getUsername();
        verify(tempService, times(3)).getTempInfo(anyString());
        verify(tempService, times(3)).getTempData(anyString());
        checkId(1L, resultObject);
    }

    @Test
    public void testCreate2() {
        System.out.println("create2");

        //arrange:
        when(daoService.getCard(EVENT_TABLE, A_KNOWN_LONG_ID1)).thenReturn(card);
        when(operationUserSupplier.getUsername()).thenReturn("A_NICE_USERNAME");
        when(emailTemplateService.getByNameOrId(anyString())).thenReturn(emailTemplate);
        when(tempService.getTempInfo(anyString())).thenReturn(tempInfo);
        when(tempService.getTempData(anyString())).thenReturn(dataHandler1);
        when(emailService.applySysTemplate(any(Email.class), anyString())).thenReturn(email1);
        when(emailService.create(any(Email.class))).thenReturn(email1);

        //act:
        Object resultObject = instance.create(A_KNOWN_LONG_ID1, true, false, tempIdList, list(attachment1));

        //assert:
        verify(daoService).getCard(EVENT_TABLE, A_KNOWN_LONG_ID1);
        verify(operationUserSupplier).getUsername();
        verify(tempService, times(3)).getTempInfo(anyString());
        verify(tempService, times(3)).getTempData(anyString());
        verify(emailService).create(any(Email.class));
        checkId(1L, resultObject);
    }

    @Test
    public void testCreate_error_noAttachments() {
        System.out.println("create_error_noAttachments");


        //act:
        try {
            instance.create(A_KNOWN_LONG_ID1, true, true, tempIdList, emptyList());
        } catch (NullPointerException ex) {
            //assert:
            assertEquals("missing 'email' multipart json payload", ex.getMessage());
        }
    }

    @Test
    public void testCreate_withExpr() {
        System.out.println("create_withExpr");

        //arrange:
        when(daoService.getCard(EVENT_TABLE, A_KNOWN_LONG_ID1)).thenReturn(card);
        when(operationUserSupplier.getUsername()).thenReturn("A_NICE_USERNAME");
        when(tempService.getTempInfo(anyString())).thenReturn(tempInfo);
        when(tempService.getTempData(anyString())).thenReturn(dataHandler1);

        //act:
        Object resultObject = instance.create(A_KNOWN_LONG_ID1, true, true, tempIdList, list(attachment2));

        //assert:
        verify(daoService).getCard(EVENT_TABLE, A_KNOWN_LONG_ID1);
        verify(operationUserSupplier).getUsername();
        verify(tempService, times(3)).getTempInfo(anyString());
        verify(tempService, times(3)).getTempData(anyString());
        checkResult("me@example.com", resultObject, "from");
        checkResult("you@example.com", resultObject, "to");
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(emailService.getOne(A_KNOWN_LONG_ID2)).thenReturn(email1);
        when(emailService.update(any(Email.class))).thenReturn(email1);
        when(dmsService.isEnabled()).thenReturn(true);
        when(dmsService.getCardAttachments(anyString(), anyLong())).thenReturn(documentInfoAndDetailList);
        when(dmsService.download(anyString(), eq(null))).thenReturn(dataHandler1);

        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, wsEmailData);

        //assert:
        verify(emailService).getOne(A_KNOWN_LONG_ID2);
        verify(emailService).update(any(Email.class));
        verify(dmsService).isEnabled();
        verify(dmsService).getCardAttachments(anyString(), anyLong());
        verify(dmsService, times(3)).download(anyString(), eq(null));
        checkResult("me@example.com", resultObject, "from");
        checkResult("you@example.com", resultObject, "to");
    }

    @Test
    public void testReadAll_detailed() {
        System.out.println("readAll_detailed");

        //arrange:
        when(emailService.getAllForCard(eq(A_KNOWN_LONG_ID1), any(DaoQueryOptions.class))).thenReturn(emailList);
        when(dmsService.getCardAttachments(EMAIL_CLASS_NAME, email1.getId())).thenReturn(documentInfoAndDetailList);
        when(dmsService.getCardAttachments(EMAIL_CLASS_NAME, email2.getId())).thenReturn(documentInfoAndDetailList);
        when(dmsService.getCardAttachments(EMAIL_CLASS_NAME, email3.getId())).thenReturn(documentInfoAndDetailList);
        when(dmsService.download(anyString(), anyString())).thenReturn(dataHandler1);
        when(dmsService.isEnabled()).thenReturn(true);

        //act:
        Object resultObject = instance.readAll(A_KNOWN_LONG_ID1, wsQueryOptions_detailed);

        //assert:
        verify(emailService).getAllForCard(eq(A_KNOWN_LONG_ID1), any(DaoQueryOptions.class));
        verify(dmsService).getCardAttachments(EMAIL_CLASS_NAME, email1.getId());
        verify(dmsService).getCardAttachments(EMAIL_CLASS_NAME, email2.getId());
        verify(dmsService).getCardAttachments(EMAIL_CLASS_NAME, email3.getId());
        verify(dmsService, times(9)).download(anyString(), anyString());
        verify(dmsService, times(3)).isEnabled();
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
        checkDetailedList(resultObject, "body");
    }

    @Test
    public void testReadAll_notDetailed() {
        System.out.println("readAll_notDetailed");

        //arrange:
        when(emailService.getAllForCard(eq(A_KNOWN_LONG_ID1), any(DaoQueryOptions.class))).thenReturn(emailList);

        //act:
        Object resultObject = instance.readAll(A_KNOWN_LONG_ID1, wsQueryOptions_notDetailed);

        //assert:
        verify(emailService).getAllForCard(eq(A_KNOWN_LONG_ID1), any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
        checkNotDetailedList(resultObject, "body");
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(emailService.getOne(A_KNOWN_LONG_ID2)).thenReturn(email1);
        when(dmsService.isEnabled()).thenReturn(true);
        when(dmsService.getCardAttachments(EMAIL_CLASS_NAME, email1.getId())).thenReturn(documentInfoAndDetailList);
        when(dmsService.download(anyString(), anyString())).thenReturn(dataHandler1);

        //act:
        Object resultObject = instance.read(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2);

        //assert:
        verify(emailService).getOne(A_KNOWN_LONG_ID2);
        verify(dmsService).isEnabled();
        verify(dmsService).getCardAttachments(EMAIL_CLASS_NAME, email1.getId());
        verify(dmsService, times(3)).download(anyString(), anyString());
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
        checkDetailed(resultObject, "body");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //arrange:
        when(emailService.getOne(A_KNOWN_LONG_ID2)).thenReturn(email1);

        //act:
        Object resutlObject = instance.delete(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2);

        //assert:
        verify(emailService).delete(eq(email1));
        checkSuccess(resutlObject);
    }

    static class StubDataSource implements DataSource {

        private final byte[] content;
        private final String contentType;
        private final String name;

        public StubDataSource(String content, String contentType, String name) {
            this.content = content.getBytes(StandardCharsets.UTF_8);
            this.contentType = contentType;
            this.name = name;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException("Not needed for tests");
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
