Ext.define('CMDBuildUI.view.dashboards.common.components.tile.Tile', {
    extend: 'CMDBuildUI.view.dashboards.common.components.Component',

    requires: [
        'CMDBuildUI.view.dashboards.common.components.tile.TileController',
        'CMDBuildUI.view.dashboards.common.components.tile.TileModel',
        'CMDBuildUI.view.dashboards.common.components.tile.TileTemplateHelper'
    ],

    alias: 'widget.dashboards-components-tile-tile',
    controller: 'dashboards-components-tile-tile',
    viewModel: {
        type: 'dashboards-components-tile-tile'
    },

    height: null,
    minHeight: null,

    privates: {
        /**
         * @override
         * @returns {null}
         */
        getComponentHeight: function () {
            return null;
        }
    },

    initComponent: function () {
        this.height = null;
        this.callParent(arguments);
    }
});
