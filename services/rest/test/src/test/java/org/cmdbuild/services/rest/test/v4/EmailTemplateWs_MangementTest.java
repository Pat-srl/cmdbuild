/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.service.rest.v4.command.EmailTemplateWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EmailTemplateWs_Management;
import org.cmdbuild.services.rest.test.common.TestHelper_Variable;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.services.serialization.EmailTemplateSerializationHelper;
import org.cmdbuild.template.TemplateBindings;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListEmailTemplate;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildTemplateBindings;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EmailTemplateWs_MangementTest extends WsTestBase {
    private final EmailTemplateWs_Management instance;

    private final String placeholderId = "exampClassId";

    private final List<String> expListNames;
    private final List<EmailTemplate> listEmailTemplate;
    private final TemplateBindings templateBindings;

    public EmailTemplateWs_MangementTest() {

        EmailTemplateSerializationHelper emailTemplateSerializationHelper = mockBuildEmailTemplateSerializationHelper();
        EmailTemplateWsCommand command = new EmailTemplateWsCommand(emailTemplateSerializationHelper, reportService, emailTemplateService);
        instance = new EmailTemplateWs_Management(emailTemplateService, emailTemplateSerializationHelper, command);

        expListNames = list(A_EMAIL_TEMPLATE_NAME1, A_EMAIL_TEMPLATE_NAME2, A_EMAIL_TEMPLATE_NAME3);
        listEmailTemplate = mockBuildListEmailTemplate();
        templateBindings = mockBuildTemplateBindings();
    }

    @Test
    public void testReadAllForClass_Detailed_IncludeBindings() {
        System.out.println("readAllForClass_Detailed_IncludeBindings");

        //arrange:
        when(emailTemplateService.getAllActive()).thenReturn(listEmailTemplate);
        when(emailTemplateService.fetchTemplateBindings(any(EmailTemplate.class))).thenReturn(templateBindings);

        //act:
        Object resultObject = instance.readAllForClass(placeholderId, A_TEST_EMPTY_FILTER, A_TEST_SORT, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED, TestHelper_Variable.INCLUDE);

        //assert:
        verify(emailTemplateService).getAllActive();
        verify(emailTemplateService, times(listEmailTemplate.size())).fetchTemplateBindings(any(EmailTemplate.class));
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAllForClass_NotDetailed_IncludeBindings() {
        System.out.println("readAllForClass_NotDetailed_IncludeBindings");

        //arrange:
        when(emailTemplateService.getAllActive()).thenReturn(listEmailTemplate);
        when(emailTemplateService.fetchTemplateBindings(any(EmailTemplate.class))).thenReturn(templateBindings);

        //act:
        Object resultObject = instance.readAllForClass(placeholderId, A_TEST_EMPTY_FILTER, A_TEST_SORT, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, NOT_DETAILED, TestHelper_Variable.INCLUDE);

        //assert:
        verify(emailTemplateService).getAllActive();
        verify(emailTemplateService, times(listEmailTemplate.size())).fetchTemplateBindings(any(EmailTemplate.class));
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAllForClass_Detailed_NotIncludeBindings() {
        System.out.println("readAllForClass_Detailed_NotIncludeBindings");

        //arrange:
        when(emailTemplateService.getAllActive()).thenReturn(listEmailTemplate);

        //act:
        Object resultObject = instance.readAllForClass(placeholderId, A_TEST_EMPTY_FILTER, A_TEST_SORT, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED, TestHelper_Variable.DONT_INCLUDE);

        //assert:
        verify(emailTemplateService).getAllActive();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAllForClass_NotDetailed_NotIncludeBindings() {
        System.out.println("readAllForClass_NotDetailed_NotIncludeBindings");

        //arrange:
        when(emailTemplateService.getAllActive()).thenReturn(listEmailTemplate);

        //act:
        Object resultObject = instance.readAllForClass(placeholderId, A_TEST_EMPTY_FILTER, A_TEST_SORT, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, NOT_DETAILED, TestHelper_Variable.DONT_INCLUDE);

        //assert:
        verify(emailTemplateService).getAllActive();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        when(emailTemplateService.getAllActive()).thenReturn(listEmailTemplate);
        when(emailTemplateService.fetchTemplateBindings(any(EmailTemplate.class))).thenReturn(templateBindings);

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_SORT, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED, TestHelper_Variable.INCLUDE);

        //assert:
        verify(emailTemplateService).getAllActive();
        verify(emailTemplateService, times(listEmailTemplate.size())).fetchTemplateBindings(any(EmailTemplate.class));
        checkListNames(expListNames, resultObject);
    }
}
