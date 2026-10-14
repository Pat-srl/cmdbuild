Ext.define('CMDBuildUI.view.management.navigation.TreeItem', {
    extend: 'Ext.list.TreeItem',

    requires: [
        'CMDBuildUI.view.management.navigation.TreeItemController'
    ],

    alias: 'widget.management-treelistitem',
    controller: 'management-treelistitem',

    listeners: {
        resize: 'onResize',
        contextmenu: {
            fn: 'onContextMenu',
            element: 'wrapElement'
        }
    },

    contextmenuCls: Ext.baseCSSPrefix + 'treelist-item-contextmenu',

    /**
     * @override
     * @param {Ext.list.TreeItem} item
     * @param {refItem} refItem
     */
    insertItem: function (item, refItem) {
        var node = item.getNode();
        var typename = node.get("objecttypename") || node.get("objectdescription");
        var testid = Ext.String.format(
            Ext.String.format(
                'management-navigation-{0}-{1}', node.get("menutype"), typename
            )
        );
        item.el.dom.dataset.testid = testid;
        if (refItem) {
            item.element.insertBefore(refItem.element);
        } else {
            this.itemContainer.appendChild(item.element);
        }
    }
});