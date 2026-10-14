Ext.define('CMDBuildUI.model.navigationTrees.TreeNode', {
    extend: 'CMDBuildUI.model.base.Base',
    statics: {
        subclassViewMode: {
            cards: 'cards',
            subclasses: 'subclasses'
        }
    },
    fields: [{
        name: 'domain',
        type: 'string'
    }, {
        name: 'filter',
        type: 'string'
    }, {
        name: 'recursionEnabled',
        type: 'boolean'
    }, {
        name: 'showOnlyOne',
        type: 'boolean'
    }, {
        name: 'direction',
        type: 'string'
    }, {
        name: 'parent',
        type: 'string'
    }, {
        name: 'targetClass',
        type: 'string'
    }, {
        name: 'params',
        type: 'string'
    }, {
        name: 'description',
        type: 'string' // optional, only for menu navtree type
    }, {
        name: 'subclassViewMode',
        type: 'string', // only if targetClass is superclass, values can be cards|subclasses
        defaultValue: 'cards'
    }, {
        // optional, only if subclassViewMode is subclasses.
        // If true show intermedie subclass as node, else show sublclass as leaf
        name: 'subclassViewShowIntermediateNodes',
        type: 'boolean',
        defaultValue: true
    }, {
        // only if targetClass is superclass, contain subclasses as list, comma separated. If empty, show Superclass
        name: 'subclassFilter',
        type: 'string'

    }], //Recursion Enabled param is not inserted //TODO: avoid the inserting of the reade of metadata cmp.

    hasMany: [{
        name: 'childs',
        model: 'CMDBuildUI.model.navigationTrees.TreeNode',
        associationKey: 'nodes',
        inverse: {
            getterName: 'getParent'
        }
    }],

    proxy: {
        type: 'memory'
    },

    isNavRoot: function () {
        return Ext.isEmpty(this.getParent());
    },

    isNavLeaf: function () {
        return Ext.isEmpty(this.childs().getRange());
    },

    /**
     * @returns {CMDBuildUI.util.AdvancedFilter}
     */
    getDownFilter: function (sourceTypeName, sourceId, destinationTypeName) {
        var filter = {};
        if (this.get("domain")) {
            filter.relation = [{
                domain: this.get("domain"),
                source: sourceTypeName,
                destination: destinationTypeName, //could be this.get('targetClass') but due to subclassViewMode the targetClass could be different;
                direction: this.get("direction") == '_1' ? '_1' : '_2',
                type: "oneof",
                cards: [{
                    className: sourceTypeName,
                    id: sourceId
                }]
            }];
        }

        // add cql filter
        if (this.get("ecqlFilter")) {
            filter.ecql = {
                id: this.get("ecqlFilter").id
            };
        }

        if (this.get("subclassViewMode") === CMDBuildUI.model.navigationTrees.TreeNode.subclassViewMode.cards && this.get("subclassFilter")) {
            filter.attributes = { 'IdClass': { operator: 'in', value: this.get("subclassFilter").split(",") } };
        }

        // var advancedfilter = new CMDBuildUI.util.AdvancedFilter();
        // advancedfilter.applyAdvancedFilter(filter);
        return filter
    },

    /**
     *
     * @param {Number} sourceId
     * @param {String} sourceTypeName
     *
     * assert sourceTypeName is equal to this.getParent().get('targhetClass') or a subclass of it
     */
    getUpFilter: function (sourceTypeName, sourceId, destinationTypeName) {
        var filter = {};
        if (this.get("domain")) {
            filter.relation = [{
                domain: this.get("domain"),
                source: sourceTypeName, // could bt this.get('targetClass'), but due to subclassViewMode we are not sure wich is the starting class;
                destination: destinationTypeName,
                direction: (this.get("direction") == '_1') ? '_2' : '_1',
                type: "oneof",
                cards: [{
                    className: sourceTypeName,
                    id: sourceId
                }]
            }];
        }

        // NOTE:  the ecql filter is not considerated because makes no sense in the inverse relazione

        // var advancedfilter = new CMDBuildUI.util.AdvancedFilter();
        // advancedfilter.applyAdvancedFilter(filter);
        return filter
    }
});