/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.time.ZonedDateTime;
import static java.time.ZonedDateTime.now;
import java.util.Properties;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmZipUtils.getZipFileContentByPath;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildInfoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static BuildInfo loadBuildInfoFromWarDirSafe(File warDir) {
        Properties properties = new Properties();
        try {
            properties.load(new ByteArrayInputStream(toByteArray(new File(warDir, "WEB-INF/classes/git.properties"))));
            properties.load(new ByteArrayInputStream(toByteArray(new File(warDir, "WEB-INF/classes/org/cmdbuild/version.properties"))));
        } catch (Exception ex) {
            LOGGER.warn("error loading build info from war directory = {}", warDir, ex);
        }
        return parseBuildInfo(properties);
    }

    public static BuildInfo loadBuildInfoFromWarFileSafe(File warFile) {
        Properties properties = new Properties();
        try {
            try (InputStream in = new FileInputStream(warFile)) {
                properties.load(new ByteArrayInputStream(getZipFileContentByPath(in, "WEB-INF/classes/git.properties")));
            }
            try (InputStream in = new FileInputStream(warFile)) {
                properties.load(new ByteArrayInputStream(getZipFileContentByPath(in, "WEB-INF/classes/org/cmdbuild/version.properties")));
            }
        } catch (Exception ex) {
            LOGGER.warn("error loading build info from war file = {}", warFile, ex);
        }
        return parseBuildInfo(properties);
    }

    public static BuildInfo loadBuildInfoFromWarData(byte[] warData) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(getZipFileContentByPath(warData, "WEB-INF/classes/git.properties")));
        properties.load(new ByteArrayInputStream(getZipFileContentByPath(warData, "WEB-INF/classes/org/cmdbuild/version.properties")));
        return parseBuildInfo(properties);
    }

    public static BuildInfo parseBuildInfo(Properties properties) {
        try {
            String commitId = firstNotNull(properties.getProperty("git.commit.id.abbrev"), "unknown");
            String branch = firstNotNull(properties.getProperty("git.branch"), "unknown");
            ZonedDateTime timestamp = CmDateUtils.toDateTime(firstNotNull(properties.getProperty("git.commit.time"), now().toString()));
            boolean isDirty = toBooleanOrDefault(properties.getProperty("git.dirty"), true);
            String versionNumber = firstNotNull(properties.getProperty("org.cmdbuild.version"), "unknown"),
                    commitInfo = format("%s/%s (%s)", commitId, branch, toIsoDateTimeUtc(timestamp)), // add dirty build? isDirty ? " (dirty)" : ""
                    vertVersionNumber = trimToNull(properties.getProperty("org.cmdbuild.vert.version")),
                    vertName = trimToNull(properties.getProperty("org.cmdbuild.vert.name"));
            return new BuildInfoImpl(versionNumber, commitInfo, vertName, vertVersionNumber);
        } catch (Exception ex) {
            LOGGER.warn("version info is not available", ex);
            return new BuildInfoImpl("unknown", "unknown", null, null);
        }
    }

    private static class BuildInfoImpl implements BuildInfo {

        private final String versionNumber, commitInfo, embeddedVertName, embeddedVertVersionNumber;

        private BuildInfoImpl(String versionNumber, String commitInfo, @Nullable String embeddedVertName, @Nullable String embeddedVertVersionNumber) {
            this.versionNumber = checkNotBlank(versionNumber);
            this.commitInfo = checkNotBlank(commitInfo);
            this.embeddedVertName = embeddedVertName;
            this.embeddedVertVersionNumber = embeddedVertVersionNumber;
        }

        @Override
        public String getCommitInfo() {
            return commitInfo;
        }

        @Override
        public String getVersionNumber() {
            return versionNumber;
        }

        @Override
        @Nullable
        public String getEmbeddedVertName() {
            return embeddedVertName;
        }

        @Override
        @Nullable
        public String getEmbeddedVertVersionNumber() {
            return embeddedVertVersionNumber;
        }

        @Override
        public String toString() {
            return "BuildInfoImpl{" + "info=" + commitInfo + ", version=" + versionNumber + '}';
        }

    }
}
