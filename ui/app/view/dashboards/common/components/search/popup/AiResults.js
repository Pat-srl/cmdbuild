Ext.define('CMDBuildUI.view.dashboards.common.components.search.popup.AiResults', {
    extend: 'Ext.container.Container',
    alias: 'widget.homepage-globalsearch-airesults',

    requires: [
        'CMDBuildUI.view.dashboards.common.components.search.popup.ResultsGrid',
        'CMDBuildUI.view.dashboards.common.components.search.popup.NoResults'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    bind: {
        loading: '{aiLoading}'
    },

    items: [
        {
            xtype: 'homepage-globalsearch-resultsgrid',
            flex: 1,
            hidden: true,
            bind: {
                hidden: '{!aiHasResources}'
            },
            store: {
                fields: ['_type', '_cmdb_id', 'Description'],
                proxy: {
                    type: 'memory'
                }
            },
            columns: [
                {
                    dataIndex: 'Description',
                    flex: 2,
                    renderer: Ext.util.Format.htmlEncode,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.relations.description'
                    }
                }
            ]
        },
        {
            xtype: 'homepage-globalsearch-noresults',
            flex: 1,
            hidden: true,
            bind: {
                hidden: '{!noResults}'
            }
        }
    ]
});
