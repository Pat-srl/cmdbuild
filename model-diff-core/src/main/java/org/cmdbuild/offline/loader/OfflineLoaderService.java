/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.offline.loader;

import jakarta.activation.DataHandler;
import java.util.List;
import java.util.Map;
import org.cmdbuild.modeldiff.data.ModelConfiguration;
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;

/**
 *
 * @author ataboga
 */
public interface OfflineLoaderService {

    boolean isOfflineAvailable();

    public ModelConfiguration getDataModel(String offlineCode);

    public void executeDataFromDataset(String offlineCode, Map<String, String> filters);

    public String executeDiffFromData(String offlineCode, Map<String, String> filters, String tempId);

    public List<Map<String, Object>> executeMergeFromDiff(String offlineCode, GeneratedDiffData diffData, String tempId);

    public String uploadToTempService(DataHandler dataHandler);

    public void sendNotificationForDiff(String offlineCode, String dataTemp);
}
