Ext.define('CMDBuildUI.store.TranslatableLanguages', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.Language'
    ],

    alias: 'store.translatable-languages',
    proxy: {
        type: 'baseproxy',
        url: '/languages'
    },

    model: 'CMDBuildUI.model.Language',
    pageSize: 0,
    autoLoad: false
});