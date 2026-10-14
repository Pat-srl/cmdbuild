/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.service;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.minions.PostStartup;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WaterwayMessageProcessorServiceImpl implements WaterwayMessageProcessorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<WaterwayMessageProcessorRepository> repositories;
    private final Holder<Map<String, WaterwayMessageProcessor>> processors;

    public WaterwayMessageProcessorServiceImpl(List<WaterwayMessageProcessorRepository> repositories, CacheService cacheService, EventBusService busService) {
        this.repositories = ImmutableList.copyOf(repositories);
        processors = cacheService.newHolder("wy_processors_all", CacheConfig.SYSTEM_OBJECTS);
        busService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                processors.invalidate();
            }

        });
    }

    @PostStartup
    public void init() {
        getProcessors();
    }

    @Override
    public WaterwayMessageProcessor getProcessor(String code) {
        return checkNotNull(getProcessors().get(checkNotBlank(code)), "message processor not found for code =< %s >", code);
    }

    private Map<String, WaterwayMessageProcessor> getProcessors() {
        return processors.get(() -> {
            try {
                logger.debug("load wy message processors");
                Map<String, WaterwayMessageProcessor> map = repositories.stream().flatMap(r -> r.getProcessors().stream()).collect(toImmutableMap(WaterwayMessageProcessor::getCode, identity()));
                logger.info("loaded {} wy message processors", map.size());
                return map;
            } catch (Exception ex) {
                throw new EtlException(ex, "error loading wy message processors");
            }
        });
    }

}
