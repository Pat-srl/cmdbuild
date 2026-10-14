Ext.define('CMDBuildUI.store.importexports.GateTemplates', {
    requires: ['CMDBuildUI.model.importexports.GateTemplate'],
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.importexports-gatetemplates',

    model: 'CMDBuildUI.model.importexports.GateTemplate',

    proxy: {
        type: 'baseproxy',
        url: '/etl/templates',
        extraParams: {
            detailed: true
        }
    },

    sorters: ['index'],
    autoLoad: false,
    pageSize: 0 // disable pagination
});