/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.easyupload.EasyuploadItemInfo;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

/**
 * @author ldare
 */
public class EasyUploadItemSerializer {

    public static Object serializeItem(EasyuploadItemInfo item) {
        return map(
                "_id", item.getId(),
                "path", item.getPath(),
                "name", item.getFileName(),
                "contentType", item.getMimeType(),
                "size", item.getSize(),
                "folder", item.getFolder(),
                "description", firstNotBlank(item.getDescription(), FilenameUtils.getBaseName(item.getFileName()))
        );
    }
}
