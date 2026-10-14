Ext.define('CMDBuildUI.model.formstructure.Column', {
    extend: 'Ext.data.Model',
    fields: [{
        name: 'attribute',
        type: 'string'
    }, {
        name: 'empty',
        type: 'boolean',
        calculate: function (data) {
            return data.attribute.length > 0;
        }
    }]
});