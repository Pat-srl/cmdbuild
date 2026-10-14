/**
 * A generic method to use for keyvalue grid
 * 
 */
Ext.define('CMDBuildUI.model.base.KeyDescriptionValue', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'key',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'value',
        type: 'string'
    }],

    proxy: {
        type: 'memory'
    }
});
