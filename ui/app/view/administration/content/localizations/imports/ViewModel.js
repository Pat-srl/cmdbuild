Ext.define('CMDBuildUI.view.administration.content.localizations.imports.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-localizations-imports-view',
    data: {
        name: 'CMDBuildUI'
    },

    formulas: {
        separatorSelection: function () {
            return [{
                value: ',',
                label: ','
            }, {
                value: ';',
                label: ';'
            }, {
                value: '|',
                label: '|'
            }];
        },

        formatSelection: function () {
            return [{
                value: 'csv',
                label: CMDBuildUI.locales.Locales.administration.navigation.csv
            }];
        }
    },

    stores: {
        separatorsStore: {
            data: '{separatorSelection}',
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label']
        },

        formatsStore: {
            data: '{formatSelection}',
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label']
        }
    }
});