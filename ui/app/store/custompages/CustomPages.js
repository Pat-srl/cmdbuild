Ext.define('CMDBuildUI.store.custompages.CustomPages', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.custompages.CustomPage'
    ],

    alias: 'store.custompages',

    model: 'CMDBuildUI.model.custompages.CustomPage',

    sorters: ['description'],
    pageSize: 0, // disable pagination
    autoLoad: false,
    autoDestroy: true
});