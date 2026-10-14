Ext.define('CMDBuildUI.store.menu.NavigationTrees', {
    extend: 'CMDBuildUI.store.Base',

    model: 'CMDBuildUI.model.navigationTrees.DomainTree',

    alias: 'store.menu.navigationTrees',

    config: {
        defaultRootProperty: 'data'
    },

    advancedFilter: {
        attributes:{
            type: [{operator: 'equal', value: ['menu']}]
        }
    },

    sorters: ['description'],

    autoLoad: false,
    pageSize: 0 // disable pagination

});