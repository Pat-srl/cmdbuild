Ext.define('CMDBuildUI.view.widgets.customform.Export', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.widgets.customform.ExportController',
        'CMDBuildUI.view.widgets.customform.ImportExportModel'
    ],

    alias: 'widget.widgets-customform-export',
    controller: 'widgets-customform-export',
    viewModel: {
        type: 'widgets-customform-importexport'
    },

    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    scrollable: true,

    config: {
        /**
         * @cfg {CMDBuildUI.model.attributes.Attribute[]} attributes
         */
        attributes: null,

        /**
         * @cfg {Ext.data.Store} gridStore
         */
        gridStore: null
    },

    items: [{
        xtype: 'combo',
        allowBlank: false,
        fieldLabel: CMDBuildUI.locales.Locales.widgets.customform.importexport.format,
        localize: {
            fieldLabel: "CMDBuildUI.locales.Locales.widgets.customform.importexport.format"
        },
        displayField: 'value',
        valueField: 'value',
        bind: {
            store: '{formats}',
            value: '{format}'
        }
    }, {
        xtype: 'combo',
        allowBlank: false,
        fieldLabel: CMDBuildUI.locales.Locales.widgets.customform.importexport.separator,
        localize: {
            fieldLabel: "CMDBuildUI.locales.Locales.widgets.customform.importexport.separator"
        },
        displayField: 'value',
        valueField: 'value',
        bind: {
            hidden: '{hideSeparator}',
            store: '{separators}',
            value: '{separator}'
        }
    }, {
        xtype: 'textfield',
        allowBlank: false,
        fieldLabel: CMDBuildUI.locales.Locales.widgets.customform.importexport.filename,
        localize: {
            fieldLabel: "CMDBuildUI.locales.Locales.widgets.customform.importexport.filename"
        },
        bind: {
            emptyText: '{filenameEmptyText}',
            value: '{filename}'
        }
    }, {
        xtype: 'fieldcontainer',
        fieldLabel: CMDBuildUI.locales.Locales.widgets.customform.importexport.expattributes,
        localize: {
            fieldLabel: "CMDBuildUI.locales.Locales.widgets.customform.importexport.expattributes"
        },
        items: [{
            xtype: 'grid',
            hideHeaders: true,
            forceFit: true,
            reference: 'attributesGrid',
            columns: [{
                dataIndex: 'description'
            }],
            selModel: {
                selType: 'checkboxmodel'
            },
            bind: {
                store: '{attrs}',
                selection: '{selected}'
            }
        }]
    }],

    buttons: [{
        itemId: 'cancelBtn',
        ui: 'secondary-action-small',
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        autoEl: {
            'data-testid': 'widgets-customform-exportform-cancel'
        }
    }, {
        itemId: 'exportBtn',
        formBind: true,
        ui: 'management-primary-small',
        text: CMDBuildUI.locales.Locales.widgets.customform.export,
        localized: {
            text: 'CMDBuildUI.locales.Locales.widgets.customform.export'
        },
        autoEl: {
            'data-testid': 'widgets-customform-exportform-export'
        }
    }]
});