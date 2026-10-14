Ext.define('CMDBuildUI.model.map.GeoAttributeStyle', {
    extend: 'Ext.data.Model',

    fields: [
        {
            name: 'externalGraphic',
            type: 'string',
            critical: true
        },
        {
            name: 'fillColor',
            type: 'string',
            critical: true,
            defaultValue: '#000000'
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
    }
});
