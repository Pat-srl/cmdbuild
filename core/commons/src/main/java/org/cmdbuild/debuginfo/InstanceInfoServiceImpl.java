/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.cluster.NodeIdProvider;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.springframework.stereotype.Component;

@Component
public class InstanceInfoServiceImpl implements InstanceInfoService {

    private final BuildInfoService versionService;
    private final NodeIdProvider nodeIdProvider;
    private final CoreConfiguration coreConfiguration;

    public InstanceInfoServiceImpl(BuildInfoService versionService, NodeIdProvider nodeIdProvider, CoreConfiguration coreConfiguration) {
        this.versionService = checkNotNull(versionService);
        this.nodeIdProvider = checkNotNull(nodeIdProvider);
        this.coreConfiguration = checkNotNull(coreConfiguration);
    }

    @Override
    public String getNodeId() {
        return nodeIdProvider.getNodeId();
    }

    @Override
    public String getInstanceName() {
        return firstNotBlank(coreConfiguration.getInstanceName(), "cmdbuild");
    }

    @Override
    public String getVersion() {
        return versionService.getVersionNumber();
    }

    @Override
    public String getRevision() {
        return versionService.getCommitInfo();
    }

}
