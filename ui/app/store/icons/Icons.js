Ext.define('CMDBuildUI.store.icons.Icons', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.icons',

    model: 'CMDBuildUI.model.icons.Icon',

    proxy: {
        type: 'baseproxy',
        url: '/uploads/'
    },

    autoLoad: false,
    autoDestroy: true,
    pageSize: 0 // disable pagination
});