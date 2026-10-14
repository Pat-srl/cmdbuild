package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.dao.ConfigurableDataSource;
import com.google.common.base.Preconditions;

import java.util.List;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.DatasourceConfiguredEvent;
import static org.cmdbuild.dao.config.inner.PatchService.DEFAULT_CATEGORY;
import org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent;
import org.cmdbuild.dao.postgres.listener.PostgresNotificationEventService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.utils.io.CmIoUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class PatchServiceImpl implements PatchService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigurableDataSource dataSource;
    private final FunctionManager functionManager;
    private final PatchManager patchManager;
    private final EventBus systemEventBus;

    @Autowired
    public PatchServiceImpl(PatchManager patchManager, ConfigurableDataSource dataSource, FunctionManager functionManager, EventBusService eventBusService, PostgresNotificationEventService postgresNotificationEventService) {
        this(patchManager, dataSource, functionManager, eventBusService.getSystemEventBus(), postgresNotificationEventService);
    }

    public PatchServiceImpl(PatchManager patchManager, ConfigurableDataSource dataSource, FunctionManager functionManager, EventBus eventBus, PostgresNotificationEventService postgresNotificationEventService) {
        this.systemEventBus = checkNotNull(eventBus);
        this.patchManager = checkNotNull(patchManager);
        this.dataSource = checkNotNull(dataSource);
        this.functionManager = checkNotNull(functionManager);
        if (dataSource.isReady()) {
            initPatches();
        }
        dataSource.getEventBus().register(new Object() {
            @Subscribe
            public void handleDatasourceConfiguredEvent(DatasourceConfiguredEvent event) {
                initPatches();
            }
        });
        postgresNotificationEventService.getEventBus().register(new Object() {

            @Subscribe
            public void handlePostgresNotificationEvent(PostgresNotificationEvent event) {
                if (event.isEvent("patch_service_all_patches_applied")) {
                    systemEventBus.post(AllPatchesAppliedEvent.INSTANCE);
                }
            }
        });
        logger.info("init");
    }

    @Override
    public List<Patch> getAvailableCorePatches() {
        List<Patch> patches = patchManager.getAvailableCorePatches();
        if (functionManager.hasPendingFunctions()) {
            patches = list(patches).with(PatchImpl.builder()
                    .withVersion("functions")
                    .withDescription("function upgrade")
                    .withCategory(DEFAULT_CATEGORY).build());
        }
        return patches;
    }

    @Override
    @Nullable
    public String getLastPatchOnDbKeyOrNull() {
        return patchManager.getLastPatchOnDbKeyOrNull();
    }

    @Override
    public List<PatchInfo> getAllPatches() {
        return patchManager.getAllPatches();
    }

    @Override
    public void rebuildPatchesHash() {
        patchManager.rebuildPatchesHash();
    }

    @Override
    public void applyPendingPatchesAndFunctions() {
        doApplyPendingPatchesAndFunctions(null);
    }

    @Override
    public void applyPendingPatchesAndFunctionsUpTo(String lastPatch) {
        doApplyPendingPatchesAndFunctions(checkNotBlank(lastPatch));
    }

    private void doApplyPendingPatchesAndFunctions(@Nullable String lastPatch) {
        logger.info("apply patches on db = {}", dataSource.getDatabaseUrl());
        aquirePatchSyslock();
        patchManager.reset();

        lastPatch = isBlank(lastPatch) ? null : patchManager.getPatchByVersion(lastPatch).getKey();

        try {
            functionManager.upgradeFunctionsWithPatches(patchManager.getPatchesOnDb());
            systemEventBus.post(PatchAppliedOnDbEvent.INSTANCE);

            int count = 0;
            List<Patch> availablePatches;
            while (!(availablePatches = getAvailableCorePatches()).isEmpty() && (isBlank(lastPatch) || !equal(lastPatch, getLastPatchOnDbKeyOrNull()))) {
                Patch patch = availablePatches.iterator().next();
                patchManager.applyPatchAndStore(patch);
                systemEventBus.post(PatchAppliedOnDbEvent.INSTANCE);
                functionManager.upgradeFunctionsWithPatches(patchManager.getPatchesOnDb());
                systemEventBus.post(PatchAppliedOnDbEvent.INSTANCE);
                count++;
            }

            if (!patchManager.hasPendingPatches()) {
                Preconditions.checkArgument(!functionManager.hasPendingFunctions(), "error: all patches applied but function manager has still some pending functions");
            }

            if (count > getAllPatches().size() / 2) { //TODO improve this
                logger.info("many patches applied, run VACUUM ANALYZE");
                new JdbcTemplate(dataSource).execute("VACUUM ANALYZE");
            }

            logger.debug("applyPendingPatchesAndFunctions END");
            handleUpToDate();
        } finally {
            releasePatchSyslock();
        }
    }

    @Override
    public boolean hasPendingPatchesOrFunctions() {
        return patchManager.hasPendingPatches() || functionManager.hasPendingFunctions();
    }

    @Override
    public EventBus getEventBus() {
        return systemEventBus;
    }

    private void initPatches() {
        logger.debug("run patch structure check on db");
        String sqlScript = CmIoUtils.readToString(getClass().getResourceAsStream("/sql/misc/patch_init.sql"));
        new JdbcTemplate(dataSource).execute(sqlScript);
    }

    private void aquirePatchSyslock() {
        logger.debug("aquiring database lock for patch operations");
        new JdbcTemplate(dataSource).execute("SELECT _cm3_system_lock_aquire('patch')");
    }

    private void releasePatchSyslock() {
        logger.debug("releasing database lock for patch operations");
        new JdbcTemplate(dataSource).execute("SELECT _cm3_system_lock_release('patch')");
    }

    private void handleUpToDate() {
        if (!patchManager.hasPendingPatches() && !functionManager.hasPendingFunctions()) {
            logger.info("database is up to date, all patches applied; last patch applied is = {}", getLastPatchOnDbKeyOrNull());
            systemEventBus.post(AllPatchesAppliedEvent.INSTANCE);
            new JdbcTemplate(dataSource).execute("SELECT _cm3_system_event_send('patch_service_all_patches_applied')");//TODO attach information about source node, avoid loopback
        }
    }

}
