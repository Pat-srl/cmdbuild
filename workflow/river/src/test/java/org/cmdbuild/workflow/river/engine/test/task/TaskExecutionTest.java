/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.test.task;

import com.google.common.base.Stopwatch;
import java.io.IOException;
import java.nio.charset.Charset;
import static java.util.Collections.emptyMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.utils.script.groovy.SimpleGroovyScriptServiceImpl;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverLiveTask;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import org.cmdbuild.workflow.river.engine.core.RiverFlowImpl;
import org.cmdbuild.workflow.river.engine.task.LiveTaskImpl;
import org.cmdbuild.workflow.river.engine.task.ScriptTaskExtraAttr;
import org.cmdbuild.workflow.river.engine.task.TaskImpl;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.TaskScriptExecutorService;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.TaskScriptExecutorServiceImpl;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.WorkflowScriptProcessingException;
import org.cmdbuild.workflow.river.engine.test.utils.SimpleBeanToImport;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.SCRIPT_LANGUAGE_BEANSHELL;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.SCRIPT_LANGUAGE_GROOVY;
import org.cmdbuild.workflow.river.engine.xpdl.XpdlParser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskExecutionTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TaskScriptExecutorService taskScriptExecutorService = new TaskScriptExecutorServiceImpl(new SimpleGroovyScriptServiceImpl(), (t) -> null);

    private RiverPlan plan;
    private String planId;
    private RiverFlow flow;
    private String taskId;

    @Before
    public void init() throws IOException {
        String complexProcessOneXpdlContent = IOUtils.toString(getClass().getResourceAsStream("/ComplexProcessOne.xpdl"), Charset.defaultCharset());
        plan = XpdlParser.parseXpdlWithDefaultOptions(complexProcessOneXpdlContent);
        planId = plan.getId();
        String walkId = randomId();
        flow = RiverFlowImpl.builder()
                .withPlan(plan)
                .withFlowId(walkId)
                .build();
        taskId = randomId();
    }

    @Test
    public void testExceptionHandling() {
        try {
            RiverLiveTask task = new LiveTaskImpl(flow, TaskImpl.batch()
                    .withPlanId(planId)
                    .withTaskId(taskId)
                    .withExtraAttr(new ScriptTaskExtraAttr(SCRIPT_LANGUAGE_BEANSHELL, "a = 1/0;"))
                    .build());
            taskScriptExecutorService.executeTask(task, emptyMap());
            fail();
        } catch (WorkflowScriptProcessingException ex) {
            assertEquals("/ by zero", ex.getCause().getCause().getMessage());
        }
    }

    @Test
    public void testConditionHandling() {
        RiverLiveTask task = new LiveTaskImpl(flow, TaskImpl.batch()
                .withPlanId(planId)
                .withTaskId(taskId)
                .withExtraAttr(new ScriptTaskExtraAttr(SCRIPT_LANGUAGE_BEANSHELL, "if ( Confirm || Message == null ) { Res = \"hello\"; }"))
                .build());
        RiverTaskCompleted completed = taskScriptExecutorService.executeTask(task, map("Confirm", true, "Message", null));
        assertEquals("hello", completed.getLocalVariables().get("Res"));
    }

    @Test
    public void testConditionHandling2() {
        RiverLiveTask task = new LiveTaskImpl(flow, TaskImpl.batch()
                .withPlanId(planId)
                .withTaskId(taskId)
                .withExtraAttr(new ScriptTaskExtraAttr(SCRIPT_LANGUAGE_BEANSHELL, "if ( Confirm || Message == null ) { Res = \"hello\"; }"))
                .build());
        RiverTaskCompleted completed = taskScriptExecutorService.executeTask(task, map("Confirm", "true", "Message", null));
        assertEquals("hello", completed.getLocalVariables().get("Res"));
    }

    @Test
    public void testConditionHandlingWithGroovy() {
        RiverLiveTask task = new LiveTaskImpl(flow, TaskImpl.batch()
                .withPlanId(planId)
                .withTaskId(taskId)
                .withExtraAttr(new ScriptTaskExtraAttr(SCRIPT_LANGUAGE_GROOVY, "if ( Confirm || Message == null ) { Res = \"hello\"; }"))
                .build());
        RiverTaskCompleted completed = taskScriptExecutorService.executeTask(task, map("Confirm", true, "Message", null));
        assertEquals("hello", completed.getLocalVariables().get("Res"));
    }

    @Test
    public void testImportWithGroovy() {
        SimpleBeanToImport.count = 0;
        RiverLiveTask task = new LiveTaskImpl(flow, TaskImpl.batch()
                .withPlanId(planId)
                .withTaskId(taskId)
                .withExtraAttr(new ScriptTaskExtraAttr(SCRIPT_LANGUAGE_GROOVY, "\n"
                        + "									import org.cmdbuild.workflow.river.engine.test.utils.SimpleBeanToImport;\n"
                        + "									\n"
                        + "									def myBean = new SimpleBeanToImport();\n"
                        + "									\n"
                        + "									"))
                .build());
        RiverTaskCompleted completed = taskScriptExecutorService.executeTask(task, map());
//		assertEquals("hello", completed.getLocalVariables().get("Res"));
        assertEquals(1, SimpleBeanToImport.count);
    }

    private final static String scriptForPerformaceTest = "int count=0; for(int i = 0;i<10000;i++){ count+=i; count/=2; };Res = \"helloo\";";

    @Test
    @Ignore
    public void testGroovyPerformance() {
        RiverLiveTask task = new LiveTaskImpl(flow, TaskImpl.batch()
                .withPlanId(planId)
                .withTaskId(taskId)
                .withExtraAttr(new ScriptTaskExtraAttr(SCRIPT_LANGUAGE_GROOVY, scriptForPerformaceTest))
                .build());
        {
            taskScriptExecutorService.executeTask(task, map("Confirm", true, "Message", null));//load in cache
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        RiverTaskCompleted completed = taskScriptExecutorService.executeTask(task, map("Confirm", true, "Message", null));
        stopwatch.stop();
        assertEquals("helloo", completed.getLocalVariables().get("Res"));
        logger.info("testGroovyPerformance total time = {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    @Ignore
    public void testBeanshellPerformance() {
        RiverLiveTask task = new LiveTaskImpl(flow, TaskImpl.batch()
                .withPlanId(planId)
                .withTaskId(taskId)
                .withExtraAttr(new ScriptTaskExtraAttr(SCRIPT_LANGUAGE_BEANSHELL, scriptForPerformaceTest))
                .build());
        Stopwatch stopwatch = Stopwatch.createStarted();
        RiverTaskCompleted completed = taskScriptExecutorService.executeTask(task, map("Confirm", true, "Message", null));
        stopwatch.stop();
        assertEquals("helloo", completed.getLocalVariables().get("Res"));
        logger.info("testBeanshellPerformance total time = {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

}
