Ext.define('CMDBuildUI.model.gis.GisMenuItem', {
    extend: 'Ext.data.Model',

    requires: [
        'CMDBuildUI.proxy.MenuProxy'
    ],

    fields: [{
        name: 'menutype',
        type: 'string',
        mapping: 'menuType',
        persist: true,
        critical: true
    }, {
        name: 'index',
        type: 'integer',
        persist: true,
        critical: true
    }, {
        name: 'objecttypename',
        type: 'string',
        mapping: 'objectTypeName',
        persist: true,
        critical: true
    }, {
        name: 'objectdescription',
        type: 'string',
        mapping: 'objectDescription',
        persist: true,
        critical: true
    }, {
        name: '_actualDescription',
        type: 'string'
    }, {
        name: 'objectdescription_translation',
        type: 'string',
        mapping: '_objectDescription_translation'
    }, {
        name: 'text',
        type: 'string',
        calculate: function (data) {
            return data.objectdescription_translation || data.objectdescription;
        }
    }, {
        name: 'leaf',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: '_targettypename',
        type: 'string',
        persist: false,
        critical: false
    }, {
        name: '_forAdmin',
        type: 'boolean',
        persist: false,
        critical: false
    }, {
        name: 'children',
        type: 'auto',
        persist: true,
        critical: true
    }, {
        name: 'findcriteria',
        type: 'string',
        persist: false,
        calculate: function (r) {
            return r.menutype + ":" + r.objecttypename;
        }
    }],

    proxy: {
        url: '/sessions/current/',
        type: 'menuproxy'
    }
});