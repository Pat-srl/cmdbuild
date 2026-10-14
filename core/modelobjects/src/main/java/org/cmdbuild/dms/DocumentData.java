/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms;

import java.util.Map;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;

public interface DocumentData {

    @Nullable
    String getAuthor();

    @Nullable
    String getFilename();

    @Nullable
    String getCategory();

    boolean isMajorVersion();

    @Nullable
    byte[] getData();

    Map<String, Object> getMetadata();

    default boolean hasData() {
        return getData() != null;
    }

    default boolean hasMetadata() {
        return !getMetadata().isEmpty();
    }

    @Nullable
    default String getDescription() {
        return toStringOrEmpty(getMetadata().get(ATTR_DESCRIPTION));
    }

    default DataSource getDataSource() {
        return newDataSource(getData(), null, getFilename());
    }
}
