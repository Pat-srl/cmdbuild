Ext.define('CMDBuildUI.view.management.navigation.Tree', {
    extend: 'Ext.list.Tree',

    requires: ['CMDBuildUI.view.management.navigation.TreeController', 'CMDBuildUI.store.menu.Menu'],

    alias: 'widget.management-navigation-tree',
    controller: 'management-navigation-tree',
    autoEl: {
        'data-testid': 'management-navigation-tree'
    },
    ui: 'navigation-mgt',

    /**
     * @property {Boolean} isCMDBuildNavigation
     *
     * Used by `Overrides.tree.RootTreeItem` to add data-test-id in items.
     */
    isCMDBuildNavigation: true,

    config: {
        defaults: {
            listeners: {
                click: {
                    fn: 'onItemClick',
                    element: 'rowElement'
                },
                dblclick: {
                    fn: 'onItemDblClick',
                    element: 'rowElement'
                }
            },

            xtype: 'management-treelistitem'
        }
    },
    scrollable: true,
    expanderOnly: true,

    bind: {
        selection: '{selected.navigation}',
        store: '{menuItems}'
    },

    privates: {
        /**
         * @override
         * Load navigation tree for navtree items.
         * @param {*} node
         * @param {*} parent
         */
        createItem: function (node, parent) {
            var item = this.callParent(arguments);

            if (node.get('menutype') === CMDBuildUI.model.menu.MenuItem.types.navtree) {
                Ext.asap(function () {
                    CMDBuildUI.view.management.navigation.Utils.navtreeAddFirstLevel(node, item);
                });
            }

            return item;
        }
    }
});
