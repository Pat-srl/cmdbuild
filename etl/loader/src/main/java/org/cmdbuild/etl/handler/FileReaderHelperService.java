/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.handler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.utils.EtlUtils;
import static org.cmdbuild.etl.utils.EtlUtils.isProcessed;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileReaderHelperService {

    public final static String FAIL_ON_MISSING_SOURCE_DATA_CONFIG = "failOnMissingSourceData";

    private final DirectoryService directoryService;

    public FileReaderHelperService(DirectoryService directoryService) {
        this.directoryService = checkNotNull(directoryService);
    }

    public FileReaderHelper newHelper(Map<String, ?> config) {
        return new FileReaderHelper(config);
    }

    public class FileReaderHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final String directory, filePattern, targetDirectory;
        private final PostImportAction postImportAction;
        private final boolean failOnMissingSourceData;

        private FileReaderHelper(Map<String, ?> config) {
            directory = checkNotBlank(toStringOrNull(config.get("directory")), "missing `directory` param");
            filePattern = toStringOrNull(config.get("filePattern"));
            postImportAction = parseEnum(toStringOrNull(config.get("postImportAction")), PostImportAction.class);
            switch (postImportAction) {
                case PIA_MOVE_FILES:
                    targetDirectory = checkNotBlank(toStringOrNull(config.get("targetDirectory")), "missing `targetDirectory` param");
                    break;
                default:
                    targetDirectory = null;
            }
            failOnMissingSourceData = toBooleanOrDefault(config.get(FAIL_ON_MISSING_SOURCE_DATA_CONFIG), true);
        }

        public boolean failForMissingFile() {
            return failOnMissingSourceData;
        }

        public boolean failForMissingDir() {
            return failOnMissingSourceData;
        }

        @Nullable
        public File getFileForImport() {
            File dir = directoryService.getFileRelativeToContainerDirectoryIfAvailableAndNotAbsolute(new File(directory));
            if (!dir.exists()) {
                if (failForMissingDir()) {
                    throw new EtlException("CM: invalid source dir =< %s >", directory);
                } else {
                    logger.warn(marker(), "CM: invalid source dir = {}", dir.getAbsolutePath());
                    return null;
                }
            }
            List<File> files = list(dir.listFiles());
            if (isNotBlank(filePattern)) {
                files = files.stream().filter(f -> Pattern.compile(filePattern).matcher(f.getName()).find()).collect(toList());
            }
            files = files.stream().filter(f -> !isProcessed(f.getName())).collect(toList());
            if (files.isEmpty()) {
                if (failOnMissingSourceData) {
                    throw new EtlException("CM: no file found in dir =< %s > with pattern =< %s >", directory, firstNotBlank(filePattern, ".*"));
                } else {
                    logger.warn(marker(), "CM: no file found in dir = {} with pattern =< {} >", dir.getAbsolutePath(), firstNotBlank(filePattern, ".*"));
                    return null;
                }
            }
            checkArgument(files.size() == 1, "expected only one file for import job, but found many = %s", files.stream().map(File::getAbsolutePath).collect(joining(", ")));
            return getOnlyElement(files);
        }

        public void handlePostImportAction() {
            EtlUtils.handlePostImportAction(checkNotNull(getFileForImport()), postImportAction, () -> directoryService.getFileRelativeToContainerDirectoryIfAvailableAndNotAbsolute(new File(checkNotBlank(targetDirectory))));
        }

    }

}
