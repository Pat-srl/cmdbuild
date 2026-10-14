Ext.define('CMDBuildUI.view.administration.content.gis.layersorder.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gis-layersorder-grid',
    data: {
        storeAutoLoad: false,
        storeProxyUrl: ''
    },
    formulas: {

        updateStoreVariables: {
            get: function (data) {
                this.set(
                    "storeProxyUrl",
                    Ext.String.format(
                        '{0}/classes/_ANY/geoattributes',
                        CMDBuildUI.util.Config.baseUrl
                    )
                );
                // set auto load
                this.set("storeAutoLoad", true);

            }
        }
    },

    stores: {
        layersStore: {
            model: 'CMDBuildUI.model.map.GeoAttribute',
            proxy: {
                type: 'baseproxy',
                url: '{storeProxyUrl}',
                extraParams: {
                    visible: true
                }
            },
            pageSize: 0,
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true
        }
    }

});