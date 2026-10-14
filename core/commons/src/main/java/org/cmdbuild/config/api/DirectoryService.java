/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.MoreObjects.firstNonNull;
import jakarta.annotation.Nullable;
import java.io.File;
import static java.util.Arrays.asList;

public interface DirectoryService {

    File getConfigDirectory();

    File getContainerDirectory();

    File getLogDirectory();

    File getWebappDirectory();

    boolean hasConfigDirectory();

    boolean hasWebappDirectory();

    boolean hasContainerDirectory();

    boolean hasLogDirectory();

    void backupFileSafe(File file);

    String getContextName();

    default File getBackupDirectory() {
        File file = new File(getConfigDirectory(), "backup");
        file.mkdirs();
        return file;
    }

    default boolean hasBackupDirectory() {
        return hasConfigDirectory();
    }

    default String getWebappName() {
        return getWebappDirectory().getName();
    }

    default File getWebappLibDirectory() {
        return new File(getWebappDirectory(), "WEB-INF/lib");
    }

    default File getWebappContextFile() {
        return new File(getWebappDirectory(), "META-INF/context.xml");
    }

    default File getContainerLibDirectory() {
        return new File(getContainerDirectory(), "lib");
    }

    default boolean hasContainerLogDirectory() {
        return hasContainerDirectory() && getContainerLogDirectory().isDirectory();
    }

    default boolean hasWebappLibDirectory() {
        return hasWebappDirectory() && getWebappLibDirectory().isDirectory();
    }

    default File getContainerLogDirectory() {
        return new File(getContainerDirectory(), "logs");
    }

    default File getFileRelativeToContainerDirectoryIfAvailableAndNotAbsolute(File file) {
        if (!file.isAbsolute() && hasContainerDirectory()) {
            return new File(getContainerDirectory(), file.getPath());
        } else {
            return file;
        }
    }

    /**
     * get file from lib dir, by regexp pattern (match against whole name)
     */
    @Nullable
    default File getLibByPattern(String pattern) {
        File libDir = getWebappLibDirectory();
        return libDir == null ? null : asList(firstNonNull(libDir.listFiles(), new File[]{})).stream().filter((file) -> file.isFile() && file.getName().matches(pattern)).findFirst().orElse(null);
    }

}
