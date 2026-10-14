/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import java.io.File;
import static java.lang.String.format;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryServiceImpl implements DirectoryService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final File webappDirectory, configDirectory, containerDirectory, logDirectory;

    public DirectoryServiceImpl(DirectoryServiceImplBuilder builder) {
        this.webappDirectory = builder.webappDirectory;
        this.configDirectory = builder.configDirectory;
        this.containerDirectory = builder.containerDirectory;
        this.logDirectory = builder.logDirectory;
    }

    @Override
    public File getConfigDirectory() {
        return checkNotNull(configDirectory, "config directory not available (check startup logs for errors)");
    }

    @Override
    public File getWebappDirectory() {
        return checkNotNull(webappDirectory, "root directory not available (check startup logs for errors)");
    }

    @Override
    public File getContainerDirectory() {
        return checkNotNull(containerDirectory, "container directory not available (check startup logs for errors)");
    }

    @Override
    public File getLogDirectory() {
        return checkNotNull(getLogDirectoryOrNull(), "log directory not available (check startup logs for errors)");
    }

    @Override
    public boolean hasLogDirectory() {
        return getLogDirectoryOrNull() != null;
    }

    @Override
    public boolean hasConfigDirectory() {
        return configDirectory != null;
    }

    @Override
    public boolean hasWebappDirectory() {
        return webappDirectory != null;
    }

    @Override
    public boolean hasContainerDirectory() {
        return containerDirectory != null;
    }

    @Override
    public final String getContextName() {
        return hasWebappDirectory() ? getContextNameFromWebappName(getWebappName()) : getContextNameFromWebappName(null);
    }

    @Nullable
    private File getLogDirectoryOrNull() {
        if (logDirectory != null) {
            return logDirectory;
        } else {
            return hasContainerLogDirectory() ? getContainerLogDirectory() : null;
        }
    }

    public static String getContextNameFromWebappName(@Nullable String webappName) {
        if (equal(webappName, "ROOT") || isBlank(webappName)) {
            return "cmdbuild";
        } else {
            return webappName;
        }
    }

    public static DirectoryServiceImplBuilder builder() {
        return new DirectoryServiceImplBuilder();
    }

    @Override
    public void backupFileSafe(File file) {
        if (!hasConfigDirectory()) {
            logger.warn("cannot backup file = {} : config directory is not available", file.getAbsolutePath());
        } else {
            try {
                File backupFile = new File(getBackupDirectory(), format("%s_%s.%s.backup", getBaseName(file.getName()), dateTimeFileSuffix(), getExtension(file.getName())).replaceFirst("[.][.]backup", ".backup"));
                logger.debug("backup file = {} to backup file = {}", file.getAbsolutePath(), backupFile.getAbsolutePath());
                FileUtils.copyFile(file, backupFile);
            } catch (Exception ex) {
                logger.error(marker(), "error executing backup of file = {}", file.getAbsolutePath(), ex);
            }
        }
    }

    public static class DirectoryServiceImplBuilder implements Builder<DirectoryServiceImpl, DirectoryServiceImplBuilder> {

        private File webappDirectory, configDirectory, containerDirectory, logDirectory;

        public DirectoryServiceImplBuilder withWebappDirectory(File webappDirectory) {
            this.webappDirectory = webappDirectory;
            return this;
        }

        public DirectoryServiceImplBuilder withConfigDirectory(File configDirectory) {
            this.configDirectory = configDirectory;
            return this;
        }

        public DirectoryServiceImplBuilder withContainerDirectory(File containerDirectory) {
            this.containerDirectory = containerDirectory;
            return this;
        }

        public DirectoryServiceImplBuilder withLogDirectory(File logDirectory) {
            this.logDirectory = logDirectory;
            return this;
        }

        @Override
        public DirectoryServiceImpl build() {
            return new DirectoryServiceImpl(this);
        }

    }
}
