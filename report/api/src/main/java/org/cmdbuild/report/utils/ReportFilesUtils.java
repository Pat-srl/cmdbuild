/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.utils;

import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import static org.cmdbuild.utils.io.CmZipUtils.unzipDataAsMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class ReportFilesUtils {

    public static Map<String, byte[]> unpackReportFiles(Map<String, byte[]> files) {
        Map<String, byte[]> map = map();
        files.forEach((name, data) -> {
            if (FilenameUtils.getExtension(name).equalsIgnoreCase("zip")) {
                map.putAll(unzipDataAsMap(data));
            } else {
                map.put(name, data);
            }
        });
        return map;
    }

}
