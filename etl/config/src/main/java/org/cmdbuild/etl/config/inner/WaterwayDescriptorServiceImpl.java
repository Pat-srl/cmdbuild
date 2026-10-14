/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.inner;

import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.io.File;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.etl.config.WaterwayItem;
import org.cmdbuild.eventbus.EventBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.etl.config.WaterwayDescriptorMeta;
import org.cmdbuild.etl.config.WaterwayDescriptorRepository;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.minions.PostStartup;

@Component
public class WaterwayDescriptorServiceImpl implements WaterwayDescriptorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorRepository configRepository;
    private final DirectoryService directoryService;

    private final EventBus eventBus;
    private final Holder<List<WaterwayItem>> cache;

    public WaterwayDescriptorServiceImpl(DirectoryService directoryService, WaterwayDescriptorRepository configRepository, EventBusService busService, CacheService cacheService) {
        this.configRepository = checkNotNull(configRepository);
        this.directoryService = checkNotNull(directoryService);
        eventBus = busService.getDaoEventBus();
        cache = cacheService.newHolder("wy_items");
    }

    private void postConfigUpdateEvent() {
        cache.invalidate();
        eventBus.post(WaterwayConfigReloadEvent.INSTANCE);
        synchronizeToFilesystemSafe();
    }

    @PostStartup
    public void init() {
        synchronizeToFilesystemSafe();
    }

    @Nullable
    @Override
    public WaterwayDescriptorRecord getDescriptorOrNull(String code) {
        return configRepository.getDescriptorOrNull(code);
    }

    @Override
    public WaterwayDescriptorRecord createUpdateDescriptor(DataSource config, @Nullable WaterwayDescriptorMeta meta, boolean overwriteExisting) {
        WaterwayDescriptorRecord configFile = configRepository.createUpdateDescriptor(config, meta, overwriteExisting);
        logger.info("update bus descriptor = {}", configFile);
        postConfigUpdateEvent();
        return configFile;
    }

    @Override
    public WaterwayDescriptorRecord updateDescriptorMeta(String code, WaterwayDescriptorMeta meta) {
        WaterwayDescriptorRecord configFile = configRepository.updateDescriptorMeta(code, meta);
        logger.info("update bus descriptor (meta) = {}", configFile);
        postConfigUpdateEvent();
        return configFile;
    }

    @Override
    public void deleteDescriptor(String code) {
        logger.info("delete bus descriptor =< {} >", code);
        configRepository.deleteDescriptor(code);
        postConfigUpdateEvent();
    }

    @Override
    public List<WaterwayDescriptorRecord> getAllDescriptors() {
        return configRepository.getAllDescriptors();
    }

    @Override
    public List<WaterwayItem> getAllItems() {
        return cache.get(() -> {
            List<WaterwayItem> items = configRepository.getAllItems();
            logger.info("bus config ready, {} items loaded", items.size());
            return items;
        });
    }

    private synchronized void synchronizeToFilesystemSafe() {
        try {
            if (directoryService.hasConfigDirectory()) {
                File folder = new File(directoryService.getConfigDirectory(), "bus");
                logger.debug("copy bus descriptors in config folder =< {} >", folder.getAbsolutePath());
                folder.mkdirs();
                List<WaterwayDescriptorRecord> descriptors = configRepository.getAllDescriptors();
                descriptors.forEach(m -> writeToFile(m.getData(), new File(folder, m.getFileName())));
                Set<String> files = list(descriptors).map(WaterwayDescriptorRecord::getFileName).collect(toSet());
                list(folder.listFiles()).filter(f -> !files.contains(f.getName())).forEach(f -> deleteQuietly(f));
            }
        } catch (Exception ex) {
            logger.error("error synchonizing bus descriptors to config directory", ex);
        }
    }

}
