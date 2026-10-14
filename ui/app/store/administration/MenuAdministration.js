Ext.define('CMDBuildUI.store.administration.MenuAdministration', {
    extend: 'Ext.data.TreeStore',

    requires: [
        'CMDBuildUI.model.administration.MenuItem'
    ],

    alias: 'store.menu-administration',
    proxy: {
        type: 'memory'
    },
    config: {
        model: 'CMDBuildUI.model.administration.MenuItem',
        defaultRootProperty: 'data',
        defaultRootId: 'menu'
    },
    autoLoad: false,
    autoSort: true,
    sorters: ['index'],

    pageSize: 0 // disable pagination

});