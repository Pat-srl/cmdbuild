Ext.define('CMDBuildUI.view.bim.xeokit.tab.Panel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.bim-xeokit-tab-panel',

    ui: 'managementlighttabpanel',

    items: [{
        title: CMDBuildUI.locales.Locales.bim.tree.label,
        localized: {
            title: 'CMDBuildUI.locales.Locales.bim.tree.label'
        },
        xtype: 'bim-xeokit-tab-objectstree'
    }, {
        title: CMDBuildUI.locales.Locales.bim.layers.label,
        localized: {
            title: 'CMDBuildUI.locales.Locales.bim.layers.label'
        },
        xtype: 'bim-xeokit-tab-layers'
    }, {
        title: CMDBuildUI.locales.Locales.bim.card.label,
        localized: {
            title: 'CMDBuildUI.locales.Locales.bim.card.label'
        },
        disabled: true,
        bind: {
            disabled: "{!enabledTabs.card}"
        },
        xtype: 'bim-xeokit-tab-card'
    }, {
        title: CMDBuildUI.locales.Locales.bim.ifcproperties.label,
        localized: {
            title: 'CMDBuildUI.locales.Locales.bim.ifcproperties.label'
        },
        disabled: true,
        bind: {
            disabled: '{!enabledTabs.properties}'
        },
        xtype: 'bim-xeokit-tab-properties'
    }]
});