/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import jakarta.activation.DataHandler;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.config.inner.*;
import org.cmdbuild.minions.MinionService;
import org.cmdbuild.minions.SystemStatus;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_NAMESPACE;
import static org.cmdbuild.minions.SystemStatus.SYST_WAITING_FOR_DATABASE_CONFIGURATION;
import static org.cmdbuild.minions.SystemStatusUtils.serializeSystemStatus;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Component
public class BootWsCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MinionService minionService;
    private final DirectoryService directoryService;
    private final GlobalConfigService globalConfigService;
    private final PatchService patchService;

    public BootWsCommand(MinionService minionService, DirectoryService directoryService, GlobalConfigService globalConfigService, PatchService patchService) {
        this.minionService = minionService;
        this.directoryService = directoryService;
        this.globalConfigService = globalConfigService;
        this.patchService = patchService;
    }

    public CmMapUtils.FluentMap<Object, Object> doStatus() {
        return map().with("status", serializeSystemStatus(minionService.getSystemStatus())).accept(m -> {
            switch (minionService.getSystemStatus()) {
                case SYST_WAITING_FOR_PATCH_MANAGER -> m.put("operationRequired", "applyPatch");
                case SYST_WAITING_FOR_DATABASE_CONFIGURATION -> m.put("operationRequired", "databaseConfiguration");
                case SYST_READY_RESTART_REQUIRED -> m.put("operationRequired", "restart");
            }
        });
    }

    public void doCheckDatabaseConfig(Map<String, String> dbConfig) {
        DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder().withConfig(dbConfig).build();
        new DatabaseCreator(config).checkConfig();
    }

    public SystemStatus doReconfigureDatabase(DataHandler dataHandler, Map<String, String> dbConfig) {
        checkArgument(minionService.hasStatus(SYST_WAITING_FOR_DATABASE_CONFIGURATION), "cannot configure database, system status is = %s", minionService.getSystemStatus());

        DatabaseCreatorConfig config = DatabaseCreatorConfigImpl.builder()
                .withConfig(dbConfig)
                .withSqlPath(new File(directoryService.getWebappDirectory(), "WEB-INF/sql").getAbsolutePath())
                .build();

        File file;
        if (dataHandler != null) {
            file = tempFile(null, "dump");//TODO improve this, use data handler directly
            copy(dataHandler, file);
            config = DatabaseCreatorConfigImpl.copyOf(config).withSource(file.getAbsolutePath()).build();
        } else {
            file = null;
        }

        try {
            new DatabaseCreator(config).configureDatabase();
        } finally {
            if (file != null) {
                deleteQuietly(file);//TODO improve this, use data handler directly
            }
        }

        globalConfigService.putStrings(DATABASE_CONFIG_NAMESPACE, config.getCmdbuildDbConfig());

        //TODO wait for next system status ??
        return minionService.getSystemStatus();
    }

    public List<Patch> doGetPendingPatches() {
        return patchService.getAvailableCorePatches();
    }

    public void doApplyPendingPatches() {
        logger.info("applyPendingPatches");
        patchService.applyPendingPatchesAndFunctions();
    }
}
