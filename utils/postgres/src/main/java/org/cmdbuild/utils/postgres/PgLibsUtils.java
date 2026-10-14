/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import java.io.File;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.DOTALL;
import jakarta.annotation.Nullable;
import org.apache.maven.artifact.versioning.ComparableVersion;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.cmdbuild.utils.io.CmPlatformUtils.OS_LINUX;
import static org.cmdbuild.utils.io.CmPlatformUtils.OS_WINDOWS;
import static org.cmdbuild.utils.io.CmPlatformUtils.getOsType;
import static org.cmdbuild.utils.io.CmPlatformUtils.isLinux;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PgLibsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static Map<String, Map<String, String>> OS_AND_COMMAND_TO_EXECUTABLE = ImmutableMap.of(
            OS_LINUX, ImmutableMap.of(
                    "pg_dump", "pg_dump",
                    "pg_restore", "pg_restore",
                    "psql", "psql"
            ),
            OS_WINDOWS, ImmutableMap.of(
                    "pg_dump", "pg_dump.exe",
                    "pg_restore", "pg_restore.exe",
                    "psql", "psql.exe"
            ));

    public static List<PgLibInfo> getPgLibs() {
        FluentList<PgLibInfo> list = list();
        if (isLinux()) {
            list("/usr/lib/postgresql/").map(File::new).filter(File::isDirectory).flatMap(f -> list(f.listFiles())).filter(File::isDirectory).sorted().map(f -> new File(f, "bin")).filter(File::isDirectory).forEach(f -> {
                try {
                    list.add(getLibVersion(f.getAbsolutePath()));
                } catch (Exception ex) {
                    LOGGER.debug("invalid pg lib folder = {}", f, ex);
                }
            });
        }
        list.sort(Ordering.natural().onResultOf(l -> new ComparableVersion(l.getVersion())));
        if (isNotBlank(System.getenv("CMDBUILD_POSTGRES_HOME"))) {
            String path = System.getenv("CMDBUILD_POSTGRES_HOME");
            try {
                list.add(getLibVersion(path));
            } catch (Exception ex) {
                LOGGER.warn(marker(), "invalid pg home from env CMDBUILD_POSTGRES_HOME =< {} >", path, ex);
            }
        }
        try {
            list.add(getLibVersion(null));
        } catch (Exception ex) {
            LOGGER.debug("invalid default pg lib", ex);
        }
        checkArgument(!list.isEmpty(), "CM: autodetect failed to find postgres libs on this system");
        return list.reverse().immutableCopy();
    }

    private static PgLibInfo getLibVersion(@Nullable String path) {
        String command = OS_AND_COMMAND_TO_EXECUTABLE.getOrDefault(getOsType(), emptyMap()).getOrDefault("psql", "psql");
        if (isNotBlank(path)) {
            command = new File(path, command).getAbsolutePath();
        }
        String out = executeProcess(command, "--version");
        Matcher matcher = Pattern.compile(".*?([0-9]+([.][0-9]+)?).*", DOTALL).matcher(out);
        checkArgument(matcher.matches(), "invalid pg version string format =< %s >", abbreviate(out));
        String version = checkNotBlank(matcher.group(1)).replaceFirst("^([0-9]{2}).[0-9]+$", "$1");
        File home = isBlank(path) ? null : new File(path).getAbsoluteFile();
        LOGGER.debug("found pg lib dir =< {} > with version =< {} >", home, version);
        return new PgLibInfo() {
            @Override
            public String getVersion() {
                return version;
            }

            @Override
            @Nullable
            public File getPgLibHome() {
                return home;
            }
        };
    }

    public interface PgLibInfo {

        String getVersion();

        @Nullable
        File getPgLibHome();
    }
}
