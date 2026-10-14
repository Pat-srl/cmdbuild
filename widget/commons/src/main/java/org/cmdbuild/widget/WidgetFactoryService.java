/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget;

import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;

public interface WidgetFactoryService {

    final String WIDGET_CLASSIDORPROCESSID = "_WIDGET_CLASSIDORPROCESSID", WIDGET_TASKDEFINITIONID = "_WIDGET_TASKDEFINITIONID";

    Widget widgetDataToWidget(WidgetData data, Map<String, Object> context);

    default Widget widgetDataToWidget(String classIdOrProcessId, @Nullable String taskDefinitionId, WidgetData data, Map<String, Object> context) {
        return widgetDataToWidget(data, map(context).with(WIDGET_CLASSIDORPROCESSID, classIdOrProcessId, WIDGET_TASKDEFINITIONID, taskDefinitionId));
    }
}
