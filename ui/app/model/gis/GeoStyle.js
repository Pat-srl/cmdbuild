Ext.define('CMDBuildUI.model.gis.GeoStyle', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        strokeStyle: {
            solid: null,
            dot: [1, 6],
            dash: [16, 6],
            dashdot: [16, 6, 1, 6],
            longdash: [32, 8],
            longdashdot: [32, 8, 1, 8]
        },
        fillType: {
            solid: 'solid',
            pattern: 'pattern'
        }
    },

    fields: [
        {
            name: 'externalGraphic',
            type: 'string',
            critical: true
        },
        {
            name: 'fillType',
            type: 'string',
            critical: true,
            defaultValue: 'solid'
        },
        {
            name: 'fillPattern',
            type: 'auto',
            critical: true
        },
        {
            name: 'fillColor',
            type: 'string',
            critical: true,
            defaultValue: '#000000'
        },
        {
            name: 'fillOpacity',
            type: 'number',
            critical: true,
            defaultValue: '1'
        },
        {
            name: 'fillOpacityCent',
            type: 'number',
            critical: true,
            calculate: function (data) {
                return parseFloat(data.fillOpacity * 100).toFixed(2);
            },
            serialize: function (value, record) {
                record.data.fillOpacity = parseFloat(value / 100).toFixed(2);
            }
        },
        {
            name: 'pointRadius',
            type: 'number',
            critical: true,
            defaultValue: 3
        },
        {
            name: 'strokeColor',
            type: 'string',
            critical: true,
            defaultValue: '#000000'
        },
        {
            name: 'strokeDashstyle',
            type: 'string',
            critical: true,
            defaultValue: 'solid'
        },
        {
            name: 'strokeOpacity',
            type: 'number',
            critical: true,
            defaultValue: 1
        },
        {
            name: 'strokeOpacityCent',
            type: 'number',
            critical: true,
            calculate: function (data) {
                return parseFloat(data.fillOpacity * 100).toFixed(0);
            }
        },
        {
            name: 'strokeWidth',
            type: 'number',
            critical: true,
            defaultValue: 1
        },
        {
            name: 'clusterEnable',
            type: 'boolean',
            critical: true,
            defaultValue: false
        },
        {
            name: 'clusterDistance',
            type: 'number',
            critical: true,
            defaultValue: 20
        }
    ],
    proxy: {
        type: 'memory'
    },

    /**
     * @returns {ol.style.Stroke}
     */
    getOlStroke: function (config) {
        config = config || {};
        return new ol.style.Stroke({
            color: config.color ? config.color : this.getStrokeColor(),
            width: config.width ? config.width : this.get('strokeWidth'),
            lineDash: CMDBuildUI.model.gis.GeoStyle.strokeStyle[this.get('strokeDashstyle')] || null
        });
    },

    /**
     * @returns {ol.style.Fill}
     */
    getOlFill: function () {
        if (this.get('fillType') === CMDBuildUI.model.gis.GeoStyle.fillType.pattern) {
            // Complete pattern with drawTile fn
            const pattern = CMDBuildUI.util.helper.MapHelper.getPatternRenderer(this.get('fillPattern'));
            const tile = document.createElement('canvas');
            const ctx = tile.getContext('2d');
            tile.width = pattern.opts.tileWidth || pattern.opts.spacing || 8;
            tile.height = pattern.opts.tileHeight || pattern.opts.spacing || 8;

            pattern.drawTile(
                ctx,
                Ext.Object.merge(pattern.opts, {
                    color: this.get('fillColor')
                })
            );

            return new ol.style.Fill({
                color: ctx.createPattern(tile, 'repeat')
            });
        }

        return new ol.style.Fill({
            color: this.getFillColor()
        });
    },

    privates: {
        /**
         * @returns {Array}
         */
        getStrokeColor: function () {
            const c = CMDBuildUI.util.Color.hexToRgbA(this.get('strokeColor'));
            c.push(this.get('strokeOpacity'));

            return c;
        },

        /**
         * @returns {Array}
         */
        getFillColor: function () {
            const c = CMDBuildUI.util.Color.hexToRgbA(this.get('fillColor'));
            c.push(Ext.num(this.get('fillOpacity')));

            return c;
        }
    }
});
