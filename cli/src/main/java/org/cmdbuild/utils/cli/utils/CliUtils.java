/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import static java.util.stream.Collectors.toList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EMBEDDED_DATABASES;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import org.cmdbuild.debuginfo.BuildInfo;
import static org.cmdbuild.debuginfo.BuildInfoUtils.loadBuildInfoFromWarDirSafe;
import static org.cmdbuild.debuginfo.BuildInfoUtils.loadBuildInfoFromWarFileSafe;
import static org.cmdbuild.utils.cli.Main.getCliHome;
import static org.cmdbuild.utils.cli.Main.isRunningFromWarFile;
import static org.cmdbuild.utils.cli.Main.isRunningFromWebappDir;
import org.cmdbuild.utils.cli.commands.DbconfigCommandRunner;
import static org.cmdbuild.utils.cli.utils.DatabaseUtils.buildDatabaseCreator;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import org.cmdbuild.utils.io.StreamProgressListener;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.LoggerFactory;

public class CliUtils {

    public static boolean hasInteractiveConsole() {
        return System.console() != null;
    }

    public static File getDbdumpFile(String name) {
        return checkNotNull(getDbdumpFileOrNull(name), "file not found for name =< %s >", name);
    }

    @Nullable
    public static File getDbdumpFileOrNull(String name) {
        File file = new File(name);
        if (file.exists()) {
            return file;
        } else {
            List<File> dirs = Splitter.on(":").trimResults().omitEmptyStrings().splitToList(nullToEmpty(System.getenv("CMDBUILD_DBDUMP_LOCATIONS"))).stream().map(d -> new File(d)).filter(File::isDirectory).collect(toList());
            for (File dir : dirs) {
                file = new File(dir, name);
                if (file.exists()) {
                    return file;
                }
            }
        }
        if (EMBEDDED_DATABASES.contains(name)) {
            DatabaseCreator databaseCreator = buildDatabaseCreator(DatabaseCreatorConfigImpl.builder().build());
            return databaseCreator.getDumpFile(name);
        }
        return null;
    }

    public static File prepareDumpFile(File sourceFile) {
        if (sourceFile.getName().endsWith(".zip")) {
            sourceFile = extractDatabaseFromZipFile(sourceFile);
        }
        return sourceFile;
    }

    public static File extractDatabaseFromZipFile(File file) {
        try (ZipFile zipFile = new ZipFile(file)) {
            Optional<? extends ZipEntry> entry = zipFile.stream().filter((e) -> e.getName().endsWith(".backup")).findAny();
            checkArgument(entry.isPresent(), "database backup not found in zip file = %s", file.getAbsolutePath());
            LoggerFactory.getLogger(DbconfigCommandRunner.class).info("selected database backup file = {}", entry.get().getName());
            File dump = tempFile(null, "dump");
            FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry.get()), dump);
            return dump;
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static StreamProgressListener buildProgressListener(String name) {
        AtomicBoolean isFirst = new AtomicBoolean(true);
        return (e) -> {
            if (!isFirst.getAndSet(false)) {
                System.out.print("\033[1A\033[2K");
            }
            System.out.printf("  %s progress: %s\n", name, e.getProgressDescriptionDetailed());
        };
    }

    public static BuildInfo getBuildInfo() {
        BuildInfo buildInfo;
        if (isRunningFromWarFile()) {
            buildInfo = loadBuildInfoFromWarFileSafe(getCliHome());
        } else {
            checkArgument(isRunningFromWebappDir());
            buildInfo = loadBuildInfoFromWarDirSafe(getCliHome());
        }
        return buildInfo;
    }

}
