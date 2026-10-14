/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.eventbus.Subscribe;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.session.inner.CurrentSessionHolder;
import org.cmdbuild.cluster.NodeIdProvider;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.config.EtlConfiguration;
import org.cmdbuild.config.WaterwayConfig;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.checkIsValidItemCode;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.isItemKey;
import org.cmdbuild.etl.waterway.message.MessageReference;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import static org.cmdbuild.etl.waterway.message.WaterwayMessage.MESSAGE_CONTEXT_ID;
import static org.cmdbuild.etl.waterway.message.WaterwayMessage.MESSAGE_REQUEST_ID;
import static org.cmdbuild.etl.waterway.message.WaterwayMessage.MESSAGE_SESSION_ID;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage.WMAS_EMBEDDED;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_BYTES;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_JSON;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_OBJECT;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_TEXT;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.DEFAULT_ATTACHMENT;
import org.cmdbuild.etl.waterway.message.WaterwayMessageImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageImpl.WaterwayMessageImplBuilder;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_DRAFT;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_FORWARDED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_QUEUED;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.addHistoryRecord;
import org.cmdbuild.etl.waterway.service.WaterwayMessageProcessor;
import org.cmdbuild.etl.waterway.service.WaterwayMessageProcessorService;
import org.cmdbuild.etl.waterway.storage.WaterwayStorageHandler;
import org.cmdbuild.etl.waterway.storage.WaterwayStorageService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionUtils.buildMinionRuntimeStatusChecker;
import static org.cmdbuild.requestcontext.RequestContext.REQUEST_ID;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.isPlaintext;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.io.CmMultipartUtils.isMultipart;
import static org.cmdbuild.utils.json.CmJsonUtils.isJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WaterwayServiceImpl implements WaterwayService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<WaterwayMessageProcessorService> processorService;
    private final WaterwayDescriptorService configService;
    private final WaterwayStorageService storageService;
    private final EtlConfiguration etlConfiguration;
    private final NodeIdProvider nodeIdProvider;
    private final RequestContextService requestContextService;
    private final CurrentSessionHolder currentSession;

    private final MinionHandler minionHandler;

    public WaterwayServiceImpl(CurrentSessionHolder currentSession, EventBusService eventBusService, Provider<WaterwayMessageProcessorService> processorService, WaterwayDescriptorService configService, WaterwayStorageService storageService, EtlConfiguration etlConfiguration, NodeIdProvider nodeIdProvider, RequestContextService requestContextService) {
        this.processorService = checkNotNull(processorService);
        this.configService = checkNotNull(configService);
        this.storageService = checkNotNull(storageService);
        this.etlConfiguration = checkNotNull(etlConfiguration);
        this.nodeIdProvider = checkNotNull(nodeIdProvider);
        this.requestContextService = checkNotNull(requestContextService);
        this.currentSession = checkNotNull(currentSession);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName(WATERWAY_SERVICE_MINION)
                .withStatusChecker(buildMinionRuntimeStatusChecker(() -> true, () -> WaterwayServiceImpl.this.configService.getAllItems()))
                .reloadOnConfigs(WaterwayConfig.class)
                .build();
        eventBusService.getDaoEventBus().register(new Object() {
            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                minionHandler.checkRuntimeStatus();
            }
        });
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public WaterwayServiceRequestHelper newRequest() {
        return new WaterwayServiceRequestHelperImpl();
    }

    @Override
    public WaterwayMessagesStats getMessagesStats() {
        return storageService.getMessagesStats();
    }

    @Override
    @Nullable
    public WaterwayMessage getMessageOrNull(MessageReference messageReference) {
        return storageService.getMessageOrNull(messageReference);
    }

    @Override
    public WaterwayMessageAttachment getMessageAttachmentLoadData(MessageReference messageReference, String attachmentName) {
        return storageService.getMessageAttachmentLoadData(messageReference, attachmentName);
    }

    @Override
    public WaterwayMessageUpdateHelper updateMessage(WaterwayMessage message) {
        return new WaterwayMessageUpdateHelperImpl(message);
    }

    @Override
    public PagedElements<WaterwayMessage> getMessages(DaoQueryOptions query) {
        return storageService.getMessages(query);
    }

    private WaterwayMessage submitMessage(WaterwayMessage message) {
        return new QueueHelper().queueNewMessage(message);
    }

    private WaterwayMessage createQueuedMessage(WaterwayMessage message) {
        message.checkHasStatus(WMS_QUEUED);
        WaterwayStorageHandler storage = storageService.getStorageOrDefaultForQueueCode(message.getQueueCode());
        message = addHistoryRecord(WaterwayMessageImpl.copyOf(message).withStorage(storage.getKey()).build());
        logger.debug("create queued message = {} with storage = {}", message, storage);
        try {
            return storage.createMessage(message);
        } catch (Exception ex) {
            throw new EtlException(ex, "error creating message = %s with storage = %s", message, storage);
        }
    }

    private WaterwayMessage updateMessage(WaterwayMessage prevMessage, WaterwayMessage message) {
        message = WaterwayMessageImpl.copyOf(message).withTimestamp(now()).withNodeId(nodeIdProvider.getNodeId()).bumpTransactionId().build();
        if (message.hasStatus(WMS_FORWARDED)) {
            return new QueueHelper().forwardMessage(prevMessage, message);
        } else {
            WaterwayStorageHandler storage = storageService.getStorageOrDefaultForQueueCode(message.getQueueCode());//TODO handle storage change douring message processing
            try {
                return storage.updateMessage(addHistoryRecord(WaterwayMessageImpl.copyOf(message).withStorage(storage.getKey()).build()));
            } catch (Exception ex) {
                throw new EtlException(ex, "error updating message = %s with storage = %s", message, storage);
            }
        }
    }

    private class QueueHelper {

        private WaterwayMessage message, prevMessage;

        public WaterwayMessage queueNewMessage(WaterwayMessage message) {
            this.message = checkNotNull(message);
            message.checkHasStatus(WMS_DRAFT);
            return queueMessage();
        }

        public WaterwayMessage forwardMessage(WaterwayMessage prevMessage, WaterwayMessage message) {
            this.message = checkNotNull(message);
            this.prevMessage = checkNotNull(prevMessage);
            message.checkHasStatus(WMS_FORWARDED);
            return queueMessage();
        }

        private WaterwayMessage queueMessage() {
            WaterwayMessageProcessor processor = processorService.get().getProcessor(message.getQueueCode());//TODO handle invalid target queue, redirect message to error handling queue
            WaterwayStorageHandler storage = storageService.getStorageOrDefaultForQueueCode(message.getQueueCode());
            try {
                boolean create;
                switch (message.getStatus()) {
                    case WMS_DRAFT -> {
                        create = true;
                    }
                    case WMS_FORWARDED -> {
                        WaterwayStorageHandler oldStorage = storageService.getStorage(prevMessage.getStorageCode());
                        message = oldStorage.updateMessage(addHistoryRecord(WaterwayMessageImpl.copyOf(message).withQueue(prevMessage.getQueue()).withStorage(oldStorage.getKey()).withMeta("cm_forwarded_to", message.getQueue()).build()));
                        message = WaterwayMessageImpl.copyOf(message).bumpTransactionId().build();
                        create = !equal(oldStorage.getCode(), storage.getCode());
                        if (create) {
                            oldStorage.deleteMessage(message);//TODO run this only if no errors (?) forwarded records _should_ be deleted, to avoid message id conflicts...
                        }
                    }
                    default ->
                        throw new IllegalArgumentException();
                }
                message = addHistoryRecord(WaterwayMessageImpl.copyOf(message).withStatus(WMS_QUEUED).withQueue(processor.getKey()).withStorage(storage.getKey()).build());
                logger.debug("create/update message = {} with storage = {}", message, storage);
                message = create ? storage.createMessage(message) : storage.updateMessage(message);
                logger.debug("submit message = {} to processor = {}", message, processor);
                message = processor.queueMessage(message);
                logger.debug("queued/processed message = {}", message);
                return message;
            } catch (Exception ex) {
                throw new EtlException(ex, "error queuing message = %s with processor = %s storage = %s", message, processor, storage);
            }
        }
    }

    private class WaterwayMessageUpdateHelperImpl implements WaterwayMessageUpdateHelper {

        private final WaterwayMessage currentMessage;
        private final WaterwayMessageImplBuilder newMessageBuilder;

        public WaterwayMessageUpdateHelperImpl(WaterwayMessage message) {
            this.currentMessage = checkNotNull(message);
            newMessageBuilder = WaterwayMessageImpl.copyOf(message);
        }

        @Override
        public WaterwayMessageUpdateHelper withStatus(WaterwayMessageStatus status) {
            newMessageBuilder.withStatus(checkNotNull(status));
            return this;
        }

        @Override
        public WaterwayMessageUpdateHelper forwardTo(String queue) {
            newMessageBuilder.withStatus(WMS_FORWARDED).withQueue(checkNotBlank(queue));
            return this;
        }

        @Override
        public WaterwayMessageUpdateHelper accept(Consumer<WaterwayMessageImplBuilder> callback) {
            newMessageBuilder.accept(callback);
            return this;
        }

        @Override
        public WaterwayMessage update() {
            return updateMessage(currentMessage, newMessageBuilder.build());
        }

    }

    private class WaterwayServiceRequestHelperImpl implements WaterwayServiceRequestHelper {

        private final Map<String, Pair<Object, Map<String, String>>> payload = map();
        private final Map<String, String> meta = map();
        private final List<String> history = list();
        private String queue, messageId;

        @Override
        public WaterwayServiceRequestHelper withTarget(String code) {
            this.queue = checkNotBlank(code);
            return this;
        }

        @Override
        public WaterwayServiceRequestHelper withMessageIdAndHistory(String messageId, List<String> history) {
            this.messageId = checkNotBlank(messageId);
            this.history.addAll(history);
            return this;
        }

        @Override
        public WaterwayServiceRequestHelper withMeta(Map<String, String> meta) {
            this.meta.putAll(meta);
            return this;
        }

        @Override
        public WaterwayServiceRequestHelper withPayload(Object payload) {
            return withPayload(DEFAULT_ATTACHMENT, payload);
        }

        @Override
        public WaterwayServiceRequestHelper withPayload(String name, Object payload, @Nullable Map<String, String> meta) {
            checkArgument(this.payload.put(checkNotBlank(renameDuplicatesIfEnabled(this.payload, name)), Pair.of(checkNotNull(payload), firstNotNull(meta, emptyMap()))) == null, "invalid duplicate payload name =< %s >", name);
            return this;
        }

        @Override
        public WaterwayMessage submit() {
            checkIsValidItemCode(queue);
            return submitMessage(new MessageBuilderHelper(this).buildMessage());
        }

        @Override
        public WaterwayMessage createQueued() {
            checkArgument(isItemKey(queue), "invalid target queue key =< %s >", queue);
            return createQueuedMessage(new MessageBuilderHelper(this).buildMessage(WMS_QUEUED));
        }

        @Override
        public WaterwayServiceRequestHelper withPayload(Collection<WaterwayMessageAttachment> payload) {
            payload.forEach(a -> withPayload(a.getName(), a.getObject(), a.getMeta()));//TODO improve this !!
            return this;
        }

    }

    private class MessageBuilderHelper {

        private final WaterwayServiceRequestHelperImpl request;

        private final Map<String, WaterwayMessageAttachment> attachments = map();

        public MessageBuilderHelper(WaterwayServiceRequestHelperImpl request) {
            this.request = checkNotNull(request);
        }

        public WaterwayMessage buildMessage() {
            return buildMessage(null);
        }

        public WaterwayMessage buildMessage(@Nullable WaterwayMessageStatus status) {
            try {
                String queue = checkNotBlank(request.queue, "missing target queue");
                logger.debug("processing request for target queue =< {} >, build message...", queue);

                buildAttachments();

                return WaterwayMessageImpl.builder()
                        .withMessageId(firstNotBlank(request.messageId, randomId()))
                        .withHistory(request.history)
                        .withMeta((Map) map(MESSAGE_SESSION_ID, currentSession.getOrNull())
                                .with(request.meta)
                                .with(MESSAGE_CONTEXT_ID, requestContextService.getRequestContextId(), MESSAGE_REQUEST_ID, requestContextService.get(REQUEST_ID)))
                        .withAttachments(attachments)
                        .withNodeId(nodeIdProvider.getNodeId())
                        .withTimestamp(now())
                        .withQueue(queue)
                        .withStorage(null)
                        .withStatus(firstNotNull(status, WMS_DRAFT))
                        .build();

            } catch (MessagingException ex) {
                throw runtime(ex);
            }
        }

        private void buildAttachments() throws MessagingException {
            if (request.payload.keySet().equals(singleton(DEFAULT_ATTACHMENT)) && getOnlyElement(request.payload.values()).getLeft() instanceof DataSource && isMultipart((DataSource) getOnlyElement(request.payload.values()).getLeft())) {
                buildAttachmentsFromMultipart((DataSource) getOnlyElement(request.payload.values()).getLeft(), getOnlyElement(request.payload.values()).getRight());
            } else {
                request.payload.forEach((k, v) -> buildAttachment(k, v.getLeft(), v.getRight()));
            }
        }

        private void buildAttachmentsFromMultipart(DataSource multipart, Map<String, String> meta) throws MessagingException {
            MimeMultipart mimeMultipart = new MimeMultipart(multipart);
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart part = mimeMultipart.getBodyPart(i);
                logger.debug("processing attachment part = {}", part);
                DataSource innerDataSource = toDataSource(part.getDataHandler());
                if (isMultipart(innerDataSource)) {
                    buildAttachmentsFromMultipart(innerDataSource, meta);
                } else {
                    buildAttachment(firstNotBlank(getOnlyElement(list(firstNotNull(part.getHeader("name"), new String[]{})), null), part.getFileName(), DEFAULT_ATTACHMENT), innerDataSource, meta);

                }
            }
        }

        private void buildAttachment(String name, Object data, Map<String, String> meta) {
            logger.debug("build message attachment for name =< {} > data =< {} > meta = {}", name, abbreviate(data), abbreviate(mapToLoggableStringInline(meta)));
            WaterwayMessageAttachmentType type;
            if (data instanceof DataSource dataSource) {
                meta = (Map) map("ContentType", dataSource.getContentType())//TODO improve this
                        .with(meta);
                if (CmIoUtils.isJson(dataSource)) {
                    data = readToString(dataSource);
                } else if (isPlaintext(dataSource)) {
                    data = readToString(dataSource);
                } else {
                    data = toByteArray(dataSource);
                }
            }
            if (data instanceof String string) {
                type = isJson(string) ? WMAT_JSON : WMAT_TEXT;
            } else if (data instanceof byte[]) {
                type = WMAT_BYTES;
            } else {
                type = WMAT_OBJECT;
            }
            //TODO handle reference attachment (temp storage, etc)
            WaterwayMessageAttachment attachment = WaterwayMessageAttachmentImpl.builder()
                    .withName(renameDuplicatesIfEnabled(attachments, name))
                    .withMeta(meta)
                    .withStorage(WMAS_EMBEDDED)
                    .withType(type)
                    .withObject(data)
                    .build();

            checkArgument(attachments.put(attachment.getName(), attachment) == null, "invalid duplicate attachment =< %s >", attachment);
            logger.debug("processed attachment = {}", attachment);
        }
    }

    private String renameDuplicatesIfEnabled(Map<String, ?> map, String name) {
        if (etlConfiguration.allowDuplicateAttachmentName()) {
            AtomicInteger index = new AtomicInteger(1);
            String originalName = name;
            while (map.containsKey(name)) {
                logger.debug("name {} already exists, renaming it");
                name = isBlank(FilenameUtils.getExtension(originalName)) ? format("%s_%s", originalName, index.getAndIncrement()) : format("%s_%s.%s", FilenameUtils.getBaseName(originalName), index.getAndIncrement(), FilenameUtils.getExtension(originalName));
                logger.debug("name {} renamed to = < {} >", originalName, name);
            }
        }
        return name;
    }
}
