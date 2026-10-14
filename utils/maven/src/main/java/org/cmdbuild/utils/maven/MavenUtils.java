/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.maven;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.File;
import static java.lang.String.format;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;
import static org.apache.maven.artifact.versioning.VersionRange.createFromVersionSpec;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeBashScript;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.io.CmPropertyUtils.toProperties;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(MavenUtils.class);
    private final static Pattern RANGE_FILE_PATTERN = Pattern.compile("^(.+)-([\\[(].*[\\])])\\.jar$");
    private final static Pattern JAR_FILE_PATTERN = Pattern.compile("^(.+)-([0-9]+(?:\\.[0-9]+)*)\\.jar$");

    public static String mavenGavToFilename(String gav) {
        List<String> list = Splitter.on(":").trimResults().omitEmptyStrings().splitToList(checkNotBlank(gav));
        checkArgument(list.size() == 3, "invalid gav format for string =< %s >", gav);
        return mavenNameVersionToFilename(list.get(1), list.get(2));
    }

    public static String mavenNameVersionToFilename(String name, String version) {
        return format("%s-%s.jar", checkNotBlank(name), checkNotBlank(version));
    }

    @Nullable
    public static DataSource getResourceByGavOrNull(String gav) {
        try {
            return getResourceByGav(gav);
        } catch (Exception ex) {
            LOGGER.debug("failed to get resource for gav =< {} >", gav, ex);
            return null;
        }
    }

    public static DataSource getResourceByGav(String gav) {
        File file = getFileByGav(gav);
        DataSource data = toDataSource(file);
        cleanupFileFromGav(file);
        return data;
    }

    public static File getFileByGav(String gav) {
        checkNotBlank(gav);
        LOGGER.info("fetch artifact =< {} >", gav);
        File tempDir = tempDir("artifact");
        mavenInvoke("org.apache.maven.plugins:maven-dependency-plugin:2.8:get", Map.of("artifact", gav));
        mavenInvoke("org.apache.maven.plugins:maven-dependency-plugin:2.8:copy", Map.of("artifact", gav, "outputDirectory", tempDir.getAbsolutePath()));
        File file = Iterables.getOnlyElement(Arrays.asList(tempDir.listFiles()));
        checkArgument(file.exists() && file.isFile());
        return file;
    }

    /**
     * remove a file obtained by {@link #getFileByGav(java.lang.String) }
     * and all related resources.
     *
     * Since {@link #getFileByGav(java.lang.String) } currently creates a new
     * directory, and the file in it, this method is responsible to clean the
     * directory as well as the file. Implementation may change in the future.
     *
     * @param file
     */
    public static void cleanupFileFromGav(File file) {
        FileUtils.deleteQuietly(file.getParentFile());
    }

    public static void mavenInvoke(String goal, @Nullable Map<String, String> params) {
        try {
            prepareMavenEnv();
            params = firstNotNull(params, emptyMap());
            LOGGER.info("execute `mvn {} {}`", goal, params.entrySet().stream().map(e -> format("-D%s=%s", e.getKey(), e.getValue())).collect(joining(" ")));
            InvocationRequest request = new DefaultInvocationRequest();
            request.setBatchMode(true);
            request.setGoals(Collections.singletonList(goal));
            request.setProperties(toProperties(params));
            request.setMavenOpts("-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN");
            Invoker invoker = new DefaultInvoker();
            InvocationResult invocationResult = invoker.execute(request);
            checkArgument(invocationResult.getExitCode() == 0, "maven invocation failed");
        } catch (MavenInvocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @param lib the name of jar lib
     * @return Pair of Name of file and version of file
     * '<cmdbuild-utils, 1.0.0>'
     */
    public static Pair<String, String> getNameAndVersionFromFilename(String lib) {
        Matcher matcher = RANGE_FILE_PATTERN.matcher(lib);
        if (!matcher.matches()) {
            matcher = JAR_FILE_PATTERN.matcher(lib);
            if (!matcher.matches()) {
                return Pair.of(lib, lib);
            }
        }
        return Pair.of(matcher.group(1), matcher.group(2));
    }

    /**
     *
     * @param rangeVersion
     * @param actualVersion
     * @return true if actualVersion is in the range of rangeVersion, otherwise
     * false
     */
    public static boolean checkRangeVersion(String rangeVersion, String actualVersion) {
        try {
            return !createFromVersionSpec(rangeVersion).containsVersion(new DefaultArtifactVersion(actualVersion));
        } catch (InvalidVersionSpecificationException ex) {
            LOGGER.warn("unable to check range version =< {} > and actual version =< {} >", rangeVersion, actualVersion);
            return false;
        }
    }

    public static String getMajorVersion(String version) {
        return toStringNotBlank(getArtifactVersion(version).getMajorVersion());
    }

    public static String getMajorMinorVersion(String version) {
        return buildVersion(getMajorVersion(version), toStringNotBlank(getArtifactVersion(version).getMinorVersion()));
    }

    public static String getMajorMinorIncrementalVersion(String version) {
        return buildVersion(getMajorMinorVersion(version), toStringNotBlank(getArtifactVersion(version).getIncrementalVersion()));
    }

    private static String buildVersion(String firstPart, String secondPart) {
        return format("%s.%s", firstPart, secondPart);
    }

    /**
     *
     * @param version
     * @return DefaultArtifactVersion
     */
    public static DefaultArtifactVersion getArtifactVersion(String version) {
        return new DefaultArtifactVersion(version);
    }

    public static String versionRangeToString(String rangeVersion) {
        if (isBlank(rangeVersion)) {
            return null;
        }
        try {
            VersionRange versionRange = createFromVersionSpec(rangeVersion);
            if (!versionRange.hasRestrictions()) {
                return versionRange.toString();
            }
            Restriction restriction = getOnlyElement(versionRange.getRestrictions());
            return restrictionToString(restriction);
        } catch (InvalidVersionSpecificationException ex) {
            throw runtime(ex);
        }
    }

    public static String restrictionToString(Restriction restriction) {
        if (restriction.getLowerBound() != null && restriction.getUpperBound() != null && restriction.getLowerBound().compareTo(restriction.getUpperBound()) == 0) {
            return restriction.getLowerBound().toString();
        }
        return format("%s %s", restrictionToString(">", restriction.isLowerBoundInclusive(), restriction.getLowerBound()), restriction.getUpperBound() != null ? format(", %s", restrictionToString("<", restriction.isUpperBoundInclusive(), restriction.getUpperBound())) : "");
    }

    private static String restrictionToString(String greaterThanOrLesserThan, boolean isBoundInclusive, ArtifactVersion artifactVersion) {
        return format("%s%s %s", greaterThanOrLesserThan, isBoundInclusive ? "=" : "", artifactVersion);
    }

    private static void prepareMavenEnv() {
        if (isBlank(System.getProperty("maven.home"))) {
            LOGGER.info("load maven home system property");
            String mvnBinary = executeBashScript("which mvn");
            if (isNotBlank(mvnBinary)) {
                File file = new File(mvnBinary);
                String mvnHome = checkNotBlank(file.getParentFile().getParentFile().getAbsolutePath());
                LOGGER.info("set maven home =< {} >", mvnHome);
                System.setProperty("maven.home", mvnHome);
            } else {
                LOGGER.warn("unable to find maven home");
            }
        }
    }

}
