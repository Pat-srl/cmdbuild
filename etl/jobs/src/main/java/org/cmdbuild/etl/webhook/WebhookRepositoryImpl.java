/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.etl.webhook;

import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_WEBHOOK;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
@Primary
public class WebhookRepositoryImpl implements WebhookRepository {

    private final WaterwayDescriptorService configService;
    private final CmCache<WebhookConfig> webhookByName;
    private final Holder<List<WebhookConfig>> webhooks;

    public WebhookRepositoryImpl(WaterwayDescriptorService configService, CacheService cacheService, EventBusService busService) {
        this.configService = configService;
        webhookByName = cacheService.newCache("webhook_by_name", CacheConfig.SYSTEM_OBJECTS);
        webhooks = cacheService.newHolder("webhooks_all", CacheConfig.SYSTEM_OBJECTS);
        busService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                invalidateCaches();
            }
        });
    }

    private void invalidateCaches() {
        webhookByName.invalidateAll();
        webhooks.invalidate();
    }

    @Override
    public List<WebhookConfig> getAll() {
        return webhooks.get(() -> configService.getAllItems(WYCIT_WEBHOOK).stream().filter(i -> i.hasConfigNotBlank("url") && i.hasConfigNotBlank("event")).map(i -> WebhookConfigImpl.copyOf(i).build()).collect(toImmutableList()));
    }

    @Override
    public WebhookConfig getByName(String webhookName) {
        return webhookByName.get(webhookName, () -> getAll().stream().filter(wh -> wh.getCode().equals(webhookName)).collect(onlyElement("webhook not found for code =< %s >", webhookName)));
    }

    @Override
    public WebhookConfig create(WebhookConfig webhook) {
        configService.createUpdateDescriptor(webhookToFile(webhook.getCode(), webhook), WaterwayDescriptorMetaImpl.builder().withEnabled(webhook.isActive()).build());
        invalidateCaches();
        return getByName(webhook.getCode());
    }

    @Override
    public WebhookConfig update(WebhookConfig webhook) {
        WaterwayDescriptorRecord record = configService.getDescriptorForSingleItemUpdate(getByName(webhook.getCode()).getCode());
        configService.createUpdateDescriptor(webhookToFile(record.getCode(), webhook), WaterwayDescriptorMetaImpl.builder().withEnabled(webhook.isActive()).build());
        invalidateCaches();
        return getByName(webhook.getCode());
    }

    @Override
    public void delete(String webhookName) {
        configService.deleteDescriptor(configService.getDescriptorForSingleItemUpdate(getByName(webhookName).getCode()).getCode());
        invalidateCaches();
    }

    private String webhookToFile(String code, WebhookConfig webhook) {
        return toJson(map("descriptor", code, "description", webhook.getDescription(), "tag", "standalone", "items", list(
                map(
                        "event", webhook.getEvents(),
                        "target", webhook.getTarget(),
                        "method", serializeEnum(webhook.getMethod()),
                        "url", webhook.getUrl(),
                        "headers", webhook.getHeaders(),
                        "body", webhook.getBody(),
                        "lang", webhook.getLanguage(),
                        "filter", serializeFilter(webhook.getFilter())
                ).with(serializeEnum(WYCIT_WEBHOOK), webhook.getCode(), "description", webhook.getDescription()))));
    }

}
