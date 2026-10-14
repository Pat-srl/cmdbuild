Ext.define('CMDBuildUI.model.navigationTrees.DomainTree', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        types: {
            'default': 'default',
            'menu': 'menu'
        }
    },

    fields: [{
        name: '_id',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'name',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        persist: true,
        critical: true
    }, {
        name: 'type',
        type: 'string',
        defaultValue: 'default',
        persist: true,
        critical: true
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.navigationTrees.TreeNode',
        name: 'nodes',
        associationKey: 'nodes'
    }],

    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.api.DomainTrees.getDomainTrees()
    },

    /**
     * @returns {Ext.data.Model} Ther root of the tree model: CMDBuildUI.model.navigationTrees.TreeNode
     */
    getRoot: function () {
        var index = this.nodes().findBy(function (record, id) {
            if (!record.parent) {
                return true;
            }
        });

        return this.nodes().getAt(index);
    },

    /**
     * This function finds the records wich have parent = id
     * @param {String} id
     * @returns {[Ext.data.Model]} model: CMDBuildUI.model.navigationTrees.TreeNode
     */
    getChild: function (id) {
        var childs = [];

        this.nodes().getRange().forEach(function (record) {
            if (record.get('parent') === id) {
                childs.push(record);
            }
        }, this);

        return childs;
    },

    /**
     * This function return the parent record of the one passed by id
     * @param {String} id
     * @returns {Ext.data.Model} model: CMDBuildUI.model.navigationTrees.TreeNode
     */
    getParent: function (id) {
        var record = this.nodes().findRecord('_id', id);
        var parentId = record.get('parent');

        return this.nodes().findRecord('_id', parentId);
    },

    /**
     * @param {String} id 
     */
    getNode: function (id) {
        return this.nodes().findRecord('_id', id);
    },

    getNodeRecursive: function (id) {
        var root = this.getRoot();
        return this._getNodeRecursive(root, '_id', id);
    },

    _getNodeRecursive: function (node, property, value) {
        if (node) {
            if (node.get(property) == value) {
                return node;
            } else {
                var childs = node.childs().getRange();

                for (var i = 0; i < childs.length; i++) {
                    var child = childs[i];
                    var foundChild = this._getNodeRecursive(child, property, value);
                    if (foundChild) {
                        return foundChild;
                    }
                }

                return null;
            }
        }
    },

    _getNodeRecursiveBy: function (node, fun, scope) {
        if (node) {
            if (fun.call(scope || this, node)) {
                return node;
            } else {
                var childs = node.childs().getRange();

                for (var i = 0; i < childs.length; i++) {
                    var child = childs[i];
                    var foundChild = this._getNodeRecursiveBy(child, fun, scope);
                    if (foundChild) {
                        return foundChild;
                    }
                }
                return null;
            }
        }
    },

    _getNodeRecursiveAllBy: function (node, fun, scope, array) {
        var a = [];
        if (node) {
            if (fun.call(scope || this, node)) {
                a.push(node);
            }

            var childs = node.childs().getRange();
            for (var i = 0; i < childs.length; i++) {
                var child = childs[i];
                var foundChild = this._getNodeRecursiveAllBy(child, fun, scope, array);
                if (foundChild) {
                    Ext.Array.forEach(foundChild, function (item, index, array) {
                        a.push(item);
                    });
                }
            }
        }
        return a;
    },

    find: function (property, value) {
        var root = this.getRoot();
        return this._getNodeRecursive(root, property, value);
    },

    findBy: function (fun, scope) {
        var root = this.getRoot();
        return this._getNodeRecursiveBy(root, fun, scope);
    },

    findAllBy: function (fun, scope) {
        var root = this.getRoot();
        return this._getNodeRecursiveAllBy(root, fun, scope);
    },
    /**
     * Get translated description
     * @param {Boolean} [force] default null (if true return always the translation even if exist,
     *  otherwise if viewContext is 'admin' return the original description)
     * @return {String} The translated description if exists. Otherwise the description.
     */
     getTranslatedDescription: function (force) {
        if (!force && CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            return this.get("description");
        }
        return this.get("_description_translation") || this.get("description");
    }
});