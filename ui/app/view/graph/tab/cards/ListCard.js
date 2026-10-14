
Ext.define('CMDBuildUI.view.graph.tab.cards.ListCard', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.graph.tab.cards.ListCardController',
        'CMDBuildUI.view.graph.tab.cards.ListCardModel'
    ],

    alias: 'widget.graph-tab-cards-listcard',
    controller: 'graph-tab-cards-listcard',
    viewModel: {
        type: 'graph-tab-cards-listcard'
    },

    layout: 'fit',
    multiSelect: true,
    bind: {
        store: '{listCardStore}'
    },

    columns: [{
        text: CMDBuildUI.locales.Locales.relationGraph.class,
        localized: {
            text: 'CMDBuildUI.locales.Locales.relationGraph.class'
        },
        dataIndex: 'destTypeDescription',
        align: 'left',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.relations.code,
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.code'
        },
        dataIndex: '_destinationCode',
        align: 'left',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.relations.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.relations.description'
        },
        dataIndex: '_destinationDescription',
        align: 'left',
        flex: 1
    }]
});