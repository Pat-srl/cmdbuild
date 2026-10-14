/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import jakarta.annotation.Nullable;
import java.util.List;

public interface ClusterConfiguration extends NodeIdProvider {

    final static String CLUSTER_CONFIG_NAMESPACE = "org.cmdbuild.cluster",
            CLUSTER_CONFIG_NAMESPACE_PREFIX = CLUSTER_CONFIG_NAMESPACE + ".",
            CLUSTER_CONFIG_ENABLED = "enabled",
            CLUSTER_CONFIG_MODE = "mode",
            CLUSTER_CONFIG_CLOUD_IPFINDER = "cloud.ipfinder",
            CLUSTER_CONFIG_AUTO_RESTART = "auto.restart",
            CLUSTER_CONFIG_FAILURE_ACTION = "failure.action",
            CLUSTER_CONFIG_FAILURE_TIMEOUT = "failure.timeout",
            CLUSTER_CONFIG_NODE_PORT = "node.tcp.port",
            CLUSTER_CONFIG_NODE_ADDR = "node.tcp.addr",
            CLUSTER_CONFIG_NODES = "nodes";

    boolean isClusterEnabled();

    String getClusterName();

    IgniteMode getClusterMode();

    IgniteCloudIpFinder getClusterCloudIpFinder();

    boolean autoRestartEnabled();

    IgniteRestartMode failureAction();

    int getTcpPort();

    long getFailureTimeout();

    @Nullable
    String getTcpAddr();

    List<String> getClusterNodes();

    long getRpcTimeout();

    @Nullable
    String getWorkDirectory();

    List<String> getWorkgroups();

    String getCloudNamespace();

    String getCloudServiceName();

    default boolean isSingleNode() {
        return !isClusterEnabled() || getClusterNodes().size() == 1;
    }

    enum IgniteRestartMode {
        IRM_NONE, IRM_RESTARTIGNITE, IRM_STOPTOMCAT, IRM_STOPIGNITE
    }

    enum IgniteMode {
        IM_ONPREMISE, IM_CLOUD
    }

    enum IgniteCloudIpFinder {
        IM_MULTICAST, IM_KUBERNETES
    }
}
