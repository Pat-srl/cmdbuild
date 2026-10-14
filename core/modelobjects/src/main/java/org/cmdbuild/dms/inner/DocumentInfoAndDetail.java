/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.inner;

import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.beans.Card;

public interface DocumentInfoAndDetail {

    String getAuthor();

    String getDescription();

    String getDocumentId();

    String getFileName();

    String getMimeType();

    String getVersion();

    @Nullable
    String getHash();

    @Nullable
    String getCategory();

    ZonedDateTime getCreated();

    ZonedDateTime getModified();

    int getFileSize();

    boolean hasContent();

    @Nullable
    Card getMetadata();

    default boolean hasMetadata() {
        return getMetadata() != null;
    }

    default boolean hasCategory() {
        return isNotBlank(getCategory());
    }

    @Nullable
    default String getMetadataString(String key) {
        return hasMetadata() ? getMetadata().getString(key) : null;
    }

    default Map<String, Object> getMetadataMap() {
        return hasMetadata() ? getMetadata().getAllValuesAsMap() : emptyMap();
    }

    default long getMetadataCardId() {
        return getMetadata().getId();
    }
}
