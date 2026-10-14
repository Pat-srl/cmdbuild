/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import jakarta.annotation.Nullable;
import jakarta.annotation.PreDestroy;
import org.apache.commons.codec.binary.Hex;
import static org.cmdbuild.audit.RequestInfo.NO_SESSION_USER;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.cluster.ClusterConfiguration;
import org.cmdbuild.config.RequestTrackingConfiguration;
import org.cmdbuild.minions.MinionService;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestTrackingServiceImpl implements RequestTrackingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RequestTrackingConfiguration config;
    private final RequestTrackingWritableRepository store;
    private final SessionService sessionService;
    private final ClusterConfiguration clusterConfiguration;
    private final List<PayloadFilter> payloadFilters;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(namedThreadFactory(getClass())); // if we use a single thread executor we can avoid a bunch of synchronization later. Also, this act as a performance control.
    private final Cache<String, OngoingRequestStatus> ongoingRequests = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.DAYS).build();

    private final MinionService systemService;

    public RequestTrackingServiceImpl(List<PayloadFilter> payloadFilters, ClusterConfiguration clusterConfiguration, RequestTrackingConfiguration config, RequestTrackingWritableRepository store, SessionService sessionService, RequestContextService contextService, MinionService bootService) {
        logger.debug("init");
        this.config = checkNotNull(config);
        this.store = checkNotNull(store);
        this.sessionService = checkNotNull(sessionService);
        this.systemService = checkNotNull(bootService);
        this.clusterConfiguration = checkNotNull(clusterConfiguration);
        this.payloadFilters = checkNotEmpty(ImmutableList.copyOf(payloadFilters));
        scheduledExecutorService.submit(() -> {
            contextService.initCurrentRequestContext("request tracking background job");
            //TODO set admin user
        });
    }

    @PreDestroy
    public void cleanup() {
        logger.info("cleanup");
        shutdownQuietly(scheduledExecutorService);
    }

    @Override
    public void dropAllData() {
        store.dropAll();
        //note: this may cause inconsistencies with ongoingRequestsNotYetPersisted and possible pending requests: use with care!
    }

    @Override
    public void requestBegin(RequestData data) {
        RequestData thisData = processRequestData(data);
        logger.debug("request begin = {}", thisData);
        if (isDbPersistEnabled()) {
            scheduledExecutorService.submit(() -> {
                ScheduledFuture persistOngoingRequestJob = scheduledExecutorService.schedule(() -> {
                    try {
                        if (isDbPersistEnabled()) {
                            ongoingRequests.put(thisData.getRequestId(), new OngoingRequestStatus(thisData.getRequestId(), true, null));
                            store.create(filterPayload(thisData));
                        }
                    } catch (Exception ex) {
                        logger.error("error processing request begin, request = {}", thisData);
                        logger.error("error processing request begin", ex);
                    }
                }, 10, TimeUnit.SECONDS);
                ongoingRequests.put(thisData.getRequestId(), new OngoingRequestStatus(thisData.getRequestId(), false, persistOngoingRequestJob));
            });
        } else {
            logger.trace("db persist disabled, skipping...");
        }
    }

    @Override
    public void requestComplete(RequestData data) {
        try {
            RequestData thisData = processRequestData(data);
            logger.debug("request complete = {}", thisData);
            checkArgument(thisData.isCompleted());
            if (isDbPersistEnabled()) {
                scheduledExecutorService.submit(() -> {
                    try {
                        OngoingRequestStatus ongoingRequestStatus = ongoingRequests.getIfPresent(data.getRequestId());
                        if (ongoingRequestStatus == null) {
                            store.create(filterPayload(thisData));
                        } else {
                            if (ongoingRequestStatus.persistOngoingRequestJob != null) {
                                ongoingRequestStatus.persistOngoingRequestJob.cancel(true);
                            }
                            if (ongoingRequestStatus.alreadyPersisted) {
                                store.update(filterPayload(thisData));
                            } else {
                                store.create(filterPayload(thisData));
                            }
                            ongoingRequests.invalidate(data.getRequestId());
                        }
                    } catch (Exception ex) {
                        logger.error("error processing request completion, request = {}", thisData);
                        logger.error("error processing request completion", ex);
                    }
                });
            } else {
                logger.trace("db persist disabled, skipping...");
            }
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    private boolean isDbPersistEnabled() {
        return config.isRequestTrackingEnabled() && systemService.isSystemReady();
    }

    private RequestData processRequestData(RequestData data) {
        String sessionId = data.getSessionId();
        if (data.hasPayload() && !data.hasSession()) {
            Matcher matcher = Pattern.compile("CMDBuild-Authorization[>]([^<]+)[<]/CMDBuild-Authorization").matcher(data.getBestPlaintextPayload());
            if (matcher.find()) {
                sessionId = matcher.group(1);
            }
        }
        String username = NO_SESSION_USER;
        try {
            if (data.hasSession()) {
                Matcher matcherForBasicAuth = Pattern.compile("basic([^g]+)g([^g]+)", CASE_INSENSITIVE).matcher(data.getSessionId());
                if (matcherForBasicAuth.find()) {
                    username = new String(Hex.decodeHex(checkNotBlank(matcherForBasicAuth.group(1))), StandardCharsets.UTF_8);
                    sessionId = format("basic%sg%s", matcherForBasicAuth.group(1), randomId()).substring(0, 24).toLowerCase(); // hide basic auth password from sessionId
                } else {
                    username = sessionService.getSessionById(data.getSessionId()).getOperationUser().getLoginUser().getUsername();
                }
            }
        } catch (Exception ex) {
            logger.debug("unable to retrieve username for session = " + data.getSessionId(), ex);
        }
        return RequestDataImpl.copyOf(data).withUser(username).withSessionId(username).withSessionId(sessionId).withNodeId(clusterConfiguration.getNodeId()).build();
    }

    private RequestData filterPayload(RequestData data) {
        if (config.filterPayload() && data.hasPayload()) {
            logger.trace("filter payload for request = {} with filters = {}", data, payloadFilters);
            for (PayloadFilter filter : payloadFilters) {
                try {
                    data = filter.filterPayload(data);
                } catch (Exception ex) {
                    logger.warn(marker(), "error processing payload filter = {} for request = {}", filter, data, ex);
                }
            }
        }
        return data;
    }

    private static class OngoingRequestStatus {

        public final String trackingId;
        public final boolean alreadyPersisted;
        public final ScheduledFuture persistOngoingRequestJob;

        public OngoingRequestStatus(String trackingId, boolean alreadyPersisted, @Nullable ScheduledFuture persistOngoingRequestJob) {
            this.trackingId = checkNotBlank(trackingId);
            this.alreadyPersisted = alreadyPersisted;
            this.persistOngoingRequestJob = persistOngoingRequestJob;
        }

    }

}
