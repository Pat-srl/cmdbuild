Ext.define('CMDBuildUI.view.administration.content.gis.icon.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.util.administration.helper.FieldsHelper',
        'CMDBuildUI.view.administration.content.gis.icon.EditController'
    ],

    alias: 'widget.administration-content-gis-icon-edit',
    controller: 'administration-content-gis-icon-edit',
    viewModel: {},

    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    items: [
        {
            xtype: 'fieldcontainer',
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
            },
            items: [
                {
                    columnWidth: 1,
                    xtype: 'filefield',
                    reference: 'file',
                    emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile,
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile'
                    },
                    accept: '.png',
                    buttonConfig: {
                        ui: 'administration-secondary-action-small'
                    }
                },
                {
                    xtype: 'image',
                    height: 32,
                    width: 32,
                    alt: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
                    },
                    itemId: 'classIconPreview',
                    bind: {
                        src: Ext.String.format('{0}/uploads/{theIcon._id}/download', CMDBuildUI.util.Config.baseUrl)
                    }
                }
            ]
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
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
        }
    ]
});
