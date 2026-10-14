Ext.define('CMDBuildUI.model.formstructure.FormItem', {
    extend: 'Ext.data.Model',

    statics: {
        types: {
            form: 'form',
            row: 'row',
            column: 'column',
            field: 'field'
        }
    },

    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'type',
        type: 'string'
    },{
        name: 'index',
        type: 'number'
    }, {
        name: 'children',
        type: 'auto'
    }],

    proxy: {
        type: 'memory'
    }
});