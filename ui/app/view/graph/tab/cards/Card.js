Ext.define('CMDBuildUI.view.graph.tab.cards.Card', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.graph.tab.cards.CardController'],

    alias: 'widget.graph-tab-cards-card',
    controller: 'graph-tab-cards-card',

    layout: {
        type: 'vbox',
        align: 'stretch'
    }
});
