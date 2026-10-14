Ext.define('CMDBuildUI.view.administration.content.dms.models.tabitems.properties.fieldsets.ValididationFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-dms-models-tabitems-properties-fieldsets-valididationfieldset',

    items: [{
        xtype: 'fieldset',
        collapsible: true,
        collapsed: true,
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.title,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.validation.title'
        },
        ui: 'administration-formpagination',
        items: [{
            columnWidth: 0.5,
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getValidationRule({
                vmObjectName: 'theModel',
                inputField: 'validationRule'
            })]
        }]
    }]
});