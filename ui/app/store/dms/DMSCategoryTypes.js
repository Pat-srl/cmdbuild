Ext.define('CMDBuildUI.store.dms.DMSCategoryTypes', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.model.dms.DMSCategoryType'
    ],

    alias: 'store.categoryTypes',

    model: 'CMDBuildUI.model.dms.DMSCategoryType',
    sorters: ['description'],
    pageSize: 0 // disable pagination

});