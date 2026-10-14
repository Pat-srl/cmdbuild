/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization.widget;

import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.services.permissions.PermissionsHandlerProxy;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.widget.model.WidgetDbData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

/**
 *
 * @author afelice
 */
@Component
public class WidgetHelper {

    private final WidgetService widgetService;
    private final ObjectTranslationService translationService;
    private final PermissionsHandlerProxy permissionsHandler;

    public WidgetHelper(WidgetService widgetService, org.cmdbuild.translation.ObjectTranslationService translationService, PermissionsHandlerProxy permissionsHandler) {
        this.permissionsHandler = permissionsHandler;
        this.widgetService = widgetService;
        this.translationService = translationService;
    }

    public List<WidgetSerializationData> fetchWidgets(Card card) {
        if (card == null) {
            return emptyList();
        }

        List<WidgetData> classWidgets = widgetService.getAllWidgetsForClass(card.getType()).stream()
                .filter(w -> permissionsHandler.cardWsSerializationHelperv3_isWidgetEnabled(card, w)).collect(toList());
        // Was in CardWsSerializationHelperv3.serializeWidgets() but seems not needed
        // List<Widget> cardWidgets = widgetService.widgetDataToWidget(card.getClassName(), null, classWidgets, card.toMap());

        return classWidgets.stream().map(w -> new WidgetSerializationData(w,
                fetchDescriptionTranslation(w, card.getTypeName())))
                .collect(Collectors.toList());
    }

    /**
     * Was in
     * { ClassSerializationHelper#serializeWidget(org.cmdbuild.widget.model.WidgetData, java.lang.String)}
     *
     * @param widgetData
     * @param className
     * @return
     */
    private String fetchDescriptionTranslation(WidgetData widgetData, String className) {
        String descriptionTranslation;

        if (widgetData instanceof WidgetDbData widgetDbData) {//TODO improve this, translate wf widgets
            descriptionTranslation = translationService.translateClassWidgetDescription(widgetDbData.getOwner(), widgetData.getId(), widgetData.getLabel());
        } else if (isNotBlank(className) && isNotBlank(widgetData.getId())) {
            descriptionTranslation = translationService.translateClassWidgetDescription(className, widgetData.getId(), widgetData.getLabel());
        } else {
            descriptionTranslation = widgetData.getLabel();
        }

        return descriptionTranslation;
    }

}
