Ext.define('CMDBuildUI.store.navigationtrees.NavigationTrees', {
    extend: 'CMDBuildUI.store.Base',

    model: 'CMDBuildUI.model.navigationTrees.DomainTree',

    alias: 'store.navigationTrees.navigationTrees',

    config: {
        defaultRootProperty: 'data'
    },

    advancedFilter: {
        attributes:{
            type: [{operator: 'equal', value: ['default']}]
        }
    },

    sorters: ['description'],

    autoLoad: false,
    pageSize: 0 // disable pagination

});