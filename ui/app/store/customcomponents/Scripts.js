Ext.define('CMDBuildUI.store.customcomponents.Scripts', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.customcomponents.Script'
    ],

    alias: 'store.customcomponents-script',

    model: 'CMDBuildUI.model.customcomponents.Script',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false,
    autoDestroy: true
});