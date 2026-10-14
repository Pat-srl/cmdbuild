package org.cmdbuild.widget;

import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.widget.dao.WidgetRepository;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.widget.model.WidgetDataImpl;
import org.cmdbuild.widget.model.WidgetDbData;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_INDEX;

@Component
public class WidgetServiceImpl implements WidgetService {

    private final WidgetRepository repository;
    private final WidgetFactoryService factoryService;
    private final CmCache<List<WidgetData>> widgetDataCache;

    public WidgetServiceImpl(WidgetRepository repository, WidgetFactoryService factoryService, CacheService cacheService) {
        this.repository = checkNotNull(repository);
        this.factoryService = checkNotNull(factoryService);
        widgetDataCache = cacheService.newCache("widget_data_by_class", CacheConfig.SYSTEM_OBJECTS);
    }

    @Override
    public List<WidgetData> getAllWidgetsForClass(String classId) {
        return widgetDataCache.get(classId, () -> repository.getAllWidgetsForClass(classId));
    }

    @Override
    public Widget widgetDataToWidget(WidgetData data, Map<String, Object> context) {
        return factoryService.widgetDataToWidget(data, context);
    }

    @Override
    public void updateWidgetsForClass(Classe classe, List<WidgetData> widgets) {
        AtomicInteger index = new AtomicInteger(0);
        widgets = list(widgets).map(w -> WidgetDataImpl.copyOf(w).withData(WIDGET_INDEX, index.getAndIncrement()).build());
        repository.updateForClass(classe.getName(), widgets);
        widgetDataCache.invalidate(classe.getName());
    }

    @Override
    public void deleteForClass(Classe classe) {
        repository.deleteForClass(classe.getName());
        widgetDataCache.invalidate(classe.getName());
    }

    @Override
    public List<WidgetDbData> getAllWidgets() {
        return repository.getAllWidgets();
    }

}
