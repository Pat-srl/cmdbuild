/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import static java.lang.String.format;
import java.time.ZonedDateTime;
import static java.time.temporal.ChronoUnit.MILLIS;
import java.util.List;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.utils.date.CmDateUtils;

public interface EtlProcessingResult {

    long getCreatedRecordCount();

    long getModifiedRecordCount();

    long getUnmodifiedRecordCount();

    long getDeletedRecordCount();

    long getProcessedRecordCount();

    List<EtlProcessingResultFault> getErrors();

    boolean hasDetails();

    EtlProcessingResultDetails getDetails();

    EtlProcessingResult and(EtlProcessingResult other);

    EtlProcessingResult withTime(ZonedDateTime begin, ZonedDateTime end);

    ZonedDateTime getBegin();

    ZonedDateTime getEnd();

    default EtlProcessingResult withEnd(ZonedDateTime end) {
        return this.withTime(getBegin(), end);
    }

    default boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    default String getErrorsDescription() {
        return getErrors().stream().map(e -> format("error at record %s : %s", e.getRecordIndex(), e.getTechErrorMessage())).collect(joining("; "));
    }

    default String getErrorsDescriptionMultiline() {
        return getErrors().stream().map(e -> format("error at record %s : %s", e.getRecordIndex(), e.getTechErrorMessage())).collect(joining("\n"));
    }

    default String getResultDescription() {
        String str = format("processed %s records, created: %s, modified: %s, deleted: %s, unmodified: %s, errors: %s", getProcessedRecordCount(), getCreatedRecordCount(), getModifiedRecordCount(), getDeletedRecordCount(), getUnmodifiedRecordCount(), getErrors().size());
        long elapsed = getBegin().until(getEnd(), MILLIS);
        if (elapsed > 1000) {
            str += format(", elapsed: %s", CmDateUtils.toUserDuration(elapsed));
        }
        return str;
    }

    default String getResultDescriptionMultiline() {
        return format("processed %s records\ncreated: %s\nmodified: %s\ndeleted: %s\nunmodified: %s\nerrors: %s", getProcessedRecordCount(), getCreatedRecordCount(), getModifiedRecordCount(), getDeletedRecordCount(), getUnmodifiedRecordCount(), getErrors().size());
    }

}
