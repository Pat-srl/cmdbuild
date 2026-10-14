Ext.define('CMDBuildUI.store.dashboards.Dashboards', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.dashboards.Dashboard'
    ],

    alias: 'store.dashboards',

    model: 'CMDBuildUI.model.dashboards.Dashboard',
    pageSize: 0 // disable pagination

});