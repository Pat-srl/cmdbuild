Ext.define('CMDBuildUI.store.lookups.LookupTypes', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.model.lookups.LookupType'
    ],

    alias: 'store.lookupTypes',

    model: 'CMDBuildUI.model.lookups.LookupType',
    sorters: ['description'],
    pageSize: 0 // disable pagination

});