Ext.define('CMDBuildUI.store.attachments.Categories', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.model.attachments.Category'
    ],

    alias: 'store.attachments-categories',

    model: 'CMDBuildUI.model.attachments.Category',

    sorters: ['description'],
    pageSize: 0 // disable pagination

});