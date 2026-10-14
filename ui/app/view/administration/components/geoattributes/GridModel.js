Ext.define('CMDBuildUI.view.administration.components.geoattributes.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-geoattributes-grid',

    data: {
        actions: {
            edit: false,
            view: true,
            add: false
        },
        storedata: {
            url: null,
            autoLoad: false
        }
    },

    formulas: {
        geoattributesStoreProxy: {
            bind: {
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objectTypeName && data.objectType) {
                    const url = Ext.String.format(
                        '/{0}/{1}/geoattributes',
                        Ext.util.Inflector.pluralize(data.objectType.toLowerCase()),
                        data.objectTypeName
                    );
                    this.set('storedata.url', url);
                    this.set('storedata.autoLoad', true);

                    CMDBuildUI.model.map.GeoAttribute.setProxy({
                        url: url,
                        type: 'baseproxy'
                    });
                }
            }
        }
    },

    stores: {
        geoattributesStore: {
            model: 'CMDBuildUI.model.map.GeoAttribute',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.url}'
            },
            pageSize: 0,
            autoLoad: '{storedata.autoLoad}',
            autoDestroy: true
        }
    }
});
