Ext.define('CMDBuildUI.view.management.navigation.TreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-navigation-tree',

    control: {
        '#': {
            selectionchange: 'onSelectionChange',
            itemexpand: 'onItemExpand'
        }
    },

    /**
     * Item click listener.
     *
     * @param {Ext.event.Event} event    The Ext.event.Event encapsulating the DOM event.
     * @param {HTMLElement} element      The target of the event.
     * @param {Object} eOpts             The options object passed to Ext.util.Observable.addListener.
     */
    onItemClick: function (event, element, eOpts) {
        const view = Ext.get(element).component;
        if (view) {
            const container = view.getOwner().up();
            container.openResourceByNode(view.getNode());

            // clear selection in favourites menu to prevent double selction in menu
            view.lookupViewModel().set('selected.favourites', null);
        }
    },

    /**
     * Item double click listener.
     *
     * @param {Ext.event.Event} event    The Ext.event.Event encapsulating the DOM event.
     * @param {HTMLElement} element      The target of the event.
     * @param {Object} eOpts             The options object passed to Ext.util.Observable.addListener.
     */
    onItemDblClick: function (event, element, eOpts) {
        const view = Ext.get(element).component;
        if (!(view && view.getExpandable())) {
            event.stopEvent();
            return false;
        }
        if (!view.isExpanded()) {
            view.expand();
        } else {
            view.collapse();
        }
    },

    /**
     * @param {Ext.list.Tree} view
     * @param {Ext.data.TreeModel} record
     * @param {Object} eOpts
     */
    onSelectionChange: function (view, record, eOpts) {
        // expand nodes
        const node = view.getItem(record);

        if (node) {
            // workaround for #622
            this.expandNodeHierarchy(node);
        }
    },

    /**
     *
     * @param {Ext.data.NodeInterface} view
     * @param {Object} item
     */
    onItemExpand: function (view, item) {
        const root = item.getNode();
        var navTreeNode;

        if (CMDBuildUI.util.Navigation.getManagementDetailsWindow()) {
            navTreeNode = CMDBuildUI.util.Navigation.getManagementDetailsWindow().navTreeNode;
        }

        if (
            (root.get('menutype') === CMDBuildUI.model.menu.MenuItem.types.navtreeitem ||
                root.get('menutype') === CMDBuildUI.model.menu.MenuItem.types.navtree) &&
            !root._childrenloaded
        ) {
            CMDBuildUI.view.management.navigation.Utils.loadChildren(item, navTreeNode);
        }
    },

    privates: {
        /**
         *
         * @param {Object} node
         */
        expandNodeHierarchy: function (node) {
            node.expand();
            if (node.getParentItem()) {
                this.expandNodeHierarchy(node.getParentItem());
            }
        }
    }
});
