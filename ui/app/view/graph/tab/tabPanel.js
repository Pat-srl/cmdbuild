Ext.define('CMDBuildUI.view.graph.tab.tabPanel', {
    extend: 'Ext.tab.Panel',

    requires: ['CMDBuildUI.view.graph.tab.tabPanelModel'],

    alias: 'widget.graph-tab-tabpanel',
    deferredRender: false,
    viewModel: {
        type: 'graph-tab-tabpanel'
    },

    items: [
        {
            xtype: 'graph-tab-cards-card',
            itemId: 'graph-tab-cards-card',
            title: CMDBuildUI.locales.Locales.relationGraph.card,
            localized: {
                title: 'CMDBuildUI.locales.Locales.relationGraph.card'
            }
        },
        {
            xtype: 'graph-tab-cards-relations',
            itemId: 'graph-tab-cards-relations',
            isRelationTab: true,
            bind: {
                title: '{relationTabTitle}'
            }
        },
        {
            xtype: 'graph-tab-cards-listcard',
            itemId: 'graph-tab-cards-listcard',
            title: CMDBuildUI.locales.Locales.relationGraph.cardList,
            localized: {
                title: 'CMDBuildUI.locales.Locales.relationGraph.cardList'
            }
        },
        {
            xtype: 'graph-tab-cards-listclass',
            title: CMDBuildUI.locales.Locales.relationGraph.classList,
            localized: {
                title: 'CMDBuildUI.locales.Locales.relationGraph.classList'
            }
        }
    ],

    activeTab: 0
});
