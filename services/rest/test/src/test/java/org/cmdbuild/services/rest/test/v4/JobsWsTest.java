package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunStats;
import org.cmdbuild.jobs.JobRunStatus;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.JobsWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.JobsWs;
import org.cmdbuild.service.rest.v4.model.WsJobData;
import org.cmdbuild.service.rest.v4.model.WsJobRunTriggerData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Page;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.cmdbuild.jobs.JobRunStatus.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildJobDataList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildJobRunPagedElements;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsJobData;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class JobsWsTest extends WsTestBase {

    private JobsWs instance;

    private final WsQueryOptions wsQueryOptions;
    private final List<JobData> jobDataList;
    private final WsJobData wsJobData;
    private final PagedElements<JobRun> jobRunPagedElements;

    public JobsWsTest() {
        JobsWsCommand command = new JobsWsCommand(jobService);
        this.instance = new JobsWs(command);

        this.wsQueryOptions = mockBuildWsQueryOptions(DETAILED);
        this.jobDataList = mockBuildJobDataList();
        this.wsJobData = mockBuildWsJobData(A_KNOWN_CODE1, "cool");
        this.jobRunPagedElements = mockBuildJobRunPagedElements();
    }

    @Test
    public void testReadMany() {
        System.out.println("testReadMany");

        //arrange:
        when(jobService.getAllJobs()).thenReturn(jobDataList);

        //act:
        Object resultObject = instance.readMany(wsQueryOptions);

        //assert:
        verify(jobService).getAllJobs();
        checkResultList(list(A_KNOWN_JOBDATA_ID1, A_KNOWN_JOBDATA_ID2, A_KNOWN_JOBDATA_ID3), resultObject, "_id");
    }

    @Test
    public void testReadOne() {
        System.out.println("testReadOne");

        //arrange:
        when(jobService.getOneByIdOrCode(A_KNOWN_GENERIC_ID1)).thenReturn(jobDataList.get(0));

        //act:
        Object resultObject = instance.readOne(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(jobService).getOneByIdOrCode(A_KNOWN_GENERIC_ID1);
        checkResult(A_KNOWN_JOBDATA_ID1, resultObject, "_id");
    }

    @Test
    public void testCreate() {
        System.out.println("testCreate");

        //arrange:
        when(jobService.createJob(any(JobData.class))).thenReturn(jobDataList.get(0));

        //act:
        Object resultObject = instance.create(wsJobData);

        //assert:
        verify(jobService).createJob(any(JobData.class));
        checkResult(A_KNOWN_JOBDATA_ID1, resultObject, "_id");
    }

    @Test
    public void testUpdate() {
        System.out.println("testUpdate");

        //arrange:
        when(jobService.getOneByIdOrCode(A_KNOWN_GENERIC_ID1)).thenReturn(jobDataList.get(0));
        when(jobService.updateJob(any(JobData.class))).thenReturn(jobDataList.get(1));

        //act:
        Object resultObject = instance.update(A_KNOWN_GENERIC_ID1, wsJobData);

        //assert:
        verify(jobService).getOneByIdOrCode(A_KNOWN_GENERIC_ID1);
        verify(jobService).updateJob(any(JobData.class));
        checkResult(A_KNOWN_JOBDATA_ID2, resultObject, "_id");
    }

    @Test
    public void testDelete() {
        System.out.println("testDelete");

        //arrange:
        when(jobService.getOneByIdOrCode(A_KNOWN_GENERIC_ID1)).thenReturn(jobDataList.get(0));

        //act:
        Object resultObject = instance.delete(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(jobService).deleteJob(anyLong());
        checkSuccess(resultObject);
    }


    @Test
    public void testRunJobNow() {
        System.out.println("testRunJobNow");

        //arrange:
        when(jobService.getOneByIdOrCode(A_KNOWN_GENERIC_ID1)).thenReturn(jobDataList.get(0));
        when(jobService.runJob(anyLong(), anyMap())).thenReturn(jobRunPagedElements.elements().get(0));

        //act:
        Object resultObject = instance.runJobNow(A_KNOWN_GENERIC_ID1, new WsJobRunTriggerData(emptyMap()));

        //assert:
        verify(jobService).getOneByIdOrCode(A_KNOWN_GENERIC_ID1);
        verify(jobService).runJob(anyLong(), anyMap());
        checkResult(A_KNOWN_CODE1, resultObject, "jobCode");
    }

    @Test
    public void testGetJobRunsForJob() {
        System.out.println("testGetJobRunsForJob");

        //arrange:
        when(jobService.getOneByIdOrCode(A_KNOWN_GENERIC_ID1)).thenReturn(jobDataList.get(0));
        when(jobService.getJobRuns(anyLong(), any(DaoQueryOptions.class))).thenReturn(jobRunPagedElements);

        //act:
        Object resultObject = instance.getJobRunsForJob(A_KNOWN_GENERIC_ID1, wsQueryOptions);

        //assert:
        verify(jobService).getOneByIdOrCode(A_KNOWN_GENERIC_ID1);
        verify(jobService).getJobRuns(anyLong(), any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_CODE1, A_KNOWN_CODE2, A_KNOWN_CODE3), resultObject, "jobCode");
    }

    @Test
    public void testGetJobRunErrorsForJob() {
        System.out.println("testGetJobRunErrorsForJob");

        //arrange:
        when(jobService.getOneByIdOrCode(A_KNOWN_GENERIC_ID1)).thenReturn(jobDataList.get(0));
        when(jobService.getJobErrors(anyLong(), any(DaoQueryOptions.class))).thenReturn(jobRunPagedElements);

        //act:
        Object resultObject = instance.getJobRunErrorsForJob(A_KNOWN_GENERIC_ID1, A_TEST_LONG_OFFSET, A_TEST_LONG_LIMIT);

        //assert:
        verify(jobService).getOneByIdOrCode(A_KNOWN_GENERIC_ID1);
        verify(jobService).getJobErrors(anyLong(), any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_CODE1, A_KNOWN_CODE2, A_KNOWN_CODE3), resultObject, "jobCode");
    }

    @Test
    public void testGetJobRuns() {
        System.out.println("testGetJobRuns");

        //arrange:
        when(jobService.getJobRuns(any(DaoQueryOptions.class))).thenReturn(jobRunPagedElements);

        //act:
        Object resultObject = instance.getJobRuns(wsQueryOptions);

        //assert:
        verify(jobService).getJobRuns(any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_CODE1, A_KNOWN_CODE2, A_KNOWN_CODE3), resultObject, "jobCode");
    }

    @Test
    public void testGetJobRunErrors() {
        System.out.println("testGetJobRunErrors");

        //arrange:
        when(jobService.getJobErrors(any(DaoQueryOptions.class))).thenReturn(jobRunPagedElements);

        //act:
        Object resultObject = instance.getJobRunErrors(A_TEST_LONG_OFFSET, A_TEST_LONG_LIMIT);

        //assert:
        verify(jobService).getJobErrors(any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_CODE1, A_KNOWN_CODE2, A_KNOWN_CODE3), resultObject, "jobCode");
    }

    @Test
    public void testGetJobRun() {
        System.out.println("testGetJobRun");

        //arrange:
        when(jobService.getJobRun(A_KNOWN_LONG_ID1)).thenReturn(jobRunPagedElements.elements().get(0));

        //act:
        Object resultObject = instance.getJobRun(A_KNOWN_LONG_ID1);

        //assert:
        verify(jobService).getJobRun(A_KNOWN_LONG_ID1);
        checkResult(A_KNOWN_CODE1, resultObject, "jobCode");
    }

    @Test
    public void testReadJobRunStats() {
        System.out.println("testReadJobRunStats");

        //arrange:
        Map<String, Object> expMap = map(
                "running", 10L,
                "completed", 5L,
                "failed", 1L
        );
        JobRunStats jobRunStats = () -> map(
                JRS_RUNNING, 10L,
                JRS_COMPLETED, 5L,
                JRS_FAILED, 1L);
        when(jobService.getJobRunStats()).thenReturn(jobRunStats);

        //act:
        Object resultObject = instance.readJobRunStats();

        //assert:
        verify(jobService).getJobRunStats();
        checkResponse(expMap, resultObject);
    }
}

