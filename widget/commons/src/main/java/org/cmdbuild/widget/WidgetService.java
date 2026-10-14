package org.cmdbuild.widget;

import static com.google.common.base.Functions.constant;
import static com.google.common.base.Objects.equal;
import java.util.Collection;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.widget.model.Widget;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.widget.model.WidgetDbData;

public interface WidgetService extends WidgetFactoryService {

    List<WidgetData> getAllWidgetsForClass(String classId);

    void updateWidgetsForClass(Classe classe, List<WidgetData> widgets);

    void deleteForClass(Classe classe);

    List<WidgetDbData> getAllWidgets();

    default List<WidgetData> getAllWidgetsForClass(Classe classe) {
        return getAllWidgetsForClass(classe.getName());
    }

    default WidgetData getWidgetForClass(String classId, String widgetId) {
        return getAllWidgetsForClass(classId).stream().filter(w -> equal(w.getId(), checkNotBlank(widgetId))).collect(onlyElement("widget not found for id =< %s >", widgetId));
    }

    default List<WidgetData> getActiveWidgetsForClass(Classe classe) {
        return list(getAllWidgetsForClass(classe)).filter(WidgetData::isActive);
    }

    default List<Widget> widgetDataToWidget(String classIdOrProcessId, @Nullable String taskDefinitionId, Collection<WidgetData> data, Map<String, Object> context) {
        return data.stream().map((wd) -> widgetDataToWidget(classIdOrProcessId, taskDefinitionId, wd, context)).collect(toList());
    }

    default Widget widgetDataToWidget(WidgetData widgetData, Classe classe) {
        return widgetDataToWidget(classe.getName(), null, widgetData, map(classe.getActiveServiceAttributes(), Attribute::getName, constant(null)));
    }
}
