
Ext.define('CMDBuildUI.view.graph.tab.cards.Relations', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.graph.tab.cards.RelationsController',
        'CMDBuildUI.view.graph.tab.cards.RelationsModel',
        'Ext.grid.feature.Grouping'
    ],

    alias: 'widget.graph-tab-cards-relations',
    controller: 'graph-tab-cards-relations',
    viewModel: {
        type: 'graph-tab-cards-relations'
    },

    layout: 'fit',
    disableSelection: true,
    ui: 'cmdbuildgrouping',
    cls: 'relationgraph',

    bind: {
        store: '{edgesRelationStore}'
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
    }],

    initComponent: function () {
        /**
         * Get group header template
         */
        const domains = Ext.getStore('domains.Domains'),
            headerTpl = Ext.create('Ext.XTemplate',
                '<div>{children:this.formatName} ({children:this.getTotalRows}) {name:this.compoundName}</div>', {
                formatName: function (children) {
                    if (children.length) {
                        const child = children[0],
                            domain = domains.getById(child.get("_type"));
                        if (domain) {
                            return child.get("_is_direct") ? domain.getTranslatedDescriptionDirect() : domain.getTranslatedDescriptionInverse();
                        }
                    }
                },
                getTotalRows: function (rows) {
                    return rows[0].nodes().getRange().length || rows.length;
                },
                compoundName: function (name) {
                    if (name.includes('compound_')) {
                        return 'Compound';
                    }
                    return;
                }
            });

        Ext.apply(this, {
            features: [{
                ftype: 'grouping',
                // groupHeaderTpl: '{name}',
                groupHeaderTpl: headerTpl,
                depthToIndent: 50,
                enableGroupingMenu: false,
                enableNoGroups: false
            }]
        });

        this.callParent(arguments);
    }
});