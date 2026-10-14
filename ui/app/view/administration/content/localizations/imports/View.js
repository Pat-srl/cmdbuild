Ext.define('CMDBuildUI.view.administration.content.localizations.imports.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.imports.ViewController',
        'CMDBuildUI.view.administration.content.localizations.imports.ViewModel'
    ],

    alias: 'widget.administration-content-localizations-imports-view',
    controller: 'administration-content-localizations-imports-view',
    viewModel: {
        type: 'administration-content-localizations-imports-view'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        xtype: 'fieldset',
        ui: 'administration-formpagination',
        items: [{
            value: 'CSV',
            xtype: 'combobox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.format,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.format'
            },
            name: 'localizationImportFormat',
            id: 'localizationImportFormat',
            bind: {
                store: '{formatsStore}'
            },
            disabled: true,
            displayField: 'label',
            valueField: 'value',
            autoEl: {
                'data-testid': 'administration-content-localizations-imports-view-formatCombobox'
            }
        }, {
            xtype: 'combobox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.separator,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.separator'
            },
            name: 'localizationImportSeparator',
            id: 'localizationImportSeparator',
            bind: {
                store: '{separatorsStore}'
            },
            value: ';',
            forceSelection: true,
            queryMode: 'local',
            displayField: 'label',
            valueField: 'value',
            autoEl: {
                'data-testid': 'administration-content-localizations-imports-view-separatorCombobox'
            }
        },{

            fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.csvfile,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.csvfile'
            },
            xtype: 'filefield',
            accept: '.csv',
            itemId: 'addfileattachment',
            allowBlank: false,
            buttonConfig: {
                ui: 'administration-secondary-action-small'
            },
            autoEl: {
                'data-testid': 'administration-content-localizations-imports-view-fileFilefield'
            }
        }]
    }],
    buttons: [{
        text: CMDBuildUI.locales.Locales.administration.localizations.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.localizations.cancel'
        },
        reference: 'cancelBtn',
        itemId: 'cancelBtn',
        ui: 'administration-secondary-action-small',
        autoEl: {
            'data-testid': 'administration-content-localizations-imports-view-cancelBtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.localizations.import,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.localizations.import'
        },
        reference: 'importBtn',
        itemId: 'importBtn',
        formBind: true,
        ui: 'administration-action-small',
        autoEl: {
            'data-testid': 'administration-content-localizations-imports-view-importBtn'
        }
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.localizations.import);
        this.callParent(arguments);
    }
});