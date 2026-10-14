Ext.define('CMDBuildUI.view.graph.tab.cards.ListClass', {
    extend: 'Ext.grid.Panel',

    requires: ['CMDBuildUI.view.graph.tab.cards.ListClassController', 'CMDBuildUI.view.graph.tab.cards.ListClassModel'],

    alias: 'widget.graph-tab-cards-listclass',
    controller: 'graph-tab-cards-listclass',
    viewModel: {
        type: 'graph-tab-cards-listclass'
    },

    bind: {
        store: '{listClassStore}'
    },

    columns: [
        {
            text: CMDBuildUI.locales.Locales.relationGraph.class,
            localized: {
                text: 'CMDBuildUI.locales.Locales.relationGraph.class'
            },
            dataIndex: 'destTypeDescription',
            hideable: false,
            align: 'left',
            flex: 0.7
        },
        {
            text: CMDBuildUI.locales.Locales.relationGraph.quantity,
            localized: {
                text: 'CMDBuildUI.locales.Locales.relationGraph.quantity'
            },
            dataIndex: 'qt',
            hideable: false,
            align: 'center',
            flex: 0.3
        }
    ]
});
