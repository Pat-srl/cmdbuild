Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.FormProperties', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-formpropertiesfieldset',
    controller: 'administration-content-processes-tabitems-properties-fieldsets-formpropertiesfieldset',
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
            hidden: '{!theProcess}'
        },
        items: [{
            columnWidth: 0.5,
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCardAutoValue({
                vmObjectName: 'theProcess',
                inputField: 'autoValue'
            })]
        }]
    }]
});