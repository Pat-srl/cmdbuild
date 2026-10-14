/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader.inner;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.time.ZonedDateTime;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlProcessingResultDetails;
import static org.cmdbuild.utils.date.CmDateUtils.max;
import static org.cmdbuild.utils.date.CmDateUtils.min;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.etl.loader.EtlProcessingResultFault;

public class EtlProcessingResultImpl implements EtlProcessingResult {

    private final ZonedDateTime begin, end;
    private final long createdRecordCount;
    private final long modifiedRecordCount;
    private final long unmodifiedRecordCount;
    private final long deletedRecordCount;
    private final long processedRecordCount;
    private final List<EtlProcessingResultFault> errors;
    private final EtlProcessingResultDetails details;

    public EtlProcessingResultImpl(ZonedDateTime begin, ZonedDateTime end, long createdRecordCount, long modifiedRecordCount, long unmodifiedRecordCount, long deletedRecordCount, long processedRecordCount, List<EtlProcessingResultFault> errors, @Nullable EtlProcessingResultDetails details) {
        this.createdRecordCount = createdRecordCount;
        this.modifiedRecordCount = modifiedRecordCount;
        this.unmodifiedRecordCount = unmodifiedRecordCount;
        this.deletedRecordCount = deletedRecordCount;
        this.processedRecordCount = processedRecordCount;
        this.errors = ImmutableList.copyOf(errors);
        this.details = details;
        this.begin = checkNotNull(begin);
        this.end = checkNotNull(end);
    }

    public EtlProcessingResultImpl(long createdRecordCount, long modifiedRecordCount, long unmodifiedRecordCount, long deletedRecordCount, long processedRecordCount, List<EtlProcessingResultFault> errors, @Nullable EtlProcessingResultDetails details) {
        this(now(), now(), createdRecordCount, modifiedRecordCount, unmodifiedRecordCount, deletedRecordCount, processedRecordCount, errors, details);
    }

    public EtlProcessingResultImpl(long createdRecordCount, long modifiedRecordCount, long unmodifiedRecordCount, long deletedRecordCount, long processedRecordCount, List<EtlProcessingResultFault> errors) {
        this(createdRecordCount, modifiedRecordCount, unmodifiedRecordCount, deletedRecordCount, processedRecordCount, errors, null);
    }

    @Override
    public boolean hasDetails() {
        return details != null;
    }

    @Override
    public EtlProcessingResultDetails getDetails() {
        return Preconditions.checkNotNull(details, "this report does not include details");
    }

    @Override
    public long getCreatedRecordCount() {
        return createdRecordCount;
    }

    @Override
    public long getModifiedRecordCount() {
        return modifiedRecordCount;
    }

    @Override
    public long getUnmodifiedRecordCount() {
        return unmodifiedRecordCount;
    }

    @Override
    public long getDeletedRecordCount() {
        return deletedRecordCount;
    }

    @Override
    public long getProcessedRecordCount() {
        return processedRecordCount;
    }

    @Override
    public List<EtlProcessingResultFault> getErrors() {
        return errors;
    }

    @Override
    public ZonedDateTime getBegin() {
        return begin;
    }

    @Override
    public ZonedDateTime getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "ImportExportOperationResult{" + "processed=" + processedRecordCount + ", errors=" + errors.size() + '}';
    }

    public static EtlProcessingResult emptyResult() {
        return new EtlProcessingResultImpl(0l, 0l, 0l, 0l, 0l, emptyList());
    }

    public static EtlProcessingResult aggregate(EtlProcessingResult... results) {
        return results.length == 1 ? checkNotNull(results[0]) : new EtlProcessingResultImpl(
                stream(results).mapToLong(EtlProcessingResult::getCreatedRecordCount).sum(),
                stream(results).mapToLong(EtlProcessingResult::getModifiedRecordCount).sum(),
                stream(results).mapToLong(EtlProcessingResult::getUnmodifiedRecordCount).sum(),
                stream(results).mapToLong(EtlProcessingResult::getDeletedRecordCount).sum(),
                stream(results).mapToLong(EtlProcessingResult::getProcessedRecordCount).sum(),
                stream(results).flatMap(e -> e.getErrors().stream()).collect(toImmutableList()));//TODO details ??
    }

    @Override
    public EtlProcessingResult and(EtlProcessingResult other) {
        return aggregate(this, other).withTime(min(begin, other.getBegin()), max(end, other.getEnd()));
    }

    @Override
    public EtlProcessingResult withTime(ZonedDateTime begin, ZonedDateTime end) {
        return new EtlProcessingResultImpl(begin, end, createdRecordCount, modifiedRecordCount, unmodifiedRecordCount, deletedRecordCount, processedRecordCount, errors, details);
    }

}
