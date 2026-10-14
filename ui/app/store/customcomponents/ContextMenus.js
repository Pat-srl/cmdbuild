Ext.define('CMDBuildUI.store.customcomponents.ContextMenus', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.customcomponents.ContextMenu'
    ],

    alias: 'store.customcomponents',

    model: 'CMDBuildUI.model.customcomponents.ContextMenu',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false,
    autoDestroy: true
});