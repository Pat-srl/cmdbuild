/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import java.util.Properties;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.dao.config.DatabaseConfiguration;
import static org.cmdbuild.debuginfo.BuildInfoUtils.loadBuildInfoFromWarDirSafe;
import static org.cmdbuild.debuginfo.BuildInfoUtils.parseBuildInfo;
import org.springframework.stereotype.Component;

@Component
public class BuildInfoServiceImpl implements BuildInfoService {

    private final DatabaseConfiguration config;

    private final BuildInfo buildInfo;

    public BuildInfoServiceImpl(DirectoryService directoryService, DatabaseConfiguration config) {
        if (directoryService.hasWebappDirectory()) {
            buildInfo = loadBuildInfoFromWarDirSafe(directoryService.getWebappDirectory());
        } else {
            buildInfo = parseBuildInfo(new Properties());
        }
        this.config = checkNotNull(config);
    }

    @Override
    public BuildInfoExt getBuildInfo() {
        return new BuildInfoExtImpl(config.getVertName(), config.getVertVersionNumber());
    }

    private class BuildInfoExtImpl implements BuildInfoExt {

        private final String dbVertName, dbVertVersionNumber;

        public BuildInfoExtImpl(@Nullable String dbVertName, @Nullable String dbVertVersionNumber) {
            this.dbVertName = dbVertName;
            this.dbVertVersionNumber = dbVertVersionNumber;
        }

        @Override
        public String getCommitInfo() {
            return buildInfo.getCommitInfo();
        }

        @Override
        public String getVersionNumber() {
            return buildInfo.getVersionNumber();
        }

        @Override
        public String getEmbeddedVertName() {
            return buildInfo.getEmbeddedVertName();
        }

        @Override
        public String getEmbeddedVertVersionNumber() {
            return buildInfo.getEmbeddedVertVersionNumber();
        }

        @Override
        public String getDbVertName() {
            return dbVertName;
        }

        @Override
        public String getDbVertVersionNumber() {
            return dbVertVersionNumber;
        }

    }

}
