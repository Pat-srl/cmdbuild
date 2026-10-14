/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.cluster;

import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import static org.apache.ignite.events.EventType.EVTS_DISCOVERY;
import org.apache.ignite.failure.NoOpFailureHandler;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.spi.failover.never.NeverFailoverSpi;
import static org.cmdbuild.cluster.ClusterConfiguration.IgniteCloudIpFinder.IM_KUBERNETES;
import static org.cmdbuild.cluster.ClusterNode.NODE_ID_ATTR;
import static org.cmdbuild.cluster.ClusterNode.NODE_WORKGROUPS_ATTR;
import org.cmdbuild.config.api.DirectoryService;
import static org.cmdbuild.utils.io.CmIoUtils.cmTmpDir;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

/**
 *
 * @author ataboga
 */
public class IgniteUtils {

    protected static Map<String, String> getLocalNodeAttrs(ClusterConfiguration config) {
        return map(NODE_ID_ATTR, config.getNodeId(), NODE_WORKGROUPS_ATTR, Joiner.on(",").join(config.getWorkgroups()));
    }

    protected static IgniteConfiguration setOnPremiseConfiguration(ClusterConfiguration config, DirectoryService directoryService) {
        IgniteConfiguration igniteConfiguration = setDefaultConfiguration(config, directoryService);

        // Ignite discovery spi configuration
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setShared(true);
        ipFinder.setAddresses(config.getClusterNodes());
        tcpDiscoverySpi.setIpFinder(ipFinder);
        tcpDiscoverySpi.setLocalPort(config.getTcpPort());
        if (isNotBlank(config.getTcpAddr())) {
            tcpDiscoverySpi.setLocalAddress(config.getTcpAddr());
        }
        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);

        // Ignite communication spi configuration
        TcpCommunicationSpi tcpCommunicationSpi = new TcpCommunicationSpi();
        if (isNotBlank(config.getTcpAddr())) {
            tcpCommunicationSpi.setLocalAddress(config.getTcpAddr());
        }
        igniteConfiguration.setCommunicationSpi(tcpCommunicationSpi);
        return igniteConfiguration;
    }

    protected static IgniteConfiguration setCloudConfiguration(ClusterConfiguration config, DirectoryService directoryService) {
        IgniteConfiguration igniteConfiguration = setDefaultConfiguration(config, directoryService);
        if (equal(IM_KUBERNETES, config.getClusterCloudIpFinder())) {
            checkArgument(new File("/var/run/secrets/kubernetes.io/serviceaccount/token").exists(), "kubernetes services account token does not exist (/var/run/secrets/kubernetes.io/serviceaccount/token)");

            // Ignite discovery spi configuration
            TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
            KubernetesConnectionConfiguration k8sConfiguration = new KubernetesConnectionConfiguration();
            k8sConfiguration.setNamespace(config.getCloudNamespace());
            k8sConfiguration.setServiceName(config.getCloudServiceName());
            TcpDiscoveryKubernetesIpFinder k8sIpFinder = new TcpDiscoveryKubernetesIpFinder(k8sConfiguration);
            tcpDiscoverySpi.setIpFinder(k8sIpFinder);
            igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
        }
        igniteConfiguration.setClientMode(true);
        return igniteConfiguration;
    }

    private static IgniteConfiguration setDefaultConfiguration(ClusterConfiguration config, DirectoryService directoryService) {
        System.setProperty(org.apache.ignite.IgniteSystemProperties.IGNITE_QUIET, "false"); // avoid duplicate log -_-
        System.setProperty(org.apache.ignite.IgniteSystemProperties.IGNITE_PERFORMANCE_SUGGESTIONS_DISABLED, "true");
        System.setProperty(org.apache.ignite.IgniteSystemProperties.IGNITE_UPDATE_NOTIFIER, "false");
        System.setProperty(org.apache.ignite.IgniteSystemProperties.IGNITE_ENABLE_OBJECT_INPUT_FILTER_AUTOCONFIGURATION, "false");
        // Ignite configurations
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setGridLogger(new Slf4jLogger());
        igniteConfiguration.setIgniteInstanceName(config.getClusterName());

        // Ignite event configuration
        igniteConfiguration.setIncludeEventTypes(EVTS_DISCOVERY);
        igniteConfiguration.setUserAttributes(getLocalNodeAttrs(config));//TODO reload if config changes (??)
        igniteConfiguration.setMetricsLogFrequency(0);
        igniteConfiguration.setWorkDirectory(Optional.ofNullable(trimToNull(config.getWorkDirectory())).orElseGet(() -> {
            if (directoryService.hasContainerDirectory() && directoryService.hasWebappDirectory()) {
                return new File(new File(directoryService.getContainerDirectory(), "ignite"), directoryService.getContextName()).getAbsolutePath();
            } else {
                return new File(new File(cmTmpDir(), "ignite"), directoryService.getContextName()).getAbsolutePath();
            }
        }));
        CacheConfiguration cacheCfg = new CacheConfiguration("ignite");
        cacheCfg.setCacheMode(CacheMode.REPLICATED);
        igniteConfiguration.setCacheConfiguration(cacheCfg);
        igniteConfiguration.setClientConnectorConfiguration(null);//disable jdbc port
        if (isNotNullAndGtZero(config.getFailureTimeout())) {
            igniteConfiguration.setFailureDetectionTimeout(config.getFailureTimeout());
        } else {
            NeverFailoverSpi failSpi = new NeverFailoverSpi();
            igniteConfiguration.setFailoverSpi(failSpi);
            igniteConfiguration.setFailureHandler(new NoOpFailureHandler());
        }
        return igniteConfiguration;
    }
}
