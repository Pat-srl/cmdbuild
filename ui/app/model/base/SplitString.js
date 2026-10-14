/**
 * A generic method to use for combos 
 * which have not a specific model.
 */
Ext.define('CMDBuildUI.model.base.SplitString', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'substring',
        type: 'string',
        validators: ['presence']
    }],

    proxy: {
        type: 'memory'
    }
});