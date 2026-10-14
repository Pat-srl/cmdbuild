/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.offline;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;

public interface OfflineRepository {

    List<OfflineData> getAllModelData();

    OfflineData getModelDataByIdOrNull(long offlineId);

    OfflineData getModelDataByCodeOrNull(String offlineCode);

    OfflineData updateModelData(OfflineData offlineData);

    OfflineData createModelData(OfflineData offlineData);

    void delete(long id);

    default OfflineData getOfflineDataById(long offlineId) {
        return checkNotNull(getModelDataByIdOrNull(offlineId));
    }
}
