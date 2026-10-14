//the same as geoValue but for shape values
Ext.define('CMDBuildUI.model.gis.GeoLayer', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        checked: 'checked',
        checkedLayer: 'checkedLayer'
    },

    fields: [{
        name: 'active',
        type: 'string'
    }, {
        name: '_attr',
        type: 'string',
        calculate: function (data) {
            return data.name;
        }
    }, {
        name: 'text',
        type: 'string',
        persist: false,
        calculate: function (data) {
            return Ext.String.format('{0} ({1})', data.name, data._owner_type);
        }
    }, {
        name: '_type',
        type: 'string'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'index',
        type: 'number'
    }, {
        name: 'geoserver_layer',
        type: 'string'
    }, {
        name: 'geoserver_name',
        type: 'string'
    }, {
        name: 'geoserver_store',
        type: 'string'
    }, {
        name: '_owner_id',
        type: 'string'
    }, {
        name: '_owner_type',
        type: 'string'
    }, {
        name: 'ollayername',
        type: 'string',
        calculate: function (data) {
            return Ext.String.format('{0}_{1}_{2}',
                CMDBuildUI.model.gis.GeoAttribute.GEOATTRIBUTE,
                data.name,
                data._owner_type);
        },
        persist: false
    }, {
        name: 'checked',
        type: 'boolean',
        defaultValue: true,
        persist: false
    }, {
        name: 'checkedLayer',
        type: 'boolean',
        defaultValue: true,
        persist: false
    }],

    proxy: {
        type: 'baseproxy'
    },

    /**
     * 
     * @returns 
     */
    getOlGeoFeature: function () {
        const olLayer = new ol.layer.Tile({
            // extent: el.get('extent')/* (el.get('extent')) ? (ol.proj.transformExtent(el.get('extent'),'EPSG:4326','EPSG:3857')) : undefined */,
            source: new ol.source.TileWMS({
                url: Ext.String.format('{0}/wms/', CMDBuildUI.util.Config.geoserverBaseUrl),
                params: {
                    'LAYERS': this.get('geoserver_name'),
                    'TILED': true
                },
                serverType: 'geoserver',
                cacheSize: 0
            }),
            zIndex: 1000 - this.get('index')
        });

        /// only for debug
        olLayer.set('visibility', {
            checked: undefined,
            checkedLayer: undefined
        });

        olLayer.setChecked = function (checked) {
            // this.setVisible(checked);
            this.get('visibility').checked = checked;
            this.updatVisibility();
        }.bind(olLayer);

        olLayer.setCheckedLayer = function (checked) {
            // this.setVisible(checked);
            this.get('visibility').checkedLayer = checked;
            this.updatVisibility();
        }.bind(olLayer);

        olLayer.updatVisibility = function () {
            if (this.isVisible()) {
                this.setVisible(true);
            } else {
                this.setVisible(false);
            }
        }.bind(olLayer);

        olLayer.isVisible = function () {
            const visibility = this.get('visibility');
            return visibility.checked && visibility.checkedLayer ? true : false;
        }.bind(olLayer);

        olLayer.setChecked(this.get('checked'));
        olLayer.setCheckedLayer(this.get('checkedLayer'));

        olLayer.set('id', this.getId());
        olLayer.set('name', this.get('ollayername'));
        olLayer.set('type', this.get('type'));

        return olLayer;
    },

    /**
     * 
     * @returns 
     */
    hasValues: function () {
        switch (this.get('_type')) {
            case 'geotiff':
            case 'shape':
                if (Ext.isEmpty(this.get('x')) || Ext.isEmpty(this.get('y'))) {
                    return false;
                }
                break;
        }
        return true;
    },

    /**
     * 
     * @returns 
     */
    getCenter: function () {
        switch (this.get('_type')) {
            case 'geotiff':
            case 'shape':
                return [
                    this.get('x'),
                    this.get('y')
                ];
        }
    },

    /**
     * 
     */
    clearValues: function () {
        switch (this.get('_type')) {
            case 'shape':
            case 'geotiff':
                this.set('x', undefined);
                this.set('y', undefined);
                break;
        }
    }

});