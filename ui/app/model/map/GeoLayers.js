Ext.define('CMDBuildUI.model.map.GeoLayers', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        types: {
            shape: 'shape',
            geotiff: 'geotiff'
        }
    },

    fields: [{
        name: 'geoserver_name',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    }, {
        name: 'index',
        type: 'integer',
        critical: true
    }, {
        name: 'name',
        type: 'string',
        critical: true
    }, {
        name: 'owner_id',
        type: 'string',
        critical: true
    }, {
        name: 'owner_type',
        type: 'string',
        critical: true
    }, {
        name: 'type',
        type: 'string',
        critical: true
    }, {
        name: 'active',
        type: 'bool',
        critical: false,
        persist: false
    }, {
        name: 'attribute_active',
        type: 'bool',
        critical: false,
        persist: false
    }, {
        name : 'layer_active',
        type: 'boolean',
        crititcal: true,
        defaultValue: true
    }],
    proxy: {
        type: 'baseproxy'
    },

    /**
     * Return a clean clone of a geoattribute.
     * 
     * @return {CMDBuildUI.model.Attribute} the fresh cloned attribute
     */
    clone: function () {
        var newGeoAttribute = this.copy();
        newGeoAttribute.set('_id', undefined);
        newGeoAttribute.set('name', '');
        newGeoAttribute.set('description', '');
        newGeoAttribute.crudState = "C";
        newGeoAttribute.phantom = true;
        delete newGeoAttribute.crudStateWas;
        delete newGeoAttribute.previousValues;
        delete newGeoAttribute.modified;
        return newGeoAttribute;
    }

});