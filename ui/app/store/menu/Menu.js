Ext.define('CMDBuildUI.store.menu.Menu', {
    extend: 'Ext.data.TreeStore',

    requires: [
        'CMDBuildUI.model.menu.MenuItem'
    ],

    alias: 'store.menu',

    model: 'CMDBuildUI.model.menu.MenuItem',
    defaultRootProperty: 'data',
    defaultRootId: 'menu',

    autoLoad: false,
    sorters: ['index'],
    pageSize: 0 // disable pagination

});