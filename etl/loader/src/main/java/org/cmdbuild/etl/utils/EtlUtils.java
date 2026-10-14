/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.utils;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.function.Supplier;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.etl.handler.PostImportAction;
import static org.cmdbuild.etl.handler.PostImportAction.PIA_DELETE_FILES;
import static org.cmdbuild.etl.handler.PostImportAction.PIA_DISABLE_FILES;
import static org.cmdbuild.etl.handler.PostImportAction.PIA_DO_NOTHING;
import static org.cmdbuild.etl.handler.PostImportAction.PIA_MOVE_FILES;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_PROCESSING_REPORT;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static String PROCESSED_FILE_EXT = "_processed", PROCESSED_FILE_REGEXP = ".*_processed[.][^.]+|.*_processed";

    public static boolean hasEtlProcessingResult(WaterwayMessageData data) {
        return data.hasAttachment(WY_PROCESSING_REPORT);
    }

    public static EtlProcessingResult getEtlProcessingResult(WaterwayMessageData data) {
        return data.getAttachmentContentOrNull(WY_PROCESSING_REPORT);//TODO processing of serialized report
    }

    @Nullable
    public static EtlProcessingResult getEtlProcessingResultOrNull(WaterwayMessageData data) {
        return hasEtlProcessingResult(data) ? getEtlProcessingResult(data) : null;
    }

    public static void handlePostImportAction(File file, PostImportAction postImportAction, Supplier<File> targetDirSupplier) {
        switch (postImportAction) {
            case PIA_DELETE_FILES -> {
                LOGGER.debug("delete processed file =< {} >", file);
                checkArgument(file.delete(), "unable to delete file =< %s >", file);
            }
            case PIA_DISABLE_FILES -> {
                File targetForRename = new File(filenameForProcessedFile(file.getAbsolutePath()));
                LOGGER.debug("move processed file to {}", targetForRename.getAbsolutePath());
                checkArgument(file.renameTo(targetForRename), "unable to move file to %s", targetForRename.getAbsolutePath());
            }
            case PIA_MOVE_FILES -> {
                File targetDir = targetDirSupplier.get();
                targetDir.mkdirs();
                checkArgument(targetDir.isDirectory(), "invalid target dir =< %s >", targetDir.getAbsolutePath());
                File targetForMove = new File(targetDir, file.getName());
                LOGGER.debug("move processed file to {}", targetForMove.getAbsolutePath());
                checkArgument(file.renameTo(targetForMove), "failed to move file to %s", targetForMove.getAbsolutePath());
            }
            case PIA_DO_NOTHING -> {
            }
        }
    }

    public static Map<String, String> getMessageAndPayloadMeta(@Nullable WaterwayMessageData data) {
        return data == null ? emptyMap() : map(data.hasPayload() ? data.getPayload().getMeta() : emptyMap()).with(data.getMeta());
    }

    public static String filenameForProcessedFile(String filename) {
        checkNotBlank(filename);
        return FilenameUtils.getPath(filename) + FilenameUtils.getBaseName(filename) + "_" + dateTimeFileSuffix() + PROCESSED_FILE_EXT + (isBlank(FilenameUtils.getExtension(filename)) ? "" : ("." + FilenameUtils.getExtension(filename)));
    }

    public static boolean isProcessed(String filename) {
        return checkNotBlank(filename).matches(PROCESSED_FILE_REGEXP);
    }
}
