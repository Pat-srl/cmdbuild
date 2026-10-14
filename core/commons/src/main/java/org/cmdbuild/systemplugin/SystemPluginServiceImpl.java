/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.systemplugin;

import com.google.common.eventbus.EventBus;
import jakarta.activation.DataSource;
import java.io.File;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventImpl;
import org.cmdbuild.minions.PostStartup;
import org.cmdbuild.minions.SystemReadyRestartRequiredEvent;
import static org.cmdbuild.systemplugin.SystemPluginUtils.getPluginFolderFromContext;
import static org.cmdbuild.systemplugin.SystemPluginUtils.hasConfigPluginDirectory;
import static org.cmdbuild.systemplugin.SystemPluginUtils.removePluginFilesFromConfigFolder;
import static org.cmdbuild.systemplugin.SystemPluginUtils.removePluginFilesFromWarFileInplace;
import static org.cmdbuild.systemplugin.SystemPluginUtils.scanConfigFolderForPlugins;
import static org.cmdbuild.systemplugin.SystemPluginUtils.scanFolderForPlugins;
import static org.cmdbuild.utils.encode.CmPackUtils.pack;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.notNull;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.unsafeConsumer;
import static org.cmdbuild.utils.maven.MavenUtils.checkRangeVersion;
import static org.cmdbuild.utils.maven.MavenUtils.getNameAndVersionFromFilename;
import static org.cmdbuild.utils.maven.MavenUtils.versionRangeToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import static org.springframework.util.FileCopyUtils.copy;

