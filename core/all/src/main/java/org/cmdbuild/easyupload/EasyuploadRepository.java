/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import java.util.List;
import jakarta.annotation.Nullable;

public interface EasyuploadRepository {

    EasyuploadItem create(EasyuploadItem item);

    EasyuploadItem update(EasyuploadItem item);

    void delete(long fileId);

    @Nullable
    EasyuploadItem getByPathOrNull(String path);

    List<EasyuploadItem> getAll();

    EasyuploadItem getItemById(long fileId);

    EasyuploadItem getByPath(String path);

    List<EasyuploadItemInfo> getAllInfo();

    List<EasyuploadItemInfo> getInfoByDir(String dir);

    List<EasyuploadItem> getAllByDir(String dir);

    List<String> getSubdirsForDir(String path);

}
