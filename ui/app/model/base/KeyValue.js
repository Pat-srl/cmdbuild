/**
 * A generic method to use for combos 
 * which have not a specific model.
 */
Ext.define('CMDBuildUI.model.base.KeyValue', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'key',
        type: 'string'
    }, {
        name: 'value',
        type: 'string'
    }],

    proxy: {
        type: 'memory'
    }
});
