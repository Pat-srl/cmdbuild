Ext.define('CMDBuildUI.store.dependencyTrees.DependencyTrees', {
    extend: 'CMDBuildUI.store.Base',

    requires: ['CMDBuildUI.model.dependencytrees.DependencyTree'],
    model: 'CMDBuildUI.model.dependencytrees.DependencyTree',

    alias: 'store.dependencyTrees-dependencyTrees',

    config: {
        defaultRootProperty: 'data'
    },

    advancedFilter: {
        attributes: {
            type: [{ operator: 'equal', value: ['dependencytree'] }]
        }
    },

    sorters: ['description'],

    autoLoad: false,
    pageSize: 0 // disable pagination
});
