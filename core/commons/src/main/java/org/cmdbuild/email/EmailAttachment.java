/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;

@JsonDeserialize(as = EmailAttachmentImpl.class)
public interface EmailAttachment {

    String getFileName();

    String getContentType();

    @Nullable
    String getContentId();

    @JsonIgnore()
    byte[] getData();

    String getContentDisposition();

    @JsonIgnore()
    default boolean hasContentId() {
        return isNotBlank(getContentId());
    }

    default boolean isOfType(String mimetype) {
        return CmIoUtils.isContentType(getContentType(), mimetype);
    }

    @JsonIgnore()
    default String getDataAsString() {
        return readToString(getData(), getContentType());
    }

    @JsonIgnore()
    default DataHandler getDataHandler() {
        return newDataHandler(getData(), getContentType(), getFileName());
    }

}
