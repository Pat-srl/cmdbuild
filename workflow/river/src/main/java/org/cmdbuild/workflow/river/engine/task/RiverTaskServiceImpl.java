/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyMap;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.script.groovy.GroovyScriptService;
import org.cmdbuild.workflow.river.engine.RiverLiveTask;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import static org.cmdbuild.workflow.river.engine.RiverTaskType.NOP;
import static org.cmdbuild.workflow.river.engine.RiverTaskType.SCRIPT_BATCH;
import static org.cmdbuild.workflow.river.engine.RiverTaskType.SCRIPT_INLINE;
import org.cmdbuild.workflow.river.engine.core.CompletedTaskImpl;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.TaskClassLoaderSupplier;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.TaskScriptExecutorService;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.TaskScriptExecutorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiverTaskServiceImpl implements RiverTaskService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TaskScriptExecutorService taskScriptExecutorService;

    public RiverTaskServiceImpl(TaskClassLoaderSupplier taskClassLoaderSupplier, GroovyScriptService groovyScriptService) {
        taskScriptExecutorService = new TaskScriptExecutorServiceImpl(groovyScriptService, taskClassLoaderSupplier);
    }

    protected Map<String, Object> getExtraDataAndApiForTaskExecutor(RiverLiveTask liveTask) {
        return emptyMap();
    }

    @Override
    public RiverTaskCompleted executeTask(RiverLiveTask liveTask) {
        logger.debug("submit for execution task = {} (next action will depend on task type)", liveTask);
        RiverTask task = liveTask.getTask();
        checkArgument(task.isOfType(SCRIPT_BATCH, SCRIPT_INLINE, NOP));
        logger.debug("execute task {}", liveTask);
        RiverTaskCompleted completedTask;
        if (task.isNoop()) {
            logger.debug("task is NOOP, no execution is required");
            completedTask = new CompletedTaskImpl(liveTask);
        } else {
            logger.debug("task is script, preparing data for execution");
            Map<String, Object> dataIn = map(liveTask.getFlow().getData()).with(getExtraDataAndApiForTaskExecutor(liveTask));
            logger.debug("begin task script execution for task = {}", liveTask);
            completedTask = taskScriptExecutorService.executeTask(liveTask, dataIn);
            logger.debug("completed task script execution for task = {}", liveTask);
        }
        return completedTask;
//        switch (task.getTaskType()) {
//            case USER:
//                //do nothing, will add user task later
////				logger.debug("add user task to task list");
////				taskRepository.createTask(task);
//                return queuedTask();
//            case SCRIPT_BATCH:
//                submitJob(task);
//                return queuedTask();
//            case SCRIPT_INLINE:
//            case NOP:
//                return inlineTask(executeTask(task));
//            default:
//                throw new IllegalArgumentException("unsupported task type " + task.getTaskType());
//        }
    }

//    private void submitJob(RiverLiveTask task) {
//        String user = "admin";//TODO get user from live task
//        executorService.executeJobAs(() -> {
//            aquireLockAndExecuteTask(task);
//        }, user);
//    }
//
////    private void aquireLockAndExecuteTask(RiverLiveTask task) {
//        logger.debug("aquire lock for task {}", task);
//        LockResponse lockResponse = lockService.aquireLock(task.getFlowId());
//        if (lockResponse.isAquired()) {
//            AquiredLock aquiredLock = lockResponse.aquired();
//            try {
//                RiverTaskCompleted completedTask = executeTask(task);
//                consumeCompletedTask(completedTask);
//            } finally {
//                lockService.releaseLock(aquiredLock);
//            }
//        } else {
//            logger.debug("unable to aquire lock, re-scheduling");
//            String waitUser = "admin";//TODO replace with low power system user
//            executorService.executeJobLaterAs(() -> {
//                submitJob(task);
//            }, waitUser, 100);
//        }
//    }
//    private void consumeCompletedTask(RiverTaskCompleted completedTask) {
//        logger.debug("consuming completed task = {}", completedTask.getTask());
//        completedTaskConsumer.accept(completedTask);
//    }
//    private RiverTaskCompleted executeTask(RiverLiveTask liveTask) {
//    }
}
