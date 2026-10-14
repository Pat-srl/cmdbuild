Ext.define('CMDBuildUI.model.gis.Externalservices', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'servicetype',
        type: 'string'
    }, {
        name: 'minimumzoom',
        type: 'number'
    }, {
        name: 'maximumzoom',
        type: 'number'
    }, {
        name: 'geoserverenabled',
        type: 'boolean'
    }, {
        name: 'geoserverurl',
        type: 'string'
    }, {
        name: 'geoserverworkspace',
        type: 'string'
    }, {
        name: 'geoserveradminuser',
        type: 'string'
    }, {
        name: 'geoserveradminpassword',
        type: 'string'
    }],
    proxy: {
        type: 'memory'
    }

});