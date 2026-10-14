Ext.define('CMDBuildUI.view.map.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.map.ContainerController',
        'CMDBuildUI.view.map.ContainerModel'
    ],

    alias: 'widget.map-container',
    controller: 'map-container',
    viewModel: {
        type: 'map-container'
    },

    mixins: [
        'CMDBuildUI.view.map.Mixing'
    ],

    config: {
        /**
         * @cfg {} hashMap 
         * key: objectIds
         * value: the visibility of that feature
         */
        hashMap: {
            keyValue: {},
            add: function (key, value) {
                this.keyValue[key] = value;
                if (this._updatecounter == 0) {
                    this.event.fireEventArgs('endupdate', [this]);
                }
            },
            get: function (key) {
                return this.keyValue[key];
            },

            _updatecounter: 0,
            endUpdate: function () {
                this._updatecounter = this._updatecounter > 0 ? --this._updatecounter : 0;

                if (this._updatecounter == 0) {
                    this.event.fireEventArgs('endupdate', [this]);
                }
            },
            beginUpdate: function () {
                ++this._updatecounter;
            },
            event: new Ext.util.Observable()
        }
    },

    /**
     * 
     * @param {*} value 
     * @param {*} oldValue 
     */
    updateHashMap: function (value, oldValue) {
        value.event.addListener('endupdate', this.getController().onHashMapUpdate, this);
    },

    layout: 'border',
    padding: '0',

    items: [{
        xtype: 'map-map',
        region: 'center',
        split: true
    }, {
        xtype: 'map-tab-tabpanel',
        title: CMDBuildUI.locales.Locales.gis.cardsMenu,
        localized: {
            title: 'CMDBuildUI.locales.Locales.gis.cardsMenu'
        },
        region: 'east',
        split: true,
        width: '30%',
        collapsible: true,
        layout: 'container'
    }]
});
