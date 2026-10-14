Ext.define('CMDBuildUI.view.administration.navigation.TreeItem', {
    extend: 'Ext.list.TreeItem',
    alias: 'widget.administration-treelistitem',
    controller: 'administration-navigation-tree-item',
    requires: [
        'CMDBuildUI.view.administration.navigation.TreeItemController'
    ],
    
    /**
     * @override
     * @param {*} item 
     * @param {*} refItem 
     */
    insertItem: function (item, refItem) {

        var data = item.getNode().getData();
        var testid = Ext.String.format(
            Ext.String.format(
                'administration-navigation-{0}-{1}', data.menutype, data.objecttype
            )
        );
        item.el.dom.dataset.testid = testid;
        if (refItem) {
            item.element.insertBefore(refItem.element);
        } else {
            this.itemContainer.appendChild(item.element);
        }
    },
    listeners: {
        dblclick: {
            fn: 'dblClick',
            element: 'rowElement'
        }
    }
});
