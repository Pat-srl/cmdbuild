
Ext.define('CMDBuildUI.view.importexport.Export', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.importexport.ExportController',
        'CMDBuildUI.view.importexport.ExportModel'
    ],

    alias: 'widget.importexport-export',
    controller: 'importexport-export',
    viewModel: {
        type: 'importexport-export'
    },

    config: {
        /**
         * @cfg {CMDBuildUI.model.importexports.Template []}
         * Allowed templates for data import.
         */
        templates: [],

        /**
         * @cfg {CMDBuildUI.model.classes.Class}
         * Class instance
         */
        object: null,

        /**
         * @cfg {String}
         * The advanced filter applied to the grid encoded
         */
        filter: null
    },

    publish: [
        'templates'
    ],

    twoWayBindable: [
        'templates'
    ],

    bind: {
        'templates': '{templatesList}'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,

    items: [{
        xtype: 'container',
        layout: {
            type: 'hbox',
            align: 'stretch'
        },
        items: [{
            xtype: 'combobox',
            itemId: 'tplcombo',
            flex: 1,
            fieldLabel: CMDBuildUI.locales.Locales.importexport.template,
            style: {
                marginRight: '10px'
            },
            allowBlank: false,
            valueField: '_id',
            displayField: 'description_composed',
            bind: {
                store: '{templates}',
                value: '{values.template}',
                selection: '{selectedTemplate}'
            },
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.importexport.template"
            }
        }, {
            xtype: 'combobox',
            itemId: 'exportcombo',
            fieldLabel: CMDBuildUI.locales.Locales.importexport.export,
            flex: 1,
            style: {
                marginLeft: '10px'
            },
            allowBlank: false,
            valueField: 'value',
            displayField: 'label',
            hidden: false,
            bind: {
                store: '{exporttypes}',
                value: '{values.export}',
                hidden: '{isDomainExport}'
            },
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.importexport.export"
            }
        }]
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.close,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.close'
        },
        ui: 'secondary-action',
        itemId: 'closebtn'
    }, {
        text: CMDBuildUI.locales.Locales.importexport.export,
        localized: {
            text: 'CMDBuildUI.locales.Locales.importexport.export'
        },
        formBind: true,
        itemId: 'exportbtn',
        ui: 'management-primary'
    }]
});
