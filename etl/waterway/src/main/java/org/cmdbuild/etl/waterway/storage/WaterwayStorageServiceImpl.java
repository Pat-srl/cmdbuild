/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.storage;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import jakarta.annotation.Nullable;
import static org.cmdbuild.cache.CacheConfig.SYSTEM_OBJECTS;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.config.WaterwayConfig;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.dao.utils.SorterProcessor.comparatorFromSorter;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_STORAGE;
import org.cmdbuild.etl.waterway.WaterwayMessagesStats;
import org.cmdbuild.etl.waterway.message.MessageReference;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class WaterwayStorageServiceImpl implements WaterwayStorageService {

    private final WaterwayConfig config;
    private final WaterwayDescriptorService configService;
    private final List<WaterwayStorageProvider> providers;

    private final Holder<Map<String, WaterwayStorageHandler>> storagesByCode;
    private final Holder<Map<String, WaterwayStorageHandler>> storagesByQueueCode;

    public WaterwayStorageServiceImpl(WaterwayConfig config, WaterwayDescriptorService configService, List<WaterwayStorageProvider> providers, CacheService cacheService, EventBusService busService) {
        this.config = checkNotNull(config);
        this.configService = checkNotNull(configService);
        this.providers = ImmutableList.copyOf(providers);
        storagesByCode = cacheService.newHolder("wy_storages_all", SYSTEM_OBJECTS);
        storagesByQueueCode = cacheService.newHolder("wy_storages_by_queue", SYSTEM_OBJECTS);
        busService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                storagesByCode.invalidate();
                storagesByQueueCode.invalidate();
            }

        });
    }

    @Override
    public WaterwayMessagesStats getMessagesStats() {
        return WaterwayMessagesStatsImpl.build(list(providers).flatMap(WaterwayStorageProvider::getStorageHandlers).map(WaterwayStorageHandler::getMessagesStats));
    }

    @Override
    @Nullable
    public WaterwayMessage getMessageOrNull(MessageReference messageReference) {
        if (messageReference.hasStorage()) {
            return getStorage(messageReference.getStorage()).getMessageOrNull(messageReference);
        } else {
            return getAllStorageHandlers().stream().map(h -> h.getMessageOrNull(messageReference)).filter(not(isNull())).collect(toOptional()).orElse(null);
        }
    }

    @Override
    public WaterwayMessageAttachment getMessageAttachmentLoadData(MessageReference messageReference, String attachmentName) {
        if (messageReference.hasStorage()) {
            return getStorage(messageReference.getStorage()).getMessageAttachmentLoadData(messageReference, attachmentName);
        } else {
            return getStorage(getMessage(messageReference).getStorageCode()).getMessageAttachmentLoadData(messageReference, attachmentName);
        }
    }

    @Override
    public PagedElements<WaterwayMessage> getMessages(DaoQueryOptions query) {
        DaoQueryOptions subquery = DaoQueryOptionsImpl.copyOf(query).accept(q -> {
            if (query.isPaged()) {
                q.withOffset(0l).withLimit(query.getLimit() + query.getOffset());
            }
        }).build();
        List<PagedElements<WaterwayMessage>> paged = list(providers).map(p -> p.getMessages(subquery));
        long totalSize = paged.stream().mapToLong(PagedElements::totalSize).sum();
        List<WaterwayMessage> records = paged.stream().flatMap(PagedElements::stream)
                .sorted(comparatorFromSorter(query.getSorter())) //TODO map query attr names !!
                .skip(query.getOffset()).limit(query.getLimitOrMaxValue()).collect(toImmutableList());
        return paged(records, totalSize);
    }

    @Override
    public WaterwayStorageHandler getStorage(String code) {
        return checkNotNull(getStorageOrNull(code), "storage provider not found for code =< %s >", code);
    }

    @Override
    public WaterwayStorageHandler getStorageOrDefaultForQueueCode(String code) {
        return firstNotNull(getStorageForQueueOrNull(code), getStorage(config.getDefaultStorage()));
    }

    @Nullable
    private WaterwayStorageHandler getStorageOrNull(String code) {
        return getStorages().get(checkNotBlank(code));
    }

    @Override
    public List<WaterwayStorageHandler> getAllStorageHandlers() {
        return ImmutableList.copyOf(getStorages().values());
    }

    private Map<String, WaterwayStorageHandler> getStorages() {
        return storagesByCode.get(this::loadStorages);
    }

    @Nullable
    private WaterwayStorageHandler getStorageForQueueOrNull(String queue) {
        return storagesByQueueCode.get(this::loadQueueStorages).get(checkNotBlank(queue));
    }

    private Map<String, WaterwayStorageHandler> loadStorages() {
        return providers.stream().flatMap(r -> r.getStorageHandlers().stream()).collect(toMap(WaterwayStorageHandler::getCode, identity()));
    }

    private Map<String, WaterwayStorageHandler> loadQueueStorages() {
        Map<String, WaterwayStorageHandler> map = map();
        configService.getAllItems().stream().filter(i -> i.isEnabled() && !i.isOfType(WYCIT_STORAGE) && i.hasStorage()).forEach(i -> {
            WaterwayStorageHandler provider = checkNotNull(getStorageOrNull(i.getStorage()), "storage not found for item = %s with key =< %s >", i, i.getStorage());
            checkArgument(map.put(i.getCode(), provider) == null, "duplicate item/storage code =< %s >", i.getCode());
        });
        return ImmutableMap.copyOf(map);
    }

}
