/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.benchmark;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import jakarta.annotation.Nullable;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.lang3.ArrayUtils;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmPlatformUtils.getProcessMemoryMegs;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static BenchmarkResults executeBenchmark() {
        return executeBenchmark(1, false);
    }

    public static BenchmarkResults executeBenchmark(int iterations, boolean failOnError) {
        return new Helper(iterations, failOnError).executeBenchmark();
    }

    public static BenchmarkResults executeBenchmark(int iterations, boolean failOnError, BenchmarkListener listener) {
        Helper helper = new Helper(iterations, failOnError);
        checkNotNull(listener);
        helper.eventBus.register(new Object() {
            @Subscribe
            public void handleBenchmarkBegin(BenchmarkBeginEventImpl begin) {
                listener.onTestBegin(begin.getCategory());
            }

            @Subscribe
            public void handleBenchmarkResult(BenchmarkResult result) {
                listener.onTestResult(result);
            }

            @Subscribe
            public void handleBenchmarkResults(BenchmarkResults results) {
                listener.onTestEnd(results);
            }
        });
        return helper.executeBenchmark();
    }

    public static Object allocateMemory(int memoryAmountMegs) {
        LOGGER.info("allocating {} megs", memoryAmountMegs);
        List list = new ArrayList(memoryAmountMegs);
        for (int i = 0; i < memoryAmountMegs; i++) {
            List blocks = new ArrayList(100);
            for (int j = 0; j < 100; j++) {
                byte[] block = new byte[10000];
                new Random().nextBytes(block);
                blocks.add(block);
            }
            list.add(blocks);
        }
        LOGGER.info("allocated {} megs", memoryAmountMegs);
        return list;
    }

    public static void allocateMemory(int memoryAmountMegs, long holdTimeSeconds) {
        Object handle = allocateMemory(memoryAmountMegs);
        LOGGER.info("sleep for {} secs", holdTimeSeconds);
        sleepSafe(holdTimeSeconds * 1000);
        LOGGER.info("release {} megs", memoryAmountMegs);
        checkNotNull(handle);//avoid undesired optimizations
    }

    public static void allocateFragmentedMemory(int memoryAmountMegs, long holdTimeSeconds, long fragmentHoldTimeSeconds) {
        LOGGER.info("allocating {} megs", memoryAmountMegs);
        long memoryAmountBytes = memoryAmountMegs * 1000000l, allocatedBytes = 0;
        List blocks = new LinkedList(), fragments = new LinkedList();
        Random random = new Random();
        while (allocatedBytes < memoryAmountBytes) {
            byte[] block = new byte[1 + random.nextInt(1000)];
            random.nextBytes(block);
            blocks.add(block);
            allocatedBytes += block.length;
            if (random.nextInt(10) == 0) {
                byte[] fragment = new byte[1 + random.nextInt(100)];
                random.nextBytes(fragment);
                fragments.add(fragment);
            }
        }
        LOGGER.info("allocated {} megs, {} fragments", memoryAmountMegs, fragments.size());
        LOGGER.info("sleep for {} secs", holdTimeSeconds);
        sleepSafe(holdTimeSeconds * 1000);
        LOGGER.info("release {} megs", memoryAmountMegs);
        checkNotNull(blocks);//avoid undesired optimizations
        blocks = null;
        LOGGER.info("hold onto fragments, sleep for {} secs", fragmentHoldTimeSeconds);
        sleepSafe(fragmentHoldTimeSeconds * 1000);
        LOGGER.info("release {} fragments", fragments.size());
    }

    public static void trackMemoryUsage() {
        new Thread(safe(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                int javaMemTotal = toIntExact((Runtime.getRuntime().totalMemory() / 1000 / 1000)),
                        javaMemUsed = toIntExact(((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000 / 1000)),
                        procMemoryUsed = getProcessMemoryMegs();
                LOGGER.info(format("heap memory:  %,d MB used, %,d MB total; process memory:  %,d MB", javaMemUsed, javaMemTotal, procMemoryUsed));
                sleepSafe(2000);
            }
        })).start();
    }

    private static class Helper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final boolean failOnError;
        private final int globalInterations;

        private final EventBus eventBus = new EventBus(logExceptions(logger));

        public Helper(int iterations, boolean failOnError) {
            this.failOnError = failOnError;
            this.globalInterations = iterations;
        }

        public BenchmarkResults executeBenchmark() {
            logger.info("system benchmark begin");
            List<BenchmarkResult> results = list();
            map(
                    "memory test", (Supplier) this::runMemoryTest,
                    "cpu test", (Supplier) this::runCpuTest,
                    "disk test", (Supplier) this::runDiskReadWriteTest
            ).forEach((desc, test) -> {
                BenchmarkResult result;
                try {
                    eventBus.post(new BenchmarkBeginEventImpl((String) desc));
                    long value = 0;
                    for (int i = 0; i < globalInterations; i++) {
                        value += ((Supplier<Long>) test).get();
                        System.gc();
                    }
                    long reference = 5000 * globalInterations;
                    double score;
                    if (value < reference) {
                        score = sqrt(reference) / sqrt(value);
                    } else {
                        score = reference / (double) value;
                    };
                    result = new BenchmarkResultImpl((String) desc, value, score, null);
                } catch (Error ex) {
                    if (failOnError) {
                        throw runtime(ex);
                    } else {
                        logger.debug("test error for test = {}", desc, ex);
                        result = new BenchmarkResultImpl((String) desc, -1, 0, ex);
                    }
                }
                results.add(result);
                eventBus.post(result);
            });
            double average = results.stream().map(BenchmarkResult::getScore).collect(averagingDouble(n -> n));

            BenchmarkResults benchmarkResults = new BenchmarkResults() {
                @Override
                public List<BenchmarkResult> getResults() {
                    return ImmutableList.copyOf(results);
                }

                @Override
                public double getAverageScore() {
                    return average;
                }
            };
            logger.info("system benchmark complete, average score = {}", average);
            eventBus.post(benchmarkResults);
            return benchmarkResults;
        }

        private long runMemoryTest() {
            int iterations = 300, elements = 40000, elementSize = 100 * 1024;

            byte[] data = new byte[elementSize];
            new Random().nextBytes(data);
            checkArgument(Runtime.getRuntime().maxMemory() > elements * elementSize, "java platform max memory is set too low; >4G expected, but found %s", byteCountToDisplaySize(Runtime.getRuntime().maxMemory()));
            List<byte[]> list = IntStream.range(0, elements).mapToObj((i) -> {
                if (i % 1000 == 0) {
                    logger.debug("allocated memory = {} ( {} elements )", byteCountToDisplaySize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()), i);
                }
                return new byte[elementSize];
            }).collect(toList());
            logger.debug("memory allocation complete, start usage testing");

            Stopwatch stopwatch = Stopwatch.createStarted();

            for (int i = 0; i < iterations; i++) {
                int count = list.size() / 50;
                Collections.shuffle(list);
                list.stream().limit(count).forEach((target) -> {
                    System.arraycopy(data, 0, target, 0, data.length);
                });
                Collections.shuffle(list);
                list.stream().limit(count).forEach((source) -> {
                    System.arraycopy(source, 0, data, 0, data.length);
                });
            }

            return stopwatch.elapsed(TimeUnit.MILLISECONDS);
        }

        private long runCpuTest() {
            int n = 4000;

            int threadCount = 4;
            List<byte[]> data = IntStream.range(0, threadCount).mapToObj(i -> {
                byte[] d = new byte[1024];
                new Random().nextBytes(d);
                return d;
            }).collect(toList());

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

            Stopwatch stopwatch = Stopwatch.createStarted();

            List<Future> futures = list();

            for (int t = 0; t < threadCount; t++) {

                byte[] d = data.get(t);

                Future future = executorService.submit(() -> {

                    for (int i = 0; i < n; i++) {

                        for (int j = 0; j < d.length; j++) {
                            double x = d[0] + ((double) Integer.MAX_VALUE);
                            double y = d[1] + ((double) Integer.MAX_VALUE);
                            double w = 1;
                            for (int k = 0; k < 100000; k++) {
                                double z = (y + k) / x;
                                w = 1 + (z / (k + 1));
                            }
                            checkArgument(w > 0);
                        }

                        for (int j = 0; j < 10; j++) {
                            List<Byte> list = Arrays.asList(ArrayUtils.toObject(d));
                            Collections.sort(list);
                        }

                    }
                });
                futures.add(future);
            }

            futures.forEach((f) -> {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException ex) {
                    throw runtime(ex);
                }
            });
            executorService.shutdown();
            try {
                checkArgument(executorService.awaitTermination(1, TimeUnit.MINUTES));
            } catch (InterruptedException ex) {
                throw runtime(ex);
            }

            return stopwatch.elapsed(TimeUnit.MILLISECONDS);

        }

        private long runDiskReadWriteTest() {
            int n = 10;

            dropSystemCache();

            Stopwatch stopwatch = Stopwatch.createStarted();

            File dir = tempDir();
            List<File> files = list();
            Random random = new Random();
            for (int i = 0; i < 100; i++) {
                File file = new File(dir, UUID.randomUUID().toString());
                byte[] data = new byte[1024 * 1024 * toIntExact(round((1 + min(1, abs(random.nextGaussian())) / 2)))];
                random.nextBytes(data);
                writeToFile(data, file);
                files.add(file);
            }

            dropSystemCache();

            for (int i = 0; i < n; i++) {
                byte[] data = null;
                for (int j = 0; j < 6; j++) {
                    data = toByteArray(files.get(random.nextInt(files.size())));
                }
                writeToFile(data, files.get(random.nextInt(files.size())));
                dropSystemCache();
            }

            deleteQuietly(dir);

            return stopwatch.elapsed(TimeUnit.MILLISECONDS);
        }

        private void dropSystemCache() {
//            try{
            executeProcess("/bin/bash", "-c", "sync; echo 3 | sudo dd of=/proc/sys/vm/drop_caches");
//            }catch(Exception ex){
//                logger.warn(marker(),"unable to drop disk cache: disk benchmark results will be unreliable",ex);
//            }
        }
    }

    private static class BenchmarkBeginEventImpl {

        private final String name;

        public BenchmarkBeginEventImpl(String name) {
            this.name = checkNotBlank(name);
        }

        public String getCategory() {
            return name;
        }

    }

    private static class BenchmarkResultImpl implements BenchmarkResult {

        private final String name;
        private final long result;
        private final double score;
        private final Throwable error;

        public BenchmarkResultImpl(String name, long result, double score, @Nullable Throwable error) {
            this.name = checkNotBlank(name);
            this.result = result;
            this.score = score;
            this.error = error;
        }

        @Override
        public String getCategory() {
            return name;
        }

        @Override
        public long getResult() {
            return result;
        }

        @Override
        public double getScore() {
            return score;
        }

        @Nullable
        @Override
        public Throwable getError() {
            return error;
        }

    }
}
