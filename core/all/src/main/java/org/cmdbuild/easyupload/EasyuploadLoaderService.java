/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import static org.cmdbuild.easyupload.EasyuploadUtils.normalizePath;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.DirectoryService;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import org.cmdbuild.minions.PostStartup;

@Component
public class EasyuploadLoaderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;
    private final EasyuploadService easyuploadService;

    public EasyuploadLoaderService(DirectoryService directoryService, EasyuploadService easyuploadService) {
        this.directoryService = checkNotNull(directoryService);
        this.easyuploadService = checkNotNull(easyuploadService);
    }

    @ScheduledJob(value = "*/10 * * * * ?", persistRun = false)//run every 10 seconds
    @PostStartup
    public synchronized void checkUploadsFolderAndLoadContent() {
        listOf(File.class).accept((l) -> {
            if (directoryService.hasConfigDirectory()) {
                l.add(new File(directoryService.getConfigDirectory(), "uploads"), new File(directoryService.getConfigDirectory(), "upload"));
            }
            if (directoryService.hasWebappDirectory()) {
                l.add(new File(directoryService.getWebappDirectory(), "uploads"), new File(directoryService.getWebappDirectory(), "upload"));
            }
        }).forEach((dir) -> {
            if (dir.exists() && dir.isDirectory() && dir.canRead()) {
                scanDir(dir);
            }
        });
    }

    private void scanDir(File dir) {
        logger.debug("scan dir = {}", dir);
        Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.fileFileFilter(), FileFilterUtils.directoryFileFilter());
        if (!files.isEmpty()) {
            logger.info("processing {} files from upload dir = {}", files.size(), dir);
            files.stream().forEach((file) -> loadFileAndDelete(dir, file));
        }
    }

    private void loadFileAndDelete(File dir, File file) {
        logger.info("load file = {}", file.getAbsolutePath());
        String path = normalizePath(dir.toPath().relativize(file.toPath()).toString());
        EasyuploadItem item = easyuploadService.getByPathOrNull(path);//TODO get only info, not content
        byte[] data = toByteArray(file);
        String hash = hash(data);
        if (item != null) {
            if (equal(hash, item.getHash())) {
                logger.info("skip file {}, already present in db with same content", file.getAbsolutePath());
            } else {
                easyuploadService.update(item.getId(), data, null);
            }
        } else {
            easyuploadService.create(newDataHandler(data), path, null);
        }
        FileUtils.deleteQuietly(file);
    }

}
