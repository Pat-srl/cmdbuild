Ext.define('CMDBuildUI.view.map.Map', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.map.MapController', 'CMDBuildUI.view.map.MapModel'],

    statics: {
        filterZoomMaxId: 'zoomMax',
        filterZoomMinId: 'zoomMin',
        filterVisibility: 'visibility'
    },

    mixins: ['CMDBuildUI.view.map.Mixing'],

    alias: 'widget.map-map',
    controller: 'map-map',
    viewModel: {
        type: 'map-map'
    },

    config: {
        /**
         * @cfg {olMap} olMap
         */
        olMap: undefined,

        /**
         * @cfg {String} divMapId
         */
        divMapId: undefined,

        /**
         * @cfg {Array} mapCenter
         */
        mapCenter: undefined,

        /**
         * @cfg {} loadedExtentsRtree
         */
        loadedExtentsRtree: null
    },

    labelsVisibility: 'hidden',
    labelSize: null,

    infoWindow: null,
    infoWindowContent: null,

    markerWindow: null,
    markerCmp: null,
    markerEl: null,

    /**
     *
     * @param {Array | undefined} value
     * @param {Array | undefined} oldValue
     * @returns
     */
    applyMapCenter: function (value, oldValue) {
        if (Ext.isArray(value) && Ext.isArray(oldValue) && !Ext.Array.equals(value, oldValue)) {
            return value;
        } else if (Ext.isArray(value) && !Ext.isArray(oldValue)) {
            return value;
        }
    },

    /**
     *
     * @param {Array | undefined} value
     * @param {Array | undefined} oldValue
     */
    updateMapCenter: function (value, oldValue) {
        if (this.getOlMap()) {
            const olMapCenter = this.getOlMap().getView().getCenter();
            if (!Ext.isArray(olMapCenter) || olMapCenter[0] != value[0] || olMapCenter[1] != value[1]) {
                this.getOlMap().getView().setCenter(value);
            }
            this.fireEvent('mapcenterchange', this, value, oldValue);
        }
    },

    /**
     *
     */
    initComponent: function () {
        this.callParent(arguments);

        this.setMapCenter(
            ol.proj.fromLonLat(
                [
                    CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.initialLon),
                    CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.initialLat)
                ],
                'EPSG:3857'
            )
        );

        this.setDivMapId('map-' + Ext.id());
    }
});
