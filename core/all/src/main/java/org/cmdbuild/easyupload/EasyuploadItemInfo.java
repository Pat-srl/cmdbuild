/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import com.google.common.collect.ImmutableList;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;

public interface EasyuploadItemInfo {

    static final String EASYUPLOAD_TABLE = "_Upload",
            EASYUPLOAD_PATH = "Path",
            EASYUPLOAD_FILENAME = "FileName",
            EASYUPLOAD_SIZE = "Size",
            EASYUPLOAD_MIMETYPE = "MimeType",
            EASYUPLOAD_HASH = "Hash";

    static final List<String> EASYUPLOAD_ITEM_INFO_ATTRS = ImmutableList.of(ATTR_ID, ATTR_CODE, ATTR_DESCRIPTION, EASYUPLOAD_PATH, EASYUPLOAD_FILENAME, EASYUPLOAD_SIZE, EASYUPLOAD_MIMETYPE, EASYUPLOAD_HASH);

    @Nullable
    Long getId();

    String getPath();

    String getFileName();

    String getHash();

    String getMimeType();

    String getDescription();

    int getSize();

    default String getFolder() {
        return EasyuploadUtils.getFolder(getPath());
    }

}
