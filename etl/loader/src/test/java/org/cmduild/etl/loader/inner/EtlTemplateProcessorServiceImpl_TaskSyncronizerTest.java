/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmduild.etl.loader.inner;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.etl.loader.EtlRecordInfo;
import org.cmdbuild.etl.loader.inner.EtlRecordInfoImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmExecutorUtils;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EtlTemplateProcessorServiceImpl_TaskSyncronizerTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ProcessingTerminatorToggle mockProcessingTerminatorToggle = mock(ProcessingTerminatorToggle.class);

//    @BeforeClass
//    public static void setUpClass() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//    
//    @Before
//    public void setUp() {
//    }
//    
//    @After
//    public void tearDown() {
//    }
    @Test
    public void testDataPartition_Current() {
        System.out.println("DataPartition_Current");

        //arrange:
        int rawRecordsCount = 15;
        int threadCount = 4;

        List<MyRawRecord> rawRecords = IntStream.range(0, rawRecordsCount).mapToObj(MyRawRecord::new).collect(Collectors.toList());

        // See EtlTemplateProcessorServiceImpl.ImportProcessor.createUpdateRecords()
        ExecutorService executor = Executors.newFixedThreadPool(threadCount + 1, namedThreadFactory(getClass()));

        int size = rawRecords.size();
        System.out.println("thread[%s] - Processing %d rawRecords with %d threads".formatted(getCurrentThreadName(), size, threadCount));

        //act:
        for (int i = 0; i < threadCount; i++) {
            int threadNumber = i;
            executor.submit(CmExecutorUtils.safe(() -> {
                final String threadName = getCurrentThreadName();
                //            MDC.put("cm_type", "req");
                //            MDC.put("cm_id", format("%s:%s", mdcCmId, threadNumber));//TODO improve mdc copy
                //            requestContextService.initCurrentRequestContext("import thread " + threadNumber, requestContext);
                System.out.println("thread[%s] - start record processing thread %2d/%d".formatted(threadName, threadNumber, threadCount));

                for (int recordNumber = threadNumber; recordNumber < size; recordNumber += threadCount) {
                    System.out.println("thread[%s] - processing record %2d/%2d".formatted(threadName, recordNumber, size));
                } // end inner for
            }));
        } // end outer for

        //assert:
    } // end testDataPartition method 

    @Test
    public void testDataPartition_New() {
        System.out.println("DataPartition_New");

        //arrange:        
        boolean hasLineNumbers = false;
        int rawRecordsCount = 15;
        int numThreads = 4;

        List<MyRawRecord> rawRecords = IntStream.range(0, rawRecordsCount).mapToObj(MyRawRecord::new).collect(Collectors.toList());

        //act:
        Map<EtlRecordInfo, MyProcessedRecord> processedRecordStack = new ConcurrentHashMap();

        // Models external interruption command; mock (by default) returns false
        // when(terminatorMock.isOperationInterrupted()).thenReturn(false);        
        // Create synchronized tasks: each task will do approsimatively a number of records/threads processings
        TaskSynchronizer taskSynchronizer = new TaskSynchronizer(rawRecords, numThreads, mockProcessingTerminatorToggle);

        processWithParallelTasks(rawRecords, taskSynchronizer, hasLineNumbers, processedRecordStack);

        //assert:
        assertTrue(taskSynchronizer.isInputDone());
        assertEquals(rawRecordsCount, processedRecordStack.size());
    } // end testDataPartition_New method         

    /**
     * Forcedly interrupt after some item processed.
     *
     */
    @Test
    public void testDataPartition_New_Interrupt() {
        System.out.println("DataPartition_New_Interrupt");

        //arrange:        
        boolean hasLineNumbers = false;
        int rawRecordsCount = 15;
        int numThreads = 4;
        final int stopAfter = 3;

        List<MyRawRecord> rawRecords = IntStream.range(0, rawRecordsCount).mapToObj(MyRawRecord::new).collect(Collectors.toList());

        //act:
        Map<EtlRecordInfo, MyProcessedRecord> processedRecordStack = new ConcurrentHashMap();

        // Models external interruption command; mock (by default) returns false
        // when(terminatorMock.isOperationInterrupted()).thenReturn(false);        
        // Create synchronized tasks: each task will do approsimatively a number of records/threads processings
        TaskSynchronizer_SimulateInterruption taskSynchronizer = new TaskSynchronizer_SimulateInterruption(rawRecords, numThreads, mockProcessingTerminatorToggle);
        taskSynchronizer.simulateTerminationAt(stopAfter);

        processWithParallelTasks(rawRecords, taskSynchronizer, hasLineNumbers, processedRecordStack);

        //assert:
        assertTrue(rawRecordsCount - stopAfter >= taskSynchronizer.getRemainingInputItems());
    } // end testDataPartition method     

    /**
     *
     * @param rawRecords input.
     * @param taskSynchronizer for task synchronization.
     * @param hasLineNumbers flag if records has line numbers in its data.
     * @param processedRecordStack output.
     */
    private void processWithParallelTasks(List<MyRawRecord> rawRecords, TaskSynchronizer taskSynchronizer, boolean hasLineNumbers, Map<EtlRecordInfo, MyProcessedRecord> processedRecordStack) {
        int numThreads = taskSynchronizer.getNumTasks();
        int numRecords = taskSynchronizer.getNumInput();

        List<Runnable> tasks = list();
        IntStream.range(0, numThreads).forEach(i -> {
            tasks.add(CmExecutorUtils.safe(() -> {
                processRecordsRows(taskSynchronizer, hasLineNumbers, processedRecordStack);
            })); // end task
        });

        // Start executing record processing
        ExecutorService executor = Executors.newFixedThreadPool(numThreads, namedThreadFactory(getClass()));
        System.out.println("thread[%s] - Processing %d rawRecords with %d threads".formatted(getCurrentThreadName(), rawRecords.size(), numThreads));
        tasks.forEach(t -> {
            executor.submit(t);
        });

        // Wait all processing termination
        try {
            taskSynchronizer.awaitInputProcessingTermination();
        } catch (InterruptedException e) {
            // Threads interrupted
            System.out.println("operation interrupted! [Explicit operation interrupted? %s]".formatted(mockProcessingTerminatorToggle.isProcessingInterrupted()));
        } finally {
            executor.shutdown();
        }

        if (taskSynchronizer.isProcessingInterrupted()) {
            System.out.println("processing was interrupted");
        } else {
            // Do something with processed records
            System.out.println();
            System.out.println();
            System.out.println("Processed records:");
            processedRecordStack.forEach((r, p)
                    -> System.out.println("Test: Processed record %s->%s/%2s".formatted(getCurrentThreadName(), r, p, numRecords))
            );
        }
    } // end processWithParallelTasks method

    /**
     * Each one of this task processes many input items, concurrently with other
     * task like this.
     *
     * @param taskSynchronizer handles input item picking and synchronization
     * between each task.
     * @param hasLineNumbers flag <code>true</code> if line information is
     * stored in each record row.
     * @param processedRecordStack output.
     */
    private void processRecordsRows(TaskSynchronizer taskSynchronizer, boolean hasLineNumbers,
            Map<EtlRecordInfo, MyProcessedRecord> processedRecordStack) {
        String threadName = getCurrentThreadName();
        int curThreadNumber = taskSynchronizer.nextTaskCounter();
        System.out.println("thread[%s] - start record processing thread %2d/%d".formatted(threadName, curThreadNumber, taskSynchronizer.getNumTasks()));
        while (!(taskSynchronizer.isProcessingInterrupted() || taskSynchronizer.isInputDone())) {
            // Poll raw record; null if all input processed
            Pair<Integer, MyRawRecord> nextItem = taskSynchronizer.getNextInputItem();
            int curRecordNumber = nextItem.getLeft();
            MyRawRecord curRawRecord = nextItem.getRight();
            if (curRawRecord == null) {
                break;
            }
            EtlRecordInfo curRecordInfo = toRecordInfo(curRawRecord, curRecordNumber, hasLineNumbers);

            System.out.println("thread[%s] - processing record %s/%2d".formatted(getCurrentThreadName(), curRawRecord, taskSynchronizer.getNumInput()));

            // Process raw record
            MyProcessedRecord curProcessedRecord = null;
            try {
                curProcessedRecord = prepareRecord(curRawRecord);
            } catch (Exception ex) {
//              handleRecordError(ex, recordInfo);
            }
            processedRecordStack.put(curRecordInfo, curProcessedRecord);
            System.out.println("thread[%s] - processed record %s/%2d".formatted(getCurrentThreadName(), curRawRecord, taskSynchronizer.getNumInput()));
            // Raw record processing done
            taskSynchronizer.notifyInputDone();
        } // end while
        if (taskSynchronizer.isProcessingInterrupted()) {
            logger.warn("thread {}/{} is interrupted, shutting down", curThreadNumber, taskSynchronizer.getNumTasks());
        }

        System.out.println("thread[%s] - end".formatted(curThreadNumber));
        taskSynchronizer.notifyTaskEnd();
    } // end processRecordsRows method

    // As in EtlTemplateProcessorServiceImpl
    private EtlRecordInfo toRecordInfo(MyRawRecord rawRecord, int recordNumber, boolean hasLineNumbers) {
        int recordLineNumber = recordNumber;
//        if (hasLineNumbers) {            
//            recordLineNumber = toInt(rawRecord.get(IMPORT_RECORD_LINE_NUMBER));
//            rawRecord = map(rawRecord).withoutKey(IMPORT_RECORD_LINE_NUMBER);
//        } else {
//            recordLineNumber = recordNumber;
//        }
        return new EtlRecordInfoImpl(recordNumber, recordLineNumber, rawRecord.getData());
    }

    private static MyProcessedRecord prepareRecord(MyRawRecord curRawRecord) {
        return new MyProcessedRecord(curRawRecord);
    }

    private static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

} // end EtlTemplateProcessorServiceImplTest class

