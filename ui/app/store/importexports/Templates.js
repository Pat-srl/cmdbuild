Ext.define('CMDBuildUI.store.importexports.Templates', {
    requires: ['CMDBuildUI.model.importexports.Template'],
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.importexports-templates',

    model: 'CMDBuildUI.model.importexports.Template',

    proxy: {
        type: 'baseproxy',
        url: '/etl/templates',
        extraParams: {
            detailed: true
        }
    },

    sorters: ['description'],
    advancedFilter: {
        attributes: {
            fileFormat: [{
                operator: 'IN',
                value: [CMDBuildUI.model.importexports.Template.fileTypes.csv, CMDBuildUI.model.importexports.Template.fileTypes.xls, CMDBuildUI.model.importexports.Template.fileTypes.xlsx]
            }]
        }
    },
    autoLoad: false,
    pageSize: 0 // disable pagination
});