/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.etl.webhook.WebhookConfig;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlWebhookWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EtlWebhookWs_Management;
import org.cmdbuild.service.rest.v4.model.WsEtlWebhookData;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildClasseWithAttr;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildWebhookConfig;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsEtlWebhookData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class EtlWebhookWs_ManagementTest extends WsTestBase {

    private final EtlWebhookWs_Management instance;

    private final WebhookConfig webhookConfig1;
    private final WebhookConfig webhookConfig2;
    private final WebhookConfig webhookConfig3;
    private final List<WebhookConfig> listWebhookConfig;
    private final WsQueryOptions wsQueryOptionsDetailed;
    private final WsQueryOptions wsQueryOptionsNotDetailed;
    private final Classe classe;
    private final List<String> expListCodes;
    private final String paramToCheck = "_target_type";
    private final WsEtlWebhookData wsEtlWebhookData;

    public EtlWebhookWs_ManagementTest() {
        EtlWebhookWsCommand command = new EtlWebhookWsCommand(webhookService);
        instance = new EtlWebhookWs_Management(classeRepository, command);
        webhookConfig1 = mockBuildWebhookConfig(A_KNOWN_WEBHOOK_CODE1);
        webhookConfig2 = mockBuildWebhookConfig(A_KNOWN_WEBHOOK_CODE2);
        webhookConfig3 = mockBuildWebhookConfig(A_KNOWN_WEBHOOK_CODE3);
        listWebhookConfig = list(webhookConfig1, webhookConfig2, webhookConfig3);
        wsQueryOptionsDetailed = TestHelper_WSData.mockBuildWsQueryOptions(DETAILED);
        wsQueryOptionsNotDetailed = TestHelper_WSData.mockBuildWsQueryOptions(NOT_DETAILED);
        classe = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME1);
        expListCodes = list(A_KNOWN_WEBHOOK_CODE1, A_KNOWN_WEBHOOK_CODE2, A_KNOWN_WEBHOOK_CODE3);
        wsEtlWebhookData = mockBuildWsEtlWebhookData();
    }

    @Test
    public void testReadAll_Detailed() {
        System.out.println("readAll_Detailed");

        //arrange:
        when(webhookService.getAll()).thenReturn(listWebhookConfig);
        when(classeRepository.getClasse(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.readAll(wsQueryOptionsDetailed);

        //assert:
        verify(webhookService).getAll();
        verify(classeRepository, times(listWebhookConfig.size())).getClasse(anyString());
        checkListIds(expListCodes, resultObject);
        checkDetailedList(resultObject, paramToCheck);
    }

    @Test
    public void testReadAll_NotDetailed() {
        System.out.println("readAll_NotDetailed");

        //arrange:
        when(webhookService.getAll()).thenReturn(listWebhookConfig);

        //act:
        Object resultObject = instance.readAll(wsQueryOptionsNotDetailed);

        //assert:
        verify(webhookService).getAll();
        checkListIds(expListCodes, resultObject);
        checkNotDetailedList(resultObject, paramToCheck);
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        when(webhookService.getByName(A_KNOWN_WEBHOOK_CODE1)).thenReturn(webhookConfig1);
        when(classeRepository.getClasse(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_WEBHOOK_CODE1);

        //assert:
        verify(webhookService).getByName(A_KNOWN_WEBHOOK_CODE1);
        verify(classeRepository).getClasse(anyString());
        checkId(A_KNOWN_WEBHOOK_CODE1, resultObject);
        checkDetailed(resultObject, paramToCheck);
    }
}
