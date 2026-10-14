Ext.define('CMDBuildUI.store.reports.Reports', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.reports.Report'
    ],

    alias: 'store.reports',

    model: 'CMDBuildUI.model.reports.Report',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false

});