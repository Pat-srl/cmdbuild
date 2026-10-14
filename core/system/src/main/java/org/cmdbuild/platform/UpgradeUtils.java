/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.platform;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import org.cmdbuild.debuginfo.BuildInfo;
import static org.cmdbuild.utils.io.CmZipUtils.validateZipFileContent;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.debuginfo.BuildInfoUtils.loadBuildInfoFromWarData;

public class UpgradeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static BuildInfo validateWarData(byte[] warData) {
        //TODO validate war data before upgrade; check version
        validateZipFileContent(warData);
        try {
            LOGGER.debug("check war data, load build properties");
            BuildInfo buildInfo = loadBuildInfoFromWarData(warData);
            LOGGER.debug("loaded build info = {}", buildInfo);
            return buildInfo;
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }
}
