
Ext.define('CMDBuildUI.view.administration.navigation.Tree', {
    extend: 'Ext.list.Tree',

    requires: [
        'CMDBuildUI.view.administration.navigation.TreeController',
        'CMDBuildUI.view.administration.navigation.TreeModel',

        'CMDBuildUI.store.administration.MenuAdministration'
    ],

    id: 'administrationNavigationTree',
    alias: 'widget.administration-navigation-tree',
    controller: 'administration-navigation-tree',
    viewModel: {
        type: 'administration-navigation-tree'
    },
    autoEl: {
        'data-testid': 'administration-navigation-tree'
    },

    /**
     * @property {Boolean} isCMDBuildNavigation
     *
     * Used by `Overrides.tree.RootTreeItem` to add data-test-id in items.
     */
    isCMDBuildNavigation: true,

    config: {
        selected: null,
        store: null,
        defaults: {
            xtype: 'administration-treelistitem'
        }
    },
    ui: 'administration-navigation-tree',
    expanderOnly: true,

    bind: {
        store: '{menuItems}',
        selection: '{selected}'
    },
    privates: {

        /**
         * Handles when a node expands.
         * @param {Ext.data.TreeModel} node The node.
         * @override
         * @private
         */
        onNodeExpand: function (node) {
            // TODO: use this function for load childrens items
            // find a method for handle childrens url's on page reresh
            this.callParent(arguments);
        }
    }
});
