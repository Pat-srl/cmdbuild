/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.cmdbuild.fault.FaultEvent;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface WaterwayMessageData {

    final String DEFAULT_ATTACHMENT = "default", WY_JOB_RUN = "jobRun", WY_PROCESSING_REPORT = "wy_report",
            WY_MESSAGE_RETRY_DELAY = "wy_retry_delay", WY_MESSAGE_RETRY_TIMESTAMP = "wy_retry_timestamp", WY_MESSAGE_DESCRIPTION = "wy_description",
            WY_FAULT_EVENT_META_JOB_RUN = "wy_job_run";

    Map<String, String> getMeta();

    Map<String, WaterwayMessageAttachment> getAttachmentMap();

    @Nullable
    String getLogs();

    List<FaultEvent> getErrors();

    default List<FaultEvent> getFaultTolerantErrors() {
        return list();
    }

    default Collection<WaterwayMessageAttachment> getAttachments() {
        return getAttachmentMap().values();
    }

    default WaterwayMessageAttachment getPayload() {
        return getOnlyElement(getAttachmentMap().values());
    }

    @Nullable
    default String getMeta(String key) {
        return getMeta().get(checkNotBlank(key));
    }

    default WaterwayMessageAttachment getAttachment(String name) {
        return checkNotNull(getAttachmentMap().get(checkNotBlank(name)), "attachment not found for name =< {} >", name);
    }

    @Nullable
    default <T> T getAttachmentContentOrNull(String name) {
        return (T) Optional.ofNullable(getAttachmentMap().get(checkNotBlank(name))).map(WaterwayMessageAttachment::getObject).orElse(null);
    }

    default boolean hasAttachment(String name) {
        return getAttachmentMap().containsKey(checkNotBlank(name));
    }

    default boolean hasMeta(String name) {
        return getMeta().containsKey(checkNotBlank(name));
    }

    default boolean hasMetaNotBlank(String name) {
        return isNotBlank(getMeta(name));
    }

    default boolean hasMultipleAttachments() {
        return getAttachmentMap().size() > 1;
    }

    default boolean hasPayload() {
        return hasSingleAttachment();
    }

    default boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    default boolean hasSingleAttachment() {
        return getAttachmentMap().size() == 1;
    }

    default boolean hasAttachments() {
        return !getAttachmentMap().isEmpty();
    }

}
