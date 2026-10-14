package org.cmdbuild.cluster;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.resources.SpringResource;
import org.cmdbuild.etl.gate.inner.EtlGateGridMessageProcessor;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.buildMessageReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.etl.gate.inner.EtlGateGridMessageProcessingHelper;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;

@Component
public class EtlGateGridMessageProcessorImpl implements EtlGateGridMessageProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final IgniteService igniteService;

    private final ExecutorService executorService;

    public EtlGateGridMessageProcessorImpl(RequestContextService contextService, IgniteService igniteService) {
        this.igniteService = checkNotNull(igniteService);
        executorService = executorService(getClass().getName(), () -> contextService.initCurrentRequestContext("gate grid message queue processing job"), contextService::destroyCurrentRequestContext);
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executorService);
    }

    @Override
    public void queueMessageForGridProcessing(WaterwayMessage message) {
        logger.debug("queue message for grid processing, message = {} ", message);
        IgniteFuture<String> future = igniteService.getIgnite().compute().applyAsync(new MessageProcessingJob(), buildMessageReference(message.getStorageCode(), message.getMessageId())); //TODO handler errors (?) async (??)
        future.listenAsync((e) -> {
            try {
                String messageReference = e.get(1, TimeUnit.HOURS);//TODO configurable timeout
                logger.debug("completed grid processing of message = {} output reference =< {} >", message, messageReference);
            } catch (Exception ex) {
                logger.error(marker(), "error grid processing message = {}", message, ex);
            }
        }, executorService);
    }

    private static class MessageProcessingJob implements IgniteClosure<String, String> {

        @SpringResource(resourceClass = EtlGateGridMessageProcessor.class)
        private transient EtlGateGridMessageProcessor messageProcessor;

        @SpringResource(resourceClass = EtlGateGridMessageProcessingHelper.class)
        private transient EtlGateGridMessageProcessingHelper processingHelper;

        @Override
        public String apply(String messageReference) {
            try {
                return ((EtlGateGridMessageProcessorImpl) messageProcessor).executorService.submit(() -> {
                    WaterwayMessage message = processingHelper.queueGridMessageForLocalProcessing(messageReference);
                    return buildMessageReference(message.getStorageCode(), message.getMessageKey());
                }).get();
            } catch (Exception ex) {
                LoggerFactory.getLogger(getClass()).error(marker(), "error processing queued grid message on this node, message reference =< {} >", messageReference, ex);
                throw runtime(ex, "error processing queued grid message on this node, message reference =< %s >", messageReference);
            }
        }

    }

}
