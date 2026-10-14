/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.offline;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.cmdbuild.auth.user.OperationUserStore;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.lock.LockType.ILT_OFFLINE;
import static org.cmdbuild.lock.LockTypeUtils.itemIdWithLockType;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class OfflineServiceImpl implements OfflineService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OfflineRepository offlineRepository;
    private final OperationUserStore userStore;
    private final LockService lockService;

    public OfflineServiceImpl(OfflineRepository offlineRepository, OperationUserStore userStore, LockService lockService) {
        this.offlineRepository = checkNotNull(offlineRepository);
        this.userStore = checkNotNull(userStore);
        this.lockService = checkNotNull(lockService);
    }

    @Override
    public List<Offline> getAll() {
        List<OfflineData> allData = offlineRepository.getAllModelData();
        return allData.stream().map(this::toOffline).collect(toList());
    }

    @Override
    public List<Offline> getActiveForCurrentUser() {
        List<OfflineData> allData = offlineRepository.getAllModelData();
        return allData.stream().map(this::toOffline).filter(this::canRead).collect(toList());
    }

    @Nullable
    @Override
    public Offline getByIdOrNull(long offlineId) {
        OfflineData data = offlineRepository.getModelDataByIdOrNull(offlineId);
        return applyOrNull(data, this::toOffline);
    }

    @Nullable
    @Override
    public Offline getByCodeOrNull(String offlineCode) {
        OfflineData data = offlineRepository.getModelDataByCodeOrNull(offlineCode);
        return applyOrNull(data, this::toOffline);
    }

    @Nullable
    @Override
    public Offline getActiveForCurrentUserByCode(String offlineCode) {
        Offline offline = getByCode(offlineCode);
        checkArgument(canRead(offline), "unable to access offline = %s: permission denied", offlineCode);
        return offline;
    }

    @Override
    public boolean lockByCode(String offlineCode) {
        Offline offline = getByCode(offlineCode);
        LockResponse aquireLock = lockService.aquireLock(itemIdWithLockType(ILT_OFFLINE, offline.getId()));
        return aquireLock.isAquired();
    }

    @Override
    public void unlockByCode(String offlineCode) {
        Offline offline = getByCode(offlineCode);
        lockService.releaseLock(itemIdWithLockType(ILT_OFFLINE, offline.getId()));
    }

    @Override
    public Offline create(OfflineData offlineData) {
        return toOffline(offlineRepository.createModelData(offlineData));
    }

    @Override
    public Offline update(OfflineData offlineData) {
        OfflineData data = offlineRepository.getModelDataByCodeOrNull(offlineData.getCode());
        return toOffline(offlineRepository.updateModelData(OfflineDataImpl.copyOf(data)
                .withCode(offlineData.getCode())
                .withDescription(offlineData.getDescription())
                .withMetadata(offlineData.getMetadata())
                .withEnabled(offlineData.isEnabled())
                .build()));
    }

    @Override
    public void delete(String offlineCode) {
        OfflineData data = offlineRepository.getModelDataByCodeOrNull(offlineCode);
        offlineRepository.delete(data.getId());
    }

    private Offline toOffline(OfflineData data) {
        return new OfflineImpl(data.getId(), data.getCode(), data.getDescription(), data.getMetadata(), data.isEnabled());
    }

    private boolean canRead(Offline value) {
        return value.isActive();
    }
}
