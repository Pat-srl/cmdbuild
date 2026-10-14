package org.cmdbuild.uicomponents.data;

import jakarta.annotation.Nullable;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.systemplugin.SystemPluginService;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_ADMINCUSTOMPAGE;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_CONTEXTMENU;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_CUSTOMPAGE;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_WIDGET;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.getAdminCustomPageCode;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.getCodeFromExtComponentData;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.normalizeComponentData;
import static org.cmdbuild.utils.hash.CmHashUtils.toIntHash;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.toOptional;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.and;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.springframework.stereotype.Component;

@Component
public class UiComponentRepositoryImpl implements UiComponentRepository {

    private final DaoService dao;
    private final SystemPluginService systemPluginService;
    private final Holder<List<UiComponentData>> all;
    private final CmCache<Optional<UiComponentData>> byCode;
    private final CmCache<UiComponentData> byId;

    public UiComponentRepositoryImpl(DaoService dao, SystemPluginService systemPluginService, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.systemPluginService = checkNotNull(systemPluginService);
        all = cacheService.newHolder("ui_components_all", CacheConfig.SYSTEM_OBJECTS);
        byCode = cacheService.newCache("ui_components_by_code", CacheConfig.SYSTEM_OBJECTS);
        byId = cacheService.newCache("ui_components_by_id", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateCache() {
        all.invalidate();
        byCode.invalidateAll();
        byId.invalidateAll();
    }

    @Override
    public List<UiComponentData> getAll() {
        return all.get(() -> list(doReadAll()).with(getAllSystemPluginUiComponent()));
    }

    @Override
    @Nullable
    public UiComponentData getByTypeAndNameOrNull(UiComponentType type, String name) {
        checkNotNull(type);
        checkNotBlank(name);
        return byCode.get(key(type, name), () -> getAll().stream().filter(and(equal(UiComponentData::getType, type), equal(UiComponentData::getName, name))).collect(toOptional())).orElse(null);
    }

    @Override
    public UiComponentData getById(long id) {
        return byId.get(id, () -> getAll().stream().filter(equal(UiComponentData::getId, id)).collect(onlyElement("ui component not found for id = %s", id)));
    }

    @Override
    public UiComponentData create(UiComponentData component) {
        component = dao.create(component);
        invalidateCache();
        return component;
    }

    @Override
    public UiComponentData update(UiComponentData component) {
        component = dao.update(component);
        invalidateCache();
        return component;
    }

    @Override
    public void delete(long id) {
        dao.delete(UiComponentData.class, id);
        invalidateCache();
    }

    private List<UiComponentData> doReadAll() {
        return dao.selectAll().from(UiComponentData.class).asList();
    }

    private List<UiComponentData> getAllSystemPluginUiComponent() {
        return systemPluginService.getSystemPlugins().stream().flatMap(this::loadSystemPluginUiComponentData).collect(toList());
    }

    private Stream<UiComponentData> loadSystemPluginUiComponentData(SystemPlugin plugin) {
        return mapOf(UiComponentType.class, String.class).with(
                UCT_CUSTOMPAGE, "customcomponents.custompages",
                UCT_ADMINCUSTOMPAGE, "customcomponents.admincustompage",
                UCT_CONTEXTMENU, "customcomponents.contextmenu",
                UCT_WIDGET, "customcomponents.widgets"
        ).entrySet().stream().flatMap(entry -> {
            return plugin.getResources(entry.getValue(), "zip").entrySet().stream().map(res -> {
                String name = getCodeFromExtComponentData(singletonList(res.getValue()));
                long id = toIntHash(name) * -1; // negative id, to distinguish from real ids from the database
                String description = Objects.equals(entry.getKey(), UCT_ADMINCUSTOMPAGE) ? getAdminCustomPageCode(plugin.getName()) : FilenameUtils.getBaseName(res.getKey());
                return UiComponentDataImpl.builder()
                        .withId(id)
                        .withActive(Boolean.TRUE)
                        .withData(normalizeComponentData(singletonList(res.getValue())))
                        .withName(name)
                        .withDescription(description)
                        .withType(entry.getKey())
                        .build();
            });
        });
    }
}
