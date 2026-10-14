Ext.define('CMDBuildUI.store.groups.Groups', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base'
    ],

    alias: 'store.groups',

    model: 'CMDBuildUI.model.users.Group',
    pageSize: 0,

    sorters: ['description'],
    autoLoad: false,
    autoDestroy: true,

    proxy: {
        url: '/roles',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        }
    }
});