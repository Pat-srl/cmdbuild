Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.treepanels.OriginPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gismenus-treepanels-originpanel',    

    stores: {
        geoattributesStore: {
            model: 'CMDBuildUI.model.map.GeoAttribute',
            proxy: {
                type: 'baseproxy',
                url: '/classes/_ANY/geoattributes'
            },
            pageSize: 0,
            autoLoad: true,
            autoDestroy: true,
            groupField: 'owner_type',
            listeners: {
                load: 'onGeoAttributesStoreloaded'
            }
        },

        originStore: {
            type: 'tree',
            model: 'CMDBuildUI.model.gis.GisMenuItem',
            root: {
                text: 'Root',
                expanded: true,
                children: []
            },
            proxy: {
                type: 'memory'
            },
            sorters: [{
                property: 'index',
                direction: 'ASC'
            }],
            autoLoad: true,
            autoDestroy: true
        }
    }
});