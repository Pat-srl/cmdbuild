/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;

@Component
public class ContextMenuServiceImpl implements ContextMenuService {

    private final ContextMenuRepository contextMenuRepository;
    private final CmCache<List<ContextMenuItem>> cache;

    public ContextMenuServiceImpl(ContextMenuRepository contextMenuRepository, CacheService cacheService) {
        this.contextMenuRepository = checkNotNull(contextMenuRepository);
        cache = cacheService.newCache("context_menu_items_by_class_oid", CacheConfig.SYSTEM_OBJECTS);
    }

    @Override
    public List<ContextMenuItem> getContextMenuItems(String ownerName, ContextMenuOwnerType ownerType) {
        return cache.get(key(ownerName, ownerType), () -> contextMenuRepository.getContextMenuItems(ownerName, ownerType).stream()
                .sorted(Ordering.natural().onResultOf(ContextMenuItemData::getIndex))
                .map(this::toContextMenuItem)
                .collect(toList()));
    }

    @Override
    public void updateContextMenuItems(String ownerName, List<ContextMenuItem> items, ContextMenuOwnerType ownerType) {
        contextMenuRepository.updateContextMenuItems(ownerName, IntStream.range(0, items.size()).mapToObj((i) -> {
            ContextMenuItem item = items.get(i);
            return ContextMenuItemDataImpl.builder()
                    .withIndex(i)
                    .withOwnerType(ownerType)
                    .withClassId(ownerName)
                    .withLabel(item.getLabel())
                    .withJsScript(item.getJsScript())
                    .withActive(item.isActive())
                    .withComponentId(item.getComponentId())
                    .withConfig(item.getConfig())
                    .withType(item.getType().name())
                    .withVisibility(item.getVisibility().name())
                    .build();
        }).collect(toList()), ownerType);
        cache.invalidate(key(ownerName, ownerType));
    }

    private ContextMenuItem toContextMenuItem(ContextMenuItemData data) {
        return ContextMenuItemImpl.builder()
                .withActive(data.isActive())
                .withComponentId(data.getComponentId())
                .withConfig(data.getConfig())
                .withJsScript(data.getJsScript())
                .withLabel(data.getLabel())
                .withType(ContextMenuType.valueOf(data.getType()))
                .withVisibility(ContextMenuVisibility.valueOf(data.getVisibility()))
                .build();
    }

    @Override
    public void deleteForOwner(String ownerName, ContextMenuOwnerType ownerType) {
        contextMenuRepository.deleteContextMenuItems(ownerName, ownerType);
        cache.invalidate(key(ownerName, ownerType));
    }

}
