
Ext.define('CMDBuildUI.view.map.tab.cards.Legend', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.LegendModel'
    ],

    viewModel: {
        type: 'map-tab-cards-legend'
    },

    alias: 'widget.map-tab-cards-legend',

    scrollable: true,

    items: [{
        xtype: 'checkboxfield',
        boxLabel: CMDBuildUI.locales.Locales.thematism.highlightSelected,
        localized: {
            boxLabel: 'CMDBuildUI.locales.Locales.thematism.highlightSelected'
        },
        checked: true,
        padding: '0 0 0 10',
        bind: {
            value: '{highlightselected}'
        }
    }, {
        xtype: 'thematisms-thematism-rules',
        itemId: 'thematisms-thematism-rules',
        forceFit: true
    }]
});
