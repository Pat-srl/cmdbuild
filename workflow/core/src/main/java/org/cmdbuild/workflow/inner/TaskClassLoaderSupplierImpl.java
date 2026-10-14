/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.workflow.river.engine.RiverLiveTask;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.TaskClassLoaderSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskClassLoaderSupplierImpl implements TaskClassLoaderSupplier {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;

    public TaskClassLoaderSupplierImpl(DirectoryService directoryService) {
        this.directoryService = checkNotNull(directoryService);
    }

    @Override
    @Nullable
    public ClassLoader getCustomClassLoader(RiverLiveTask task) {
        if (directoryService.hasWebappDirectory()) {
//            List<File> customLibs = list(checkNotNull(directoryService.getLibByPattern("cmdbuild-client-soap-.*.jar_disabled_cli_only")));//TODO improve this, make configurable, etc
//            logger.info("load custom classloader for task = {} with extra libs = {}", task, customLibs);
//            return buildClassloaderWithJarOverride(customLibs, getClass().getClassLoader());//TODO cache classloader instance
            return null; //TODO
        } else {
            logger.warn("webapp dir is not available, unable to load custom task class loader");
            return null;
        }
    }

}
