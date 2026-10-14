/**
 * A generic method to use for combos 
 * which have not a specific model.
 */
Ext.define('CMDBuildUI.model.base.ComboItem', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'value',
        type: 'string'
    }, {
        name: 'label',
        type: 'string'
    }],

    idProperty: 'value',

    proxy: 'memory'
});
