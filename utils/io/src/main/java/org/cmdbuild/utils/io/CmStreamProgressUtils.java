package org.cmdbuild.utils.io;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.currentTimeMillis;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.io.CmIoUtils.getAvailableLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmStreamProgressUtils {

    public static InputStream listenToStreamProgress(InputStream in, Consumer<StreamProgressEvent> listener) {
        return listenToStreamProgress("stream", "stream progress", in, listener);
    }

    public static Consumer<Long> buildProgressListener(long estimateTotal, Consumer<StreamProgressEvent> listener) {
        return new Consumer<Long>() {

            private final Logger logger = LoggerFactory.getLogger(getClass());

            private final long beginTimestamp = System.currentTimeMillis();
            private StreamProgressEvent lastEvent = null;

            @Override
            public synchronized void accept(Long count) {  //TODO duplicate code, refactor
                double perc = estimateTotal == 0 ? 0 : count / ((double) estimateTotal);
                long now = currentTimeMillis(), elapsed = now - beginTimestamp;
                StreamProgressEvent event = new StreamProgressEventImpl("_", "_", perc, count, estimateTotal, elapsed, beginTimestamp, true);
                if (shouldNotify(lastEvent, event)) {
                    logger.debug("perc = {} estimateTotal = {} count = {} elapsed = {}", perc, estimateTotal, count, elapsed);
                    lastEvent = event;
                    listener.accept(event);
                }
            }
        };
    }

    public static String detailedProgressDescription(long processed, long total, long beginTimestampMillis) {
        return detailedProgressDescription(processed, total, beginTimestampMillis, false);
    }

    public static String detailedProgressDescription(long processed, long total, long beginTimestampMillis, boolean umRecords) {
        return String.format("%s  %s / %s (%s)",
                progressDescription(processed, total),
                umRecords ? recordCountToDisplaySize(processed) : FileUtils.byteCountToDisplaySize(processed),
                total == 0 ? "unknown" : (umRecords ? recordCountToDisplaySize(total) : FileUtils.byteCountToDisplaySize(total)),
                progressDescriptionEta(processed, total, beginTimestampMillis));
    }

    public static String recordCountToDisplaySize(long count) {
        return byteCountToDisplaySize(count).replaceFirst("( bytes|B)$", "");
    }

    public static String progressDescriptionEta(long processed, long total, long beginTimestampMillis) {
        return progressDescriptionEtaWithElapsed(processed, total, System.currentTimeMillis() - beginTimestampMillis);
    }

    public static String progressDescriptionEtaWithElapsed(long processed, long total, long elapsedMillis) {
        long remaining = total - processed;
        if (remaining == 0 && elapsedMillis > 0) {
            return String.format("completed in %s", toUserDuration(elapsedMillis));
        } else {
            long eta = processed == 0 ? -1 : (elapsedMillis * remaining / processed);
            return String.format("eta %s", eta > 0 ? CmDateUtils.toUserDuration(eta) : "unknown");
        }
    }

    public static String progressDescription(long processed, long total) {
        return total == 0 ? "    " : String.format("%s%%", Math.round(processed / (double) total * 1000) / 10d);
    }

    public static boolean shouldNotify(@Nullable StreamProgressEvent previousEvent, StreamProgressEvent currentEvent) {
        return (previousEvent == null
                || (currentEvent.getElapsedTime() - previousEvent.getElapsedTime()) > 1000
                || (currentEvent.getProgress() - previousEvent.getProgress()) > 0.1d
                || currentEvent.getCount() == currentEvent.getTotal())
                && (currentEvent.getCount() == 0 || previousEvent == null || previousEvent.getCount() != currentEvent.getCount());
    }

    public static InputStream listenToStreamProgress(String id, String description, InputStream in, Consumer<StreamProgressEvent> listener) {
        checkNotBlank(id);
        checkNotBlank(description);
        checkNotNull(in);
        return new InputStream() {

            private final Logger logger = LoggerFactory.getLogger(getClass());

            private long estimateTotal = 0;
            private long count = 0;
            private Long beginTimestamp = null;
            private StreamProgressEvent lastEvent = null;

            @Override
            public int read() throws IOException {
                if (beginTimestamp == null) {
                    beginTimestamp = currentTimeMillis();
                }
                int res = in.read();
                if (res >= 0) {
                    count++;
                }
                notifyEvent();
                return res;
            }

            @Override
            public void close() throws IOException {
                in.close();
            }

            @Override
            public int available() throws IOException {
                return in.available();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (beginTimestamp == null) {
                    beginTimestamp = currentTimeMillis();
                }
                int res = in.read(b, off, len);
                if (res > 0) {
                    count += res;
                }
                notifyEvent();
                return res;
            }

            private void notifyEvent() {
                checkAvailable();
                double perc = estimateTotal == 0 ? 0 : count / ((double) estimateTotal);
                long now = currentTimeMillis(), elapsed = now - beginTimestamp;
                StreamProgressEvent event = new StreamProgressEventImpl(id, description, perc, count, estimateTotal, elapsed, beginTimestamp, false);
                if (shouldNotify(lastEvent, event)) {
                    logger.debug("perc = {} estimateTotal = {} count = {} elapsed = {}", perc, estimateTotal, count, elapsed);
                    lastEvent = event;
                    listener.accept(event);
                }
            }

            private void checkAvailable() {
                try {
                    estimateTotal = getAvailableLong(in) + count;
                } catch (IOException ex) {
                    throw runtime(ex);
                }
            }

        };
    }
}
