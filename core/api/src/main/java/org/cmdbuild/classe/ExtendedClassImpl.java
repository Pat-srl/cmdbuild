/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import java.util.List;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.widget.model.WidgetData;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.calendar.CalendarTriggerInfo;
import org.cmdbuild.formstructure.FormStructure;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.utils.lang.Builder;

public class ExtendedClassImpl implements ExtendedClass {

    private final Classe classe;
    private final List<FormTrigger> formTriggers;
    private final List<ContextMenuItem> contextMenuItems;
    private final List<WidgetData> widgets;
    private final List<CalendarTriggerInfo> calendarTriggers;
    private final FormStructure formStructure;
    private final Map<String, List<LookupValue>> lookupValuesByAttr;

    private ExtendedClassImpl(ExtendedClassImplBuilder builder) {
        this.classe = checkNotNull(builder.classe);
        this.formTriggers = ImmutableList.copyOf(builder.formTriggers);
        this.contextMenuItems = ImmutableList.copyOf(builder.contextMenuItems);
        this.widgets = ImmutableList.copyOf(builder.widgets);
        this.calendarTriggers = ImmutableList.copyOf(builder.calendarTriggers);
        this.formStructure = builder.formStructure;
        this.lookupValuesByAttr = ImmutableMap.copyOf(builder.lookupValuesByAttr);
    }

    @Override
    public List<CalendarTriggerInfo> getCalendarTriggers() {
        return calendarTriggers;
    }

    @Override
    public Classe getClasse() {
        return classe;
    }

    @Override
    public List<FormTrigger> getFormTriggers() {
        return formTriggers;
    }

    @Override
    public List<ContextMenuItem> getContextMenuItems() {
        return contextMenuItems;
    }

    @Override
    public List<WidgetData> getWidgets() {
        return widgets;
    }

    @Nullable
    @Override
    public FormStructure getFormStructure() {
        return formStructure;
    }

    /**
     * <p>beware</b>: returns only <b>in use</b> {@link LookupValue}s, useful for
     * UI. Use {@link UserClassService#getAllLookupValues()}.
     * 
     * @return 
     */
    @Override
    public Map<String, List<LookupValue>> getLookupValuesByAttr() {
        return lookupValuesByAttr;
    }

    public static ExtendedClassImplBuilder builder() {
        return new ExtendedClassImplBuilder();
    }

    public static ExtendedClassImplBuilder copyOf(ExtendedClass source) {
        return copyOf((ExtendedClassData) source)
                .withLookupValuesByAttr(source.getLookupValuesByAttr())
                .withClasse(source.getClasse());
    }

    public static ExtendedClassImplBuilder copyOf(ExtendedClassData source) {
        return new ExtendedClassImplBuilder()
                .withFormTriggers(source.getFormTriggers())
                .withContextMenuItems(source.getContextMenuItems())
                .withWidgets(source.getWidgets())
                .withFormStructure(source.getFormStructure())
                .withCalendarTriggers(source.getCalendarTriggers());
    }

    public static class ExtendedClassImplBuilder implements Builder<ExtendedClassImpl, ExtendedClassImplBuilder> {

        private Classe classe;
        private List<FormTrigger> formTriggers;
        private List<ContextMenuItem> contextMenuItems;
        private List<WidgetData> widgets;
        private FormStructure formStructure;
        private List<CalendarTriggerInfo> calendarTriggers;
        private Map<String, List<LookupValue>> lookupValuesByAttr;

        public ExtendedClassImplBuilder withClasse(Classe classe) {
            this.classe = classe;
            return this;
        }

        public ExtendedClassImplBuilder withFormTriggers(List<FormTrigger> formTriggers) {
            this.formTriggers = formTriggers;
            return this;
        }

        public ExtendedClassImplBuilder withContextMenuItems(List<ContextMenuItem> contextMenuItems) {
            this.contextMenuItems = contextMenuItems;
            return this;
        }

        public ExtendedClassImplBuilder withWidgets(List<WidgetData> widgets) {
            this.widgets = widgets;
            return this;
        }

        public ExtendedClassImplBuilder withCalendarTriggers(List<CalendarTriggerInfo> calendarTriggers) {
            this.calendarTriggers = calendarTriggers;
            return this;
        }

        public ExtendedClassImplBuilder withFormStructure(FormStructure formStructure) {
            this.formStructure = formStructure;
            return this;
        }

        public ExtendedClassImplBuilder withLookupValuesByAttr(Map<String, List<LookupValue>> lookupValuesByAttr) {
            this.lookupValuesByAttr = lookupValuesByAttr;
            return this;
        }

        @Override
        public ExtendedClassImpl build() {
            return new ExtendedClassImpl(this);
        }

    }
}