/**
 * Processing of all input is subdivided in a number of chunks, of tasks.
 *
 * @author afelice
 */
class TaskSynchronizer<T> {

    public final static int UNDEFINED_INPUT_COUNT = -1;

    private final AtomicInteger taskCounter = new AtomicInteger(); // Give thread a autoincrement value
    private final AtomicInteger inputCounter = new AtomicInteger(); // Give record a autoincrement value

    private final Thread mainThread;
    private final int numInput;
    private final int numTasks;
    protected final Deque<T> inputStack;
    private boolean doneInput;
    private final CountDownLatch processingTerminationLatch;

    private final ProcessingTerminatorToggle processingTerminatorToggle;

    public TaskSynchronizer(List<T> input, int numTasks, ProcessingTerminatorToggle processingTerminatorToggle) {
        this.mainThread = Thread.currentThread();
        this.numInput = input.size();
        this.doneInput = input.isEmpty();
        this.inputStack = new LinkedList<>(input);
        this.numTasks = numTasks;
        this.processingTerminationLatch = new CountDownLatch(numInput);
        this.processingTerminatorToggle = processingTerminatorToggle;
    }

    public int getNumTasks() {
        return numTasks;
    }

    public int getNumInput() {
        return numInput;
    }

    public boolean isInputDone() {
        return doneInput;
    }

