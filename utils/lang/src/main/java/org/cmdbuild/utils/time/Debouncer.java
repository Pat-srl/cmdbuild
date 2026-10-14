/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.time;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;

/**
 * @author ldare
 */
public class Debouncer implements AutoCloseable {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private final long delayMs;
    private final AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

    public Debouncer(long delayMs) {
        this.delayMs = delayMs;
    }

    public Boolean trigger() {
        synchronized (this) {
            if (System.currentTimeMillis() - startTime.get() < delayMs) {
                return false;
            }
            startTime.set(System.currentTimeMillis() + delayMs);
        }
        sleepSafe(delayMs);
        return true;
    }

    @Override

    public void close() {
        executor.close();
    }
}
