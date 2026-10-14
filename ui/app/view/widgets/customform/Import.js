
Ext.define('CMDBuildUI.view.widgets.customform.Import',{
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.widgets.customform.ImportController',
        'CMDBuildUI.view.widgets.customform.ImportExportModel'
    ],

    alias: 'widget.widgets-customform-import',
    controller: 'widgets-customform-import',
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
        itemId: 'formatCombo',
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
        xtype: 'draganddropfilefield',
        allowBlank: false,
        fieldLabel: CMDBuildUI.locales.Locales.widgets.customform.importexport.file,
        localize: {
            fieldLabel: "CMDBuildUI.locales.Locales.widgets.customform.importexport.file"
        },
        allowedExtensions: ['csv', 'xls', 'xlsx']
    }, {
        xtype: 'combo',
        allowBlank: false,
        fieldLabel: CMDBuildUI.locales.Locales.widgets.customform.importexport.importmode,
        localize: {
            fieldLabel: "CMDBuildUI.locales.Locales.widgets.customform.importexport.importmode"
        },
        displayField: 'label',
        valueField: 'value',
        bind: {
            store: '{importmodes}',
            value: '{importmode}'
        }
    }, {
        xtype: 'fieldcontainer',
        fieldLabel: CMDBuildUI.locales.Locales.widgets.customform.importexport.keyattributes,
        localize: {
            fieldLabel: "CMDBuildUI.locales.Locales.widgets.customform.importexport.keyattributes"
        },
        hidden: true,
        bind: {
            hidden: '{importmode !== "merge"}'
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
                store: '{attrs}'
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
        itemId: 'importBtn',
        formBind: true,
        ui: 'management-primary-small',
        text: CMDBuildUI.locales.Locales.widgets.customform.import,
        localized: {
            text: 'CMDBuildUI.locales.Locales.widgets.customform.import'
        },
        autoEl: {
            'data-testid': 'widgets-customform-importform-import'
        }
    }]
});
