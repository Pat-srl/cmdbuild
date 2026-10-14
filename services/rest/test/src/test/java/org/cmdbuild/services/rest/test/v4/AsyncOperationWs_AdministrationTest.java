/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.service.rest.v4.command.AsyncOperationWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.AsyncOperationWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.junit.Test;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResponse;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_JOB_ID;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author ldare
 */
public class AsyncOperationWs_AdministrationTest extends WsTestBase {

    private final AsyncOperationWs_Administration instance;
    private final CmMapUtils.FluentMap<String, Object> expAsyncRequestJobSerialization;

    public AsyncOperationWs_AdministrationTest() {
        AsyncOperationWsCommand command = new AsyncOperationWsCommand(asyncRequestJobService);
        instance = new AsyncOperationWs_Administration(command);

        expAsyncRequestJobSerialization = map(
                "_id", A_KNOWN_JOB_ID,
                "status", "running",
                "_completed", false);
    }

    @Test
    public void testGetAsyncJobStatus() {
        System.out.println("getAsyncJobStatus");

        // arrange
        when(asyncRequestJobService.getJobForCurrentUserById(A_KNOWN_JOB_ID)).thenReturn(asyncRequestJob);

        // act
        Object resultObject = instance.getAsyncJobStatus(A_KNOWN_JOB_ID);

        // assert
        verify(asyncRequestJobService).getJobForCurrentUserById(A_KNOWN_JOB_ID);
        checkResponse(expAsyncRequestJobSerialization, resultObject);
    }

    @Test
    public void testGetAsyncJobResult() {
        System.out.println("getAsyncJobResult");

        // arrange
        when(asyncRequestJobService.getJobForCurrentUserById(A_KNOWN_JOB_ID)).thenReturn(asyncRequestJob);
        when(asyncRequestJob.isCompleted()).thenReturn(true);
        byte[] byteResponseContent = {50};
        when(asyncRequestJob.getResponseContent()).thenReturn(byteResponseContent);

        // act
        Object resultObject = instance.getAsyncJobResult(A_KNOWN_JOB_ID);

        // assert
        verify(asyncRequestJobService).getJobForCurrentUserById(A_KNOWN_JOB_ID);
        verify(asyncRequestJobService).deleteJob(anyLong());
        verify(asyncRequestJob).isCompleted();
        verify(asyncRequestJob).getResponseContent();
        assertEquals("2", resultObject);

    }
}
