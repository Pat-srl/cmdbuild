Ext.define('CMDBuildUI.view.dependencytree.Panel', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.dependencytree.PanelModel'],

    alias: 'widget.dependencytree-panel',
    viewModel: {
        type: 'dependencytree-panel'
    },

    config: {
        rootId: null,
        rootClassName: null,
        rootDescription: null,
        childrenPath: null
    },

    layout: 'border',

    items: [
        {
            xtype: 'dependencytree-canvas',
            region: 'center',
            flex: 0.7
        },
        {
            xtype: 'tabpanel',
            region: 'east',
            split: true,
            flex: 0.3,
            ui: 'managementlighttabpanel',
            items: [
                {
                    xtype: 'dependencytree-tab-gerarchic',
                    title: CMDBuildUI.locales.Locales.dependencytree.gerarchicview,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.dependencytree.gerarchicview'
                    }
                },
                {
                    xtype: 'dependencytree-tab-card',
                    title: CMDBuildUI.locales.Locales.dependencytree.cardinfo,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.dependencytree.cardinfo'
                    },
                    disabled: false,
                    bind: {
                        disabled: '{disableTabCard}'
                    }
                }
            ]
        }
    ]
});
