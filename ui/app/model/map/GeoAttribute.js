Ext.define('CMDBuildUI.model.map.GeoAttribute', {
    requires: ['CMDBuildUI.model.map.GeoAttributeStyle'],
    extend: 'Ext.data.Model',

    statics: {
        type: {
            geometry: 'geometry', // postgis,
            shape: 'shape', // geoserver
            geotiff: 'geotiff' // geoserver
        },
        // SUBTYPES ARE USED ONLY IF type === geometry
        subtype: {
            linestring: 'linestring',
            point: 'point',
            polygon: 'polygon'
        },
        strokeStyle: {
            dash: 'dash',
            dashdot: 'dashdot',
            dot: 'dot',
            longdash: 'longdash',
            longdashdot: 'longdashdot',
            solid: 'solid'
        }
    },
    fields: [
        {
            name: '_id',
            type: 'string',
            critical: true
        },
        {
            name: 'name',
            type: 'string',
            critical: true
        },
        {
            name: 'geoAttributeName',
            type: 'string',
            critical: true
        },
        {
            name: 'owner_type',
            type: 'string',
            critical: true
        },
        {
            name: 'description',
            type: 'string',
            critical: true
        },
        {
            name: 'type',
            type: 'string',
            critical: true,
            defaultValue: 'geometry'
        },
        {
            name: 'subtype',
            type: 'string',
            critical: true,
            defaultValue: 'point'
        },
        {
            name: 'index',
            type: 'integer',
            critical: true
        },
        {
            name: 'zoomMin', // Vanno bene separati i livelli dello zoom?
            type: 'integer',
            critical: true,
            defaultValue: 1
        },
        {
            name: 'zoomDef',
            type: 'integer',
            critical: true,
            defaultValue: 13
        },
        {
            name: 'zoomMax',
            type: 'integer',
            critical: true,
            defaultValue: 25
        },
        {
            name: 'active',
            type: 'bool',
            critical: true,
            defaultValue: true
        },
        {
            name: 'style', //e cosi che si specifica un oggetto
            critical: true,
            reference: {
                type: 'CMDBuildUI.model.map.GeoAttributeStyle',
                unique: true
            }
        },
        {
            name: 'visibility',
            type: 'auto', // array,
            defaultValue: [],
            critical: true
        },
        {
            name: '_icon',
            type: 'string',
            critical: true,
            persistent: true
        },
        {
            name: '_iconPath',
            type: 'string',
            calculate: function (data) {
                return Ext.String.format('{0}/uploads/{1}/image.png', CMDBuildUI.util.Config.baseUrl, data._icon);
            }
        },
        {
            name: 'associatedClass',
            type: 'string',
            critical: true
            // defaultValue: 'Floor' // solo per test, da togliere
        },
        {
            name: 'associatedCard',
            type: 'number',
            critical: true
            // defaultValue: 5974
        },
        {
            name: 'infoWindowEnabled',
            type: 'boolean',
            critical: true
        },
        {
            name: 'infoWindowImage',
            type: 'string',
            critical: true
        },
        {
            name: 'infoWindowContent',
            type: 'string',
            critical: true
        }
    ],

    idProperty: '_id',

    /**
     * Return a clean clone of a geoattribute.
     *
     * @return {CMDBuildUI.model.Attribute} the fresh cloned attribute
     */
    clone: function (clone) {
        var newGeoAttribute = this.copy();
        if (clone) {
            newGeoAttribute.set('_id', undefined);
            newGeoAttribute.set('name', Ext.String.format('{0}_copy', this.get('name')));
            newGeoAttribute.set('description', Ext.String.format('{0}_copy', this.get('description')));
        }
        newGeoAttribute.set('style', this.getAssociatedData().style);
        var geoAttribute = CMDBuildUI.model.map.GeoAttribute.create(newGeoAttribute.getData());
        geoAttribute._style = this._style;

        return geoAttribute;
    },

    proxy: {
        type: 'baseproxy'
    }
});
