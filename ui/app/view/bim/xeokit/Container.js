Ext.define('CMDBuildUI.view.bim.xeokit.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.bim.xeokit.ContainerController',
        'CMDBuildUI.view.bim.xeokit.ContainerModel'
    ],

    alias: 'widget.bim-xeokit-container',
    controller: 'bim-xeokit-container',
    viewModel: {
        type: 'bim-xeokit-container'
    },

    mixins: [
        'CMDBuildUI.view.bim.xeokit.Mixin'
    ],

    layout: 'border',

    items: [{
        xtype: 'bim-xeokit-tab-panel',
        region: 'west',
        split: true,
        flex: 0.3
    }, {
        xtype: 'bim-xeokit-canvas',
        region: 'center',
        flex: 0.7
    }],

    /**
     * 
     * @returns the viewer of the view
     */
    getViewer: function () {
        return this._viewer;
    },

    /**
     * 
     * @param {String} plugin 
     * @returns a specified plugin 
     */
    getViewerPlugin: function (plugin) {
        return Ext.Array.findBy(this._viewer._plugins, function (item, index) {
            return item.id == plugin;
        });
    },

    /**
     * 
     * @returns the objects tree view
     */
    getObjectTreeTab: function () {
        return this.down("bim-xeokit-tab-objectstree");
    },

    /**
     * 
     * @returns the layer view
     */
    getLayerTab: function () {
        return this.down("bim-xeokit-tab-layers");
    },

    /**
     * 
     * @returns the card view
     */
    getCardTab: function () {
        return this.down("bim-xeokit-tab-card");
    },

    /**
     * 
     * @returns the properties view
     */
    getPropertiesTab: function () {
        return this.down("bim-xeokit-tab-properties");
    },

    /**
     * 
     * @returns the tab panel view
     */
    getTabPanel: function () {
        return this.down("bim-xeokit-tab-panel");
    },

    /**
     * 
     * @returns the canvas panel view
     */
    getCanvasPanel: function () {
        return this.down("bim-xeokit-canvas");
    }

});