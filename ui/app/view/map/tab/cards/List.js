Ext.define('CMDBuildUI.view.map.tab.cards.List', {
    extend: 'Ext.grid.Panel',

    requires: ['CMDBuildUI.view.map.tab.cards.ListController'],

    mixins: ['CMDBuildUI.mixins.grids.Grid', 'CMDBuildUI.view.map.Mixing'],

    plugins: {
        // Enable columns filters
        gridfilters: true
    },

    alias: 'widget.map-tab-cards-list',
    controller: 'map-tab-cards-list',

    config: {
        /**
         * @cfg {Boolean} allowFilter
         */
        allowFilter: true
    },

    bind: {
        store: '{cards}'
    },

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false, // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
        mode: 'SINGLE'
    }
});