    /**
     *
     * @return pair with <code>(count, inputItem)</code>;
     * <code>(-1, null)</code> if input is empty
     */
    public Pair<Integer, T> getNextInputItem() {
        int curInputCount = UNDEFINED_INPUT_COUNT;
        T curInput = null;
        synchronized (inputStack) {
            if (!doneInput) {
                curInput = inputStack.poll();
                curInputCount = nextInputCounter();
                doneInput = inputStack.isEmpty();
            }
        }
        return Pair.of(curInputCount, curInput);
    }

    public int nextTaskCounter() {
        return taskCounter.incrementAndGet();
    }

    public int nextInputCounter() {
        return inputCounter.getAndIncrement();
    }

    public void notifyInputDone() {
        processingTerminationLatch.countDown();
    }

    public void awaitInputProcessingTermination() throws InterruptedException {
        if (!isProcessingInterrupted()) {
            processingTerminationLatch.await();
        }
    }

    public boolean isProcessingInterrupted() {
        return processingTerminatorToggle.isProcessingInterrupted();
    }

    /**
     * To handle main thread awake if task operation interrupted
     *
     * @return
     */
    public void notifyTaskEnd() {
        if (isProcessingInterrupted()) {
            // stop main thread
            mainThread.interrupt();
        }
    }
} // end TaskSynchronizer class

class MyRawRecord {

    private final int id;

    MyRawRecord(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "raw%2d".formatted(id);
    }

    /**
     * Fake data
     *
     * @return
     */
    Map<String, Object> getData() {
        return map("record" + id, Character.toString('a' + id));
    }
} // end MayRawRecord class

class MyProcessedRecord {

    private final int id;

    MyProcessedRecord(MyRawRecord rawRecord) {
        this.id = rawRecord.getId();
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "proc%2d".formatted(id);
    }
} // end MayRawRecord class

/**
 * Handle to explicitly invoke input processing termination from an external
 * client.
 *
 * @author afelice
 */
class ProcessingTerminatorToggle {

    boolean isProcessingInterrupted() {
        return false;
    }
} // end InputProcessingTerminator class

/**
 * Simulates the external interruption
 *
 * @author afelice
 */
class TaskSynchronizer_SimulateInterruption extends TaskSynchronizer {

    public static final int NO_TERMINATION = -1;

    private int terminateAfterItem = NO_TERMINATION;

    TaskSynchronizer_SimulateInterruption(List<MyRawRecord> rawRecords, int numThreads, ProcessingTerminatorToggle processingTerminatorToggle) {
        super(rawRecords, numThreads, processingTerminatorToggle);
    }

    void simulateTerminationAt(int inputItemCount) {
        this.terminateAfterItem = inputItemCount;
    }

    @Override
    public boolean isProcessingInterrupted() {
        return super.isProcessingInterrupted()
                || (terminateAfterItem > NO_TERMINATION && (getNumInput() - inputStack.size() >= terminateAfterItem));
    }

    /**
     * Not synchronized, not thread safe, method.
     *
     * @return
     */
    int getRemainingInputItems() {
        return inputStack.size();
    }
}
