/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestInfo;
import org.cmdbuild.service.rest.v4.command.AuditWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.AuditWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildRequestData;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildRequestInfoList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class AuditWsTest extends WsTestBase {

    private final AuditWs instance;

    private final List<RequestInfo> requestInfoList;
    private final RequestData requestData;

    public AuditWsTest() {
        AuditWsCommand command = new AuditWsCommand(requestTrackingRepository);
        this.instance = new AuditWs(command);
        this.requestInfoList = mockBuildRequestInfoList();
        this.requestData = mockBuildRequestData(A_KNOWN_GENERIC_ID1);
    }

    @Test
    public void testMark() {
        System.out.println("mark");

        //arrange:
        CmMapUtils.FluentMap<String, Object> expectedResult = map("success", null, "data", map("mark", null));

        //act:
        Object resultObject = instance.mark();

        //assert:
        checkSuccess(resultObject);
        checkResultStructure(expectedResult, (Map<String, Object>) resultObject);
    }

    @Test
    public void testGetRequests_withMark() {
        System.out.println("getRequests_withMark");

        //arrange:
        when(requestTrackingRepository.getRequestsSince(any(ZonedDateTime.class))).thenReturn(requestInfoList);

        //act:
        Object resultObject = instance.getRequests("123", 0L);

        //assert:
        verify(requestTrackingRepository).getRequestsSince(any(ZonedDateTime.class));
        checkResultList(list(A_KNOWN_GENERIC_ID1, A_KNOWN_GENERIC_ID2, A_KNOWN_GENERIC_ID3), resultObject, "requestId");
    }

    @Test
    public void testGetRequests_withoutMark() {
        System.out.println("getRequests_withoutMark");

        //arrange:
        when(requestTrackingRepository.getLastRequests(anyLong())).thenReturn(requestInfoList);

        //act:
        Object resultObject = instance.getRequests("", 10L);

        //assert:
        verify(requestTrackingRepository).getLastRequests(anyLong());
        checkResultList(list(A_KNOWN_GENERIC_ID1, A_KNOWN_GENERIC_ID2, A_KNOWN_GENERIC_ID3), resultObject, "requestId");
    }

    @Test
    public void testGetErrors_withMark() {
        System.out.println("getErrors_withMark");

        //arrange:
        when(requestTrackingRepository.getErrorsSince(any(ZonedDateTime.class))).thenReturn(requestInfoList);

        //act:
        Object resultObject = instance.getErrors("123", 0L);

        //assert:
        verify(requestTrackingRepository).getErrorsSince(any(ZonedDateTime.class));
        checkResultList(list(A_KNOWN_GENERIC_ID1, A_KNOWN_GENERIC_ID2, A_KNOWN_GENERIC_ID3), resultObject, "requestId");
    }

    @Test
    public void testGetErrors_withoutMark() {
        System.out.println("getErrors_withoutMark");

        //arrange:
        when(requestTrackingRepository.getLastErrors(anyLong())).thenReturn(requestInfoList);

        //act:
        Object resultObject = instance.getErrors("", 10L);

        //assert:
        verify(requestTrackingRepository).getLastErrors(anyLong());
        checkResultList(list(A_KNOWN_GENERIC_ID1, A_KNOWN_GENERIC_ID2, A_KNOWN_GENERIC_ID3), resultObject, "requestId");
    }

    @Test
    public void testGetRequest() {
        System.out.println("getRequests");

        //arrange:
        when(requestTrackingRepository.getRequest(A_KNOWN_GENERIC_ID1)).thenReturn(requestData);

        //act:
        Object resultObject = instance.getRequest(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(requestTrackingRepository).getRequest(A_KNOWN_GENERIC_ID1);
        checkResult(A_KNOWN_GENERIC_ID1, resultObject, "requestId");
    }
}
