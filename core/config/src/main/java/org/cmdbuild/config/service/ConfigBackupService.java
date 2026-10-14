package org.cmdbuild.config.service;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import static java.lang.String.format;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.io.CmPropertyUtils.writeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConfigBackupService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigRepositoryFacade configStore;
    private final DirectoryService directoryService;

    public ConfigBackupService(DirectoryService directoryService, ConfigRepositoryFacade configStore) {
        this.configStore = checkNotNull(configStore);
        this.directoryService = checkNotNull(directoryService);
    }

    @ScheduledJob("0 0 04 * * ?")// run once per day at 4 am
    public void backupConfig() {
        if (directoryService.hasBackupDirectory()) {
            try {
                File file = new File(directoryService.getBackupDirectory(), format("config_%s.backup", dateTimeFileSuffix()));
                writeProperties(file, configStore.getAllConfig());//TODO encrypt where required
                logger.debug("backup of system config to file = %s", file.getAbsolutePath());
            } catch (Exception ex) {
                logger.warn("unable to backup system config", ex);
            }
        }
    }
}
