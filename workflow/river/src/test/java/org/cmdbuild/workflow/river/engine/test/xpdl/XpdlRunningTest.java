/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.test.xpdl;

import org.cmdbuild.workflow.river.engine.xpdl.XpdlParser;
import com.google.common.base.Stopwatch;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import org.cmdbuild.workflow.river.engine.RiverFlowStatus;
import org.cmdbuild.workflow.river.engine.core.RiverFlowServiceImpl;
import org.cmdbuild.workflow.river.engine.core.CompletedTaskImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.river.engine.RiverFlowService;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.data.RiverPlanRepository;
import org.cmdbuild.workflow.river.engine.task.LiveTaskImpl;
import org.cmdbuild.workflow.river.engine.task.RiverTaskService;
import org.cmdbuild.workflow.river.engine.task.RiverTaskServiceImpl;
import org.cmdbuild.utils.script.groovy.SimpleGroovyScriptServiceImpl;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.TaskScriptExecutorService;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.TaskScriptExecutorServiceImpl;
import org.junit.Ignore;

public class XpdlRunningTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static RiverPlan RFC = XpdlParser.parseXpdlWithDefaultOptions(readToString(XpdlRunningTest.class.getResourceAsStream("/RequestForChange.xpdl"))),
            RFC_G = XpdlParser.parseXpdlWithDefaultOptions(readToString(XpdlRunningTest.class.getResourceAsStream("/RequestForChange_groovy.xpdl"))),
            CPO = XpdlParser.parseXpdlWithDefaultOptions(readToString(XpdlRunningTest.class.getResourceAsStream("/ComplexProcessOne.xpdl")));

    private RiverPlanRepository planRepository;
