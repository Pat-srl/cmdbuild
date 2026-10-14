/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe;

import java.util.List;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.widget.model.WidgetData;

import org.apache.commons.lang3.tuple.Pair;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.formstructure.FormStructure;

public class ExtendedClassDefinitionImpl implements ExtendedClassDefinition {

    private final ClassDefinition classDefinition;
    private final List<FormTrigger> formTriggers;
    private final List<ContextMenuItem> contextMenuItems;
    private final List<WidgetData> widgets;
    private final List<Pair<String, Direction>> defaultClassOrdering;
    private final List<AttributeGroupInfo> attributeGroups;
    private final FormStructure formStructure;

    private ExtendedClassDefinitionImpl(ExtendedClassDefinitionImplBuilder builder) {
        this.classDefinition = checkNotNull(builder.classDefinition);
        this.formTriggers = ImmutableList.copyOf(builder.formTriggers);
        this.contextMenuItems = ImmutableList.copyOf(builder.contextMenuItems);
        this.widgets = ImmutableList.copyOf(builder.widgets);
        this.attributeGroups = ImmutableList.copyOf(builder.attributeGroups);
        this.defaultClassOrdering = ImmutableList.copyOf(builder.defaultClassOrdering);
        this.formStructure = builder.formStructure;
    }

    @Override
    public ClassDefinition getClassDefinition() {
        return classDefinition;
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

    @Override
    public List<Pair<String, Direction>> getDefaultClassOrdering() {
        return defaultClassOrdering;
    }

    @Override
    public List<AttributeGroupInfo> getAttributeGroups() {
        return attributeGroups;
    }

    @Nullable
    @Override
    public FormStructure getFormStructure() {
        return formStructure;
    }

    public static ExtendedClassDefinitionImplBuilder builder() {
        return new ExtendedClassDefinitionImplBuilder();
    }

    public static ExtendedClassDefinitionImplBuilder copyOf(ExtendedClassDefinition source) {
        return new ExtendedClassDefinitionImplBuilder()
                .withClassDefinition(source.getClassDefinition())
                .withFormTriggers(source.getFormTriggers())
                .withContextMenuItems(source.getContextMenuItems())
                .withWidgets(source.getWidgets())
                .withAttributeGroups(source.getAttributeGroups())
                .withDefaultClassOrdering(source.getDefaultClassOrdering())
                .withFormStructure(source.getFormStructure());
    }

    public static class ExtendedClassDefinitionImplBuilder implements Builder<ExtendedClassDefinitionImpl, ExtendedClassDefinitionImplBuilder> {

        private ClassDefinition classDefinition;
        private List<FormTrigger> formTriggers;
        private List<ContextMenuItem> contextMenuItems;
        private List<WidgetData> widgets;
        private List<Pair<String, Direction>> defaultClassOrdering;
        private List<AttributeGroupInfo> attributeGroups;
        private FormStructure formStructure;

        public ExtendedClassDefinitionImplBuilder withClassDefinition(ClassDefinition classDefinition) {
            this.classDefinition = classDefinition;
            return this;
        }

        public ExtendedClassDefinitionImplBuilder withFormTriggers(List<FormTrigger> formTriggers) {
            this.formTriggers = formTriggers;
            return this;
        }

        public ExtendedClassDefinitionImplBuilder withContextMenuItems(List<ContextMenuItem> contextMenuItems) {
            this.contextMenuItems = contextMenuItems;
            return this;
        }

        public ExtendedClassDefinitionImplBuilder withWidgets(List<WidgetData> widgets) {
            this.widgets = widgets;
            return this;
        }

        public ExtendedClassDefinitionImplBuilder withDefaultClassOrdering(List<Pair<String, Direction>> defaultClassOrdering) {
            this.defaultClassOrdering = defaultClassOrdering;
            return this;
        }

        public ExtendedClassDefinitionImplBuilder withAttributeGroups(List<AttributeGroupInfo> attributeGroups) {
            this.attributeGroups = attributeGroups;
            return this;
        }

        public ExtendedClassDefinitionImplBuilder withFormStructure(FormStructure formStructure) {
            this.formStructure = formStructure;
            return this;
        }

        @Override
        public ExtendedClassDefinitionImpl build() {
            return new ExtendedClassDefinitionImpl(this);
        }

    }
}
