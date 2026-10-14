Ext.define('CMDBuildUI.model.views.JoinViewSorter', {
    extend: 'Ext.data.Model',
    statics: {
        getDefaultOrders: function(){
            return [{
                'value': 'ASC',
                'label': CMDBuildUI.locales.Locales.administration.common.strings.ascending // Ascending
            }, {
                'value': 'DESC',
                'label': CMDBuildUI.locales.Locales.administration.common.strings.descending // Descending
            }];
        }
    },
    fields: [{
        name: 'property',
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
