Ext.define('CMDBuildUI.view.administration.content.dependencytrees.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-dependencytrees-tabpanel',
    controller: 'administration-content-dependencytrees-tabpanel',
    viewModel: {
        type: 'administration-content-dependencytrees-tabpanel'
    },

    requires: [
        'CMDBuildUI.view.administration.content.dependencytrees.TabPanelController',
        'CMDBuildUI.view.administration.content.dependencytrees.TabPanelModel',
        'CMDBuildUI.view.administration.content.dependencytrees.View',
        'CMDBuildUI.view.administration.content.dependencytrees.Domains',
        'CMDBuildUI.view.administration.content.dependencytrees.NodeSettings',
        'CMDBuildUI.view.administration.content.dependencytrees.PermissionsTab'
    ],

    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    layout: 'fit',

    bind: {
        activeTab: '{activeTab}'
    },

    items: [],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.administration.dependencytrees.adddependencytree,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.dependencytrees.adddependencytree'
                    },
                    ui: 'administration-action-small',
                    itemId: 'addBtn',
                    autoEl: {
                        'data-testid': 'administration-class-toolbar-adddependencytreeBtn'
                    },
                    disabled: true,
                    bind: {
                        disabled: '{!toolAction._canAdd}'
                    }
                },
                {
                    xtype: 'admin-globalsearchfield',
                    objectType: 'dependencytrees'
                },
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'tbtext',
                    bind: {
                        hidden: '{!theDependencyTree.description}',
                        html:
                            '{dependencyTreeLabel}' +
                            ': <b data-testid="administration-dependencytree-description">{theDependencyTree.description}</b>'
                    }
                }
            ]
        }
    ]
});
