/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;


import org.cmdbuild.temp.TempInfo;

import java.util.Map;

import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class TempInfoSerializer {

    public static Map<String, String> serializeTempInfo(TempInfo tempInfo) {
        return map(
                //                "path", "_TEMP",
                //                "folder", "_TEMP",
                "name", tempInfo.getFileName(),
                "contentType", tempInfo.getContentType(),
                "size", tempInfo.getSize());
    }
}
