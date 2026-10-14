/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;
import org.cmdbuild.offline.loader.OfflineLoaderService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class OfflineDataWsCommand {

    private final OfflineLoaderService offlineLoaderService;

    public OfflineDataWsCommand(OfflineLoaderService offlineLoaderService) {
        this.offlineLoaderService = checkNotNull(offlineLoaderService);
    }

    public void doLoad(String offlineCode, Map<String, String> filters) {
        offlineLoaderService.executeDataFromDataset(offlineCode, filters);
    }

    public String doDiff(String offlineCode, String tempId) {
        return offlineLoaderService.executeDiffFromData(offlineCode, map(), tempId);
    }

    public List<Map<String, Object>> doMerge(String offlineCode, String tempId, GeneratedDiffData wsDiffData) {
        return offlineLoaderService.executeMergeFromDiff(offlineCode, wsDiffData, tempId);
    }

    public void doNotify(DataHandler dataHandler, String offlineCode) {
        String tempId = offlineLoaderService.uploadToTempService(dataHandler);
        offlineLoaderService.sendNotificationForDiff(offlineCode, tempId);
    }
}