@Component
@Primary
public class SystemPluginServiceImpl implements SystemPluginService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;
    private final BuildInfoService buildInfoService;
    private final GlobalConfigService configService;
    private final SystemPluginConfiguration pluginConfiguration;
    private final CacheService cacheService;
    private final EventBus systemEventBus;

    private final List<SystemPlugin> list = list();
    private final List<SystemPlugin> toBeRemovedlist = list();

    public SystemPluginServiceImpl(DirectoryService directoryService, BuildInfoService buildInfoService, GlobalConfigService configService, SystemPluginConfiguration pluginConfiguration, CacheService cacheService, EventBusService systemEventService) {
        this.directoryService = checkNotNull(directoryService);
        this.buildInfoService = checkNotNull(buildInfoService);
        this.configService = checkNotNull(configService);
        this.pluginConfiguration = checkNotNull(pluginConfiguration);
        this.cacheService = checkNotNull(cacheService);
        this.systemEventBus = systemEventService.getSystemEventBus();
    }

    @PostStartup
    public void init() {
        if (directoryService.hasWebappLibDirectory()) {
            logger.info("scan libs for system plugins");
            scanFolderForPlugins(directoryService.getWebappLibDirectory()).forEach(this::loadPlugin);
            if (directoryService.hasConfigDirectory()) {
                logger.debug("plugin folder =< {} >", getPluginFolderFromContext(directoryService.getWebappContextFile()).getAbsolutePath());
                scanConfigFolderForPlugins(getPluginFolderFromContext(directoryService.getWebappContextFile())).forEach(this::loadPlugin);
            }
            logger.info("found {} system plugins: {}", list.size(), list.stream().map(SystemPlugin::getName).sorted().collect(joining(", ")));
            configService.putString("org.cmdbuild.plugin.info", pack(toJson(list(list).map(p -> map("name", p.getName(), "version", p.getVersion())))));
            configService.putString("org.cmdbuild.plugin.list", list(list).map(SystemPlugin::getNameVersion).collect(joining(",")));
            postPluginUpdateEvent();
        } else {
            logger.warn("webapp lib directory not available: skip system plugin processing");
        }
    }

    public void postPluginUpdateEvent() {
        cacheService.invalidate("wy_items"); //TODO improve this, drop cache to reload all bus descriptors inside jars
        cacheService.invalidate("ui_components_all"); //TODO improve this, drop cache to reload all ui components inside jars
        cacheService.invalidate("ui_components_by_code"); //TODO improve this, drop cache to reload all ui components inside jars
        cacheService.invalidate("ui_components_by_id"); //TODO improve this, drop cache to reload all ui components inside jars
    }

    @Override
    public List<SystemPlugin> getSystemPlugins() {
        return unmodifiableList(list);
    }

    @Override
    public SystemPlugin getSystemPlugin(String pluginName) {
        return getSystemPlugins().stream().filter(equal(SystemPlugin::getName, pluginName)).collect(onlyElement("plugin with name =< %s > not found", pluginName));
    }

    @Override
    public void deploySystemPlugins(Collection<DataSource> dataFiles) {
        checkArgument(directoryService.hasWebappLibDirectory(), "unable to deploy system plugins: webapp lib directory not available");
        Collection<File> files = createTemporaryFiles(dataFiles);
        Map<String, SystemPlugin> plugins = map(files, File::getName, SystemPluginUtils::scanFileForPlugin).filterValues(notNull()).mapValues(p -> addHealthCheck(p, files));
        checkArgument(!plugins.isEmpty(), "no plugin found for deploy");
        files.forEach(unsafeConsumer(f -> {
            logger.info(marker(), "deploy plugin file =< {} >", f.getName());
            SystemPlugin oldPlugin = list(list).filter(equal(SystemPlugin::getName, plugins.get(f.getName()).getName())).collect(onlyElementOrNull());
            // add plugin to be deleted (old version); skip if old plugin has the same name of the newer
            if (oldPlugin != null && !Objects.equals(oldPlugin.getFilename(), f.getName())) {
                logger.info(marker(), "upgrading plugin =< {} > with =< {} >", oldPlugin.getFilename(), f.getName());
                toBeRemovedlist.add(oldPlugin);
            }
            if (hasConfigPluginDirectory(directoryService.getWebappContextFile())) {
                copy(f, new File(getPluginFolderFromContext(directoryService.getWebappContextFile()), f.getName()));
            } else {
                copy(f, new File(directoryService.getWebappLibDirectory(), f.getName()));
            }
        }));
        systemEventBus.post(SystemReadyRestartRequiredEvent.INSTANCE);
    }

    @Override
    public void removeSystemPlugins() {
        List<String> pluginsFilename = list(toBeRemovedlist).map(SystemPlugin::getFilename);
        if (!pluginsFilename.isEmpty()) {
            removePluginFilesFromWarFileInplace(directoryService.getWebappDirectory(), pluginsFilename);
            if (hasConfigPluginDirectory(directoryService.getWebappContextFile())) {
                removePluginFilesFromConfigFolder(getPluginFolderFromContext(directoryService.getWebappContextFile()), pluginsFilename);
            }
        }
    }

    private void loadPlugin(SystemPlugin plugin) {
        checkArgument(!list.stream().anyMatch(equal(SystemPlugin::getName, plugin.getName())), "duplicate plugin found for name =< %s >", plugin);
        list.add(addHealthCheck(plugin));
    }

    private SystemPlugin addHealthCheck(SystemPlugin plugin) {
        return addHealthCheck(plugin, emptyList());
    }

    private SystemPlugin addHealthCheck(SystemPlugin plugin, Collection<File> extraLibs) {
        List<FaultEvent> healthCheck = list();
        if (pluginConfiguration.isVersionCheckEnabled()) {
            if (checkRangeVersion(plugin.getRequiredCoreVersion(), buildInfoService.getBuildInfo().getVersionNumber())) {
                healthCheck.add(FaultEventImpl.warning("plugin version mismatch: required core version =< %s > actual core version =< %s >", versionRangeToString(plugin.getRequiredCoreVersion()), buildInfoService.getBuildInfo().getVersionNumber()));
            }
        }
        if (directoryService.hasWebappLibDirectory()) {
            plugin.getRequiredLibFiles().forEach(l -> {
                List<File> pluginsLoaded = list(directoryService.getWebappLibDirectory().listFiles());
                if (hasConfigPluginDirectory(directoryService.getWebappContextFile())) {
                    pluginsLoaded = list(pluginsLoaded).with(getPluginFolderFromContext(directoryService.getWebappContextFile()).listFiles());
                }
                String requiredLib = list(pluginsLoaded).with(extraLibs)
                        .map(File::getName)
                        .filter(rl -> Objects.equals(getNameAndVersionFromFilename(rl).getLeft(), getNameAndVersionFromFilename(l).getLeft()))
                        .collect(onlyElementOrNull());
                if (requiredLib != null && Objects.equals(getNameAndVersionFromFilename(requiredLib).getRight(), getNameAndVersionFromFilename(l).getRight())) {
                    healthCheck.add(FaultEventImpl.error("missing required lib =< %s >", l));
                }
            });
        } else {
            logger.warn("unable to check plugin dependencies, webapp lib directory not available");
        }
        healthCheck.forEach(f -> logger.warn(marker(), "found issue for plugin = {} : {}", plugin.getNameVersion(), f.getMessageAndLevel()));
        return SystemPluginImpl.copyOf(plugin).withHealthCheck(healthCheck).build();
    }

    private Collection<File> createTemporaryFiles(Collection<DataSource> dataFiles) {
        File tempDir = tempDir();
        dataFiles.forEach(safe(a -> writeToFile(a, new File(tempDir, a.getName()))));
        return list(tempDir.listFiles());
    }
}
