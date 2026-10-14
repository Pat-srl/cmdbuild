Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.layers.LayersModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-layers-layers',
    data: {
        name: 'CMDBuildUI',
        actions: {
            edit: false,
            view: true
        },
        storedata: {
            url: null,
            autoLoad: false
        },
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theObject._can_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        layersStoreProxy: {
            bind: '{objectTypeName}',
            get: function (objectTypeName) {
                if(objectTypeName){
                    this.set('storedata.url', Ext.String.format('/classes/_ANY/geoattributes', objectTypeName));
                    this.set('storedata.autoLoad', true);
                }
            }
        }
    },
    stores: {
        layersStore: {
            model: 'CMDBuildUI.model.map.GeoAttribute',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.url}',
                extraParams: {
                    visible: true
                }
            },
            pageSize: 0,
            autoLoad: '{storedata.autoLoad}',
            autoDestroy: true
        }
    }
});