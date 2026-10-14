Ext.define('CMDBuildUI.store.views.Views', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.views.View'
    ],

    alias: 'store.views',

    model: 'CMDBuildUI.model.views.View',

    // sorters: ['description'],
    remoteFilter: true,
    pageSize: 0, // disable pagination
    autoLoad: false
});