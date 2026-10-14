/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.customclassloader;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import java.io.File;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.customclassloader.ClassloaderFromComponentsService.ClassLoaderHelper;
import org.cmdbuild.easyupload.EasyuploadItemInfo;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.utils.classpath.ClasspathUtils;
import static org.cmdbuild.utils.classpath.ClasspathUtils.buildClassloaderWithJarOverride;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomClassloaderServiceImpl implements CustomClassloaderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;
    private final EasyuploadService easyuploadService;
    private final ClassloaderFromComponentsService classloaderFromComponentsService;

    private final CmCache<ClassLoader> classLoadersByName;

    public CustomClassloaderServiceImpl(DirectoryService directoryService, EasyuploadService easyuploadService, CacheService cacheService, ClassloaderFromComponentsService classloaderFromComponentsService, EventBusService busService) {
        this.directoryService = checkNotNull(directoryService);
        this.easyuploadService = checkNotNull(easyuploadService);
        this.classloaderFromComponentsService = checkNotNull(classloaderFromComponentsService);

        this.classLoadersByName = cacheService.newCache("custom_class_loaders_by_name");

        busService.getDaoEventBus().register(new Object() { //TODO also invalidate on upload, and lib change (?)

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        classLoadersByName.invalidateAll();
    }

    @Override
    public ClassLoader getCustomClassLoader(String path) {
        logger.debug("get custom classloader for path =< {} >", path);
        return classLoadersByName.get(path, () -> doGetCustomClassLoader(path));
    }

    @Override
    public <T> T doWithCustomClassLoader(String path, Callable<T> job) {
        return ClasspathUtils.doWithCustomClassLoader(getCustomClassLoader(path), job);
    }

    private ClassLoader doGetCustomClassLoader(String pathConfig) {
        checkNotBlank(pathConfig);
        logger.debug("load custom classloader for path config=< {} >", pathConfig);

        List<String> elements = list(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(pathConfig));
        List<ClassLoaderHelper> classLoaders = list();
        list(elements).forEach(e -> {
            ClassLoaderHelper classLoader = classloaderFromComponentsService.getClassLoaderFromComponentOrNull(e);
            if (classLoader != null) {
                elements.remove(e);
                classLoaders.add(classLoader);
            }
        });
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (!elements.isEmpty()) {
            classLoader = getCustomClassLoaderFromFiles(elements, classLoader);
        }
        for (ClassLoaderHelper helper : list(classLoaders).reverse()) {
            classLoader = helper.getClassLoader(classLoader);
        }
        return classLoader;
    }

    private ClassLoader getCustomClassLoaderFromFiles(List<String> elements, ClassLoader parent) {
        return buildClassloaderWithJarOverride(elements.stream().flatMap(path -> {

            logger.debug("lookup classloader for path =< {} > as absolute path", path);
            if (new File(path).exists()) {
                return loadFiles(new File(path)).stream();
            }

            File fileFromConfigDir = new File(new File(directoryService.getConfigDirectory(), "lib"), path);
            logger.debug("lookup classloader for path =< {} > with config dir (file = {} )", path, fileFromConfigDir);
            if (directoryService.hasConfigDirectory() && fileFromConfigDir.exists()) {
                return loadFiles(fileFromConfigDir).stream();
            }

            logger.debug("lookup classloader for path =< {} > within easyupload files", path);
            //TODO invalidate cache on file upload if source is easyupload

            List<EasyuploadItemInfo> files = easyuploadService.getByDir(path);
            if (!files.isEmpty()) {
                return files.stream().map(this::toTempFile);
            }

            if (easyuploadService.hasElementByPath(path)) {
                return singletonList(toTempFile(easyuploadService.getByPath(path))).stream();
            }

            throw new IllegalArgumentException(format("custom class loader element not found for path =< %s >", path));

        }).distinct().collect(toImmutableList()), parent);
    }

    private Collection<File> loadFiles(File file) {
        if (file.isFile() && Objects.equals(Files.getFileExtension(file.getName()), "jar")) {
            return singletonList(file);
        } else if (file.isDirectory()) {
            return FileUtils.listFiles(file, new String[]{"jar"}, true);
        } else {
            throw new IllegalArgumentException(format("invalid class loader file =< %s >", file));
        }
    }

    private File toTempFile(EasyuploadItemInfo item) {
        logger.debug("load classloader from easyupload item = {}", item);
        checkArgument(Objects.equals(Files.getFileExtension(item.getFileName()), "jar"), "invalid file = %s", item);
        File tempDir = tempDir();
        File file = new File(tempDir, item.getFileName());
        writeToFile(easyuploadService.getById(item.getId()).getContent(), file);
        return file;
    }

}
