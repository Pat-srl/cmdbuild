/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import org.cmdbuild.common.CmContentInfo;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadItemInfo;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.easyupload.EasyuploadUtils;
import org.cmdbuild.service.rest.v4.model.WsUploadData;
import org.cmdbuild.temp.TempService;
import org.cmdbuild.utils.io.CmIoUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.temp.TempServiceUtils.isTempId;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;

/**
 * @author ldare
 */
@Component
public class UploadWsCommand {

    private final EasyuploadService easyuploadService;
    private final TempService tempService;

    public UploadWsCommand(EasyuploadService easyuploadService, TempService tempService) {
        this.easyuploadService = easyuploadService;
        this.tempService = tempService;
    }

    public List<EasyuploadItemInfo> doReadMany(String dir) {
        List<EasyuploadItemInfo> list;
        if (isNotBlank(dir)) {
            list = easyuploadService.getByDir(dir);
        } else {
            list = easyuploadService.getAll();
        }
        return list;
    }

    public CmContentInfo doReadFile(String fileId) {
        if (isTempId(fileId)) {
            return tempService.getTempInfo(fileId);
        } else {
            return easyuploadService.getById(toLong(fileId));
        }
    }

    public DataHandler doDownloadFile(String fileId) {
        if (isTempId(fileId)) {
            return tempService.getTempData(fileId);
        } else {
            EasyuploadItem item = easyuploadService.getById(toLong(fileId));
            return EasyuploadUtils.toDataHandler(item);
        }
    }

    public DataHandler doDownloadManyFiles(String dir) {
        return new DataHandler(easyuploadService.getUploadsAsZipFile(dir));
    }

    public DataHandler doDownloadAllFiles() {
        return new DataHandler(easyuploadService.getAllUploadsAsZipFile());
    }

    public void doDeleteTempFile(String tempId) {
        tempService.deleteTempData(tempId);
    }

    public EasyuploadItem doCreate(DataHandler dataHandler, String pathFromQuery, WsUploadData uploadData, String pathFromMultipart, Boolean overwriteExisting) {
        String path = firstNotBlankOrNull(pathFromQuery, pathFromMultipart, applyOrNull(uploadData, WsUploadData::getPath));
        String description = applyOrNull(uploadData, ud -> nullToEmpty(ud.getDescription()));
        return overwriteExisting ? easyuploadService.createOrUpdate(dataHandler, path, description) : easyuploadService.create(dataHandler, path, description);
    }

    public void doLoadZipFile(DataHandler dataHandler) {
        easyuploadService.uploadZip(toByteArray(dataHandler));
    }

    public EasyuploadItem doUpdate(Long fileId, DataHandler dataHandler, WsUploadData uploadData) {
        String description = applyOrNull(uploadData, ud -> nullToEmpty(ud.getDescription()));
        return easyuploadService.update(fileId, applyOrNull(dataHandler, CmIoUtils::toByteArray), description);
    }

    public void doDeleteFile(String fileId) {
        if (isTempId(fileId)) {
            tempService.deleteTempData(fileId);
        } else {
            easyuploadService.delete(toLong(fileId));
        }
    }
}
