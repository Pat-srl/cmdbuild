Ext.define('CMDBuildUI.store.searchfilters.Searchfilters', {
    extend: 'CMDBuildUI.store.Base',
    
    requires: [
        'CMDBuildUI.model.searchfilters.Searchfilter'
    ],

    alias: 'store.searchfilters',

    model: 'CMDBuildUI.model.searchfilters.Searchfilter',

    sorters: ['description'],
    pageSize: 0 // disable pagination
});