//    private RiverFlowRepository flowRepository;

    private RiverTaskService taskService;
    private RiverFlowService flowService;

    private RiverFlow flow;

    private final TaskScriptExecutorService taskScriptExecutorService = new TaskScriptExecutorServiceImpl(new SimpleGroovyScriptServiceImpl(), (t) -> null);

    @Before
    public void init() throws IOException {
        planRepository = mock(RiverPlanRepository.class);
        when(planRepository.getPlanById(RFC.getId())).thenReturn(RFC);
        when(planRepository.getPlanById(CPO.getId())).thenReturn(CPO);
        when(planRepository.getPlanById(RFC_G.getId())).thenReturn(RFC_G);
//        flowRepository = mock(RiverFlowRepository.class);
//        when(flowRepository.getFlowById(anyString())).thenAnswer((i) -> flow);
        taskService = new RiverTaskServiceImpl((t) -> null, new SimpleGroovyScriptServiceImpl());
        flowService = new RiverFlowServiceImpl(planRepository, taskService);
    }

    @Test
    @Ignore("TODO fix this")
    public void testRequestForChangeRunning() {
        runRequestForChange(RFC);
    }

    @Test
    @Ignore("TODO fix this")
    public void testRequestForChangeRunningWithGroovy() {
        runRequestForChange(RFC_G);
    }

    @Test
    @Ignore
    public void testRequestForChangeRunningPerformance() {
        runRequestForChange(RFC);//cache stuff
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < 100; i++) {
            runRequestForChange(RFC);
        }
        stopwatch.stop();
        logger.info("testRequestForChangeRunningPerformance total time = {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    @Ignore
    public void testRequestForChangeRunningWithGroovyPerformance() {
        runRequestForChange(RFC_G);//cache stuff

        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < 100; i++) {
            runRequestForChange(RFC_G);
        }
        stopwatch.stop();
        logger.info("testRequestForChangeRunningWithGroovyPerformance total time = {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    private void runRequestForChange(RiverPlan plan) {
        String entryPointId = getOnlyElement(plan.getEntryPointStepIds());

        flow = flowService.createFlow(plan.getId());
        assertNotNull(flow);
        assertEquals(RiverFlowStatus.READY, flow.getStatus());

        flow = flowService.startFlow(flow, entryPointId);
        assertNotNull(flow);
        assertEquals(RiverFlowStatus.RUNNING, flow.getStatus());

//		checkAndCompleteNextTask("Process_requestforchange_act1_noop", RiverTask::isNoop);
        assertEquals(1, flow.getTaskCount());
        checkAndCompleteTask("RegisterRFC");

//        checkAndCompleteNextTask("SYS010_script", RiverTask::isInline);
        assertEquals(1, flow.getTaskCount());
        checkAndCompleteTask("FormalEvaluation");

//        checkAndCompleteNextTask("SYS020_script", RiverTask::isInline);
//        checkAndCompleteNextTask("check_SYS020_task", RiverTask::isInline);
//        {
//            RiverTask task = flow.getOnlyTask();
//            assertTrue(task.isInline());
//            assertEquals("check_SYS020_task", task.getId());
//
//            RiverTaskCompleted completedTask = taskScriptExecutorService.executeTask(new RiverLiveTask(task), map("FormalEvaluation", new LookupType(1, "myType", "desc", "REJECTED")));
//
//            flow = flowService.completedTask(completedTask);
//        }
        assertEquals(1, flow.getTaskCount());
        checkAndCompleteTask("FinalEvaluation");

//        checkAndCompleteNextTask("SYS070_script", RiverTask::isInline);
//
//        checkAndCompleteNextTask("Process_requestforchange_act2_noop", RiverTask::isNoop);
        assertEquals(0, flow.getTaskCount());
        assertEquals(RiverFlowStatus.COMPLETE, flow.getStatus());
    }

    @Test
    public void testComplexProcessOneRunning() {
        String entryPointId = getOnlyElement(CPO.getEntryPointStepIds());

        flow = flowService.createFlow(CPO.getId());
        assertNotNull(flow);
        assertEquals(RiverFlowStatus.READY, flow.getStatus());

        flow = flowService.startFlow(flow, entryPointId);
        assertNotNull(flow);
        assertEquals(RiverFlowStatus.RUNNING, flow.getStatus());

        flow = flowService.suspendFlow(flow);
        assertEquals(RiverFlowStatus.SUSPENDED, flow.getStatus());

        flow = flowService.resumeFlow(flow);
        assertEquals(RiverFlowStatus.RUNNING, flow.getStatus());

        assertEquals(1, flow.getTaskCount());
        checkAndCompleteTask("StepOne");

//        checkAndCompleteNextTask("ComplexProcessOne_wp1_act4_noop", RiverTask::isNoop);
//        {
//            List<RiverLiveTask> tasks = asList(taskService.popTask(), taskService.popTask());
        assertEquals(2, flow.getTaskCount());
        checkAndCompleteTask("StepTwo");
        assertEquals(1, flow.getTaskCount());
//            assertEquals(0,flow.getTaskCount());
        checkAndCompleteTask("StepThree");
//        }

//        checkAndCompleteNextTask("ComplexProcessOne_wp1_act11_noop", RiverTask::isNoop);
        assertEquals(2, flow.getTaskCount());
        checkAndCompleteTask("StepFive");
//            assertEquals(1,flow.getTaskCount());
////            assertEquals(0,flow.getTaskCount());
//            checkAndCompleteTask( "StepThree");
//        {
//            List<RiverLiveTask> tasks = asList(taskService.popTask(), taskService.popTask());
//            assertEquals(0,flow.getTaskCount());
//            checkAndCompleteTask(tasks, "StepFive", RiverTask::isUser);
//            assertEquals(0,flow.getTaskCount());
//            checkAndCompleteTask(tasks, "ComplexProcessOne_wp1_act9_noop", RiverTask::isNoop);
//        }

        assertEquals(1, flow.getTaskCount());
        checkAndCompleteTask("StepFour");

        assertEquals(1, flow.getTaskCount());
        checkAndCompleteTask("StepSix");

//        checkAndCompleteNextTask("ComplexProcessOne_wp1_act10_noop", RiverTask::isNoop);
//
//        checkAndCompleteNextTask("ComplexProcessOne_wp1_act2_noop", RiverTask::isNoop);
        assertEquals(0, flow.getTaskCount());
        assertEquals(RiverFlowStatus.COMPLETE, flow.getStatus());
    }

//    private void checkAndCompleteNextTask(String expectedTaskId, Function<RiverTask, Boolean> taskTypeCheck) {
//        RiverTask task = flow.getTaskById(expectedTaskId);
//        assertEquals(1, flow.getTaskCount());
//        checkAndCompleteTask(new LiveTaskImpl(flow, task), expectedTaskId, taskTypeCheck);
//    }
    private void checkAndCompleteTask(String expectedTaskId) {
        RiverTask task = flow.getTaskById(expectedTaskId);
        assertEquals(expectedTaskId, task.getId());
        flow = flowService.completedTask(flow, new CompletedTaskImpl(new LiveTaskImpl(flow, task)));
//        assertEquals(1, flow.getTaskCount());
//        checkAndCompleteTask(new LiveTaskImpl(flow, task), expectedTaskId, taskTypeCheck);
    }
//
//    private void checkAndCompleteTask(Collection<RiverLiveTask> tasks, String expectedTaskId, Function<RiverTask, Boolean> taskTypeCheck) {
//        Optional<RiverLiveTask> task = tasks.stream().filter((t) -> t.getTaskId().equals(expectedTaskId)).findAny();
//        assertTrue(task.isPresent());
//        checkAndCompleteTask(task.get(), expectedTaskId, taskTypeCheck);
//    }

//    private void checkAndCompleteTask(RiverLiveTask task, String expectedTaskId, Function<RiverTask, Boolean> taskTypeCheck) {
//        assertTrue(taskTypeCheck.apply(task.getTask()));
//        assertEquals(expectedTaskId, task.getTaskId());
//
//        flow = flowService.completedTask(new CompletedTaskImpl(task));
//    }
}
