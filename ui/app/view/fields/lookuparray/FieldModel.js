Ext.define('CMDBuildUI.view.fields.lookuparray.FieldModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-lookuparray-field',

    formulas: {
        proxyUrl: function (get) {
            return CMDBuildUI.util.api.Lookups.getLookupValues(this.getView().getLookupType());
        }
    },

    stores: {
        lookupValues: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: {
                url: '{proxyUrl}',
                type: 'baseproxy'
            },
            pageSize: 0,
            remoteFilter: false,
            remoteSort: true,
            autoLoad: false,
            autoDestroy: true
        }
    }

});
