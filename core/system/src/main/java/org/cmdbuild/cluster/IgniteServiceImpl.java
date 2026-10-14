package org.cmdbuild.cluster;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Provider;
import java.io.File;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.DiscoveryEvent;
import static org.apache.ignite.events.EventType.EVTS_DISCOVERY;
import org.apache.ignite.lang.IgnitePredicate;
import static org.cmdbuild.cluster.ClusterConfiguration.IgniteMode.IM_CLOUD;
import static org.cmdbuild.cluster.ClusterMessage.THIS_INSTANCE_ID;
import static org.cmdbuild.cluster.ClusterNode.NODE_ID_ATTR;
import static org.cmdbuild.cluster.IgniteUtils.getLocalNodeAttrs;
import static org.cmdbuild.cluster.IgniteUtils.setCloudConfiguration;
import static org.cmdbuild.cluster.IgniteUtils.setOnPremiseConfiguration;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerExt;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import static org.cmdbuild.minions.SystemStatus.SYST_STARTING_CLUSTER;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.cmdbuild.utils.hash.CmHashUtils.toIntHash;
import static org.cmdbuild.utils.io.CmIoUtils.cmTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class IgniteServiceImpl implements IgniteService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String MESSAGE_TOPIC = "main",
            MSG_TIMESTAMP = "timestamp",
            MSG_SOURCE = "source",
            MSG_TYPE = "type",
            MSG_TYPE_RPC_REQUEST = "cm_rpc_request",
            MSG_TYPE_RPC_RESPONSE = "cm_rpc_response",
            MSG_RPC_SESSIONID = "sessionId",
            MSG_RPC_REQUESTID = "requestId",
            MSG_RPC_PAYLOAD = "payload",
            MSG_ID = "id",
            MSG_DATA = "data";

    private final ClusterConfiguration config;
    private final EventBus eventBus;
    private final Provider<RpcHelper> rpcHelper;
    private final DirectoryService directoryService;
    private final ApplicationContext applicationContext;

    private final MinionHandlerExt minionHandler;
    private final ExecutorService executorService;

    private Ignite ignite;
    private IgnitePredicate<DiscoveryEvent> discoveryEvent;
    private List<ClusterNodeExt> clusterNodes = emptyList();
    private boolean isFailed = false;

    private final Map<String, Consumer<ClusterMessage>> rpcResponseListeners = new ConcurrentHashMap<>();

    public IgniteServiceImpl(ClusterConfiguration config, EventBusService eventBusService, Provider<RpcHelper> rpcHelper, RequestContextService contextService, ApplicationContext applicationContext, DirectoryService directoryService) {
        this.config = checkNotNull(config);
        this.rpcHelper = checkNotNull(rpcHelper);
        this.directoryService = checkNotNull(directoryService);
        this.applicationContext = checkNotNull(applicationContext);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Clustering")
                .withEnabledChecker(config::isClusterEnabled)
                .withRequires(SYST_STARTING_CLUSTER)
                .withRestartable(false)
                .reloadOnConfigs(ClusterConfiguration.class)
                .build();
        eventBus = eventBusService.getClusterMessagesEventBus();
        executorService = executorService(getClass().getName(), () -> contextService.initCurrentRequestContext("cluster message processing job"), contextService::destroyCurrentRequestContext);
        eventBusService.getSystemEventBus().register(new Object() {
            @Subscribe
            public void handleDisconnectedNodeDetectedEvent(DisconnectedNodeDetectedEvent event) {
                switch (event) {
                    case DNDE_RESTART -> {
                        if (isEnabled()) {
                            restart();
                        }
                    }
                    case DNDE_FAILURE -> {
                        if (isEnabled()) {
                            switch (config.failureAction()) {
                                case IRM_NONE -> {
                                    logger.warn("failure detected: skipping any action");
                                }
                                case IRM_RESTARTIGNITE -> {
                                    logger.warn("failure detected: restarting ignite");
                                    restart();
                                }
                                case IRM_STOPTOMCAT -> {
                                    logger.warn("failure detected: stopping tomcat");
                                    stopTomcat();
                                }
                                case IRM_STOPIGNITE -> {
                                    logger.warn("failure detected: stopping reconnection to cluster");
                                    isFailed = true;
                                }
                                default ->
                                    throw illegalArgument("unsupported failure action =< %s >", config.failureAction());
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void start() {
        startUnsafe();
    }

    @Override
    public synchronized void stop() {
        if (ignite != null) {
            logger.info("stop ignite cluster");
            try {
                ignite.events().stopLocalListen(discoveryEvent);
                ignite.message().stopLocalListen(MESSAGE_TOPIC, (nodeId, msg) -> {
                    logger.warn("stop message listener");
                    return false;
                });
                ignite.close();
            } catch (Exception ex) {
                logger.warn(marker(), "cluster service not stopped", ex);
            }
            ignite = null;
            clusterNodes = emptyList();
            logger.info("ignite cluster stopped");
        } else {
            logger.debug("ignite cluster not running, ignore stop request");
        }
        minionHandler.setStatus(MRS_NOTRUNNING);
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executorService);
    }

    @Override
    public boolean isEnabled() {
        return config.isClusterEnabled();
    }

    @Override
    public boolean isFailed() {
        return isFailed;
    }

    @Override
    public boolean isRunning() {
        if (!config.isClusterEnabled()) {
            return false;
        } else {
            try {
                return ignite != null && !clusterNodes.isEmpty() && Objects.equals(ignite.cluster().state(), ClusterState.ACTIVE);
            } catch (Exception ex) {
//                minionHandler.setStatus(MRS_ERROR); //TODO check this
                logger.warn(marker(), "cluster service not running", ex);
                return false;
            }
        }
    }

    @Override
    public Ignite getIgnite() {
        checkArgument(isRunning(), "ignite service is not running");
        return checkNotNull(ignite);
    }

    @Override
    public String getNodeId() {
        return config.getNodeId();
    }

    @Override
    public String getNodeInfo() {
        return config.getNodeInfo();
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public ClusterNode getThisClusterNode() {
        if (isRunning()) {
            return getClusterNodes().stream().filter(ClusterNode::isThisNode).collect(onlyElement());
        } else {
            return new ClusterNodeImpl(config.getNodeId(), "127.0.0.1", true, getLocalNodeAttrs(config));
        }
    }

    @Override
    public List<ClusterNode> getClusterNodes() {
        if (isRunning()) {
            return checkNotEmpty(clusterNodes.stream().filter(not(ClusterNodeExt::isIgniteServer)).collect(toList()));
        } else {
            return singletonList(getThisClusterNode());
        }
    }

    @Override
    public void sendMessage(ClusterMessage clusterMessage) {
        if (!isRunning()) {
            logger.debug("cluster not enabled, skip outgoing message = {}", clusterMessage);
        } else if (!clusterMessage.hasTarget() && isSingleActiveNode()) {
            logger.debug("only this node on cluster, skip outgoing message = {}", clusterMessage);
        } else {
            try {
                doSendMessage(clusterMessage);
            } catch (Exception ex) {
                logger.error(marker(), "error sending cluster message", ex);
            }
        }
    }

    @Override
    public String invokeRpcMethod(String nodeId, @Nullable String sessionId, String payload) {
        checkArgument(isRunning());
        ClusterMessage request = ClusterMessageImpl.builder().withTargetNodeId(nodeId).withMessageType(MSG_TYPE_RPC_REQUEST).withMessageData(map(
                MSG_RPC_SESSIONID, sessionId,
                MSG_RPC_PAYLOAD, checkNotBlank(payload)
        )).build();
        CompletableFuture<ClusterMessage> future = new CompletableFuture<>();
        rpcResponseListeners.put(request.getMessageId(), (response) -> {
            future.complete(response);
        });
        try {
            doSendMessage(request);
            ClusterMessage response = future.get(config.getRpcTimeout(), TimeUnit.MILLISECONDS);
            return response.getValue(MSG_RPC_PAYLOAD);
        } catch (Exception ex) {
            throw runtime(ex, "error invoking rpc method =< %s >", abbreviate(payload));
        } finally {
            rpcResponseListeners.remove(request.getMessageId());
        }
    }

    @Override
    public boolean isActiveNodeForKey(@Nullable String key, @Nullable String workgroup) {
        ClusterNode node = selectSingleNodeForKeyOrNull(key, workgroup);
        if (node == null) {
            logger.warn(marker(), "no available node found for key =< {} > workgroup =< {} >", key, workgroup);
        }
        return node != null && node.isThisNode();
    }

    @Override
    @Nullable
    public ClusterNode selectSingleNodeForKeyOrNull(@Nullable String key, @Nullable String workgroup) {//TODO improve this, move to utils (?)
        key = nullToEmpty(key);
        List<ClusterNode> candidates = Ordering.natural().onResultOf(ClusterNode::getNodeId).sortedCopy(getClusterNodes());
        if (isNotBlank(workgroup)) {
            candidates.removeIf(c -> !c.hasWorkgroup(workgroup));
        }
        if (candidates.isEmpty()) {
            return null;
        } else {
            int i = toIntHash(key) % candidates.size();
            ClusterNode node = candidates.get(i);
            logger.debug("select node for key =< {} > workgroup =< {} >, selected node {} = {}", key, workgroup, i, node);
            return node;
        }
    }

    private synchronized void restart() {
        if (config.autoRestartEnabled()) {
            logger.info("restart ignite cluster");
            stop();
            start();
        } else {
            logger.warn("node disconnected from cluster due to network or hardware issue");
        }
    }

    private synchronized void stopTomcat() {
        String tomcatDir = directoryService.getContainerDirectory().getAbsolutePath();
        String scriptContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/platform/scripts/cmdbuild_platform_helper.sh"));
        File file = new File(cmTmpDir(), format("%s_script.sh", randomId()));
        file.getParentFile().mkdirs();
        writeToFile(scriptContent, file);
        executeProcess(list("/bin/bash", "-l", file.getAbsolutePath(), tomcatDir, "stop"));
    }

    private void handleRpcRequest(ClusterMessage request) {
        String sessionId = request.getValue(MSG_RPC_SESSIONID), payload = checkNotBlank(request.getValue(MSG_RPC_PAYLOAD));
        logger.debug("received rpc request =< {} >", request);
        logger.trace("rpc request payload =< {} >", payload);
        String responsePayload = rpcHelper.get().invokeRpcMethod(sessionId, payload);
        logger.trace("rpc response payload =< {} >", responsePayload);
        doSendMessage(ClusterMessageImpl.builder().withTargetNodeId(request.getSourceInstanceId()).withMessageType(MSG_TYPE_RPC_RESPONSE).withMessageData(map(
                MSG_RPC_REQUESTID, request.getMessageId(),
                MSG_RPC_PAYLOAD, responsePayload
        )).build());
    }

    private void handleRpcResponse(ClusterMessage response) {
        try {
            String requestId = checkNotBlank(response.getValue(MSG_RPC_REQUESTID));
            checkNotNull(rpcResponseListeners.get(requestId), "invalid request id for rpc response, id =< %s >", requestId).accept(response);
        } catch (Exception ex) {
            throw runtime(ex, "error processing rpc response message = %s", response);
        }
    }

    private void doSendMessage(ClusterMessage message) {
        try {
            logger.trace("send cluster message = {}", message);
            checkArgument(Objects.equals(message.getSourceInstanceId(), THIS_INSTANCE_ID));
            checkArgument(!Objects.equals(message.getTargetNodeId(), getNodeId()));

            String payload = toJson(map(
                    MSG_SOURCE, config.getNodeId(),
                    MSG_TIMESTAMP, toIsoDateTimeLocal(now()),
                    MSG_ID, message.getMessageId(),
                    MSG_TYPE, message.getMessageType(),
                    MSG_DATA, map(message.getMessageData())));

            logger.trace("send cluster message payload =< {} >", payload);

            (message.hasTarget() ? ignite.message(ignite.cluster().forNodeId(getNodeForNodeId(message.getTargetNodeId()).getIgniteNodeId())) : ignite.message(ignite.cluster().forRemotes()))
                    .send(MESSAGE_TOPIC, payload);
        } catch (Exception ex) {
            throw runtime(ex, "error sending cluster message = %s", message);
        }
    }

    private void handleReceivedMessage(UUID nodeId, Object msg) {
        try {
            Map<String, Object> map = fromJson(toStringNotBlank(msg), MAP_OF_OBJECTS);
            logger.debug("message data = \n\n{}\n", mapToLoggableString(map));
            String source = toStringNotBlank(map.get(MSG_SOURCE)),
                    messageId = toStringNotBlank(map.get(MSG_ID)),
                    messageType = toStringNotBlank(map.get(MSG_TYPE));
            ZonedDateTime timestamp = toDateTime(toStringNotBlank(map.get(MSG_TIMESTAMP)));
            Map<String, Object> data = (Map<String, Object>) map.get(MSG_DATA);

            ClusterMessage clusterMessage = ClusterMessageImpl.builder()
                    .withTimestamp(timestamp)
                    .withSourceInstanceId(source)
                    .withMessageType(messageType)
                    .withMessageId(messageId)
                    .withMessageData(data)
                    .build();

            logger.trace("received cluster message = {}", clusterMessage);

            ClusterNodeExt node = getNodeForIgniteNodeId(nodeId);
            checkArgument(Objects.equals(node.getNodeId(), source) && node.isOtherNode(), "invalid source node or source node id mismatch");
            checkArgument(ignite != null && Objects.equals(ignite.cluster().state(), ClusterState.ACTIVE), "ignite not active");

            switch (clusterMessage.getMessageType()) {
                case MSG_TYPE_RPC_REQUEST ->
                    handleRpcRequest(clusterMessage);
                case MSG_TYPE_RPC_RESPONSE ->
                    handleRpcResponse(clusterMessage);
                default ->
                    eventBus.post(new ClusterMessageReceivedEventImpl(clusterMessage));

            }
        } catch (Exception ex) {
            logger.error(marker(), "error processing incoming message from node =< {} > with payload =< {} >", nodeId, msg, ex);
        }
    }

    private synchronized void startUnsafe() {
        logger.info("start ignite cluster, node =< {} >", config.getNodeId());
        try {
            // init check
            List<String> clusterNodesConfig = config.getClusterNodes();
            logger.info("cluster nodes = {}", Joiner.on(", ").join(clusterNodesConfig));
            checkArgument(Objects.equals(config.getClusterMode(), IM_CLOUD) || !clusterNodesConfig.isEmpty(), "cannot start cluster, missing cluster nodes config");

            // Ignite configurations
            IgniteConfiguration igniteConfiguration = switch (config.getClusterMode()) {
                case IM_ONPREMISE ->
                    setOnPremiseConfiguration(config, directoryService);
                case IM_CLOUD ->
                    setCloudConfiguration(config, directoryService);
            };

            // Ignite start
            isFailed = false;
            ignite = IgniteSpring.start(igniteConfiguration, applicationContext);
            discoveryEvent = evt -> {
                logger.debug("ignite discovery event = {}", evt);
                reloadClusterView();
                return true;
            };
            ignite.events().localListen(discoveryEvent, EVTS_DISCOVERY);

            ignite.message().localListen(MESSAGE_TOPIC, (nodeId, msg) -> {
                logger.trace("received message =< {} > from node =< {} >", msg, nodeId);
                executorService.submit(() -> handleReceivedMessage(nodeId, msg));
                return true;
            });
            reloadClusterView();
            minionHandler.setStatus(MRS_READY);

            logger.info("ignite cluster started");
        } catch (Exception ex) {
            logger.warn("ignite startup failed, execute cleanup");
            stop();
            minionHandler.setStatus(MRS_ERROR);
            throw runtime(ex, "ignite startup failed");
        }
    }

    private synchronized void reloadClusterView() {
        logger.info("reload cluster view");
        checkArgument(ignite != null && Objects.equals(ignite.cluster().state(), ClusterState.ACTIVE), "ignite not active");
        clusterNodes = ImmutableList.copyOf(list(ignite.cluster().nodes()).map(n -> {
            logger.info("found node = {}", n);
            logger.debug("found node attrs =\n\n{}\n", mapToLoggableString(n.attributes()));
            if (Objects.equals(config.getClusterMode(), IM_CLOUD) && !n.isClient()) {
                return new ClusterNodeExt(toStringNotBlank(n.attribute("HOSTNAME")), n.addresses().stream().collect(joining(",")), n.isLocal(), n.id(), map(n.attributes()).mapValues(CmStringUtils::toStringOrNullSafe), true);
            } else {
                return new ClusterNodeExt(toStringNotBlank(n.attribute(NODE_ID_ATTR)), n.addresses().stream().collect(joining(",")), n.isLocal(), n.id(), map(n.attributes()).mapValues(CmStringUtils::toStringOrNullSafe));
            }
        }));
    }

    private ClusterNodeExt getNodeForIgniteNodeId(UUID igniteNodeId) {
        checkNotNull(igniteNodeId);
        return clusterNodes.stream().filter(equal(ClusterNodeExt::getIgniteNodeId, igniteNodeId)).collect(onlyElement("cluster node not found for ignite node id =< %s >", igniteNodeId));
    }

    private ClusterNodeExt getNodeForNodeId(String nodeId) {
        checkNotBlank(nodeId);
        return clusterNodes.stream().filter(equal(ClusterNodeExt::getNodeId, nodeId)).collect(onlyElement("cluster node not found for id =< %s >", nodeId));
    }

    private static class ClusterNodeExt extends ClusterNodeImpl {

        private final UUID igniteNodeId;
        private final boolean isIgniteServer;

        public ClusterNodeExt(String nodeId, String address, boolean isThisNode, UUID igniteNodeId, Map<String, String> meta, boolean isIgniteServer) {
            super(nodeId, address, isThisNode, meta);
            this.igniteNodeId = checkNotNull(igniteNodeId);
            this.isIgniteServer = isIgniteServer;
        }

        public ClusterNodeExt(String nodeId, String address, boolean isThisNode, UUID igniteNodeId, Map<String, String> meta) {
            this(nodeId, address, isThisNode, igniteNodeId, meta, false);
        }

        public boolean isIgniteServer() {
            return isIgniteServer;
        }

        public UUID getIgniteNodeId() {
            return igniteNodeId;
        }

    }

    private static class ClusterMessageReceivedEventImpl implements ClusterMessageReceivedEvent {

        private final ClusterMessage clusterMessage;

        public ClusterMessageReceivedEventImpl(ClusterMessage clusterMessage) {
            this.clusterMessage = checkNotNull(clusterMessage);
        }

        @Override
        public ClusterMessage getClusterMessage() {
            return clusterMessage;
        }
    }
}
