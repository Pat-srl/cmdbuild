/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.base.Supplier;
import org.cmdbuild.offline.Offline;
import org.cmdbuild.offline.OfflineService;
import org.cmdbuild.offline.loader.OfflineLoaderService;
import org.cmdbuild.service.rest.v4.model.WsOfflineData;
import org.cmdbuild.utils.lang.CmPreconditions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ldare
 */
@Component
public class OfflineWsCommand {

    private final OfflineService offlineService;
    private final OfflineLoaderService offlineLoaderService;

    public OfflineWsCommand(OfflineService offlineService, OfflineLoaderService offlineLoaderService) {
        this.offlineService = offlineService;
        this.offlineLoaderService = offlineLoaderService;
    }

    public List<Offline> doReadAll(Supplier<List<Offline>> function) {
        checkOfflineAvailable();
        return function.get();
    }

    public Offline doRead(String offlineCode) {
        checkOfflineAvailable();
        return offlineService.getByCode(offlineCode);
    }

    public Offline doCreate(WsOfflineData data) {
        checkOfflineAvailable();
        return offlineService.create(data.toOfflineData().build());
    }

    public Offline doUpdate(String offlineCode, WsOfflineData data) {
        checkOfflineAvailable();
        return offlineService.update(data.toOfflineData().withCode(offlineCode).build());
    }

    public void doDelete(String offlineCode) {
        checkOfflineAvailable();
        offlineService.delete(offlineCode);
    }

    public void checkOfflineAvailable() {
        CmPreconditions.checkArgument(offlineLoaderService.isOfflineAvailable(), "offline is not available");
    }
}
