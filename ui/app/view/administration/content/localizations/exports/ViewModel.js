Ext.define('CMDBuildUI.view.administration.content.localizations.exports.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-localizations-exports-view',
    data: {
        selection: []
    },

    formulas: {
        sectionsSelection: function () {
            return [{
                value: 'all',
                label: CMDBuildUI.locales.Locales.administration.localizations.all
            }, {
                value: 'classes',
                label: CMDBuildUI.locales.Locales.administration.navigation.classes
            }, {
                value: 'processes',
                label: CMDBuildUI.locales.Locales.administration.navigation.processes
            }, {
                value: 'domains',
                label: CMDBuildUI.locales.Locales.administration.navigation.domains
            }, {
                value: 'views',
                label: CMDBuildUI.locales.Locales.administration.navigation.views
            }, {
                value: 'searchfilters',
                label: CMDBuildUI.locales.Locales.administration.navigation.searchfilters
            }, {
                value: 'lookuptypes',
                label: CMDBuildUI.locales.Locales.administration.navigation.lookuptypes
            }, {
                value: 'reports',
                label: CMDBuildUI.locales.Locales.administration.navigation.reports
            }, {
                value: 'menu',
                label: CMDBuildUI.locales.Locales.administration.navigation.menus
            }];
        },

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
        languages: {
            type: 'translatable-languages',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url: Ext.String.format(
                    '{0}/languages?active=true',
                    CMDBuildUI.util.Config.baseUrl
                ),
                type: 'baseproxy'
            },
            listeners: {
                datachanged: 'onLaguagesStoreLoad'
            },
            pageSize: 0
        },

        sectionsStore: {
            data: '{sectionsSelection}',
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label']
        },

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