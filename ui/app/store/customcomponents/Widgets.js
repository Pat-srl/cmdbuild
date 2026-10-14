Ext.define('CMDBuildUI.store.customcomponents.Widgets', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.customcomponents.Widget'
    ],

    alias: 'store.customcomponents-widgets',

    model: 'CMDBuildUI.model.customcomponents.Widget',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false,
    autoDestroy: true
});