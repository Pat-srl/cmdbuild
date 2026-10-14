Ext.define('CMDBuildUI.model.AttributeOrder', {
    extend: 'Ext.data.Model',
    statics: {
        getDefaultOrders: function(){
            return [{
                'value': 'ascending',
                'label': CMDBuildUI.locales.Locales.administration.common.strings.ascending // Ascending
            }, {
                'value': 'descending',
                'label': CMDBuildUI.locales.Locales.administration.common.strings.descending // Descending
            }];
        }
    },
    fields: [{
        name: 'attribute',
        type: 'string',
        defaultValue: ''
    }, {
        name: 'direction',
        type: 'string',
        defaultValue: ''
    }],
    proxy: {
        type: 'memory'
    }
});
