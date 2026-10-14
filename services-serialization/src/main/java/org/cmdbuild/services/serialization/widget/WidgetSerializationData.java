/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization.widget;

import org.cmdbuild.widget.model.WidgetData;

/**
 *
 * @author afelice
 */
public class WidgetSerializationData {

    public WidgetData widgetData;
    public String descriptionTranslation;

    public WidgetSerializationData(WidgetData widgetData, String descriptionTranslation) {
        this.widgetData = widgetData;
        this.descriptionTranslation = descriptionTranslation;
    }
}
