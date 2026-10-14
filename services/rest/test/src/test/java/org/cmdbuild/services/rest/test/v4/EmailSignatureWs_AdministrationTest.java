/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.service.rest.v4.command.EmailSignatureWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EmailSignatureWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildEmailSignature;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildEmailSignatureNotActive;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsEmailSignatureData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EmailSignatureWs_AdministrationTest extends WsTestBase {

    private final EmailSignatureWs_Administration instance;

    public final EmailSignature emailSignature;
    public final EmailSignature emailSignatureNoActive;
    public final List<EmailSignature> listEmailSignature;

    public EmailSignatureWs_AdministrationTest() {
        EmailSignatureWsCommand command = new EmailSignatureWsCommand(emailSignatureService);
        instance = new EmailSignatureWs_Administration(emailConfiguration, objectTranslationService, command);
        emailSignature = mockBuildEmailSignature(A_EMAIL_SIGNATURE_ID1);
        emailSignatureNoActive = mockBuildEmailSignatureNotActive(A_EMAIL_SIGNATURE_ID2);
        listEmailSignature = list(emailSignature, emailSignatureNoActive);
    }

    @Test
    public void testReadAll_Detailed() {
        System.out.println("readAll_Detailed");

        //arrange:
        List<Long> expListIds = list(A_EMAIL_SIGNATURE_ID1, A_EMAIL_SIGNATURE_ID2);
        when(emailSignatureService.getAll()).thenReturn(listEmailSignature);
        when(objectTranslationService.translateEmailSignatureDescription(anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateEmailSignatureContenthtml(anyString(), anyString())).thenReturn("examp_content_html_translation");

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_SORT, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED);

        //assert:
        verify(emailSignatureService).getAll();
        verify(objectTranslationService, times(2)).translateEmailSignatureDescription(anyString(), anyString());
        verify(objectTranslationService, times(2)).translateEmailSignatureContenthtml(anyString(), anyString());
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testReadAll_NotDetailed() {
        System.out.println("readAll_NotDetailed");

        //arrange:
        List<Long> expListIds = list(A_EMAIL_SIGNATURE_ID1, A_EMAIL_SIGNATURE_ID2);
        when(emailSignatureService.getAll()).thenReturn(listEmailSignature);
        when(objectTranslationService.translateEmailSignatureDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_SORT, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, NOT_DETAILED);

        //assert:
        verify(emailSignatureService).getAll();
        verify(objectTranslationService, times(2)).translateEmailSignatureDescription(anyString(), anyString());
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        Long expId = A_EMAIL_SIGNATURE_ID1;
        when(emailSignatureService.getOne(anyString())).thenReturn(emailSignature);
        when(objectTranslationService.translateEmailSignatureDescription(anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateEmailSignatureContenthtml(anyString(), anyString())).thenReturn("examp_content_html_translation");

        //act:
        Object resultObject = instance.read("exampId");

        //assert:
        verify(emailSignatureService).getOne(anyString());
        verify(objectTranslationService).translateEmailSignatureContenthtml(anyString(), anyString());
        verify(objectTranslationService).translateEmailSignatureDescription(anyString(), anyString());
        checkId(expId, resultObject);
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        Long expId = A_EMAIL_SIGNATURE_ID1;
        when(emailSignatureService.create(any(EmailSignature.class))).thenReturn(emailSignature);
        when(objectTranslationService.translateEmailSignatureDescription(anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateEmailSignatureContenthtml(anyString(), anyString())).thenReturn("examp_content_html_translation");

        //act:
        Object resultObject = instance.create(mockBuildWsEmailSignatureData());

        //assert:
        verify(emailSignatureService).create(any(EmailSignature.class));
        verify(objectTranslationService).translateEmailSignatureContenthtml(anyString(), anyString());
        verify(objectTranslationService).translateEmailSignatureDescription(anyString(), anyString());
        checkId(expId, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        Long expId = A_EMAIL_SIGNATURE_ID1;
        when(emailSignatureService.update(any(EmailSignature.class))).thenReturn(emailSignature);
        when(objectTranslationService.translateEmailSignatureDescription(anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateEmailSignatureContenthtml(anyString(), anyString())).thenReturn("examp_content_html_translation");

        //act:
        Object resultObject = instance.update(A_EMAIL_SIGNATURE_ID1, mockBuildWsEmailSignatureData());

        //assert:
        verify(emailSignatureService).update(any(EmailSignature.class));
        verify(objectTranslationService).translateEmailSignatureContenthtml(anyString(), anyString());
        verify(objectTranslationService).translateEmailSignatureDescription(anyString(), anyString());
        checkId(expId, resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_EMAIL_SIGNATURE_ID1);

        //assert:
        verify(emailSignatureService).delete(anyLong());
        checkSuccess(resultObject);
    }
}
