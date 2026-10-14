
Ext.define('CMDBuildUI.view.map.tab.cards.Layers', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.LayersController',
        'CMDBuildUI.view.map.tab.cards.LayersModel'
    ],

    alias: 'widget.map-tab-cards-layers',
    controller: 'map-tab-cards-layers',
    viewModel: {
        type: 'map-tab-cards-layers'
    },

    mixins: ['CMDBuildUI.view.map.Mixing'],

    bind: {
        store: '{layersTreeStore}'
    },

    rootVisible: false,

    cls: 'layerTreeCls',

    tbar: [{
        xtype: 'tbfill'
    }, {
        xtype: 'tool',
        cls: Ext.baseCSSPrefix + 'tool-gray',
        itemId: 'saveLayersPreferencesBtn',
        tooltip: CMDBuildUI.locales.Locales.gis.layersTab.savePreferences,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.gis.layersTab.savePreferences'
        }
    }]
});
