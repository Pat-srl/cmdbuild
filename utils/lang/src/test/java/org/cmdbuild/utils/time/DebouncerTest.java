/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.time;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author ataboga
 */
public class DebouncerTest {

    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Test
    public void debouncerTest() throws InterruptedException, ExecutionException {
        try (Debouncer debouncer = new Debouncer(1000)) {
            sleepSafe(2000);
            Future<Boolean> result1 = executor.submit(debouncer::trigger);
            sleepSafe(10);
            Future<Boolean> result2 = executor.submit(debouncer::trigger);
            sleepSafe(10);
            Future<Boolean> result3 = executor.submit(debouncer::trigger);
            sleepSafe(10);
            Future<Boolean> result4 = executor.submit(debouncer::trigger);
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
//            System.out.println("1: " + result1.get());
//            System.out.println("2: " + result2.get());
//            System.out.println("3: " + result3.get());
//            System.out.println("4: " + result4.get());
            assertFalse(result2.get());
            assertFalse(result3.get());
            assertFalse(result4.get());
            assertTrue(result1.get());
        }
    }
}
