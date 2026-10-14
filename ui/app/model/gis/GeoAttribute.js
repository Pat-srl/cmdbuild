Ext.define('CMDBuildUI.model.gis.GeoAttribute', {
    extend: 'CMDBuildUI.model.base.Base',

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
            polygon: 'polygon',
            shape: 'shape', // geoserver
            geotiff: 'geotiff' // geoserver
        },
        strokeStyle: {
            dash: 'dash',
            dashdot: 'dashdot',
            dot: 'dot',
            longdash: 'longdash',
            longdashdot: 'longdashdot',
            solid: 'solid'
        },
        GEOATTRIBUTE: 'GEOATTRIBUTE',
        BASETILE: 'BASETILE',

        /**
         *
         * @returns
         */
        getOlLayerVectorStyleRemoved: function () {
            return new ol.style.Style({
                image: new ol.style.Circle({
                    fill: new ol.style.Fill({
                        color: 'rgba(255,165,0,1)' //'orange',//[0, 255, 0, 0.5],
                    }),
                    stroke: new ol.style.Stroke({
                        width: 1,
                        color: 'rgba(255,165,0,1)'
                    }),
                    radius: 6
                }),
                fill: new ol.style.Fill({
                    color: 'rgba(255,165,0,0.5)' //[255, 165, 0, 0.7], //'orange'
                }),
                stroke: new ol.style.Stroke({
                    width: 1,
                    color: 'rgba(255,165,0,1)'
                })
            });
        }
    },

    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'owner_type',
            type: 'string'
        },
        {
            name: 'active',
            type: 'boolean'
        },
        {
            name: 'type', //geometry, geotiff, shape
            type: 'string'
        },
        {
            name: 'subtype', //point, linestring, poligon, geotiff, shape
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: '_description_translation',
            type: 'string'
        },
        {
            name: 'index',
            type: 'number'
        },
        {
            name: 'visibility',
            type: 'auto' //@type {String[]}
        },
        {
            name: 'zoomMin',
            type: 'number'
        },
        {
            name: 'zoomMax',
            type: 'number'
        },
        {
            name: 'zoomDef',
            type: 'number'
        },
        {
            name: '_beginDate',
            type: 'string'
        },
        {
            name: 'style',
            type: 'auto',
            reference: 'CMDBuildUI.model.gis.GeoStyle'
        },
        {
            name: '_icon',
            type: 'auto',
            convert: function (value, record) {
                if (value) {
                    const _img = Ext.dom.Element.create(
                        {
                            tag: 'img',
                            src: Ext.String.format('{0}/uploads/{1}/download', CMDBuildUI.util.Config.baseUrl, value)
                        },
                        'img'
                    );

                    _img.onload = function (event) {
                        record.set('_img', _img);
                    }.bind(_img);
                }
                return value;
            }
        },
        {
            name: 'checked',
            type: 'boolean',
            defaultValue: true,
            persist: false
        },
        {
            name: 'text',
            type: 'string',
            persist: false,
            calculate: function (data) {
                return Ext.String.format(
                    '{0} ({1})',
                    data._description_translation,
                    CMDBuildUI.util.helper.ModelHelper.getObjectDescription(data.owner_type)
                );
            }
        },
        {
            name: 'ollayername',
            type: 'string',
            calculate: function (data) {
                return Ext.String.format(
                    '{0}_{1}_{2}',
                    CMDBuildUI.model.gis.GeoAttribute.GEOATTRIBUTE,
                    data.name,
                    data.owner_type
                );
            },
            persist: false
        },
        {
            name: 'composed_name',
            type: 'string',
            calculate: function (data) {
                return Ext.String.format('{0}.{1}', data.owner_type, data.name);
            },
            persist: false
        },
        {
            name: 'thematism',
            type: 'auto',
            persist: false
        },
        {
            name: '_img',
            type: 'auto',
            persist: false
        }
    ],

    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.api.Classes.getGeoAttributes('Building') //FIXME: work with parent proxy without setting it
    },

    /**
     *
     * @param {*} ownerId
     * @returns
     */
    createEmptyGeoValue: function (ownerId) {
        return Ext.create('CMDBuildUI.model.gis.GeoValue', {
            _id: null,
            _type: this.get('subtype'),
            _attr: this.get('name'),
            _attr_description: this.get('_description_translation'),
            _owner_type: this.get('owner_type'),
            _owner_id: ownerId,
            _can_write: this.get('writable')
        });
    }
});
