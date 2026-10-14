Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.FormPropertiesFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-formpropertiesfieldset',
    controller: 'administration-content-classes-tabitems-properties-fieldsets-formpropertiesfieldset',
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        collapsed: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.common.labels.formproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.labels.formproperties'
        },
        ui: 'administration-formpagination',
        bind: {
            hidden: '{!theObject}'
        },
        items: [{
            columnWidth: 0.5,
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getValidationRule({
                vmObjectName: 'theObject',
                inputField: 'validationRule'
            })]
        }, {
            columnWidth: 0.5,
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCardAutoValue({
                vmObjectName: 'theObject',
                inputField: 'autoValue'
            })]
        }]
    }]
});