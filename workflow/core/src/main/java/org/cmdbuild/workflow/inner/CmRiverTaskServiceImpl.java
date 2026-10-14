/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.Map;
import org.cmdbuild.utils.script.groovy.GroovyScriptService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.river.engine.RiverLiveTask;
import org.cmdbuild.workflow.river.engine.task.RiverTaskServiceImpl;
import org.cmdbuild.workflow.river.engine.task.scriptexecutors.TaskClassLoaderSupplier;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.api.CmApiServiceExt;

@Component
public class CmRiverTaskServiceImpl extends RiverTaskServiceImpl {

    private final CmApiServiceExt apiService;

    public CmRiverTaskServiceImpl(CmApiServiceExt apiService, TaskClassLoaderSupplier taskClassLoaderSupplier, GroovyScriptService groovyScriptService) {
        super(taskClassLoaderSupplier, groovyScriptService);
        this.apiService = checkNotNull(apiService);
    }

    @Override
    protected Map<String, Object> getExtraDataAndApiForTaskExecutor(RiverLiveTask liveTask) {
        return map(apiService.getCmApiAsDataMap()).with(
                "logger", LoggerFactory.getLogger(format("org.cmdbuild.workflow.custom.%s", liveTask.getFlow().getPlan().getName()))
        );
    }

}
