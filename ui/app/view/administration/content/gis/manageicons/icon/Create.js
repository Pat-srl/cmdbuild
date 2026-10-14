Ext.define('CMDBuildUI.view.administration.content.gis.icon.Create', {
    extend: 'Ext.form.Panel',

    requires: ['CMDBuildUI.view.administration.content.gis.icon.CreateController'],

    alias: 'widget.administration-content-gis-icon-create',
    controller: 'administration-content-gis-icon-create',
    viewModel: {},
    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    items: [
        {
            xtype: 'filefield',
            name: 'file',
            reference: 'file',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
            },
            allowBlank: false,
            accept: '.png',
            buttonConfig: {
                ui: 'administration-secondary-action-small'
            }
        },
        {
            xtype: 'textfield',
            name: 'description',
            allowBlank: false,
            bind: {
                value: '{theIcon.description}'
            },
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            }
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons()
        }
    ]
});
