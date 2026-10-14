/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.calendar.inner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.cmdbuild.utils.lang.CmExecutorUtils;

/**
 * Wraps asynchronous invocations to be easily testable with a synchronous
 * behavior.
 *
 * @author afelice
 */
public class ExecutorServiceWrapper {

    private final ExecutorService notificationProcessorService;

    ExecutorServiceWrapper(ExecutorService notificationProcessorService) {
        this.notificationProcessorService = notificationProcessorService;
    }

    void shutdownQuietly() {
        CmExecutorUtils.shutdownQuietly(notificationProcessorService);
    }

    Future<?> submit(Runnable task) {
        return notificationProcessorService.submit(task);
    }
} // end ExecutorServiceWrapper class
