Ext.define('CMDBuildUI.mixins.model.Hierarchy', {
    mixinId: 'model-hierarchy-mixin',

    getObjectParent: Ext.emptyFn,
    getObjectStore: Ext.emptyFn,
    source: undefined,
    objectType: undefined,

    /**
     * Get CMDBuild hierarchy
     * @return {String[]} A list of class names
     */
    getHierarchy: function () {
        var hierarchy = [];
        var parentName = this.get("parent");
        if (parentName) {
            var parent = this.getObjectParent();
            if (parent) {
                hierarchy = parent.getHierarchy();
            }
            hierarchy.push(this.getId());
        }
        return hierarchy;
    },

    /**
     * Get all children
     * @param {Boolean} leafs Get only leafs
     * @return {Ext.Data.Model[]} A list of children objects
     */
    getChildren: function (leafs) {
        var children = [];
        var store = this.getObjectStore();

        store.filter({
            property: "parent",
            value: this.get("name"),
            exactMatch: true
        });

        var allitems = store.getRange();
        store.clearFilter();

        allitems.forEach(function (p) {
            if (!leafs || (leafs && !p.get("prototype"))) {
                children.push(p);
            }
            children = Ext.Array.merge(children, p.getChildren(leafs));
        });

        return children;
    },

    /**
     * Get all children as tree
     * @param {Boolean} leafs Get only leafs
     * @return {Ext.Data.Model[]} A list of children objects
     */
    getChildrenAsTree: function (leafs, itemCorrection, prototypeDisabled, nodeCorrection) {
        var me = this;
        var children = [];
        var filters = [function (item) {
            return item.get('parent') === me.get('name');
        }];
        var store = Ext.create('Ext.data.ChainedStore', {
            source: me.source,
            filters: filters
        });
        var allitems = store.getRange();
        store.clearFilter();

        allitems.forEach(function (item) {
            if (itemCorrection) {
                item = itemCorrection(item);
            }
            var treeItem = {
                objecttype: me.objectType,
                enabled: item.get('enabled'),
                text: item.get("description"),
                name: item.get('name')
            };
            if ((leafs && item.get("prototype"))) {                
                treeItem.leaf = false;
                treeItem.menutype = 'folder';
                treeItem.expanded = true;
                treeItem.enabled = !prototypeDisabled ? item.get('enabled') ? true : false : false;
                var childrens = item.getChildrenAsTree(leafs, itemCorrection, prototypeDisabled, nodeCorrection);
                treeItem.children = childrens;
            } else {
                treeItem.children = [];
                treeItem.leaf = true;
            }
            if(nodeCorrection){
                treeItem = nodeCorrection(treeItem);
            }
            children.push(treeItem);
        });
        return children;
    }

});