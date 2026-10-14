Ext.define('CMDBuildUI.view.administration.content.views.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-views-grid',
    data: {
        name: 'CMDBuildUI',
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
        viewfiltersStoreProxy: {
            bind: {
                objectType:'{objectType}',
                objectTypeName:'{objectTypeName}'
            },
            get: function (data) {
                if(data.objectTypeName && data.objectType){
                
                    this.set('storedata.url', Ext.String.format('/{0}/{1}/viewfilters',Ext.util.Inflector.pluralize( data.objectType.toLowerCase()), data.objectTypeName));
                    this.set('storedata.autoLoad', true);
                }
            }
        }
    },

    stores: {
        viewfiltersStore: {
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
