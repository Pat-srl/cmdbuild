Ext.define('CMDBuildUI.store.localizations.LocalizationsByCode', {
    extend: 'CMDBuildUI.store.Base',

    alias: 'store.localizations-by-code',

    model: 'CMDBuildUI.model.localizations.LocalizationByCode',

    proxy: {
        type: 'baseproxy',
        url: '/translations/by-code',
        extraParams: {
            includeRecordsWithoutTranslation: true
        }
    },

    autoLoad: false,
    autoDestroy: true,
    pageSize: 0 // disable pagination
});