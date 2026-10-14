/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.corecomponents;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import static org.cmdbuild.cache.CacheConfig.SYSTEM_OBJECTS;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.corecomponents.CoreComponentType.CCT_SCRIPT;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorMetaImpl;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_SCRIPT;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class CoreComponentServiceImpl implements CoreComponentService {

    private final WaterwayDescriptorService service;

    private final CmCache<Optional<CoreComponent>> componentsByCode;
    private final Holder<List<CoreComponent>> components;

    public CoreComponentServiceImpl(WaterwayDescriptorService service, CacheService cacheService, EventBusService busService) {
        this.service = checkNotNull(service);
        this.componentsByCode = cacheService.newCache("core_components_by_id_or_code", SYSTEM_OBJECTS);
        this.components = cacheService.newHolder("core_components_all", SYSTEM_OBJECTS);
        busService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                invalidateAll();
            }
        });
    }

    private void invalidateAll() {
        components.invalidate();
        componentsByCode.invalidateAll();;
    }

    @Override
    @Nullable
    public CoreComponent getComponentOrNull(String code) {
        checkNotBlank(code);
        return componentsByCode.get(code, () -> getComponents().stream().filter(c -> equal(c.getCode(), code)).collect(toOptional())).orElse(null);
    }

    @Override
    public List<CoreComponent> getComponents() {
        return components.get(() -> service.getAllItems(WYCIT_SCRIPT).stream().map(i -> CoreComponentImpl.builder()
                .withActive(i.isEnabled()).withCode(i.getCode()).withData(i.getConfig("data")).withDescription(i.getDescription()).withType(CCT_SCRIPT).build()
        ).collect(toImmutableList()));
    }

    @Override
    public CoreComponent createComponent(CoreComponent component) {
        service.createUpdateDescriptor(componentToModule(component.getCode(), component), WaterwayDescriptorMetaImpl.builder().withEnabled(component.isActive()).build());
        return getComponent(component.getCode());
    }

    @Override
    public CoreComponent updateComponent(CoreComponent component) {
        WaterwayDescriptorRecord record = service.getDescriptorForSingleItemUpdate(getComponent(component.getCode()).getCode());
        service.createUpdateDescriptor(componentToModule(record.getCode(), component), WaterwayDescriptorMetaImpl.builder().withEnabled(component.isActive()).build());
        return getComponent(component.getCode());
    }

    @Override
    public void deleteComponent(String code) {
        service.deleteDescriptor(service.getDescriptorForSingleItemUpdate(getComponent(code).getCode()).getCode());
    }

    private String componentToModule(String descriptorCode, CoreComponent component) {
        checkArgument(component.isScript());
        return toJson(map("descriptor", descriptorCode, "description", component.getDescription(), "tag", "standalone", "items", list(map("script", component.getCode(), "data", component.getData(), "description", component.getDescription()))));
    }

}
