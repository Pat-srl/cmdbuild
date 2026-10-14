/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage.WMAS_EMBEDDED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage.WMAS_REFERENCE;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_BYTES;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_JSON;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_TEXT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface WaterwayMessageAttachment {

    final String WMA_CONTENT_TYPE = "cm_contentType", WMA_FILE_NAME = "cm_fileName", WMA_BYTE_SIZE = "cm_byteSize";

    String getName();

    Map<String, String> getMeta();

    WaterwayMessageAttachmentStorage getStorage();

    WaterwayMessageAttachmentType getType();

    Object getObject();

    @Nullable
    default String getMeta(String key) {
        return getMeta().get(checkNotBlank(key));
    }

    default boolean isOfType(WaterwayMessageAttachmentType... types) {
        return set(types).contains(getType());
    }

    default boolean hasStorage(WaterwayMessageAttachmentStorage storage) {
        return equal(getStorage(), checkNotNull(storage));
    }

    default byte[] getBytes() {
        checkArgument(isOfType(WMAT_BYTES) && hasStorage(WMAS_EMBEDDED), "cannot get bytes content for attachment = %s", this);
        return (byte[]) getObject();
    }

    default String getText() {
        checkArgument(isOfType(WMAT_TEXT, WMAT_JSON) || hasStorage(WMAS_REFERENCE), "cannot get text content for attachment = %s", this);
        return (String) getObject();
    }

    @Nullable
    default String getContentType() {
        return getMeta(WMA_CONTENT_TYPE);
    }

    @Nullable
    default Long getByteSize() {
        return toLongOrNull(getMeta(WMA_BYTE_SIZE));
    }

}
