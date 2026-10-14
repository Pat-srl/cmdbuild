/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;

public interface EasyuploadService {

    EasyuploadItem create(DataHandler dataHandler, @Nullable String fileOrDir, @Nullable String description);

    EasyuploadItem createOrUpdate(DataHandler dataHandler, @Nullable String fileOrDir, @Nullable String description);

    EasyuploadItem update(long fileId, @Nullable byte[] data, @Nullable String description);

    List<EasyuploadItemInfo> getByDir(String dir);

    List<EasyuploadItemInfo> getAll();

    void delete(long fileId);

    @Nullable
    EasyuploadItem getByPathOrNull(String path);

    EasyuploadItem get(String path);

    EasyuploadItem getById(long fileId);

    DataSource getAllUploadsAsZipFile();

    DataSource getUploadsAsZipFile(String dir);

    void uploadZip(byte[] toByteArray);

    List<String> getSubdirsForDir(String path);

    default boolean hasElementByPath(String path) {
        return getByPathOrNull(path) != null;
    }

    default EasyuploadItem getByPath(String path) {
        return checkNotNull(getByPathOrNull(path), "easyupload item not found for path =< %s >", path);
    }
}
