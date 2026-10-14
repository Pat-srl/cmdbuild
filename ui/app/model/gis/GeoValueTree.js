Ext.define('CMDBuildUI.model.gis.GeoValueTree', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        attach_nav_tree: 'attach_nav_tree',
        nav_tree_items: 'nav_tree_items'
    },

    idProperty: '_id_composed',
    fields: [{
        name: '_id_composed',
        type: 'string'
    }, {
        name: 'text',
        type: 'string',
        calculate: function (data) {
            return data.description;
        }
    }, {
        name: '_objectid',
        type: 'string'
    }, {
        name: '_objecttypename',
        type: 'string'
    }, {
        name: '_navtreedef',
        type: 'auto'
    }, {
        name: 'isIntermediate', //returned from server
        type: 'boolean',
        defaultValue: false
    }, {
        name: 'navTreeNodeId',  //returned from server
        type: 'string',
        reference: {
            parent: 'CMDBuildUI.model.navigationTrees.TreeNode'
        }
    }, {
        name: 'parentid',   //returned from server
        type: 'number'
    }, {
        name: 'parenttype', //returned from server
        type: 'string'
    }, {
        name: 'description',    //returned from server
        type: 'string'
    }, {
        name: '_id',    //returned from server
        type: 'number'
    }, {
        name: 'type',
        type: 'string'
    }, {
        name: 'checked',
        type: 'boolean'
    }],

    proxy: {
        type: 'memory'
    }
});