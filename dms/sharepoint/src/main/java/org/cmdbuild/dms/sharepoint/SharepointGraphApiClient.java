/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.sharepoint;

import java.util.List;
import java.util.Map;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import static org.cmdbuild.dms.sharepoint.SharepointDmsUtils.splitPath;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SharepointGraphApiClient extends AutoCloseable {

    boolean isOk();

    void checkOk();

    void checkRefreshToken();

    List<Map<String, Object>> listFolderContent(List<String> path);

    List<Map<String, Object>> listFolderContentCreateIfMissing(List<String> path);

    Map<String, Object> getItemById(String itemId, boolean includeVersions);

    Map<String, Object> getItemByPath(List<String> path);

    @Nullable
    Map<String, Object> getItemByPathOrNull(List<String> path);

    DataHandler getItemContentByPath(List<String> path);

    DataHandler getItemContent(String itemId);

    DataHandler getItemPreview(String itemId);

    DataHandler getVersionContent(String itemId, String version);

    Map<String, Object> setItem(List<String> folder, DocumentData document);

    Map<String, Object> setItem(String documentId, DocumentData document);

    void deleteItemIfExists(List<String> path);

    void deleteItem(List<String> path);

    void deleteItem(String documentId);

    List<String> queryPathWithFulltext(String path, String textQuery, boolean orNull);

    default Map<String, Object> setItem(String folder, String fileName, DataHandler data) {
        return setItem(splitPath(folder), DocumentDataImpl.builder().withData(data).withFilename(checkNotBlank(fileName)).build());
    }

    default DataHandler getItemContentByPath(String path) {
        return getItemContentByPath(splitPath(path));
    }

    default void deleteItemByPath(String path) {
        deleteItem(splitPath(path));
    }

    default Map<String, Object> getItemById(String itemId) {
        return getItemById(itemId, false);
    }

    default List<Map<String, Object>> listFolderContent(String path) {
        return listFolderContent(splitPath(path));
    }

    default void renameOrMoveItem(String sourcePath, String targetPath) {
        DataHandler data = getItemContentByPath(sourcePath);
        deleteItemByPath(sourcePath);
        setItem(FilenameUtils.getPath(targetPath), FilenameUtils.getName(targetPath), data);//TODO improve this, use move api
    }
}
