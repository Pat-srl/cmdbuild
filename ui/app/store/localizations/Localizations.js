Ext.define('CMDBuildUI.store.localizations.Localizations', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.localizations',

    model: 'CMDBuildUI.model.localizations.Localization',

    proxy: {
        type: 'baseproxy',
        url: '/translations'
    },

    autoLoad: false,
    autoDestroy: true,
    pageSize: 0 // disable pagination
});