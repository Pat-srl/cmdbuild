Ext.define('Overrides.tree.RootTreeItem', {
    override: 'Ext.list.RootTreeItem',

    /**
     * @override
     * 
     * @param {*} item 
     * @param {*} refItem 
     */
    insertItem: function (item, refItem) {
        if (item.parent.isCMDBuildNavigation) {
            var data = item.getNode().getData();
            var typename = data.objecttypename || data.objectdescription;
            var testid = Ext.String.format(
                Ext.String.format(
                    'management-navigation-{0}-{1}', data.menutype, typename
                )
            );
            item.el.dom.dataset.testid = testid;
        }
        if (refItem) {
            item.element.insertBefore(refItem.element);
        } else {
            this.element.appendChild(item.element);
        }
    }
});