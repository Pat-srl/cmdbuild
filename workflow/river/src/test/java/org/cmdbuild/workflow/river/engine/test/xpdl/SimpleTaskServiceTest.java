package org.cmdbuild.workflow.river.engine.test.xpdl;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.utils.script.groovy.SimpleGroovyScriptServiceImpl;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverFlowService;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.data.RiverFlowRepository;
import org.cmdbuild.workflow.river.engine.data.RiverPlanRepository;
import org.cmdbuild.workflow.river.engine.lock.RiverLockService;
import org.cmdbuild.workflow.river.engine.task.RiverTaskService;
import org.cmdbuild.workflow.river.engine.task.RiverTaskServiceImpl;
import org.cmdbuild.workflow.river.engine.test.utils.InMemoryLockService;
import org.cmdbuild.workflow.river.engine.xpdl.XpdlParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTaskServiceTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RiverPlanRepository planRepository;
    private RiverFlowRepository flowRepository;

    private RiverLockService lockService;
    private RiverTaskService taskService;
    private RiverFlowService flowService;

    private RiverPlan complexProcessOne;

    private RiverFlow walk;

    @Before
    public void init() throws IOException {

        String xpdlContent = IOUtils.toString(getClass().getResourceAsStream("/ComplexProcessOne.xpdl"), Charset.defaultCharset());
        complexProcessOne = XpdlParser.parseXpdlWithDefaultOptions(xpdlContent);

        planRepository = mock(RiverPlanRepository.class);
        when(planRepository.getPlanById(complexProcessOne.getId())).thenReturn(complexProcessOne);

        flowRepository = mock(RiverFlowRepository.class);
        when(flowRepository.getFlowById(anyString())).thenAnswer((i) -> walk);

        lockService = new InMemoryLockService();
//        taskService = new SimpleTaskService(lockService, planRepository, (completedTask) -> {
//            walk = flowService.completedTask(completedTask);//complete AND save (to global variable)
//        });
        taskService = new RiverTaskServiceImpl((t) -> null, new SimpleGroovyScriptServiceImpl());

//        flowService = new RiverFlowServiceImpl(planRepository, flowRepository, taskService, (t, c) -> {
//            //TODO
//        });
    }

    @Test
    @Ignore//TODO fix this test, use a process with a script
    public void testLockService() {
//        String entryPointId = getOnlyElement(complexProcessOne.getEntryPointStepIds());
//
//        walk = flowService.createFlow(complexProcessOne.getId());
//        assertNotNull(walk);
//        assertEquals(FlowStatus.READY, walk.getStatus());
//
//        AquiredLock lock = lockService.aquireLock(walk.getId()).aquired();
//
//        walk = flowService.startFlow(walk, entryPointId);
//        assertNotNull(walk);
//        assertEquals(FlowStatus.RUNNING, walk.getStatus());
//
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException ex) {
//        }
//
//        assertFalse(taskService.hasUserTasks());
//
//        lockService.releaseLock(lock);
//
//        checkAndCompleteNextUserTask("StepOne");
    }

    @Test
    @Ignore("TODO")
    public void testComplexProcessOneRunning() {
//        String entryPointId = getOnlyElement(complexProcessOne.getEntryPointStepIds());
//
//        walk = flowService.createFlow(complexProcessOne.getId());
//        assertNotNull(walk);
//        assertEquals(FlowStatus.READY, walk.getStatus());
//
//        walk = flowService.startFlow(walk, entryPointId);
//        assertNotNull(walk);
//        assertEquals(FlowStatus.RUNNING, walk.getStatus());
//
//        checkAndCompleteNextUserTask("StepOne");
//
//        {
//            waitForUserTasks(2);
//            List<RiverLiveTask> tasks = asList(taskService.popUserTask(), taskService.popUserTask());
//            assertFalse(taskService.hasUserTasks());
//            checkAndCompleteUserTask(tasks, "StepTwo");
//            assertFalse(taskService.hasUserTasks());
//            checkAndCompleteUserTask(tasks, "StepThree");
//        }
//
//        {
//            waitForUserTasks(2);
//            List<RiverLiveTask> tasks = asList(taskService.popUserTask(), taskService.popUserTask());
//            assertFalse(taskService.hasUserTasks());
//            checkAndCompleteUserTask(tasks, "StepFour");
//            assertFalse(taskService.hasUserTasks());
//            checkAndCompleteUserTask(tasks, "StepFive");
//        }
//
//        waitFor(() -> equal(FlowStatus.COMPLETE, walk.getStatus()));
    }

//    private void checkAndCompleteNextUserTask(String expectedTaskId) {
//        waitForUserTasks(1);
//        RiverLiveTask task = taskService.popUserTask();
//        assertFalse(taskService.hasUserTasks());
//        checkAndCompleteUserTask(task, expectedTaskId);
//    }
//
//    private void checkAndCompleteUserTask(Collection<RiverLiveTask> tasks, String expectedTaskId) {
//        Optional<RiverLiveTask> task = tasks.stream().filter((t) -> t.getTaskId().equals(expectedTaskId)).findAny();
//        assertTrue(task.isPresent());
//        checkAndCompleteUserTask(task.get(), expectedTaskId);
//    }
//
//    private void checkAndCompleteUserTask(RiverLiveTask task, String expectedTaskId) {
//        assertTrue(task.getTask().isUser());
//        assertEquals(expectedTaskId, task.getTaskId());
//        taskService.completeUserTask(new CompletedTaskImpl(task));
//    }
//
//    private void waitForUserTasks(int count) {
//        waitFor(() -> taskService.countUserTasks() >= count);
//    }
//
//    private void waitFor(Supplier<Boolean> test) {
//        for (int i = 0; i < 10; i++) {
//            if (test.get()) {
//                return;
//            }
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException ex) {
//            }
//        }
//        fail();
//    }
}
