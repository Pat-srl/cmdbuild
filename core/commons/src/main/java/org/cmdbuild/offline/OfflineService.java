/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.offline;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;

/**
 *
 * @author ataboga
 */
public interface OfflineService {

    List<Offline> getAll();

    List<Offline> getActiveForCurrentUser();

    Offline getByIdOrNull(long offlineId);

    default Offline getById(long offlineId) {
        return checkNotNull(getByIdOrNull(offlineId), "offline not found for id =< %s >", offlineId);
    }

    Offline getByCodeOrNull(String offlineCode);

    default Offline getByCode(String offlineCode) {
        return checkNotNull(getByCodeOrNull(offlineCode), "offline not found for code =< %s >", offlineCode);
    }

    Offline getActiveForCurrentUserByCode(String offlineCode);

    boolean lockByCode(String offlineCode);

    void unlockByCode(String offlineCode);

    Offline create(OfflineData offlineData);

    Offline update(OfflineData offlineData);

    void delete(String offlineCode);
}
