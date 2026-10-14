/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.gate.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_GATE;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_HANDLER;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import static org.cmdbuild.etl.gate.inner.EtlGate.ETL_GATE_KEY;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.jobs.JobData.JOB_MODULE;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class EtlGateRepositoryImpl implements EtlGateRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService service;
    private final Holder<List<EtlGate>> gates;
    private final CmCache<Optional<EtlGate>> gatesByCode;

    public EtlGateRepositoryImpl(WaterwayDescriptorService service, CacheService cacheService, EventBusService busService) {
        this.service = checkNotNull(service);
        gates = cacheService.newHolder("etl_gates_all", CacheConfig.SYSTEM_OBJECTS);
        gatesByCode = cacheService.newCache("etl_gates_by_code", CacheConfig.SYSTEM_OBJECTS);
        busService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        gates.invalidate();
        gatesByCode.invalidateAll();
    }

    @Override
    @Nullable
    public EtlGate getByCodeOrNull(String gate) {
        return gatesByCode.get(checkNotBlank(gate), () -> getAll().stream().filter(g -> equal(gate, g.getCode())).collect(toOptional())).orElse(null);
    }

    @Override
    public List<EtlGate> getAll() {
        return gates.get(this::doGetAll);
    }

    @Override
    public EtlGate create(EtlGate gate) {
        service.createUpdateDescriptor(gateToFile(gate.getCode(), gate), WaterwayDescriptorMetaImpl.builder().withEnabled(gate.isEnabled()).build());
        return getByCode(gate.getCode());
    }

    @Override
    public EtlGate update(EtlGate gate) {
        WaterwayDescriptorRecord record = service.getDescriptorForSingleItemUpdate(getByCode(gate.getCode()).getCode());
        service.createUpdateDescriptor(gateToFile(record.getCode(), gate), WaterwayDescriptorMetaImpl.builder().withEnabled(gate.isEnabled()).build());
        return getByCode(gate.getCode());
    }

    @Override
    public void delete(String code) {
        service.deleteDescriptor(service.getDescriptorForSingleItemUpdate(getByCode(code).getCode()).getCode());
    }

    private List<EtlGate> doGetAll() {
        List<EtlGate> list = service.getAllItems(WYCIT_GATE).stream().map(this::loadGate).sorted(Ordering.natural().onResultOf(EtlGate::getCode)).collect(toImmutableList());
        logger.debug("loaded {} gates", list.size());
        return list;
    }

    private String gateToFile(String code, EtlGate gate) {
        return toJson(map("descriptor", code, "description", gate.getDescription(), "tag", "standalone", "items", list(map(gate.getConfig()).withoutKeys(ETL_GATE_KEY, JOB_MODULE).with(
                "gate", gate.getCode(),
                "description", gate.getDescription(),
                "processing", serializeEnum(gate.getProcessingMode()),
                "access", gate.getAllowPublicAccess() ? "public" : "private",
                "handlers", list(gate.getHandlers()).map(h -> map(h.getConfig())
                .withoutKeys(k -> gate.getConfig().containsKey(k) && equal(gate.getConfig(k), h.getConfig(k))).with("type", h.getType()))))));
    }

    private EtlGate loadGate(WaterwayItem item) {
        return EtlGateImpl.builder()
                .withCode(item.getCode())
                .withConfig(item.getConfig())
                .withConfig(JOB_MODULE, item.getDescriptorCode())//TODO improve this
                .withConfig(ETL_GATE_KEY, item.getKey())
                .withDescription(item.getDescription())
                .withEnabled(item.isEnabled())
                .withAllowPublicAccess(equal(item.getConfig("access"), "public"))
                .withProcessingMode(parseEnumOrNull(item.getConfig("processing"), EtlProcessingMode.class))
                .withHandlers(list(item.getItems()).map(service::getItemByCode).filter(i -> i.isOfType(WYCIT_HANDLER)).map(i
                        -> EtlGateHandlerImpl.builder().withType(checkNotBlank(i.getSubtype(), "missing handler type")).withConfig(i.getConfig()).build()))
                //                                .withoutKeys(k -> item.getConfig().containsKey(k) && equal(i.getConfig().get(k), item.getConfig(k)))
                .build();
    }

}
