package org.cmdbuild.utils.io;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static org.cmdbuild.utils.io.CmStreamProgressUtils.detailedProgressDescription;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.progressDescription;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.progressDescriptionEta;
import org.cmdbuild.utils.lang.CmPreconditions;

public class StreamProgressEventImpl implements StreamProgressEvent {

    private final String id;
    private final String description;
    private final double progress;
    private final long count;
    private final long total;
    private final long beginTimestamp, elapsed;
    private final boolean umRecords;

    public StreamProgressEventImpl(String id, String description, double progress, long count, long total, long elapsed, long beginTimestamp, boolean umRecords) {
        this.id = CmPreconditions.checkNotBlank(id);
        this.description = CmPreconditions.checkNotBlank(description);
        this.progress = progress;
        this.count = count;
        this.total = total;
        this.elapsed = elapsed;
        this.beginTimestamp = beginTimestamp;
        this.umRecords = umRecords;
    }

    @Override
    public String getStreamId() {
        return id;
    }

    @Override
    public String getStreamDescription() {
        return description;
    }

    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public long getElapsedTime() {
        return elapsed;
    }

    @Override
    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    @Override
    public String getProgressDescription() {
        return progressDescription(getCount(), getTotal());
    }

    @Override
    public String getProgressDescriptionDetailed() {
        return detailedProgressDescription(getCount(), getTotal(), getBeginTimestamp(), umRecords);
    }

    @Override
    public String getProgressDescriptionEta() {
        return progressDescriptionEta(getCount(), getTotal(), getBeginTimestamp());
    }

    @Override
    public String toString() {
        return "StreamProgressEvent{" + "description=" + description + ", progress=" + getProgressDescription() + '}';
    }

}
