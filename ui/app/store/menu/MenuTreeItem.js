Ext.define('CMDBuildUI.store.menu.MenuTreeItem', {
    extend: 'Ext.data.TreeStore',

    requires: [
        'CMDBuildUI.model.navigationTrees.TreeNode'
    ],

    model: 'CMDBuildUI.model.navigationTrees.TreeNode',
    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.api.DomainTrees.getDomainTrees(),
        extraParams: {
            treeMode: 'tree'
        },
        reader: {
            type: 'json',
            rootProperty: function (data) {
                // Extract child nodes from the nodes or data.nodes (for root) property in the dataset
                return data.nodes || data.data && data.data.nodes;
            }
        }
    },

    root: {},
    rootVisible: false,

    autoLoad: false,
    // sorters: ['index'],
    pageSize: 0 // disable pagination

});