Ext.define('CMDBuildUI.model.gis.GeoValue', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: '_type', //POINT, LINESTRING, POLYGON
        type: 'string'
    }, {
        name: '_attr',
        type: 'string'
    }, {
        name: '_owner_type',
        type: 'string'
    }, {
        name: '_owner_id',
        type: 'number'
    }, {
        name: 'points', //if type is POINT or POLYGON
        type: 'auto'
    }, {
        name: 'x', //If type is POINT
        type: 'auto'
    }, {
        name: 'y', //If type is POINT
        type: 'auto'
    }, {
        name: 'checked',
        type: 'boolean',
        defaultValue: true,
        persist: false
    }, {
        name: 'ollayername',
        type: 'string',
        calculate: function (data) {
            return Ext.String.format('{0}_{1}_{2}',
                CMDBuildUI.model.gis.GeoAttribute.GEOATTRIBUTE,
                data._attr,
                data._owner_type);
        },
        persist: false
    }, {
        name: 'text',
        type: 'string',
        persist: false,
        calculate: function (data) {
            return Ext.String.format('{0} ({1})', Ext.isEmpty(data._attr_description) ? data._attr : data._attr_description, CMDBuildUI.util.helper.ModelHelper.getObjectDescription(data._owner_type));
        }
    }, {
        name: 'hasBim',
        type: 'boolean',
        defaultValue: false,
        persist: false
    }, {
        name: 'projectId',
        type: 'auto',
        defaultValue: null,
        persist: false
    }, {
        name: '_attr_description',
        type: 'string',
        defaultValue: '',
        persist: false
    }],

    proxy: {
        type: 'baseproxy'
    },

    /**
     * 
     * @returns 
     */
    hasValues: function () {
        switch (this.get('_type')) {
            case 'point':
            case 'shape':
            case 'geotiff':
                if (Ext.isEmpty(this.get('x')) || Ext.isEmpty(this.get('y'))) {
                    return false;
                }
                break;
            case 'linestring':
            case 'polygon':
                if (!Ext.isArray(this.get('points'))) {
                    return false;
                }
                break;
        }
        return true;
    },

    /**
     * 
     */
    clearValues: function () {
        switch (this.get('_type')) {
            case 'point':
            case 'shape':
            case 'geotiff':
                this.set('x', undefined);
                this.set('y', undefined);
                break;
            case 'linestring':
            case 'polygon':
                this.set('points', undefined);
                break;
        }
    },

    /**
     * 
     * @returns 
     */
    getJsonData: function () {
        switch (this.get('_type')) {
            case 'point':
                return {
                    _type: 'point',
                    x: this.get('x'),
                    y: this.get('y')
                };
            case 'linestring':
                return {
                    _type: 'linestring',
                    points: this.get('points')
                };
            case 'polygon':
                return {
                    _type: 'polygon',
                    points: this.get('points')
                };
        }
    }
});