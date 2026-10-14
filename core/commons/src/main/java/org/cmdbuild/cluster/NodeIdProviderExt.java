/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import static java.lang.String.format;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.io.CmNetUtils.getHostname;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public interface NodeIdProviderExt extends NodeIdProvider {

    @Nullable
    String getConfiguredNodeId();

    String getRuntimeInfo();

    @Override
    default String getNodeId() {
        return firstNotBlank(getConfiguredNodeId(), getNodeInfo());
    }

    @Override
    default String getNodeInfo() {
        String info = format("%s/%s", getHostname(), getRuntimeInfo());
        if (isNotBlank(getConfiguredNodeId())) {
            info = format("%s/%s", getConfiguredNodeId(), info);
        }
        return info;
    }

}
