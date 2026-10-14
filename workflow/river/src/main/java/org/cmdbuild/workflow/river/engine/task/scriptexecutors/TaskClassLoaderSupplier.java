/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task.scriptexecutors;

import jakarta.annotation.Nullable;
import org.cmdbuild.workflow.river.engine.RiverLiveTask;

public interface TaskClassLoaderSupplier {

    @Nullable
    ClassLoader getCustomClassLoader(RiverLiveTask task);

}
